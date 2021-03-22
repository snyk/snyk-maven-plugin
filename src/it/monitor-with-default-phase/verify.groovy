import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("Explore this snapshot at")) {
    throw new Exception("Snapshot link not found.");
}

return true;
