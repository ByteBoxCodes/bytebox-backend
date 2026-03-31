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
     * The target Docker image for this language execution (e.g. "python:3.11-slim")
     */
    protected abstract String getDockerImage();

    /**
     * Constructs the specific internal bash command that securely parses the base64 code string, 
     * writes it to the appropriate file if necessary, compiles it if necessary, and executes it.
     */
    protected abstract String getExecutionCommand(String base64Code);

    @Override
    public DockerExecutionResult execute(String code, String input) {
        try {
            String base64Code = Base64.getEncoder().encodeToString(code.getBytes());
            String command = getExecutionCommand(base64Code);

            ProcessBuilder pb = new ProcessBuilder(
                    "docker",
                    "run",
                    "-i",
                    "--rm",
                    "--network", "none",
                    "--memory", "256m",
                    "--cpus", "0.5",
                    getDockerImage(),
                    "sh",
                    "-c",
                    command
            );

            Process process = pb.start();

            // Feed user input to the container
            if (input != null && !input.isEmpty()) {
                OutputStream os = process.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                os.close();
            } else {
                process.getOutputStream().close();
            }

            // Await completion with strict timeout
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);

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
