package moe.tristan.mdas.client.service.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ImageRequestReferrerValidator {

    private static final Pattern MANGADEX_HOST_MATCHER = Pattern.compile(
        // a subdomain followed by a dot (if any), then either mangadex.org or mangadex.network
        "^(.+[.])?mangadex(\\.org|\\.network)$"
    );

    public void validate(HttpServletRequest request) {
        String referrer = request.getHeader(HttpHeaders.REFERER);

        if (referrer == null || "".equals(referrer)) {
            return;
        }

        try {
            URI referrerUri = new URI(referrer);
            String host = referrerUri.getHost();
            if (host == null) {
                throw new IllegalArgumentException("Invalid referrer didn't have a host for " + referrer);
            }

            if (!MANGADEX_HOST_MATCHER.matcher(host).find()) {
                throw new IllegalArgumentException("Invalid Referrer header had unexpected host for " + referrer);
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid Referrer header was present but not a URI for " + referrer, e);
        }
    }

}
