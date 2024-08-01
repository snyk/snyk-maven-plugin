import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"))

if (!log.contains("introduced by org.postgresql:postgresql@42.3.5")) {
    throw new Exception("Vulnerability in dependency not found. Log output:\n" + log + "\n")
}

return true;
