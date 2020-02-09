package io.snyk.maven.plugins;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * holds constants, error display functionality and helper functions
 * Created by dror on 05/05/2017.
 */
public class Constants {

    private static final String ERROR_UNAUTHORIZED_MORE_INFO = "See https://snyk.io/docs/using-snyk#authentication for more information.";

    private static final String ERROR_UNAUTHORIZED_ENSURE_API_TOKEN = "Please ensure you have provided your Snyk's API token in the <apiToken></apiToken> plugin configuration option.";

    private static final String ERROR_UNAUTHORIZED_SNYK_PLUGIN = "Unauthorized Snyk plugin.";

    public static final String SNYK_FILENAME = ".snyk";

    public static final String DEFAULT_ENDPOINT = "https://snyk.io/api";

    public static final String ERROR_GENERAL = "There was a problem with the Snyk plugin.";

    public static final String ERROR_RERUN_WITH_DEBUG = "Re-run Maven using the -X switch to enable full debug logging.";

    private Constants() {}

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
    public static void displayAuthError(Log log, boolean failOnAuthError) throws MojoFailureException {
        if (failOnAuthError) {
            throw new MojoFailureException(
                    String.join(" ", ERROR_UNAUTHORIZED_SNYK_PLUGIN, ERROR_UNAUTHORIZED_ENSURE_API_TOKEN, ERROR_UNAUTHORIZED_MORE_INFO));
        }
        log.error(ERROR_UNAUTHORIZED_SNYK_PLUGIN);
        log.error(ERROR_UNAUTHORIZED_ENSURE_API_TOKEN);
        log.error(ERROR_UNAUTHORIZED_MORE_INFO);
    }

}
