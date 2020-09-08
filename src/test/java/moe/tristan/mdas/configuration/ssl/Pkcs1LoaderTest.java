package moe.tristan.mdas.configuration.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

class Pkcs1LoaderTest {

    @Test
    void convertsFromPkcs1ToPkcs8() throws IOException {
        try (InputStream pkcs1 = getClass().getResourceAsStream("pkcs1.pem")) {
            String pkcs1Bytes = StreamUtils.copyToString(pkcs1, StandardCharsets.UTF_8);
            Pkcs1Loader.parse(pkcs1Bytes);
        }
    }

}
