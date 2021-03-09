package io.snyk;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Runner {
    public static void run(Log log, String... command) throws IOException, InterruptedException, MojoFailureException {
        ProcessBuilder pb = new ProcessBuilder().command(command);
        Process process = pb.start();

        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream())); // getInputStream() is for stdout
        BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        while ((line = stdoutReader.readLine()) != null) {
            log.info(line);
        }
        while ((line = stderrReader.readLine()) != null) {
            log.error(line);
        }

        process.waitFor();

        int exitStatus = process.exitValue();
        if (exitStatus != 0) {
            throw new MojoFailureException("command existed with non-zero exit code (" + exitStatus + ")");
        }
    }

}
