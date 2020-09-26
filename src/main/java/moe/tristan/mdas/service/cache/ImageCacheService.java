package moe.tristan.mdas.service.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import moe.tristan.mdas.model.ImageRequest;
import moe.tristan.mdas.service.fetch.UpstreamImageFetcher;

@Component
public class ImageCacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCacheService.class);

    private final CachedImageRepository cachedImageRepository;
    private final UpstreamImageFetcher upstreamImageFetcher;
    private final Path cacheDirectory;

    public ImageCacheService(
        CachedImageRepository cachedImageRepository,
        UpstreamImageFetcher upstreamImageFetcher,
        @Value("${mdah.client.cache.directory}") String cacheDirectory
    ) throws IOException {
        this.cachedImageRepository = cachedImageRepository;
        this.upstreamImageFetcher = upstreamImageFetcher;
        this.cacheDirectory = verifyCacheDirectory(cacheDirectory);
    }

    public byte[] load(ImageRequest imageRequest) {
        String imageId = imageRequest.getUniqueIdentifier();
        Optional<CachedImageEntity> cachedImageSearch = cachedImageRepository.findById(imageId);

        if (cachedImageSearch.isPresent()) {
            LOGGER.info("Cache hit for {}", imageRequest);
            CachedImageEntity cachedImage = cachedImageSearch.get();
            try {
                return loadCached(cachedImage);
            } catch (IOException e) {
                LOGGER.error("Cache fail for: {} - UID: {}", imageRequest, cachedImage.getId(), e);
            }
        } else {
            LOGGER.info("Cache miss for {}", imageRequest);
        }

        return loadUncached(imageRequest);
    }

    Path verifyCacheDirectory(String cacheDirectory) throws IOException {
        LOGGER.info("Cache directory setting is: {}", cacheDirectory);
        Path cacheDirectoryPath = Path.of(cacheDirectory).toAbsolutePath();

        if (!Files.exists(cacheDirectoryPath)) {
            LOGGER.info("Creating cache directory at: {}", cacheDirectoryPath);
            cacheDirectoryPath = Files.createDirectories(cacheDirectoryPath).toAbsolutePath();
        }
        LOGGER.debug("Cache directory set to: {}", cacheDirectoryPath);

        if (!Files.isReadable(cacheDirectoryPath)) {
            throw new IllegalStateException("Cache directory " + cacheDirectoryPath.toString() + " is not readable.");
        }
        if (!Files.isWritable(cacheDirectoryPath)) {
            throw new IllegalStateException("Cache directory " + cacheDirectoryPath.toString() + " is not writable.");
        }

        return cacheDirectoryPath;
    }

    private byte[] loadCached(CachedImageEntity cachedImage) throws IOException {
        String inCachePath = cachedImage.getId().replaceAll("_", File.separator);
        Path cachedImagePath = cacheDirectory.resolve(inCachePath);

        LOGGER.debug("Loading image from: {}", cachedImagePath);
        return Files.readAllBytes(cachedImagePath);
    }

    private byte[] loadUncached(ImageRequest imageRequest) {
        String inCachePath = String.join(File.separator, imageRequest.getIdentifiers());
        Path imagePath = cacheDirectory.resolve(inCachePath);

        byte[] imageBytes = upstreamImageFetcher.download(imageRequest);
        CompletableFuture.runAsync(() -> {
            try {
                Path directory = imagePath.toAbsolutePath().getParent();
                if (!Files.exists(directory)) {
                    Files.createDirectories(directory);
                }
                Files.write(imagePath, imageBytes);
                CachedImageEntity imageEntity = new CachedImageEntity(imageRequest.getUniqueIdentifier(), imageBytes.length);
                cachedImageRepository.saveAndFlush(imageEntity);
                LOGGER.info("Committed {} to cache as {}", imageRequest, imagePath);
            } catch (IOException e) {
                LOGGER.error("Cannot commit image {} to cache!", imageRequest, e);
            }
        });

        return imageBytes;
    }

}
