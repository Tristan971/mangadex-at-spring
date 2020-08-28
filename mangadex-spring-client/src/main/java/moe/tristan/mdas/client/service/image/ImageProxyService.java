package moe.tristan.mdas.client.service.image;

import org.springframework.stereotype.Service;

import moe.tristan.mdas.api.image.ImageMode;

@Service
public class ImageProxyService {

    private final ImageFetchingService imageFetchingService;

    public ImageProxyService(ImageFetchingService imageFetchingService) {
        this.imageFetchingService = imageFetchingService;
    }

    public byte[] serve(String token, ImageMode imageMode, String chapterHash, String fileName) {
        // TODO: validate token
        return serve(imageMode, chapterHash, fileName);
    }

    public byte[] serve(ImageMode imageMode, String chapterHash, String fileName) {
        return imageFetchingService.serve(imageMode, chapterHash, fileName);
    }

}
