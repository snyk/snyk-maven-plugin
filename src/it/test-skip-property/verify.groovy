import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("snyk test skipped")) {
    throw new Exception("skip message not found. Log output:\n" + log + "\n");
}

return true;
