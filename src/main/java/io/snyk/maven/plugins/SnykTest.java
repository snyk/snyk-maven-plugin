package io.snyk.maven.plugins;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    // The entry point to Aether, the component that's doing all the work
    @Component
    private RepositorySystem repoSystem;

    // The current repository/network configuration of Maven.
    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private List<RemoteRepository> remoteProjectRepositories;

    @Parameter(defaultValue = "${project.remotePluginRepositories}", readonly = true)
    private List<RemoteRepository> remotePluginRepositories;

    @Parameter( defaultValue = "${settings}", readonly = true, required = true )
    private Settings settings;

    // specific snyk plugin configurations
    @Parameter
    private String apiToken = "";

    @Parameter
    private String org = "";

    @Parameter(property = "snyk.remoteRepoUrl")
    private String remoteRepoUrl = "";

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

    /**
     * executes this Mojo
     * @throws MojoFailureException if the build had to be stopped (a normal behavior)
     */
    public void execute() throws MojoFailureException {
        try {
            executeInternal();
        } catch(MojoFailureException e) {
            throw e;
        } catch(Throwable t) {
            if (getLog().isDebugEnabled()) {
                getLog().error(Constants.ERROR_GENERAL, t);
            } else {
                getLog().error(Constants.ERROR_GENERAL);
                getLog().error(Constants.ERROR_RERUN_WITH_DEBUG);
            }
        }
    }

    /**
     * main engine for this Mojo
     * @throws MojoFailureException
     * @throws IOException
     */
    private void executeInternal() throws MojoFailureException, IOException {
        if (skip) {
            getLog().info("Security tests are skipped");
            return;
        }

        if(!validateParameters()) {
            return;
        }

        for (RemoteRepository remoteProjectRepository : remoteProjectRepositories) {
            getLog().debug("Remote project repository: " + remoteProjectRepository);
        }
        for (RemoteRepository remotePluginRepository : remotePluginRepositories) {
            getLog().debug("Remote plugin repository: " + remotePluginRepository);
        }
        List<RemoteRepository> remoteRepositories = new ArrayList<>(remoteProjectRepositories);
        remoteRepositories.addAll(remotePluginRepositories);

        JSONObject projectTree = new ProjectTraversal(
                project, repoSystem, repoSession, remoteRepositories, includeProvidedDependencies).getTree();

        if (!remoteRepoUrl.equals("")) {
            projectTree.replace("name", remoteRepoUrl);
        }

        HttpResponse response = sendDataToSnyk(projectTree);
        parseResponse(response);
    }

    /**
     * validate the plugin's parameters
     * @return false if validation didn't pass
     */
    private boolean validateParameters() throws MojoFailureException {
        boolean validated = true;
        if(apiToken.isEmpty()) {
            Constants.displayAuthError(getLog(), failOnAuthError);
            validated = false;
        }
        baseUrl = Constants.parseEndpoint(endpoint);

        return validated;
    }

    /**
     * send the data to api/vuln/maven in the Snyk backend,
     * which returns
     * @param projectTree the dependencies tree as collected by ProjectTraversal
     * @return the HTTP response object
     * @throws IOException
     * @throws ParseException
     */
    private HttpResponse sendDataToSnyk(JSONObject projectTree) throws IOException {
        String vulnMavenEndpoint = "/api/vuln/maven/?applyPolicy=true";
        if (org != null && !org.isEmpty()) {
            vulnMavenEndpoint += "&org=" + org;
        }
        HttpPost request = new HttpPost(baseUrl + vulnMavenEndpoint);
        request.addHeader("authorization", "token " + apiToken);
        request.addHeader("x-is-ci", "false"); // TODO: how do we know this ??
        request.addHeader("content-type", "application/json");
        String snykPolicy = SnykPolicy.readPolicyFile(project);
        if(snykPolicy != null) {
            projectTree.put("policy", snykPolicy);
        }
        HttpEntity entity = new StringEntity(projectTree.toString());
        request.setEntity(entity);

        if (getLog().isDebugEnabled()) {
            getLog().debug("Snyk http request payload: " + projectTree);
        }

        HttpClientHelper httpClientHelper = new HttpClientHelper(getLog(), settings);
        HttpClient client = httpClientHelper.buildHttpClient();
        return client.execute(request);
    }

    /**
     * parse Snyk's response and present it in the build log
     * @param response the HTTP response from the call to Snyk
     * @throws MojoFailureException
     */
    private void parseResponse(HttpResponse response) throws MojoFailureException {
        if(response.getStatusLine().getStatusCode() >= 400) {
            processError(response);
            return;
        }

        JSONObject responseJson = parseResponseBody(response);
        if(responseJson.get("ok") != null && (Boolean)responseJson.get("ok")) {
            getLog().info("✓ Tested " + responseJson.get("dependencyCount") +
                    " dependencies for known vulnerabilities, no vulnerable paths found.");
            return;
        }
        if(responseJson.get("vulnerabilities") != null) {
            processVulns(responseJson);
        } else {
            getLog().info((String)responseJson.get("summary"));
        }
    }

    /**
     * process the error from an HTTP response object,
     * and log it in the build log
     * @param response an HTTP response object
     */
    private void processError(HttpResponse response) throws MojoFailureException {
        // process an error in the response object
        if(response.getStatusLine().toString().contains("401")) {
            Constants.displayAuthError(getLog(), failOnAuthError);
        } else {
            getLog().error("Bad response from Snyk: " +
                response.getStatusLine().toString());
            if (getLog().isDebugEnabled()) {
                getLog().debug("Snyk http response: " + parseResponseBody(response));
            }
        }
    }

    /**
     * parse the response body into a JSON object
     * @param response an HTTP response object
     * @return a JSON object containing the response
     */
    private JSONObject parseResponseBody(HttpResponse response) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(
                new BufferedReader(
                    new InputStreamReader(
                        response.getEntity().getContent())));
            return jsonObject;
        } catch (IOException|ParseException e) {
            return null;
        }
    }

    /**
     * process and print vulns from a response body to the build log;
     * optionally stop the build if a certain vuln severity level was reached
     * @param responseJson a JSON object containing the parsed response object
     * @throws MojoFailureException if the build should be stopped due to a minimum severity level
     */
    private void processVulns(JSONObject responseJson) throws MojoFailureException {
        HashSet<String> vulnIdSet = new HashSet<String>();

        JSONArray vulns = (JSONArray)responseJson.get("vulnerabilities");
        int highestSeverity = SEVERITY_LOW;

        Iterator<JSONObject> iterator = vulns.iterator();
        while (iterator.hasNext()) {
            JSONObject vuln = iterator.next();
            vulnIdSet.add((String)vuln.get("id"));
            Integer severityInt = severityMap.get(vuln.get("severity"));
            if(severityInt != null && severityInt > highestSeverity) {
                highestSeverity = severityInt;
            }
            printVuln(vuln);
        }

        getLog().warn("Tested " + responseJson.get("dependencyCount") +
                " dependencies for known vulnerabilities, " +
                "found " + vulnIdSet.size() + " vulnerabilities, " +
                vulns.size() + " vulnerable paths.");

        if(severityMap.containsKey(failOnSeverity) &&
                highestSeverity >= severityMap.get(failOnSeverity)) {
            String maxSeverity = failOnSeverity.toLowerCase();
            String msg = "Found vulnerabilities with severity " + maxSeverity;
            if(!maxSeverity.equals("high")) {
                msg +=  " or higher";
            }
            msg += ".";
            throw new MojoFailureException(msg);
        }
    }

    /**
     * print a single vuln to the build log
     * @param vuln a JSON object containing a single vuln
     */
    private void printVuln(JSONObject vuln) {
        getLog().warn("✗ " + vuln.get("severity") + " severity vulnerability found on " +
                vuln.get("packageName") + "@" +
                vuln.get("version"));
        getLog().warn("- desc: " + vuln.get("title"));
        getLog().warn("- info: " + baseUrl + "/vuln/" + vuln.get("id"));
        if(vuln.get("from") != null) {
            JSONArray fromArr = (JSONArray)vuln.get("from");
            getLog().warn("- from: " + printJSONArray(fromArr, " > "));
        }

        getLog().warn("- fix: " + findFix(vuln));
        getLog().warn("");
    }

    private String findFix(JSONObject vuln) {
        String noUpgrade = "No direct upgrade available";

        if (vuln.get("isUpgradable") != null && (boolean)vuln.get("isUpgradable")) {
            JSONArray upgradePath = (JSONArray)vuln.get("upgradePath");

            //if isUpgradable and upgradePath is available the first item is a boolean "false" the second item is the top level dep.
            //check if upgrade is not same package as original
            if (upgradePath.size() >= 2 && vuln.get("from") != null) {
                JSONArray fromArr = (JSONArray)vuln.get("from");
                if (!fromArr.contains(upgradePath.get(1))) {
                    return "update to " + upgradePath.get(1);
                }
            }
        }

        if (vuln.get("fixedIn") != null) {
            JSONArray fixedIn = (JSONArray)vuln.get("fixedIn");
            if (!fixedIn.isEmpty()) {
                noUpgrade += " - This issue was fixed in versions: " + printJSONArray(fixedIn, ", ");
            }
        }

        return noUpgrade;
    }

    private String printJSONArray(JSONArray array, String delimiter) {
        StringBuilder result = new StringBuilder();
        Iterator<JSONObject> iterator = array.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            result.append(object);
            if (iterator.hasNext()) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }

}
