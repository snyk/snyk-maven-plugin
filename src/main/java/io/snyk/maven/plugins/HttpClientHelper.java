package io.snyk.maven.plugins;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

/**
 * Helper that builds a {@link HttpClient}, setting up a proxy server, if one is present in ~/.m2/settings.xml
 */
public class HttpClientHelper {

    private final Log log;
    private final Settings settings;

    public HttpClientHelper(Log log, Settings settings) {
        this.log = log;
        this.settings = settings;
    }

    public HttpClient buildHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        return addProxy(httpClientBuilder)
                .build();
    }

    /**
     * Adds first active proxy server from ~/.m2/settings.xml, if present,
     * that will be passed to <code>HttpClientBuilder</code> used by <code>HttpClient</code>
     *
     * @param builder {@link HttpClientBuilder}
     */
    private HttpClientBuilder addProxy(HttpClientBuilder builder) {
        Proxy settingsProxy = settings.getActiveProxy();
        if (settingsProxy != null) {
            getLog().debug("proxy server present, trying to set the first active one");
            final String proxyHost = settingsProxy.getHost();
            final int proxyPort = settingsProxy.getPort();
            final String proxyUsername = settingsProxy.getUsername();
            final String proxyPassword = settingsProxy.getPassword();

            if (proxyHost != null && !proxyHost.isEmpty()) {
                getLog().debug("Using proxy=" + proxyHost + " with port=" + proxyPort + ".");

                final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                builder.setProxy(proxy);
                prepareCredentials(builder, proxyUsername, proxyPassword);
            }
        }
        return builder;
    }

    private void prepareCredentials(HttpClientBuilder builder,
                                    String proxyUsername,
                                    String proxyPassword) {
        if (proxyUsername != null && !proxyUsername.isEmpty()) {
            getLog().debug("Using proxy user name=" + proxyUsername + ".");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }
    }

    private Log getLog() {
        return log;
    }
}
