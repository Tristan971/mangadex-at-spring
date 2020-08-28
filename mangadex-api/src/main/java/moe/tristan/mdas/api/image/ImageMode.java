package moe.tristan.mdas.api.image;

public enum ImageMode {
    /**
     * Full-size original images in all their glory
     */
    DATA,

    /**
     * Compressed Jpeg version of these images for our american friends and their nightmarish data caps
     */
    DATA_SAVER
}
