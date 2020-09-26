package moe.tristan.mdas.api;

import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import moe.tristan.mdas.mangadex.image.ImageMode;
import moe.tristan.mdas.model.ImageRequest;
import moe.tristan.mdas.model.ImageResponse;
import moe.tristan.mdas.service.cache.ImageCacheService;
import moe.tristan.mdas.service.security.ImageRequestReferrerValidator;
import moe.tristan.mdas.service.security.ImageTokenValidator;

@RestController
public class ImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private final ImageCacheService imageCacheService;
    private final ImageTokenValidator imageTokenValidator;
    private final ImageRequestReferrerValidator imageRequestReferrerValidator;

    public ImageController(
        ImageCacheService imageCacheService,
        ImageTokenValidator imageTokenValidator,
        ImageRequestReferrerValidator imageRequestReferrerValidator
    ) {
        this.imageCacheService = imageCacheService;
        this.imageTokenValidator = imageTokenValidator;
        this.imageRequestReferrerValidator = imageRequestReferrerValidator;
    }

    @GetMapping("/{token}/{image-mode}/{chapterHash}/{fileName}")
    public byte[] tokenizedImage(
        @PathVariable String token,
        @PathVariable("image-mode") String imageMode,
        @PathVariable String chapterHash,
        @PathVariable String fileName,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        ImageMode mode = ImageMode.fromHttpPath(imageMode);
        ImageRequest imageRequest = ImageRequest.of(mode, chapterHash, fileName);

        imageTokenValidator.validateToken(imageRequest, token);

        return serve(request, response, imageRequest);
    }

    @GetMapping("/{image-mode}/{chapterHash}/{fileName}")
    public byte[] unTokenizedImage(
        @PathVariable("image-mode") String imageMode,
        @PathVariable String chapterHash,
        @PathVariable String fileName,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        ImageMode mode = ImageMode.fromHttpPath(imageMode);
        ImageRequest imageRequest = ImageRequest.of(mode, chapterHash, fileName);
        return serve(request, response, imageRequest);
    }

    public byte[] serve(HttpServletRequest request, HttpServletResponse response, ImageRequest imageRequest) {
        LOGGER.info("Requested: {}", imageRequest.getUniqueIdentifier());

        imageRequestReferrerValidator.validate(request);
        ImageResponse imageResponse = imageCacheService.load(imageRequest);

        // MDAH spec headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://mangadex.org");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "public/ max-age=1209600");
        response.setHeader("Timing-Allow-Origin", "https://mangadex.org");

        // Upstream mimicking as required
        imageResponse.getUpstreamHeaders().ifPresent(upstreamHeaders -> {
            Stream.of(
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.CONTENT_LENGTH,
                HttpHeaders.LAST_MODIFIED
            ).forEach(mimicked -> {
                List<String> upstream = upstreamHeaders.get(mimicked);
                if (upstream != null && upstream.size() != 0) {
                    response.setHeader(mimicked, upstream.get(0));
                }
            });

            if (upstreamHeaders.get(HttpHeaders.CONTENT_LENGTH) == null) {
                response.setHeader(HttpHeaders.TRANSFER_ENCODING, "Chunked");
            }
        });

        // extra headers
        response.setHeader("X-Cache", imageResponse.getCacheHint().name());

        return imageResponse.getBytes();
    }

}
