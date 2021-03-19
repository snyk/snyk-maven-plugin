package io.snyk.snyk_maven_plugin.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static java.lang.String.format;

public class ExecutableDownloader {
    private static final String SNYK_RELEASES_LATEST = "https://static.snyk.io/cli/%s/%s";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void download(File destination, Platform platform, String version) {
        try {
            destination.getParentFile().mkdirs();
            URL url = new URL(format(SNYK_RELEASES_LATEST, version, platform.snykExecutableFileName));
            try (
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(destination);
            ) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            destination.setExecutable(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
