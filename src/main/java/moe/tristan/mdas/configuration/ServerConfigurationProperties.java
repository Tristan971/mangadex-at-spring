package moe.tristan.mdas.configuration;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration relative to the "server" (i.e.: Mangadex's master server, as opposed to this application)
 */
@ConfigurationProperties("server")
public class ServerConfigurationProperties {

    private URI controlServerUrl;

    public URI getControlServerUrl() {
        return controlServerUrl;
    }

    /**
     * The URI to the control server's API
     */
    public void setControlServerUrl(URI controlServerUrl) {
        this.controlServerUrl = controlServerUrl;
    }

}
