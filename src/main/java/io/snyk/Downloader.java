package io.snyk;

import java.io.IOException;
import java.nio.file.Path;

public interface Downloader {
    void download(Path destination, Platform platform) throws IOException;
}
