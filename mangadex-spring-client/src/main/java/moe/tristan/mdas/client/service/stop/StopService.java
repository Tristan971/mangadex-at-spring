package moe.tristan.mdas.client.service.stop;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import moe.tristan.mdas.api.stop.StopRequest;
import moe.tristan.mdas.client.configuration.ClientConfigurationProperties;
import moe.tristan.mdas.client.configuration.ServerConfigurationProperties;

@Service
public class StopService {

    private final RestTemplate restTemplate;
    private final String clientSecret;
    private final URI stopEndpoint;

    public StopService(
        RestTemplate restTemplate,
        ClientConfigurationProperties clientConfigurationProperties,
        ServerConfigurationProperties serverConfigurationProperties
    ) {
        this.restTemplate = restTemplate;
        this.clientSecret = clientConfigurationProperties.getSecret();
        this.stopEndpoint = UriComponentsBuilder
            .fromUri(serverConfigurationProperties.getControlServerUrl())
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
