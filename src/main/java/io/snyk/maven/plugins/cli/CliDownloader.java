package io.snyk.maven.plugins.cli;


import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.logging.Log;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CliDownloader {

    private static final String LATEST_RELEASES_URL = "https://api.github.com/repos/snyk/snyk/releases/latest";
    private static final String LATEST_RELEASE_DOWNLOAD_URL = "https://github.com/snyk/snyk/releases/download/%s/%s";
    private final Log log;

    public CliDownloader(Log logger) {
        this.log = logger;
    }

    public String getLatestVersion() throws IOException {
        URL url = new URL(LATEST_RELEASES_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();

        String result = new BufferedReader(new InputStreamReader(responseStream))
                .lines().collect(Collectors.joining("\n"));

        JSONObject json = new JSONObject(result);
        return json.getString("name");
    }

    private String snykWrapperFileName() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "snyk-win.exe";
        } else if (SystemUtils.IS_OS_MAC) {
            return "snyk-macos";
        } else if (SystemUtils.IS_OS_LINUX) {
            return "snyk-linux";
        } else {
            throw new IllegalArgumentException("Unsupported OS: " + System.getProperty("os.name"));
        }
    }

    public String downloadLatestVersion() throws IOException{
            String latestVersion = getLatestVersion();
            String filename = snykWrapperFileName();
            String downloadURL = String.format(LATEST_RELEASE_DOWNLOAD_URL, latestVersion, filename);
            log.info("Download version " + latestVersion + " of " + filename);
            if (SystemUtils.IS_OS_WINDOWS) {
                download(downloadURL, "snyk.exe");
            } else {
                download(downloadURL, "snyk");
            }
            return latestVersion;
    }

    private void download(String url, String fileName) throws IOException {
        log.info("Downloading: " + url);
        URL website = new URL(url);
        try(ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            log.info("Downloading finished");
            setFilePermissions(fileName);
        }
    }

    private void setFilePermissions(String fileName) throws IOException {
        log.debug("Setting file permissions");

        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        Files.setPosixFilePermissions((new File(fileName)).toPath(), perms);
    }

    public void downloadOrUpdateCli() throws IOException{
        Runner.Result versionResult = Runner.runCommand("-version");
        if (versionResult.failed()) {
            downloadLatestVersion();
        } else {
            String version = versionResult.getOutput();
            String latestVersion = getLatestVersion();
            String[] versionSplit = version.split(" ");
            Version current = Version.of(versionSplit[0]);
            Version latest = Version.of(latestVersion.substring(1,latestVersion.length()));
            if (latest.isGreaterThan(current)) {
                log.info("Auto update snyk binary: "+current+" -> "+latest);
                downloadLatestVersion();
            } else {
                log.info("CLI up to date, version: "+version);
            }
        }
    }
}
