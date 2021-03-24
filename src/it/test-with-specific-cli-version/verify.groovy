import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!(log =~ /\[INFO\] Snyk CLI version:\R\[INFO\] 1\.487\.0/)) {
    throw new Exception("snyk version output not found");
}

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("`snyk test` success output not found");
}

return true;
