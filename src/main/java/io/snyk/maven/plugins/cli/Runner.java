package io.snyk.maven.plugins.cli;

import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Runner {

    private static final String INTEGRATION_NAME = "MAVEN_PLUGIN";

    private Runner() {
    }

    public static Result runSnyk(String task) {
        String command;
        if (SystemUtils.IS_OS_WINDOWS) {
            command = "snyk.exe";
        } else {
            command = "./snyk";
        }

        return runCommand(command + " " + task);
    }

    public static Result runCommand(String task) {
        try {
            Process process = Runtime.getRuntime().exec(task + " --integration-name="+INTEGRATION_NAME);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder content = new StringBuilder();
            StringBuilder error = new StringBuilder();
            boolean hasError = false;
            while ((line = stdInput.readLine()) != null) {
                content.append("\n");
                content.append(line);
            }

            while ((line = stdError.readLine()) != null) {
                if (!hasError) hasError = true;
                content.append("\n");
                error.append(line);
            }

            process.waitFor();
            int exitStatus = process.exitValue();
            String result = content.toString() + (hasError ? "Error: " + error.toString() : "");
            return new Result(result, exitStatus);

        } catch (InterruptedException e) {
            throw new RuntimeException("Internal error", e);
        } catch (IOException e) {
            return new Result(e.getMessage(), 1);
        }
    }

    public static class Result {

        private String output;
        private int exitcode;

        public Result(String output, int exitcode) {
            this.output = output;
            this.exitcode = exitcode;
        }

        public String getOutput() {
            return output;
        }

        public int getExitcode() {
            return exitcode;
        }

        public boolean failed() {
            return exitcode != 0;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "output='" + output + '\'' +
                    ", exitcode=" + exitcode +
                    '}';
        }
    }
}
