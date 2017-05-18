package io.snyk.maven.plugins;

import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
        if(snykFilename == null) {
            return null;
        }

        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(snykFilename);
            br = new BufferedReader(fr);
            String sCurrentLine;
            br = new BufferedReader(new FileReader(snykFilename));
            String contents = "";
            while ((sCurrentLine = br.readLine()) != null) {
                contents += sCurrentLine + System.getProperty("line.separator");
            }
            return contents;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
        if(project == null) {
            return null;
        }
        String filename = project.getBasedir().toString() + File.separator + Constants.SNYK_FILENAME;
        File f = new File(filename);
        if(f.exists() && !f.isDirectory()) {
            return filename;
        }
        return getPolicyFilePath(project.getParent());
    }
}
