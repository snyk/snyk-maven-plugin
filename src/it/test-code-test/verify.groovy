import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"))

if (!log.contains("[High] SQL Injection")) {
    throw new Exception("no sql injection issue found")
}

return true;
