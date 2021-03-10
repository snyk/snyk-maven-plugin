import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("`snyk test` success output not found");
}

if (!log.contains("io.snyk.it:test-without-dependency @ 1.0-SNAPSHOT")) {
    throw new Exception("`snyk test --print-deps` --print-deps output not found");
}

return true;
