import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("`snyk test` success output not found. Log output:\n" + log + "\n");
}

return true;
