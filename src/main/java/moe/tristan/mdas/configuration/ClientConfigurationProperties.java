package moe.tristan.mdas.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration relative to the "client". (i.e.: this application, as opposed to Mangadex's master server)
 */
@ConfigurationProperties("mdah.client")
public class ClientConfigurationProperties {

    private String secret;
    private int port;
    private int maxCacheSizeMegabytes;
    private int maxNetworkSpeedKilobytesPerSecond;

    /*
     * Boilerplate getters/setters ; unfortunately needed here
     */

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxCacheSizeMegabytes() {
        return maxCacheSizeMegabytes;
    }

    public void setMaxCacheSizeMegabytes(int maxCacheSizeMegabytes) {
        this.maxCacheSizeMegabytes = maxCacheSizeMegabytes;
    }

    public int getMaxNetworkSpeedKilobytesPerSecond() {
        return maxNetworkSpeedKilobytesPerSecond;
    }

    public void setMaxNetworkSpeedKilobytesPerSecond(int maxNetworkSpeedKilobytesPerSecond) {
        this.maxNetworkSpeedKilobytesPerSecond = maxNetworkSpeedKilobytesPerSecond;
    }

}
