import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"))

if (!log.contains("SQL Injection")) {
    throw new Exception("no sql injection issue found. Log output:\n" + log + "\n")
}

return true;
