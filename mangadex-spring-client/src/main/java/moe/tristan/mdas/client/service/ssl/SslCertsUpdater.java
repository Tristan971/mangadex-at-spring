package moe.tristan.mdas.client.service.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import moe.tristan.mdas.client.service.ping.PingService;

@Component
public class SslCertsUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(SslCertsUpdater.class);

    private final KeyStore keyStore;
    private final CertificateFactory x509CertificateFactory;

    private final PingService pingService;

    public SslCertsUpdater(
        @Value("${server.ssl.key-store}") String keystorePath,
        @Value("${server.ssl.key-store-type}") String keystoreType,
        @Value("${server.ssl.key-store-password}") String keystorePass,
        PingService pingService
    ) throws CertificateException {
        this.keyStore = loadKeyStore(keystorePath, keystoreType, keystorePass);
        this.x509CertificateFactory = CertificateFactory.getInstance("X509");

        this.pingService = pingService;
    }

    public void loadCertificates() {
        pingService.getPreviousPingResponse().getTls().ifPresent(tlsData -> {
            String certificateStr = tlsData.getCertificate();

            try {
                Collection<? extends Certificate> certificates = x509CertificateFactory.generateCertificates(
                    new ByteArrayInputStream(certificateStr.getBytes())
                );
                certificates.forEach(cert -> LOGGER.info("Loaded certificate: {}", cert));
            } catch (CertificateException e) {
                throw new RuntimeException("Cannot import certificate!", e);
            }
        });
    }

    private KeyStore loadKeyStore(String path, String type, String pass) {
        Resource ksResource = new ClassPathResource(path.substring(path.indexOf(':') + 1));
        try (InputStream fis = ksResource.getInputStream()) {
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(fis, pass.toCharArray());

            String entries = String.join(",", Collections.list(ks.aliases()));
            LOGGER.info("Loaded KS {}, entries: {}", path, entries);

            return ks;
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            throw new RuntimeException("KeyStore error!", e);
        }
    }

}
