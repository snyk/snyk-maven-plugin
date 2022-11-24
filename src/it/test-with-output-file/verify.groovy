import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"))

if (!log.contains("introduced by axis:axis@1.4")) {
    throw new Exception("no vulnerable paths found")
}

String capturedOutput = FileUtils.fileRead(new File(new File(basedir, "target"), "snyk-test.txt"))

if (!capturedOutput.contains("Package manager:   maven\nTarget file:       pom.xml")) {
    throw new Exception("expected captured output not present");
}

return true
