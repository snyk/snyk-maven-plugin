package io.snyk.maven.plugins;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.rtinfo.RuntimeInformation;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dror on 15/01/2017.
 *
 * Records the current state of dependencies of the project in the Snyk system.
 * This record will be continuously scanned to alert the user for new or updated vulnerabilities.
 */
@Mojo( name = "monitor")
public class SnykMonitor extends AbstractMojo {

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

    @Component
    private RuntimeInformation runtimeInformation;

    // specific snyk plugin configurations
    @Parameter
    private String apiToken = "";

    @Parameter
    private String org = "";

    @Parameter
    private String endpoint = Constants.DEFAULT_ENDPOINT;

    private String baseUrl = "";

    /**
     * executes this Mojo
     * cannot fail the build under any circumstance
     */
    public void execute() {
        try {
            executeInternal();
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
     * @throws MojoExecutionException
     * @throws MojoFailureException
     * @throws IOException
     * @throws ParseException
     */
    private void executeInternal()
            throws MojoExecutionException, MojoFailureException, IOException, ParseException {
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
                project, repoSystem, repoSession, remoteRepositories).getTree();
        HttpResponse response = sendDataToSnyk(projectTree);
        parseResponse(response);
    }

    /**
     * validate the plugin's parameters
     * @return false if validation didn't pass
     * @throws MojoExecutionException
     */
    private boolean validateParameters() throws MojoExecutionException {
        boolean validated = true;
        if(apiToken.equals("")) {
            Constants.displayAuthError(getLog());
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
    private HttpResponse sendDataToSnyk(JSONObject projectTree)
            throws IOException, ParseException {
        HttpPut request = new HttpPut(baseUrl + "/api/monitor/maven");
        request.addHeader("authorization", "token " + apiToken);
        request.addHeader("x-is-ci", "false"); // how do we know ??
        request.addHeader("content-type", "application/json");

        JSONObject jsonDependencies = prepareRequestBody(projectTree);
        HttpEntity entity = new StringEntity(jsonDependencies.toString());
        request.setEntity(entity);

        HttpClient client = HttpClientBuilder.create().build();
        return client.execute(request);
    }

    /**
     * prepares a request body to send the Snyk API
     * @param projectTree the dependencies tree as collected by ProjectTraversal
     * @return a JSON object which should be sent as the body of the POST request to the Snyk API
     */
    private JSONObject prepareRequestBody(JSONObject projectTree) {
        JSONObject body = new JSONObject();

        JSONObject meta = new JSONObject();
        String groupId = project.getGroupId();
        String artifactId = project.getArtifactId();
        String version = project.getVersion();
        meta.put("method", "maven-plugin"); // maybe put "plugin" ??
        try {
            meta.put("hostname", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            meta.put("hostname", "");
        }
        meta.put("id", groupId + ":" + artifactId);
        // TBD: find out whether we're inside a build machine
        meta.put("ci", "false");
        meta.put("maven", runtimeInformation.getMavenVersion());
        meta.put("name", groupId + ":" + artifactId);
        meta.put("version", version);
        meta.put("org", org);
        body.put("meta", meta);
        body.put("package", projectTree);
        String snykPolicy = SnykPolicy.readPolicyFile(project);
        if(snykPolicy != null) {
            body.put("policy", snykPolicy);
        }

        return body;
    }

    /**
     * parse Snyk's response and present it in the build log
     * @param response the HTTP response from the call to Snyk
     * @throws IOException
     * @throws ParseException
     * @throws MojoFailureException
     */
    private void parseResponse(HttpResponse response)
            throws IOException, ParseException, MojoFailureException {
        if(response.getStatusLine().getStatusCode() >= 400) {
            processError(response);
            return;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(
                new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent())));

        Boolean ok = (Boolean) jsonObject.get("ok");
        if(ok != null && ok == true) {
            getLog().info("Captured a snapshot of this project's dependencies.");
            getLog().info("Explore this snapshot at " +
                    getMonitorWebURL((String)jsonObject.get("org"), (String)jsonObject.get("id")));
            getLog().info("");
            getLog().info("Notifications about newly disclosed vulnerabilities " +
                    "related to these dependencies will be emailed to you.");
            getLog().info("");
        } else if(jsonObject.get("error") != null) {
            getLog().error("There was a problem monitoring the project: "
                    + jsonObject.get("error"));
        } else if(jsonObject.get("message") != null) {
            getLog().warn("Could not complete the monitoring action: "
                    + jsonObject.get("message"));
        }
    }

    /**
     * creates the URL for the recorded project in the Snyk website
     * @param org the organization name
     * @param id the ID of the project
     * @return the full URL of the recorded project
     */
    private String getMonitorWebURL(String org, String id) {
        return baseUrl + "/org/" + org + "/monitor/" + id;
    }

    /**
     * process the error from an HTTP response object,
     * and log it in the build log
     * @param response an HTTP response object
     */
    private void processError(HttpResponse response) {
        // process an error in the response object
        if(response.getStatusLine().toString().contains("401")) {
            Constants.displayAuthError(getLog());
        } else {
            getLog().error("Bad response from Snyk: " +
                response.getStatusLine().toString());
        }
    }
}
