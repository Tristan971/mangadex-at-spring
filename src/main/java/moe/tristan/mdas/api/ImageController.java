package moe.tristan.mdas.api;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import moe.tristan.mdas.mangadex.image.ImageMode;
import moe.tristan.mdas.model.ImageRequest;
import moe.tristan.mdas.service.image.ImageService;
import moe.tristan.mdas.service.security.ImageRequestReferrerValidator;

@RestController
public class ImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;
    private final ImageRequestReferrerValidator imageRequestReferrerValidator;

    public ImageController(
        ImageService imageService,
        ImageRequestReferrerValidator imageRequestReferrerValidator
    ) {
        this.imageService = imageService;
        this.imageRequestReferrerValidator = imageRequestReferrerValidator;
    }

    @GetMapping("/{token}/{image-mode}/{chapterHash}/{fileName}")
    public byte[] tokenizedImage(
        @PathVariable String token,
        @PathVariable("image-mode") String imageMode,
        @PathVariable String chapterHash,
        @PathVariable String fileName,
        HttpServletRequest request
    ) {
        ImageRequest imageRequest = ImageRequest.of(ImageMode.fromHttpPath(imageMode), chapterHash, fileName);
        LOGGER.info("Requested: {}", imageRequest.getUniqueIdentifier());

        imageRequestReferrerValidator.validate(request);
        return imageService.serve(imageRequest, token);
    }

    @GetMapping("/{image-mode}/{chapterHash}/{fileName}")
    public byte[] unTokenizedImage(
        @PathVariable("image-mode") String imageMode,
        @PathVariable String chapterHash,
        @PathVariable String fileName,
        HttpServletRequest request
    ) {
        ImageRequest imageRequest = ImageRequest.of(ImageMode.fromHttpPath(imageMode), chapterHash, fileName);
        LOGGER.info("Requested: {}", imageRequest.getUniqueIdentifier());

        imageRequestReferrerValidator.validate(request);
        return imageService.serve(imageRequest);
    }

}
