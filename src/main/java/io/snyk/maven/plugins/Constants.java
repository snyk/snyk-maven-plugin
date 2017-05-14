package io.snyk.maven.plugins;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;

/**
 * Created by dror on 05/05/2017.
 */
public class Constants {

    public static String parseEndpoint(String endpoint) {
        if (endpoint == null) {
            return "";
        }
        try {
            URL url = new URL(endpoint);
            return url.getProtocol() + "://" + url.getAuthority();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static final String DEFAULT_ENDPOINT = "https://snyk.io/api";

    public static final String ERROR_GENERAL = "There was a problem with the Snyk plugin.";

    public static void displayAuthError(Log log) {
        log.error("Unauthorized Snyk plugin.");
        log.error("Please ensure you have provided your Snyk's API token " +
                "in the <apiToken></apiToken> plugin configuration option.");
        log.error("See https://snyk.io/docs/using-snyk#authentication " +
                "for more information.");
    }

}
