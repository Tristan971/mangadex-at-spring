package moe.tristan.mdas.service.security;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import moe.tristan.mdas.model.ImageRequest;
import moe.tristan.mdas.service.ping.PingService;

@Component
public class ImageTokenValidator {

    private final PingService pingService;

    public ImageTokenValidator(PingService pingService) {
        this.pingService = pingService;
    }

    public void validateToken(ImageRequest imageRequest, String token) {
        String tokenKey = pingService.getLastPingResponse().getTokenKey();
        byte[] tokenArray = Base64.getDecoder().decode(token);

        // first 24 bytes
        byte[] nonce = Arrays.copyOfRange(tokenArray, 0, 24);

        // the rest
        byte[] cipherText = Arrays.copyOfRange(tokenArray, 24, tokenArray.length);


    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Invalid image token.")
    public static final class InvalidTokenException extends IllegalArgumentException {

        public InvalidTokenException(ImageRequest request, String token, String reason) {
            super("Invalid token [" + token + "] for requested image [" + request + "] | " + reason);
        }

    }

}
