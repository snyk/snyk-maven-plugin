package io.snyk.snyk_maven_plugin.download;

import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.snyk.snyk_maven_plugin.download.UpdatePolicy.shouldUpdate;

public class ExecutableDownloader {

    private static final String SNYK_CLI_DOWNLOAD_PRIMARY = "https://downloads.snyk.io/cli/%s/%s";
    private static final String SNYK_CLI_DOWNLOAD_SECONDARY = "https://static.snyk.io/cli/%s/%s";
    private static final List<String> SNYK_CLI_DOWNLOAD_URLS = Arrays.asList(SNYK_CLI_DOWNLOAD_PRIMARY, SNYK_CLI_DOWNLOAD_SECONDARY);

    /**
     * Ensure that the CLI is downloaded and that it matches the corresponding `.sha256` file.
     * Check that the CLI exists, that the sha 256 file exists, that they match, and that we don't need to do an update (per the update policy).
     * If any of those conditions are not true, then we delete both the CLI file and the sha256 file and (re-)download them.
     *
     * @param cliDownloadURL the URL to download the CLI from.
     * @param cliFile        the File representing where the CLI should either be downloaded to, or the location in which it should be verified.
     * @param updatePolicy   describes how/when to do CLI version updates.
     * @param downloader     the FileDownloader instance to be used for downloading.
     * @return
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File ensure(URL cliDownloadURL, File cliFile, String updatePolicy, FileDownloader downloader) {
        try {
            File checksumFile = new File(cliFile.getPath() + ".sha256");

            if (cliFile.exists() && checksumFile.exists()) {
                if (verifyChecksum(cliFile, checksumFile)) {
                    if (!shouldUpdate(
                            updatePolicy,
                            cliFile.lastModified(),
                            System.currentTimeMillis()
                    )) {
                        return cliFile;
                    }
                }
            }

            cliFile.delete();
            checksumFile.delete();

            downloader.download(cliDownloadURL, cliFile);

            URL checksumUrl = new URL(cliDownloadURL.toString() + ".sha256");
            downloader.download(checksumUrl, checksumFile);

            if (!verifyChecksum(cliFile, checksumFile)) {
                throw new RuntimeException("computed sha256 checksum for CLI download does not expected");
            }

            cliFile.setExecutable(true);
            return cliFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Iterate a list of download URLs and ensures the CLI is downloaded and that it matches the corresponding `.sha256` file.
     * Check that the CLI exists, that the sha 256 file exists, that they match, and that we don't need to do an update (per the update policy).
     * If any of those conditions are not true, then we delete both the CLI file and the sha256 file and (re-)download them.
     * If a download URL fails, try the next URL in the list
     *
     * @param cliDownloadURLs the list of URLs to try and download the CLI from.
     * @param cliFile         the File representing where the CLI should either be downloaded to, or the location in which it should be verified.
     * @param updatePolicy    describes how/when to do CLI version updates.
     * @param downloader      the FileDownloader instance to be used for downloading.
     * @return
     */
    public static File iterateAndEnsure(List<URL> cliDownloadURLs, File cliFile, String updatePolicy, FileDownloader downloader) {
        for (URL cliDownloadURL : cliDownloadURLs) {
            try {
                File downloadedFile = ensure(cliDownloadURL, cliFile, updatePolicy, downloader);

                if (downloadedFile.exists()) {
                    return downloadedFile;
                }
            } catch (RuntimeException e) {
                System.err.println("Failed to download from '" + cliDownloadURL + "': " + e.getMessage());
            }
        }
        throw new RuntimeException("Failed to download from URLs");
    }

    static String downloadToString(URL url) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String all = br.lines().collect(Collectors.joining("\n"));
        return all;
    }

    public static String getLatestVersion() throws IOException {
        String version = "v" + downloadToString(new URL("https://downloads.snyk.io/cli/stable/version")).trim();
        return version;
    }

    public static List<URL> getDownloadUrls(Platform platform, String version) {
        List<URL> downloadUrls = new ArrayList<>();
        
        for (String url : SNYK_CLI_DOWNLOAD_URLS) {
            downloadUrls.add(getDownloadUrl(url, platform, version));
        }
        
        return downloadUrls;
    }

    public static URL getDownloadUrl(String url, Platform platform, String version) {
        try {
            if (version.equals("stable")) {
                String actualLatestVersion = getLatestVersion();
                return new URL(String.format(url, actualLatestVersion, platform.snykExecutableFileName));
            }
            return new URL(String.format(url, version, platform.snykExecutableFileName));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Download URL is malformed", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed resolving 'latest' version", e);
        }
    }

    /**
     * Extract the SHA256 value from the given `.sha256` file.
     *
     * @param checksumFile the File which should contain.
     * @return the SHA256 value from the file.
     * @throws IOException
     * @throws RuntimeException
     */
    public static String extractChecksumValue(File checksumFile) throws IOException, RuntimeException {
        List<String> lines = Files.readAllLines(checksumFile.toPath());
        if (lines.size() == 1) {
            // SHA256 are 64 characters long so grab the first 64 characters of the line.
            return lines.get(0).substring(0, 64);
        } else {
            throw new RuntimeException("Invalid number of lines in a checksum file");
        }
    }

    public static String computeChecksum(File targetFile) throws IOException {
        byte[] bytes = Files.readAllBytes(targetFile.toPath());
        return DigestUtils.sha256Hex(bytes);
    }

    /**
     * Verify that the SHA256 of the given file matches the expected value.
     *
     * @param targetFile   the file who's SHA256 is to be validated.
     * @param checksumFile the file containing the SHA256 checksum to use for verification.
     * @return true if the SHA256 of targetDownload is equal to expectedChecksum; false if not.
     * @throws IOException
     */
    public static boolean verifyChecksum(File targetFile, File checksumFile) throws IOException {
        String expectedChecksum = extractChecksumValue(checksumFile);
        String computed = computeChecksum(targetFile);
        return computed.equals(expectedChecksum);
    }

}
