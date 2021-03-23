import org.codehaus.plexus.util.FileUtils

import java.nio.file.Paths;
import io.snyk.snyk_maven_plugin.download.Platform;
import io.snyk.snyk_maven_plugin.download.ExecutableDestination;

String log = FileUtils.fileRead(new File(basedir, "build.log"));

String cliExFullPath = ExecutableDestination.getDownloadDestination(
    Platform.current(),
    Optional.ofNullable(System.getProperty("user.home")).map{x -> Paths.get(x)},
    System.getenv()
).getAbsolutePath();

if (!log.contains("snyk executable path: " + cliExFullPath)) {
    throw new Exception("snyk executable path message not found.");
}

if (!(log =~ /(?:\[INFO\] snyk version:)\s(?:\[INFO\]) \d+\.\d+\.\d+/)) {
    throw new Exception("snyk version output not found");
}

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("`snyk test` success output not found");
}

return true;
