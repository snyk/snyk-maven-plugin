package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExecutableDownloaderTest {

    @Test
    public void throwsWhenShaDoesntMatch() throws Exception {
        Path tempDirectory = Files.createTempDirectory(getClass().getSimpleName());
        Path cliPath = tempDirectory.resolve("snyk-macos");
        File cliFile = new File(cliPath.toString());

        FileDownloader mockDownloader = (URL url, File target) -> {
            if (url.toString().equals("https://static.snyk.io/cli/latest/snyk-macos.sha256")) {
                Files.write(
                    target.toPath(),
                    "0000000000000000000000000000000000000000000000000000000000000000  snyk-macos".getBytes()
                );
            } else {
                FileDownloader.downloadFile(url, target);
            }
        };

        RuntimeException e = assertThrows(RuntimeException.class, () -> ExecutableDownloader.ensure(
            new URL("https://static.snyk.io/cli/latest/snyk-macos"),
            cliFile,
            "never",
            mockDownloader
        ));

        assertEquals(e.getMessage(), "computed sha256 checksum for CLI download does not expected");
    }
}
