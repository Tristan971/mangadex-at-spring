package moe.tristan.mdas.service.stop;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import moe.tristan.mdas.mangadex.Constants;
import moe.tristan.mdas.mangadex.stop.StopRequest;
import moe.tristan.mdas.configuration.ClientConfigurationProperties;

@Service
public class StopService {

    private final RestTemplate restTemplate;
    private final String clientSecret;
    private final URI stopEndpoint;

    public StopService(
        RestTemplate restTemplate,
        ClientConfigurationProperties clientConfigurationProperties
    ) {
        this.restTemplate = restTemplate;
        this.clientSecret = clientConfigurationProperties.getSecret();
        this.stopEndpoint = UriComponentsBuilder
            .fromHttpUrl(Constants.MANGADEX_API_URL)
            .path("/stop")
            .build()
            .toUri();
    }

    public void stop() {
        restTemplate.postForObject(
            stopEndpoint,
            StopRequest.of(clientSecret),
            Void.class
        );
    }

}
