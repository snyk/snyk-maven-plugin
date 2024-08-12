package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static io.snyk.snyk_maven_plugin.download.ExecutableDownloader.ensure;
import static org.junit.jupiter.api.Assertions.*;

public class ExecutableDownloaderTest {
    private final byte[] incorrectShasum = "0000000000000000000000000000000000000000000000000000000000000000  snyk-macos".getBytes();
    private final byte[] helloWorldShasum = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9  snyk-macos".getBytes();
    private final byte[] helloWorldBytes = "hello world".getBytes();

    @Test
    public void throwsWhenShaDoesntMatch() throws Exception {
        Path tempDirectory = Files.createTempDirectory(getClass().getSimpleName());
        Path cliPath = tempDirectory.resolve("snyk-macos");
        File cliFile = new File(cliPath.toString());

        FileDownloader mockDownloader = (URL url, File target) -> {
            if (url.toString().equals("https://static.snyk.io/cli/stable/snyk-macos.sha256?utm_source=MAVEN_PLUGIN")) {
                Files.write(target.toPath(), incorrectShasum);
            } else {
                Files.write(target.toPath(), helloWorldBytes);
            }
        };

        RuntimeException e = assertThrows(RuntimeException.class, () -> ensure(
            new URL("https://static.snyk.io/cli/stable/snyk-macos"),
            cliFile,
            "never",
            mockDownloader
        ));

        assertEquals("computed sha256 checksum for CLI download does not expected", e.getMessage());
    }

    @Test
    public void iteratesToNextURLOnDownloadFailure() throws Exception {
        // Mock objects
        Path tempDirectory = Files.createTempDirectory(getClass().getSimpleName());
        Path cliPath = tempDirectory.resolve("snyk-macos");
        File cliFile = new File(cliPath.toString());

        FileDownloader mockDownloader = (URL url, File target) -> {
            if (url.toString().contains("https://downloads.snyk.io/cli/")) {
                throw new IOException("mock download failure");
            }
            if (url.toString().equals("https://static.snyk.io/cli/1.1292.0/snyk-macos.sha256?utm_source=MAVEN_PLUGIN")) {
                Files.write(target.toPath(), helloWorldShasum);
            }
            if (url.toString().equals("https://static.snyk.io/cli/1.1292.0/snyk-macos?utm_source=MAVEN_PLUGIN")) {
                Files.write(target.toPath(), helloWorldBytes);
            }
        };

        // Prepare test data
        List<URL> urls = ExecutableDownloader.getDownloadUrls(Platform.MAC_OS, "1.1292.0");

        // Call the method
        File result = ExecutableDownloader.iterateAndEnsure(urls, cliFile, "never", mockDownloader);

        // Verify that the downloaded file exists (from the second URL)
        assertEquals(cliFile, result);
        assertTrue(result.exists());
    }
}
