package moe.tristan.mdas.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration relative to the "client". (i.e.: this application, as opposed to Mangadex's master server)
 */
@ConfigurationProperties("mdah.client")
public class ClientConfigurationProperties {

    private String secret;
    private int port;
    private int maxNetworkSpeedKilobytesPerSecond;

    private Cache cache;

    public static class Cache {

        private String directory;
        private String databaseFile;
        private int maxSizeMegabytes;

        public int getMaxSizeMegabytes() {
            return maxSizeMegabytes;
        }

        public void setMaxSizeMegabytes(int maxSizeMegabytes) {
            this.maxSizeMegabytes = maxSizeMegabytes;
        }

        public String getDatabaseFile() {
            return databaseFile;
        }

        public void setDatabaseFile(String databaseFile) {
            this.databaseFile = databaseFile;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

    }

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

    public int getMaxNetworkSpeedKilobytesPerSecond() {
        return maxNetworkSpeedKilobytesPerSecond;
    }

    public void setMaxNetworkSpeedKilobytesPerSecond(int maxNetworkSpeedKilobytesPerSecond) {
        this.maxNetworkSpeedKilobytesPerSecond = maxNetworkSpeedKilobytesPerSecond;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

}
