/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.ws.helper.conf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.InputSource;

/**
 * 
 * @author rlamarche
 */
public class WSDLLocatorImpl implements WSDLLocator {

    private static final String HTTP_HEADER_COOKIE = "Cookie";

    private String wsdlUri;

    private String latestImportUri;

    private ServiceHelperConfiguration configuration;

    private HttpClient httpClient;

    private Set<InputStream> inputStreams;

    public WSDLLocatorImpl(ServiceHelperConfiguration configuration, String wsdlUri) {
        this.configuration = configuration;
        this.httpClient = createHttpClient();
        this.wsdlUri = wsdlUri;
        inputStreams = new HashSet<InputStream>();
    }

    public InputSource getBaseInputSource() {
        GetMethod get = createGedMethod(wsdlUri);
        try {
            httpClient.executeMethod(get);
            InputStream is = get.getResponseBodyAsStream();
            inputStreams.add(is);
            return new InputSource(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        try {
            String contextURI = parentLocation;
            URL contextURL = (contextURI != null) ? getURL(null, contextURI) : null;
            URL url = getURL(contextURL, importLocation);
            latestImportUri = url.toExternalForm();
            GetMethod get = createGedMethod(latestImportUri);
            httpClient.executeMethod(get);
            InputStream is = get.getResponseBodyAsStream();
            inputStreams.add(is);
            return new InputSource(is);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static URL getURL(URL contextURL, String spec) throws MalformedURLException {
        try {
            return new URL(contextURL, spec);
        } catch (MalformedURLException e) {
            File tempFile = new File(spec);
            if (contextURL == null || (contextURL != null && tempFile.isAbsolute())) {
                return tempFile.toURI().toURL();
            }

            // only reach here if the contextURL !null, spec is relative path and
            // MalformedURLException thrown
            throw e;
        }
    }

    public String getBaseURI() {
        return wsdlUri;
    }

    public String getLatestImportURI() {
        return latestImportUri;
    }

    public void close() {
        for (InputStream is : inputStreams) {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(WSDLLocatorImpl.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        inputStreams.clear();
    }

    private GetMethod createGedMethod(String uri) {
        GetMethod get = new GetMethod(uri);
        if (configuration.getCookie() != null) {
            get.setRequestHeader(HTTP_HEADER_COOKIE, configuration.getCookie());
        }

        return get;
    }

    private HttpClient createHttpClient() {
        HttpClient httpClient = new HttpClient();
        if (configuration.getProxyServer() != null) {
            HostConfiguration hostConfiguration = new HostConfiguration();
            hostConfiguration.setProxy(configuration.getProxyServer(), configuration.getProxyPort());
            httpClient.setHostConfiguration(hostConfiguration);
        }

        if (configuration.getUsername() != null) {
            Credentials credentials = new UsernamePasswordCredentials(configuration.getUsername(), configuration.getPassword());

            httpClient.getState().setCredentials(AuthScope.ANY, credentials);
        }

        if (configuration.getProxyUsername() != null) {
            Credentials credentials = new UsernamePasswordCredentials(configuration.getProxyUsername(), configuration
                    .getProxyPassword());

            httpClient.getState().setProxyCredentials(AuthScope.ANY, credentials);
            httpClient.getHostConfiguration().setProxy(configuration.getProxyServer(), configuration.getProxyPort());
        }
        return httpClient;
    }
}
