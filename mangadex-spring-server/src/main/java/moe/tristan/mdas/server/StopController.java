package moe.tristan.mdas.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import moe.tristan.mdas.api.stop.StopRequest;

@RestController
public class StopController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopController.class);

    @PostMapping("/stop")
    void stop(StopRequest stopRequest) {
        LOGGER.info("Client with {} requested stop.", stopRequest.getSecret());
    }

}
