package moe.tristan.mdas.client.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import moe.tristan.mdas.api.image.ImageMode;
import moe.tristan.mdas.client.service.image.ImageProxyService;

@RestController
public class ImageController {

    private final ImageProxyService imageProxyService;

    public ImageController(ImageProxyService imageProxyService) {
        this.imageProxyService = imageProxyService;
    }

    @GetMapping("/{token}/{image-mode}/{chapterHash}/{fileName}")
    public byte[] tokenizedImage(
        @PathVariable String token,
        @PathVariable("image-mode") ImageMode imageMode,
        @PathVariable String chapterHash,
        @PathVariable String fileName
    ) {
        return imageProxyService.serve(token, imageMode, chapterHash, fileName);
    }

    @GetMapping("/{image-mode}/{chapterHash}/{fileName}")
    public byte[] unTokenizedImage(
        @PathVariable("image-mode") ImageMode imageMode,
        @PathVariable String chapterHash,
        @PathVariable String fileName
    ) {
        return imageProxyService.serve(imageMode, chapterHash, fileName);
    }

}
