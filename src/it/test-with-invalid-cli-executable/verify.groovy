import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("command execution failed")) {
    throw new Exception("`snyk test` failure output not found");
}

return true;

