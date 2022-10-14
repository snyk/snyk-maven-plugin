import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"))
String snykCliExecutable = System.getenv("SNYK_CLI_EXECUTABLE")

if (snykCliExecutable == null || snykCliExecutable.isEmpty()) {
   throw new Exception("the environment variable `SNYK_CLI_EXECUTABLE` is not defined")
}

if (!log.contains("Snyk Executable Path: " + System.getenv("SNYK_CLI_EXECUTABLE"))) {
    throw new Exception("snyk executable path log line not found.")
}

if (!(log =~ /Snyk CLI Version:\s+\d+\.\d+\.\d+/)) {
    throw new Exception("snyk version log line not found")
}

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("`snyk test` success output not found")
}

return true
