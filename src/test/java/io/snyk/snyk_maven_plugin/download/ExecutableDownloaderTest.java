package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExecutableDownloaderTest {

    @Test
    public void throwsWhenShaDoesntMatch() throws Exception {
        Path tempDirectory = Files.createTempDirectory(getClass().getSimpleName());
        Path cliPath = tempDirectory.resolve("snyk-macos");
        File cliFile = new File(cliPath.toString());

        FileDownloader mockDownloader = (URL url, File target) -> {
            if (url.toString().equals("https://static.snyk.io/cli/stable/snyk-macos.sha256")) {
                Files.write(
                    target.toPath(),
                    "0000000000000000000000000000000000000000000000000000000000000000  snyk-macos".getBytes()
                );
            } else {
                FileDownloader.downloadFile(url, target);
            }
        };

        RuntimeException e = assertThrows(RuntimeException.class, () -> ExecutableDownloader.ensure(
            new URL("https://static.snyk.io/cli/stable/snyk-macos"),
            cliFile,
            "never",
            mockDownloader
        ));

        assertEquals(e.getMessage(), "computed sha256 checksum for CLI download does not expected");
    }

    @Test
    public void iteratesToNextURLOnDownloadFailure() throws Exception {
        // Mock objects
        FileDownloader downloader = Mockito.mock(FileDownloader.class);
        File cliFile = new File("test.cli");

        // Prepare test data
        URL url1 = new URL("https://fail.download/cli1");
        URL url2 = new URL("https://successful.download/cli2");
        List<URL> urls = Arrays.asList(url1, url2);

        // Mock download failure for the first URL
        doThrow(new IOException("mock download failure")).when(downloader).download(url1, cliFile);

        // Mock successful download for the second URL
        doNothing().when(downloader).download(url2, cliFile);

        // Call the method
        File result = ExecutableDownloader.iterateAndEnsure(urls, cliFile, "never", downloader);

        // Verify that the downloaded file exists (from the second URL)
        assertEquals(cliFile, result);
        assertTrue(result.exists());
    }
}
