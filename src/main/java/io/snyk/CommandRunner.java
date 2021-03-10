package io.snyk;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandRunner {

    public static void run(CommandLine commandLine, Log log) throws MojoFailureException, MojoExecutionException {
        try {
            Process process = commandLine.start();
            logStream(process.getInputStream(), log::info); // process stdout goes to plugin input
            logStream(process.getErrorStream(), log::error);
            process.waitFor();

            int exitStatus = process.exitValue();
            if (exitStatus != 0) {
                throw new MojoFailureException("command existed with non-zero exit code (" + exitStatus + ")");
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("command execution failed", e);
        }
    }

    private static void logStream(InputStream stream, LineLogger logger) {
        new BufferedReader(new InputStreamReader(stream)).lines().forEach(logger::log);
    }

    public interface LineLogger {
        void log(String line);
    }

}
