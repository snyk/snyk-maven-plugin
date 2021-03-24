import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("introduced by axis:axis@1.4")) {
    throw new Exception("Vulnerability not found.");
}

return true;
