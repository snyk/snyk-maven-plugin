import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("snyk executable path: " + System.getenv("SNYK_CLI_EXECUTABLE"))) {
    throw new Exception("snyk executable path message not found.");
}

if (!(log =~ /(?:\[INFO\] snyk version:)\s(?:\[INFO\]) \d+\.\d+\.\d+/)) {
    throw new Exception("snyk version output not found");
}

if (!log.contains("io.snyk.it:test-with-args @ 1.0-SNAPSHOT")) {
    throw new Exception("`snyk test --print-deps` --print-deps output not found");
}

return true;
