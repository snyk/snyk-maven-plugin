import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("no vulnerable paths found")) {
    throw new Exception("Expected vulnerabilities to be ignored.");
}

return true;
