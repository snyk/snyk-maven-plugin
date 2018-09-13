package io.snyk.maven.plugins;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by dror on 16/05/2017.
 *
 * Deals with the Snyk policy file
 */
public class SnykPolicy {

    /**
     * search for a Snyk policy file for this project, and return its contents
     * @param project the Maven project object
     * @return the contents of the .snyk file, or null if the file was not found
     */
    public static String readPolicyFile(MavenProject project) {
        String snykFilename = getPolicyFilePath(project);
        if(snykFilename == null) { return null; }
        String sep = System.getProperty("line.separator");

        try (Stream<String> stream = Files.lines(Paths.get(snykFilename))) {
            return stream.map(v -> v + sep).reduce("", String::concat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * recursively get the first Snyk policy file encountered,
     * starting from this project and the parent chain
     * @param project the current Maven project
     * @return the path of the located file, or null if none was found
     */
    public static String getPolicyFilePath(MavenProject project) {
        if(project == null || project.getBasedir() == null) {
            return null;
        }

        Path baseDir = project.getBasedir().toPath();
        Path filePath = baseDir.resolve(Constants.SNYK_FILENAME);
        File f = filePath.toFile();
        if(f.exists() && !f.isDirectory()) {
            return filePath.toString();
        } else {
            return getPolicyFilePath(project.getParent());
        }
    }
}
