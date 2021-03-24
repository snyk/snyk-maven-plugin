import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

String downloadDestination = System.getenv("SNYK_DOWNLOAD_DESTINATION");

if (!(log =~ /\[INFO\] snyk version:\R\[INFO\] 1\.487\.0/)) {
    throw new Exception("snyk version output not found");
}

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("`snyk test` success output not found");
}

return true;
