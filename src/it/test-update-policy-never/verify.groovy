import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("command execution failed")) {
    throw new Exception("Expected dummy snyk to not be replaced. Log output:\n" + log + "\n");
}

return true;
