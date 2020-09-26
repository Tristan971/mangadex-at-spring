package moe.tristan.mdas.service.image;

import org.springframework.stereotype.Service;

import moe.tristan.mdas.model.ImageRequest;
import moe.tristan.mdas.service.cache.ImageCacheService;

@Service
public class ImageService {

    private final ImageCacheService imageCacheService;

    public ImageService(ImageCacheService imageCacheService) {
        this.imageCacheService = imageCacheService;
    }

    public byte[] serve(ImageRequest imageRequest, String token) {
        // TODO: validate token
        return serve(imageRequest);
    }

    public byte[] serve(ImageRequest imageRequest) {
        return imageCacheService.load(imageRequest);
    }

}
