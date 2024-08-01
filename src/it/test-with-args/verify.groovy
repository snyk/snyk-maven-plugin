import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("io.snyk.it:test-with-args @ 1.0-SNAPSHOT")) {
    throw new Exception("`snyk test --print-deps` --print-deps output not found. Log output:\n" + log + "\n");
}

return true;
