package moe.tristan.mdas.client.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final String applicationVersion;

    public HelloController(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    @GetMapping("/")
    public String hello() {
        return "Hi from Mangadex@Spring version " + applicationVersion;
    }

}
