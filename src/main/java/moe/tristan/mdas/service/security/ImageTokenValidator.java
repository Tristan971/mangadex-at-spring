package moe.tristan.mdas.service.security;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;

import org.abstractj.kalium.crypto.SecretBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import moe.tristan.mdas.mangadex.image.ImageToken;
import moe.tristan.mdas.service.ping.PingService;

import io.micrometer.core.annotation.Timed;

@Component
public class ImageTokenValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTokenValidator.class);

    private final PingService pingService;
    private final ObjectMapper objectMapper;

    public ImageTokenValidator(PingService pingService, ObjectMapper objectMapper) {
        this.pingService = pingService;
        this.objectMapper = objectMapper;
    }

    @Timed
    public void validateToken(String chapter, String token) {
        byte[] tokenArray = Base64.getDecoder().decode(token);

        if (tokenArray.length < (24 + 32)) {
            throw new InvalidTokenException(chapter, token, "Token was invalid (too short!)");
        }

        byte[] nonce = Arrays.copyOfRange(tokenArray, 0, 24);
        byte[] cipherText = Arrays.copyOfRange(tokenArray, 24, tokenArray.length);

        byte[] decrypted = buildSecretBox().decrypt(nonce, cipherText);

        try {
            var imageToken = objectMapper.readValue(decrypted, ImageToken.class);

            if (imageToken.getExpires().isBefore(ZonedDateTime.now())) {
                throw new InvalidTokenException(chapter, token, "Token is expired!");
            }

            if (chapter.equals(imageToken.getHash())) {
                throw new InvalidTokenException(
                    chapter,
                    token,
                    "Token hash doesn't match image chapter hash! (was for chapter hash: " + imageToken.getHash() + ")"
                );
            }

            LOGGER.debug("Token {} is valid for chapter {}", token, chapter);
        } catch (IOException e) {
            throw new RuntimeException("Cannot deserialize token!", e);
        }
    }

    private SecretBox buildSecretBox() {
        String tokenKey = pingService.getLastPingResponse().getTokenKey();
        return new SecretBox(Base64.getDecoder().decode(tokenKey));
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Invalid image token.")
    public static final class InvalidTokenException extends IllegalArgumentException {

        public InvalidTokenException(String chapter, String token, String reason) {
            super("Invalid token [" + token + "] for requested chapter [" + chapter + "] | " + reason);
        }

    }

}
