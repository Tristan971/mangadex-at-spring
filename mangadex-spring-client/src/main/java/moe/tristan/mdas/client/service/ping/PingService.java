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
import moe.tristan.mdas.client.configuration.ServerConfigurationProperties;

@Service
public class PingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingService.class);

    private final URI pingEndpoint;
    private final RestTemplate restTemplate;

    private PingResponse previousPingResponse;

    public PingService(ServerConfigurationProperties serverConfigurationProperties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.pingEndpoint = UriComponentsBuilder
            .fromUri(serverConfigurationProperties.getControlServerUrl())
            .path("/ping")
            .build()
            .toUri();
    }

    public void ping() {
        Optional<ZonedDateTime> lastPingTlsCreatedAt = Optional
            .ofNullable(previousPingResponse)
            .map(PingResponse::getTls)
            .map(TlsData::getCreatedAt);

        PingRequest request = PingRequest
            .builder()
            .secret("secret")
            .port(0)
            .diskSpace(1000000)
            .networkSpeed(1000000)
            .buildVersion("0")
            .tlsCreatedAt(lastPingTlsCreatedAt)
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
        if (!newPingResponse.equals(previousPingResponse)) {
            LOGGER.info("New ping response: {}", newPingResponse);
            previousPingResponse = newPingResponse;
        }
    }

    public PingResponse getPreviousPingResponse() {
        return previousPingResponse;
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
