package com.byteboxcodes.byteboxbackend.service.strategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;

public abstract class AbstractDockerExecutionStrategy implements CodeExecutionStrategy {

    /**
     * The target Docker image for this language execution.
     * Prefer Alpine-based images for minimal footprint (e.g. "python:3.11-alpine").
     */
    protected abstract String getDockerImage();

    /**
     * Constructs the internal shell command that decodes the base64 source code,
     * writes it to /tmp (RAM-backed tmpfs), compiles if needed, and executes.
     * All file writes MUST target /tmp/ — never the container's read-only root layer.
     */
    protected abstract String getExecutionCommand(String base64Code);

    @Override
    public DockerExecutionResult execute(String code, String input) {
        try {
            String base64Code = Base64.getEncoder().encodeToString(code.getBytes());
            String command = getExecutionCommand(base64Code);

            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "run",
                    "-i",                                 // interactive stdin pipe
                    "--rm",                               // auto-delete container on exit
                    "--log-driver", "none",               // disable logs to save disk space
                    "--read-only",                        // root filesystem is fully read-only
                    "--tmpfs", "/tmp:rw,nosuid,exec,size=64m", // exec needed: gcc/g++ binaries run from /tmp
                    "--network", "none",                  // zero internet access
                    "--memory", "156m",                   // hard RAM cap (enough for JVM + code)
                    "--memory-swap", "156m",              // disable swap — no disk overflow
                    "--cpus", "0.5",                      // max half a CPU core
                    "--pids-limit", "50",                 // block fork bombs (max 50 processes)
                    "--ulimit", "nofile=64:64",           // max 64 open file descriptors
                    getDockerImage(),
                    "sh", "-c",
                    command
            );

            Process process = pb.start();

            // Feed user input to the container via stdin
            if (input != null && !input.isEmpty()) {
                OutputStream os = process.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                os.close();
            } else {
                process.getOutputStream().close();
            }

            // Enforce 10-second hard timeout
            boolean finished = process.waitFor(15, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return DockerExecutionResult.builder()
                        .isTimeout(true)
                        .build();
            }

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String stdout = stdInput.lines().collect(Collectors.joining("\n"));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String stderr = stdError.lines().collect(Collectors.joining("\n"));

            return DockerExecutionResult.builder()
                    .stdout(stdout)
                    .stderr(stderr)
                    .exitCode(process.exitValue())
                    .isTimeout(false)
                    .build();

        } catch (Exception e) {
            return DockerExecutionResult.builder()
                    .stderr("System Execution Error: " + e.getMessage())
                    .exitCode(-1)
                    .build();
        }
    }
}
