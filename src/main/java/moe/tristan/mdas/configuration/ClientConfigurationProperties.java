package moe.tristan.mdas.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration relative to the "client". (i.e.: this application, as opposed to Mangadex's master server)
 */
@ConfigurationProperties("client")
public class ClientConfigurationProperties {

    private String secret;

    private int pingFrequencySeconds;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getPingFrequencySeconds() {
        return pingFrequencySeconds;
    }

    /**
     * Set the frequency (in number of seconds between attempts) for the continuous upstream ping
     */
    public void setPingFrequencySeconds(int pingFrequencySeconds) {
        this.pingFrequencySeconds = pingFrequencySeconds;
    }

}
