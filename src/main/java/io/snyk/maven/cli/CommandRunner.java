package io.snyk.maven.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.maven.plugin.logging.Log;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

public class CommandRunner {

  private final Log log;

  private CommandRunner(Log log) {
    this.log = log;
  }

  public static CommandRunner newInstance(Log log) {
    return new CommandRunner(log);
  }

  public void execute(List<String> commands) throws InterruptedException, TimeoutException, IOException {
    ProcessResult result = new ProcessExecutor().command(commands)
                                                .readOutput(true)
                                                .destroyOnExit()
                                                .execute();

    BufferedReader reader = new BufferedReader(new StringReader(result.outputString()));
    String line;
    while ((line = reader.readLine()) != null) {
      log.info(line);
    }
  }
}
