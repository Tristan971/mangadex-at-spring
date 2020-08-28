package moe.tristan.mdas.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import moe.tristan.mdas.api.ping.PingRequest;
import moe.tristan.mdas.api.ping.PingResponse;
import moe.tristan.mdas.api.ping.TlsData;

@RestController
public class PingController {

    @PostMapping("/ping")
    PingResponse ping(@RequestBody PingRequest pingRequest) throws MalformedURLException {
        return PingResponse
            .builder()
            .imageServer("http://localhost:8081")
            .latestBuild("1.2.3")
            .url(new URL("http://localhost:8081"))
            .tokenKey("token")
            .compromised(false)
            .paused(false)
            .tls(
                TlsData
                    .builder()
                    .certificate("certificate")
                    .privateKey("private-key")
                    .createdAt(ZonedDateTime.now(ZoneOffset.UTC))
                    .build()
            )
            .build();
    }

}
