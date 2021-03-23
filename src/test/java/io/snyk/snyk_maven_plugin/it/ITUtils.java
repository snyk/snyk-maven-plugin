package io.snyk.snyk_maven_plugin.it;

import java.io.File;
import java.util.Optional;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
public class ITUtils {

    public static File createDummyAtDownloadDestination(File basedir, long ageInDays) throws Exception {
        long ageInMs = ageInDays * 24 * 60 * 60 * 1000;
        File file = getDownloadDestination(basedir);
        file.getParentFile().mkdirs();
        file.createNewFile();
        file.setLastModified(System.currentTimeMillis() - ageInMs);
        System.out.println("Created dummy file: " + file.getAbsolutePath());
        return file;
    }

    private static File getDownloadDestination(File basedir) {
        String downloadDestination = Optional.ofNullable(System.getenv("SNYK_DOWNLOAD_DESTINATION"))
            .orElseThrow(() -> new RuntimeException("Need environment variable. (SNYK_DOWNLOAD_DESTINATION)"));
        return new File(basedir, downloadDestination);
    }

}
