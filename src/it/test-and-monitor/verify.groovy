import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("snyk test skipped")) {
    throw new Exception("skip message not found");
}

if (!log.contains("Explore this snapshot at")) {
    throw new Exception("Snapshot link not found.");
}

return true;
