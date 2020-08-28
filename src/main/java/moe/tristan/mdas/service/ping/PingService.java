package moe.tristan.mdas.service.ping;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import moe.tristan.mdas.configuration.MangadexConfigurationProperties;
import moe.tristan.mdas.model.ping.PingRequest;
import moe.tristan.mdas.model.ping.PingResponse;
import moe.tristan.mdas.model.ping.TlsData;

@Service
public class PingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingService.class);

    private final URI pingEndpoint;
    private final RestTemplate restTemplate;

    private PingResponse lastPingResponse;

    public PingService(MangadexConfigurationProperties mangadexConfigurationProperties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.pingEndpoint = UriComponentsBuilder
            .fromUri(mangadexConfigurationProperties.getControlServer().getApiUrl())
            .path("/ping")
            .build()
            .toUri();
    }

    public PingResponse ping() {
        LOGGER.info("Ping...");

        Optional<ZonedDateTime> lastPingTlsCreatedAt = Optional
            .ofNullable(lastPingResponse)
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

        LOGGER.info("... Pong!");

        if (!response.getStatusCode().is2xxSuccessful()) {
            handleErrorResponse(response);
        }

        PingResponse responseBody = response.getBody();
        lastPingResponse = responseBody;

        return responseBody;
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
