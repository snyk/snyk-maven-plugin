import org.codehaus.plexus.util.FileUtils;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("Target file:       pom.xml")) {
    throw new Exception("parent was not scanned.");
}

if (!log.contains("Target file:       child-module-1")) {
    throw new Exception("child-module-1 was not scanned.");
}

if (!log.contains("Target file:       child-module-2")) {
    throw new Exception("child-module-2 was not scanned.");
}

if (!log.contains("test-multi-module-parent ........................... FAILURE")) {
    throw new Exception("parent should have failed with vulnerabilities, including from children.");
}

if (!log.contains("child-module-1 ..................................... SKIPPED")) {
    throw new Exception("child-module-1 should have been skipped.");
}

if (!log.contains("child-module-2 ..................................... SKIPPED")) {
    throw new Exception("child-module-2 should have been skipped.");
}

if (!log.contains("introduced by axis:axis@1.4")) {
    throw new Exception("Could not find vulnerability in parent.");
}

return true;
