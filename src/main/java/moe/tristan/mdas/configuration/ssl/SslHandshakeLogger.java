package moe.tristan.mdas.configuration.ssl;

import org.eclipse.jetty.io.ssl.SslHandshakeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SslHandshakeLogger implements SslHandshakeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SslHandshakeLogger.class);

    @Override
    public void handshakeSucceeded(Event event) {
        LOGGER.debug("Success: {}", event);
    }

    @Override
    public void handshakeFailed(Event event, Throwable failure) {
        LOGGER.debug("Failure: {}", event, failure);
    }

}
