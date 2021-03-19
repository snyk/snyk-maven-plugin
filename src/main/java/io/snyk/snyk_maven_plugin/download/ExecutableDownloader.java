package io.snyk.snyk_maven_plugin.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

import static java.lang.String.format;

public class ExecutableDownloader {
    private static final String SNYK_RELEASES_LATEST = "https://static.snyk.io/cli/%s/%s";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File download(Path destination, Platform platform, String version) {
        try {
            URL url = new URL(format(SNYK_RELEASES_LATEST, version, platform.snykExecutableFileName));
            destination.toFile().mkdirs();
            File file = destination.resolve(platform.snykExecutableFileName).toFile();
            try (
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(file);
            ) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            file.setExecutable(true);
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
