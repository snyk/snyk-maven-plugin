package io.snyk.maven.plugins;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.maven.plugin.logging.Log;

/**
 * holds constants, error display functionality and helper functions
 * Created by dror on 05/05/2017.
 */
public class Constants {

    public static final String SNYK_FILENAME = ".snyk";

    public static final String DEFAULT_ENDPOINT = "https://snyk.io/api";

    public static final String ERROR_GENERAL = "There was a problem with the Snyk plugin.";

    public static final String ERROR_RERUN_WITH_DEBUG =
        "Re-run Maven using the -X switch to enable full debug logging.";

    /**
     * parses the protocol://host:port from the defined endpoint
     * @param endpoint the defined endpoint
     * @return the protocol://host:port part of the endpoint URL
     */
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

    /**
     * display a generic authentication error message to the build log
     * @param log the build log
     */
    public static void displayAuthError(Log log) {
        log.error("Unauthorized Snyk plugin.");
        log.error("Please ensure you have provided your Snyk's API token " +
                "in the <apiToken></apiToken> plugin configuration option.");
        log.error("See https://snyk.io/docs/using-snyk#authentication " +
                "for more information.");
    }

}
