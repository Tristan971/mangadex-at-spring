package moe.tristan.mdas.service.cache;

import org.springframework.stereotype.Component;

import moe.tristan.mdas.mangadex.image.ImageMode;
import moe.tristan.mdas.service.fetch.UpstreamImageFetcher;

@Component
public class ImageCacheService {

    private final UpstreamImageFetcher upstreamImageFetcher;

    public ImageCacheService(UpstreamImageFetcher upstreamImageFetcher) {
        this.upstreamImageFetcher = upstreamImageFetcher;
    }

    public byte[] load(ImageMode mode, String chapter, String file) {
        return upstreamImageFetcher.download(mode, chapter, file);
    }

}
