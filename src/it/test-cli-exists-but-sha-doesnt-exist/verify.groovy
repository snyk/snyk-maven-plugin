import org.codehaus.plexus.util.FileUtils;
import io.snyk.snyk_maven_plugin.it.ITUtils

String shaOfCliFile = ITUtils.computeShaOfCLIFile(basedir);
String shaFromShaFile = ITUtils.getShaFromShaFile(basedir);

if (!shaOfCliFile.equals(shaFromShaFile)) {
    throw new Exception("sha256 of CLI file does not match the one in the `.sha256` file. Log output:\n" + log + "\n");
}

String log = FileUtils.fileRead(new File(basedir, "build.log"));

if (!log.contains("for known issues, no vulnerable paths found.")) {
    throw new Exception("Expected dummy snyk to be replaced with updated CLI and executed. Log output:\n" + log + "\n");
}

return true;
