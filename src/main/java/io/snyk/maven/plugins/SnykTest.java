package io.snyk.maven.plugins;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dror on 15/01/2017.
 *
 * Runs 'snyk test' on the enclosing project
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

    // specific snyk plugin configurations
    @Parameter
    private String apiToken = "";

    @Parameter
    private String org = "";

    @Parameter
    private String failOnSeverity = "low";

    @Parameter
    private String endpoint = Constants.DEFAULT_ENDPOINT;

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

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            executeInternal();
        } catch(MojoExecutionException e) {
            throw e;
        } catch(MojoFailureException e) {
            throw e;
        } catch(Throwable t) {
            getLog().warn(Constants.ERROR_GENERAL);
        }
    }

    private void executeInternal()
            throws MojoExecutionException, MojoFailureException, IOException, ParseException {
        if(!validateParameters()) {
            return;
        }

        JSONObject projectTree = new ProjectTraversal(
                project, repoSystem, repoSession).getTree();
        HttpResponse response = sendDataToSnyk(projectTree);
        parseResponse(response);
    }

    private boolean validateParameters() throws MojoExecutionException {
        boolean validated = true;
        if(apiToken.equals("")) {
            Constants.displayAuthError(getLog());
            validated = false;
        }
        baseUrl = Constants.parseEndpoint(endpoint);

        return validated;
    }

    private HttpResponse sendDataToSnyk(JSONObject projectTree)
            throws IOException, ParseException {
        HttpPost request = new HttpPost(baseUrl + "/api/vuln/maven");
        request.addHeader("authorization", "token " + apiToken);
        request.addHeader("x-is-ci", "false"); // how do we know this ??
        request.addHeader("content-type", "application/json");
        HttpEntity entity = new StringEntity(projectTree.toString());
        request.setEntity(entity);

        HttpClient client = HttpClientBuilder.create().build();
        return client.execute(request);
    }

    private void parseResponse(HttpResponse response)
            throws IOException, ParseException, MojoFailureException {
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

    private void processError(HttpResponse response) {
        // process an error in the response object
        if(response.getStatusLine().toString().contains("401")) {
            Constants.displayAuthError(getLog());
        } else {
            getLog().error(Constants.ERROR_GENERAL);
        }
    }

    private JSONObject parseResponseBody(HttpResponse response) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(
                new BufferedReader(
                    new InputStreamReader(
                        response.getEntity().getContent())));
            return jsonObject;
        } catch (IOException e) {
        } catch (ParseException e) {
        }
        return null;
    }

    private void processVulns(JSONObject responseJson) throws MojoFailureException {
        HashSet<String> vulnIdSet = new HashSet<String>();

        JSONArray vulns = (JSONArray)responseJson.get("vulnerabilities");
        int highestSeverity = SEVERITY_LOW;
        Iterator<JSONObject> iterator = vulns.iterator();
        while (iterator.hasNext()) {
            JSONObject vuln = (JSONObject)iterator.next();
            vulnIdSet.add((String)vuln.get("id"));
            Integer severityInt = severityMap.get((String)vuln.get("severity"));
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

    private void printVuln(JSONObject vuln) {
        getLog().warn("✗ " + vuln.get("severity") + " severity vulnerability found on " +
                vuln.get("moduleName") + "@" +
                vuln.get("version"));
        getLog().warn("- desc: " + vuln.get("title"));
        getLog().warn("- info: " + baseUrl + "/vuln/" + vuln.get("id"));
        if(vuln.get("from") != null) {
            JSONArray fromArr = (JSONArray)vuln.get("from");
            String fromStr = "";
            for(int i = 0; i < fromArr.size(); i++) {
                fromStr += fromArr.get(i) + " > ";
            }
            fromStr = fromStr.substring(0, fromStr.length() - 3);
            getLog().warn("- from: " + fromStr);
        }
        getLog().warn("");
    }

}
