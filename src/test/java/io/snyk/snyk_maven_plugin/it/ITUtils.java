package io.snyk.snyk_maven_plugin.it;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
public class ITUtils {

    public static File createDummyAtDownloadDestination(File basedir, long ageInDays) throws Exception {
        long ageInMs = ageInDays * 24 * 60 * 60 * 1000;
        File file = getDownloadDestination(basedir);
        file.getParentFile().mkdirs();
        file.createNewFile();
        file.setLastModified(System.currentTimeMillis() - ageInMs);
        System.out.println("Created dummy file: " + file.getAbsolutePath());
        createSha256For(file);
        return file;
    }

    public static void createSha256For(File targetFile) throws Exception {
        String shaFilename = targetFile.getPath() + ".sha256";
        byte[] bytes = Files.readAllBytes(targetFile.toPath());
        String sha256hex = DigestUtils.sha256Hex(bytes);

        File shaFile = new File(shaFilename);
        Files.write(shaFile.toPath(), String.format("%s  %s", sha256hex, targetFile.getName()).getBytes());
    }

    public static void createInvalidSha256For(File basedir) throws Exception {
        File cliFile = getDownloadDestination(basedir);
        String shaFilename = cliFile.getPath() + ".sha256";
        String badSha = "0000000000000000000000000000000000000000000000000000000000000000";
        File shaFile = new File(shaFilename);
        Files.write(shaFile.toPath(), String.format("%s  %s", badSha, cliFile.getName()).getBytes());
    }

    public static void deleteSha256For(File basedir) {
        File cliFile = getDownloadDestination(basedir);
        String shaFilename = cliFile.getPath() + ".sha256";
        File shaFile = new File(shaFilename);
        shaFile.delete();
    }

    private static File getDownloadDestination(File basedir) {
        String downloadDestination = Optional.ofNullable(System.getenv("SNYK_DOWNLOAD_DESTINATION"))
            .orElseThrow(() -> new RuntimeException("Need environment variable. (SNYK_DOWNLOAD_DESTINATION)"));
        return new File(basedir, downloadDestination);
    }

    public static String getShaFromShaFile(File basedir) throws Exception {
        File cliFile = getDownloadDestination(basedir);
        String shaFilename = cliFile.getPath() + ".sha256";
        File shaFile = new File(shaFilename);

        List<String> lines = Files.readAllLines(shaFile.toPath());
        if (lines.size() == 1) {
            // SHA256 are 64 characters long so grab the first 64 characters of the line.
            return lines.get(0).substring(0, 64);
        } else {
            throw new RuntimeException("Invalid number of lines in a checksum file");
        }
    }

    public static String computeShaOfCLIFile(File basedir) throws Exception {
        File cliFile = getDownloadDestination(basedir);
        byte[] bytes = Files.readAllBytes(cliFile.toPath());
        return DigestUtils.sha256Hex(bytes);
    }

}
