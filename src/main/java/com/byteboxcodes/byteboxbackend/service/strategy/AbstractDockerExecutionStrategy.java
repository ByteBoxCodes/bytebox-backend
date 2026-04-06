package com.byteboxcodes.byteboxbackend.service.strategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.byteboxcodes.byteboxbackend.config.DockerExecutionConfig;
import com.byteboxcodes.byteboxbackend.dto.DockerExecutionResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDockerExecutionStrategy implements CodeExecutionStrategy {

    @Autowired
    protected DockerExecutionConfig dockerConfig;

    /**
     * The target Docker image for this language execution.
     * Prefer Alpine-based images for minimal footprint (e.g. "python:3.11-alpine").
     */
    public abstract String getDockerImage();

    /**
     * Constructs the internal shell command that decodes the base64 source code,
     * writes it to /tmp (RAM-backed tmpfs), compiles if needed, and executes.
     * All file writes MUST target /tmp/ — never the container's read-only root
     * layer.
     */
    protected abstract String getExecutionCommand(String base64Code);

    @Override
    public DockerExecutionResult execute(String code, String input, String containerId) {
        try {
            String base64Code = Base64.getEncoder().encodeToString(code.getBytes());
            String command = getExecutionCommand(base64Code);
            
            // Execute using "docker exec -i <container_id> sh -c <command>"
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "exec", "-i",
                    containerId,
                    "sh", "-c", command
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

            // Read output sequentially to prevent buffer deadlocks
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String stdout = stdInput.lines().collect(Collectors.joining("\n"));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String stderr = stdError.lines().collect(Collectors.joining("\n"));

            // Enforce hard timeout
            boolean finished = process.waitFor(dockerConfig.getTimeoutSeconds(), TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return DockerExecutionResult.builder()
                        .isTimeout(true)
                        .build();
            }

            return DockerExecutionResult.builder()
                    .stdout(stdout)
                    .stderr(stderr)
                    .exitCode(process.exitValue())
                    .isTimeout(false)
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Execution interrupted", e);
            return DockerExecutionResult.builder()
                    .stderr("Execution interrupted")
                    .exitCode(-1)
                    .build();
        } catch (Exception e) {
            log.error("System Execution Error", e);
            return DockerExecutionResult.builder()
                    .stderr("System Execution Error: " + e.getMessage())
                    .exitCode(-1)
                    .build();
        }
    }
}

