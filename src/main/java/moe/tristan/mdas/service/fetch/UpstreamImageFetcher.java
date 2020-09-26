package moe.tristan.mdas.service.fetch;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import moe.tristan.mdas.mangadex.image.ImageMode;
import moe.tristan.mdas.model.ImageRequest;
import moe.tristan.mdas.service.ping.PingService;

import io.micrometer.core.annotation.Timed;

@Service
public class UpstreamImageFetcher {

    private static final Map<ImageMode, String> IMAGE_MODE_PATHS = Map.of(
        ImageMode.DATA, "/data",
        ImageMode.DATA_SAVER, "/data_saver"
    );

    private final PingService pingService;
    private final RestTemplate restTemplate;

    public UpstreamImageFetcher(PingService pingService, RestTemplate restTemplate) {
        this.pingService = pingService;
        this.restTemplate = restTemplate;
    }

    @Timed
    public ResponseEntity<byte[]> download(ImageRequest imageRequest) {
        String imageServer = pingService.getLastPingResponse().getImageServer();

        URI serverSideUri = UriComponentsBuilder
            .fromHttpUrl(imageServer.trim())
            .path("/{mode}/{chapter}/{file}")
            .build(
                IMAGE_MODE_PATHS.get(imageRequest.getMode()),
                imageRequest.getChapter(),
                imageRequest.getFile()
            );

        ResponseEntity<byte[]> response = restTemplate.getForEntity(serverSideUri, byte[].class);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new HttpServerErrorException(response.getStatusCode(), "Upstream server returned non-200 answer!");
        }

        if (response.getBody() == null) {
            throw new IllegalStateException("Upstream returned an empty body for " + imageRequest);
        }

        return response;
    }

}
