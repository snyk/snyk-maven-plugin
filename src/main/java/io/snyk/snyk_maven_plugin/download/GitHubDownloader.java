package io.snyk.snyk_maven_plugin.download;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.json.JsonObject;
import jodd.json.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

import static java.lang.String.format;

public class GitHubDownloader {
    private static final String SNYK_RELEASES_LATEST = "https://api.github.com/repos/snyk/snyk/releases/latest";
    private static final String SNYK_RELEASES_DOWNLOAD = "https://github.com/snyk/snyk/releases/download/%s/%s";

    public static void download(Path destination, Platform platform) throws IOException {
        URL url = getDownloadUrlForSnyk(platform);
        File file = destination.toFile();
        try (
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(file);
        ) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        file.setExecutable(true);
    }

    private static URL getDownloadUrlForSnyk(Platform platform) throws MalformedURLException {
        HttpResponse latestReleaseResponse = HttpRequest.get(SNYK_RELEASES_LATEST).acceptJson().send();
        JsonObject json = JsonParser.create().parseAsJsonObject(latestReleaseResponse.bodyText());
        String tagName = json.getString("tag_name");
        return new URL(format(SNYK_RELEASES_DOWNLOAD, tagName, platform.snykExecutableFileName));
    }
}
