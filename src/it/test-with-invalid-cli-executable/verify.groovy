import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("snyk executable path: /usr/local/bin/snyk-does-not-exist")) {
    throw new Exception("snyk executable path message not found.");
}

if (!log.contains("command execution failed")) {
    throw new Exception("`snyk test` failure output not found");
}

return true;

