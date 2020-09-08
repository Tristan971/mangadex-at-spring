package moe.tristan.mdas.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import moe.tristan.mdas.mangadex.image.ImageMode;
import moe.tristan.mdas.service.image.ImageService;
import moe.tristan.mdas.service.security.ImageRequestReferrerValidator;

@RestController
public class ImageController {

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
        imageRequestReferrerValidator.validate(request);
        return imageService.serve(token, ImageMode.fromHttpPath(imageMode), chapterHash, fileName);
    }

    @GetMapping("/{image-mode}/{chapterHash}/{fileName}")
    public byte[] unTokenizedImage(
        @PathVariable("image-mode") String imageMode,
        @PathVariable String chapterHash,
        @PathVariable String fileName,
        HttpServletRequest request
    ) {
        imageRequestReferrerValidator.validate(request);
        return imageService.serve(ImageMode.fromHttpPath(imageMode), chapterHash, fileName);
    }

}
