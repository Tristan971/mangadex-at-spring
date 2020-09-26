package moe.tristan.mdas.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final String applicationName;
    private final String applicationVersion;

    public HelloController(
        @Value("${spring.application.name}") String applicationName,
        @Value("${spring.application.version}") String applicationVersion
    ) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    @GetMapping("/")
    public String hello() {
        return applicationName + " " + applicationVersion;
    }

}
