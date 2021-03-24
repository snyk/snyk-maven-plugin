package io.snyk.snyk_maven_plugin.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

import static io.snyk.snyk_maven_plugin.download.UpdatePolicy.shouldUpdate;
import static java.lang.String.format;

public class ExecutableDownloader {

    private static final String SNYK_RELEASES_LATEST = "https://static.snyk.io/cli/%s/%s";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File download(URL url, File destination, String updatePolicy) {
        try {
            if (
                destination.exists() &&
                !shouldUpdate(updatePolicy, destination.lastModified(), System.currentTimeMillis())
            ) {
                return destination;
            }
            destination.getParentFile().mkdirs();
            try (
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(destination);
            ) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            destination.setExecutable(true);
            return destination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getDownloadUrl(Platform platform, String version) {
        try {
            return new URL(String.format(SNYK_RELEASES_LATEST, version, platform.snykExecutableFileName));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Download URL is malformed", e);
        }
    }
}
