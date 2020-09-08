package moe.tristan.mdas.client.service.image;

import org.springframework.stereotype.Service;

import moe.tristan.mdas.api.image.ImageMode;
import moe.tristan.mdas.client.service.cache.ImageCacheService;

@Service
public class ImageService {

    private final ImageCacheService imageCacheService;

    public ImageService(ImageCacheService imageCacheService) {
        this.imageCacheService = imageCacheService;
    }

    public byte[] serve(String token, ImageMode imageMode, String chapterHash, String fileName) {
        // TODO: validate token
        return serve(imageMode, chapterHash, fileName);
    }

    public byte[] serve(ImageMode imageMode, String chapterHash, String fileName) {
        return imageCacheService.load(imageMode, chapterHash, fileName);
    }

}
