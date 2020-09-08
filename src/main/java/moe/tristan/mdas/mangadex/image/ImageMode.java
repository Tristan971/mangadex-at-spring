package moe.tristan.mdas.mangadex.image;

public enum ImageMode {
    /**
     * Full-size original images in all their glory
     */
    DATA,

    /**
     * Compressed Jpeg version of these images for our american friends and their nightmarish data caps
     */
    DATA_SAVER;

    public static ImageMode fromHttpPath(String code) {
        return switch (code) {
            case "data" -> DATA;
            case "data-saver" -> DATA_SAVER;
            default -> throw new IllegalArgumentException("Unknown image mode: " + code);
        };
    }

}
