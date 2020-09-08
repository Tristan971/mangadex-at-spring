package moe.tristan.mdas.client.service.ping;

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

import moe.tristan.mdas.api.ping.PingRequest;
import moe.tristan.mdas.api.ping.PingResponse;
import moe.tristan.mdas.api.ping.TlsData;
import moe.tristan.mdas.client.configuration.ClientConfigurationProperties;
import moe.tristan.mdas.client.configuration.ServerConfigurationProperties;

import io.micrometer.core.annotation.Timed;

@Service
public class PingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingService.class);

    private final URI pingEndpoint;
    private final RestTemplate restTemplate;
    private final ClientConfigurationProperties clientConfigurationProperties;

    private TlsData lastTlsData;
    private PingResponse lastPingResponse;

    public PingService(
        RestTemplate restTemplate,
        ServerConfigurationProperties serverConfigurationProperties,
        ClientConfigurationProperties clientConfigurationProperties
    ) {
        this.restTemplate = restTemplate;
        this.clientConfigurationProperties = clientConfigurationProperties;
        this.pingEndpoint = UriComponentsBuilder
            .fromUri(serverConfigurationProperties.getControlServerUrl())
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
            .port(0)
            .diskSpace(0)
            .networkSpeed(0)
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
