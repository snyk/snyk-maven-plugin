package io.snyk.snyk_maven_plugin.download;

import java.io.IOException;
import java.nio.file.Path;

public interface Downloader {
    void download(Path destination, Platform platform) throws IOException;
}
