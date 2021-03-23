import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("snyk executable path: " + System.getenv("SNYK_CLI_EXECUTABLE"))) {
    throw new Exception("snyk executable path message not found.");
}

if (!(log =~ /(?:\[INFO\] snyk version:)\s(?:\[INFO\]) \d+\.\d+\.\d+/)) {
    throw new Exception("snyk version output not found");
}

if (!log.contains("test-multi-module-child ............................ SUCCESS")) {
    throw new Exception("parent should have succeeded.");
}

if (!log.contains("child-module-1 ..................................... SUCCESS")) {
    throw new Exception("child-module-1 should have succeeded.");
}

if (!log.contains("child-module-2 ..................................... FAILURE")) {
    throw new Exception("child-module-2 should have failed.");
}

if (!log.contains("introduced by io.snyk.it:child-module-2@1.0-SNAPSHOT > axis:axis@1.4")) {
    throw new Exception("Could not find vulnerability in child-module-2.");
}

return true;
