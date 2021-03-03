package io.snyk.maven.plugins;

import io.snyk.maven.plugins.cli.CliDownloader;
import io.snyk.maven.plugins.cli.Runner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dror on 15/01/2017.
 *
 * Tests the current project's dependencies for vulnerabilities,
 * and lists all those vulnerabilities in the build log.
 * Can fail the build on a condition of minimum vulnerability severity.
 */
@Mojo( name = "test")
public class SnykTest extends AbstractMojo {

    @Parameter( defaultValue = "${settings}", readonly = true, required = true )
    private Settings settings;

    // specific snyk plugin configurations
    @Parameter
    private String apiToken = "";

    @Parameter
    private String org = "";

    @Parameter
    private String failOnSeverity = "low";

    @Parameter
    private String endpoint = Constants.DEFAULT_ENDPOINT;

    @Parameter
    private boolean includeProvidedDependencies = true;

    @Parameter
    private boolean failOnAuthError = false;

    @Parameter(property = "snyk.skip")
    private boolean skip;

    private String baseUrl = "";

    private static int SEVERITY_LOW     = 100;
    private static int SEVERITY_MEDIUM  = 200;
    private static int SEVERITY_HIGH    = 300;
    private static final Map<String, Integer> severityMap = new HashMap<String, Integer>();
    static {
        severityMap.put("low",      SEVERITY_LOW);
        severityMap.put("medium",   SEVERITY_MEDIUM);
        severityMap.put("high",     SEVERITY_HIGH);
    }
    private Log log = getLog();
    private CliDownloader cliDownloader = new CliDownloader(getLog());

    /**
     * executes this Mojo
     * @throws MojoFailureException if the build had to be stopped (a normal behavior)
     */
    public void execute() throws MojoFailureException {
        checkCLI();
        authentication();
        Runner.Result result = Runner.runSnyk("test");
        log.info(result.getOutput());

        if (result.getExitcode() > 0 && severityMap.containsKey(failOnSeverity)) {
            throw new MojoFailureException("Snyk Test failed");
        }
    }

    private void checkCLI() throws MojoFailureException {
        try {
            cliDownloader.downloadOrUpdateCli();
        } catch (IOException ioe) {
            log.error(ioe);
            throw new MojoFailureException(ioe.getMessage());
        }
    }

    /*
    - token can be env variable
    - cli can already been authenticated (either in earlier run, or manually)
    - cli will be authenticated using the token declared in the pom.xml
     */
    private void authentication() throws MojoFailureException{
        log.debug("check auth");
        String envToken = System.getenv("SNYK_TOKEN");
        if (envToken != null && !envToken.isEmpty()) {
            log.debug("api token found in env");
            return;
        }

        String apiConfigToken = Runner.runSnyk("config get api").getOutput().trim();
        if (apiConfigToken != null && !apiConfigToken.isEmpty()) {
            log.debug("api token found");
            return;
        }

        if (apiToken == null || apiToken.isEmpty()) {
            throw new MojoFailureException("No API key found");
        }

        Runner.Result authResult = Runner.runSnyk("auth "+ apiToken);
        if (authResult.failed()) {
            throw new MojoFailureException("snyk auth went wrong: " + authResult.getOutput());
        }
    }
}
