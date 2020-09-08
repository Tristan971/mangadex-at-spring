package moe.tristan.mdas.service.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest(classes = ImageRequestReferrerValidator.class)
class ImageRequestReferrerValidatorTest {

    @Autowired
    private ImageRequestReferrerValidator imageRequestReferrerValidator;

    @Test
    void succeedsOnAbsentReferrer() {
        HttpServletRequest request = new MockHttpServletRequest();
        imageRequestReferrerValidator.validate(request);
    }

    @Test
    void succeedsOnEmptyReferrer() {
        imageRequestReferrerValidator.validate(requestWithReferrer(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "this-doesnt-have-a-host",
        "https://not a uri! no, really this isn't a valid uri you know",
        "https://notmangadex.org",
        "https://pulling-tricks-mangadex.org",
        "https://mangadex.org-pulling-tricks"
    })
    void failsOnInvalidReferrers(String referrer) {
        var request = requestWithReferrer(referrer);
        assertThatThrownBy(() -> imageRequestReferrerValidator.validate(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(referrer);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://mangadex.org",
        "https://subdomain.mangadex.org",
        "https://mangadex.network",
        "https://subdomain.mangadex.network"
    })
    void succeedsOnReferrerHosts(String host) {
        var request = requestWithReferrer(host);
        imageRequestReferrerValidator.validate(request);
    }

    private MockHttpServletRequest requestWithReferrer(String referrer) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.REFERER, referrer);
        return request;
    }

}
