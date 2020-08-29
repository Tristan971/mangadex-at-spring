package moe.tristan.mdas.client.service.image;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import moe.tristan.mdas.api.image.ImageMode;
import moe.tristan.mdas.client.configuration.ClientInformation;
import moe.tristan.mdas.client.service.ping.PingService;

@Service
public class ImageFetchingService {

    private static final Map<ImageMode, String> IMAGE_MODE_PATHS = Map.of(
        ImageMode.DATA, "/data",
        ImageMode.DATA_SAVER, "/data_saver"
    );

    private final PingService pingService;
    private final ClientInformation clientInformation;
    private final RestTemplate restTemplate;

    public ImageFetchingService(PingService pingService, ClientInformation clientInformation, RestTemplate restTemplate) {
        this.pingService = pingService;
        this.clientInformation = clientInformation;
        this.restTemplate = restTemplate;
    }

    public byte[] serve(ImageMode imageMode, String chapterHash, String fileName) {
        String imageServer = pingService.getPreviousPingResponse().getImageServer();

        URI serverSideUri = UriComponentsBuilder
            .fromHttpUrl(imageServer)
            .path(IMAGE_MODE_PATHS.get(imageMode))
            .path(chapterHash)
            .path(fileName)
            .build()
            .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://mangadex.org");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
        headers.add(HttpHeaders.CACHE_CONTROL, "public/ max-age=1209600");
        headers.add(HttpHeaders.SERVER, clientInformation.toString());
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("Timing-Allow-Origin", "https://mangadex.org");

        RequestEntity<byte[]> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, serverSideUri);

        ResponseEntity<byte[]> response = restTemplate.exchange(requestEntity, byte[].class);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new HttpServerErrorException(response.getStatusCode(), "Upstream server returned non-200 answer!");
        } else {
            return requireNonNull(response.getBody(), "Upstream server returned an empty response!");
        }
    }

}
