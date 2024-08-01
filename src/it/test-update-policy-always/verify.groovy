import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("Expected dummy snyk to be replaced with updated CLI and executed. Log output:\n" + log + "\n");
}

return true;
