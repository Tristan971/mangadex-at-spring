package moe.tristan.mdas.configuration.ssl;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.io.ssl.SslHandshakeListener;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import moe.tristan.mdas.mangadex.ping.TlsData;

@Component
public class KeyStoreInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, JettyServerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStoreInitializer.class);

    private static final String KEY_STORE_TYPE = "PKCS12";
    private static final String KEY_STORE_PASS = UUID.randomUUID().toString();

    private static Path KEY_STORE_PATH;
    private static KeyStore KEY_STORE;

    private Server server;
    private SslHandshakeListener sslHandshakeListener;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LOGGER.info("Generate first empty keystore before application is started.");

        KEY_STORE_PATH = createTempKeystoreFile();
        KEY_STORE = generateEmptyKeyStore(KEY_STORE_PATH);
        registerKeystoreInContext(applicationContext.getEnvironment(), KEY_STORE_PATH);
    }

    public void injectCertificates(TlsData tlsData) {
        try (
            InputStream certificatesInputStream = new ByteArrayInputStream(tlsData.getCertificate().getBytes());
            OutputStream keystoreOutputStream = new FileOutputStream(KEY_STORE_PATH.toAbsolutePath().toFile())
        ) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            Certificate certificate = certificateFactory.generateCertificate(certificatesInputStream);

            KeyStore ks = KEY_STORE;

            ks.setCertificateEntry("certificate", certificate);

            PrivateKey privateKey = Pkcs1Loader.parse(tlsData.getPrivateKey());
            ks.setKeyEntry("private-key", privateKey, null, new Certificate[]{certificate});

            ks.store(keystoreOutputStream, KEY_STORE_PASS.toCharArray());

            Connector connector = server.getConnectors()[0];
            connector.stop();

            SslConnectionFactory sslConnectionFactory = connector.getBean(SslConnectionFactory.class);
            sslConnectionFactory.addBean(sslHandshakeListener);

            connector.start();
            LOGGER.info("Restarted Jetty connector!");
        } catch (CertificateException e) {
            throw new IllegalStateException("Cannot create X509 certificate factory!", e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Cannot open KeyStore!", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot store KeyStore!", e);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to restart Jetty!", exception);
        }
    }

    private Path createTempKeystoreFile() {
        try {
            return Files.createTempFile("keystore-", "-" + Instant.now().toEpochMilli() + ".p12");
        } catch (IOException e) {
            throw new IllegalStateException("Could not create temporary file for the KeyStore!", e);
        }
    }

    private KeyStore generateEmptyKeyStore(Path keystorePath) {
        try (OutputStream keystoreOutputStream = new FileOutputStream(keystorePath.toAbsolutePath().toFile())) {
            KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE, "BC");
            ks.load(null, KEY_STORE_PASS.toCharArray());
            ks.store(keystoreOutputStream, KEY_STORE_PASS.toCharArray());
            LOGGER.info("Created empty {} KeyStore at {} with pass {}", KEY_STORE_TYPE, keystorePath, KEY_STORE_PASS);
            return ks;
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Could not create KeyStore!", e);
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not initialize KeyStore!", e);
        } catch (NoSuchProviderException e) {
            throw new IllegalStateException("Invalid provider!", e);
        }
    }

    private void registerKeystoreInContext(ConfigurableEnvironment environment, Path keystorePath) {
        Map<String, Object> sslPropertiesMap = Map.of(
            "server.ssl.enabled", "true",
            "server.ssl.enabled-protocols", "TLSv1.3, TLSv1.2, TLSv1.1, TLSv1",
            "server.ssl.key-store", keystorePath.toAbsolutePath().toString(),
            "server.ssl.key-store-type", KEY_STORE_TYPE,
            "server.ssl.key-store-password", KEY_STORE_PASS
        );
        MapPropertySource sslProperties = new MapPropertySource("ssl-properties", sslPropertiesMap);

        environment.getPropertySources().addFirst(sslProperties);
    }

    @Override
    public void customize(Server server) {
        this.server = server;
    }

    @Autowired
    public void setSslHandshakeListener(SslHandshakeListener sslHandshakeListener) {
        this.sslHandshakeListener = sslHandshakeListener;
    }

}
