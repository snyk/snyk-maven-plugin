import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("test-multi-module-child ............................ SUCCESS")) {
    throw new Exception("parent should have succeeded. Log output:\n" + log + "\n");
}

if (!log.contains("child-module-1 ..................................... SUCCESS")) {
    throw new Exception("child-module-1 should have succeeded. Log output:\n" + log + "\n");
}

if (!log.contains("child-module-2 ..................................... FAILURE")) {
    throw new Exception("child-module-2 should have failed. Log output:\n" + log + "\n");
}

if (!log.contains("introduced by axis:axis@1.4")) {
    throw new Exception("Could not find expected vulnerability in child-module-2. Log output:\n" + log + "\n");
}

return true;
