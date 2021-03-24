import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!(log =~ /Snyk CLI Version:\s+1\.487\.0/)) {
    throw new Exception("snyk version log line not found");
}

return true;
