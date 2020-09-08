package moe.tristan.mdas.service.ping;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import moe.tristan.mdas.configuration.ClientConfigurationProperties;
import moe.tristan.mdas.mangadex.Constants;
import moe.tristan.mdas.mangadex.ping.PingRequest;
import moe.tristan.mdas.mangadex.ping.PingResponse;
import moe.tristan.mdas.mangadex.ping.TlsData;

import io.micrometer.core.annotation.Timed;

@Service
public class PingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingService.class);

    private final URI pingEndpoint;
    private final RestTemplate restTemplate;
    private final ClientConfigurationProperties clientConfigurationProperties;

    private TlsData lastTlsData;
    private PingResponse lastPingResponse;

    public PingService(RestTemplate restTemplate, ClientConfigurationProperties clientConfigurationProperties) {
        this.restTemplate = restTemplate;
        this.clientConfigurationProperties = clientConfigurationProperties;
        this.pingEndpoint = UriComponentsBuilder
            .fromHttpUrl(Constants.MANGADEX_API_URL)
            .path("/ping")
            .build()
            .toUri();
    }

    @Timed
    public void ping() {
        Optional<ZonedDateTime> lastTlsCreatedAt = Optional.ofNullable(lastTlsData).map(TlsData::getCreatedAt);

        PingRequest request = PingRequest
            .builder()
            .secret(clientConfigurationProperties.getSecret())
            .port(clientConfigurationProperties.getPort())
            .diskSpace(clientConfigurationProperties.getMaxCacheSizeMegabytes())
            .networkSpeed(clientConfigurationProperties.getMaxNetworkSpeedKilobytesPerSecond())
            .tlsCreatedAt(lastTlsCreatedAt)
            .build();

        ResponseEntity<PingResponse> response = restTemplate.postForEntity(
            pingEndpoint,
            request,
            PingResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            handleErrorResponse(response);
        }

        PingResponse newPingResponse = Objects.requireNonNull(response.getBody(), "null ping response from server!");
        if (!newPingResponse.equals(lastPingResponse)) {
            LOGGER.info("New ping response: {}", newPingResponse);
        }
        if (newPingResponse.getTls().isPresent()) {
            LOGGER.info("New tls data received!");
            lastTlsData = newPingResponse.getTls().get();
        }
        lastPingResponse = newPingResponse;
    }

    public PingResponse getLastPingResponse() {
        return lastPingResponse;
    }

    public TlsData getLastTlsData() {
        return lastTlsData;
    }

    private void handleErrorResponse(ResponseEntity<?> responseEntity) {
        switch (responseEntity.getStatusCode()) {
            case UNSUPPORTED_MEDIA_TYPE:
                throw new IllegalStateException("Content-Type was not set to application/json");
            case BAD_REQUEST:
                throw new IllegalStateException("Json body was malformed!");
            case FORBIDDEN:
                throw new IllegalStateException("Secret is not valid anymore!");
            default:
                throw new RuntimeException("Unexpected exception: " + responseEntity);
        }
    }

}
