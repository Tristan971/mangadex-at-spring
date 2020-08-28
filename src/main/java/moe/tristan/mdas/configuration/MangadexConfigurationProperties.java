package moe.tristan.mdas.configuration;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mangadex")
public class MangadexConfigurationProperties {

    private ControlServer controlServer;

    public ControlServer getControlServer() {
        return controlServer;
    }

    public void setControlServer(ControlServer controlServer) {
        this.controlServer = controlServer;
    }

    public static class ControlServer {

        private URI apiUrl;

        public URI getApiUrl() {
            return apiUrl;
        }

        /**
         * @param apiUrl the URL to the mangadex control server
         */
        public void setApiUrl(URI apiUrl) {
            this.apiUrl = apiUrl;
        }

    }

}
