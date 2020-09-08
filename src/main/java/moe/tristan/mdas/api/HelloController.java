package moe.tristan.mdas.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import moe.tristan.mdas.configuration.ClientInformation;

@RestController
public class HelloController {

    private final ClientInformation clientInformation;

    public HelloController(ClientInformation clientInformation) {
        this.clientInformation = clientInformation;
    }

    @GetMapping("/")
    public ClientInformation hello() {
        return clientInformation;
    }

}
