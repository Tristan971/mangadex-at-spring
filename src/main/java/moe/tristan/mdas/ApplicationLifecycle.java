package moe.tristan.mdas;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import moe.tristan.mdas.configuration.ClientConfigurationProperties;
import moe.tristan.mdas.configuration.ssl.KeyStoreInitializer;
import moe.tristan.mdas.service.ping.PingService;
import moe.tristan.mdas.service.stop.StopService;

@Component
public class ApplicationLifecycle implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLifecycle.class);
    private static final ScheduledExecutorService PING_SERVICE = newSingleThreadScheduledExecutor();

    private final PingService pingService;
    private final StopService stopService;
    private final KeyStoreInitializer keyStoreInitializer;
    private final ClientConfigurationProperties clientConfigurationProperties;

    private final AtomicBoolean running;
    private ScheduledFuture<?> pingDaemon;

    public ApplicationLifecycle(
        PingService pingService,
        StopService stopService,
        KeyStoreInitializer keyStoreInitializer,
        ClientConfigurationProperties clientConfigurationProperties
    ) {
        this.pingService = pingService;
        this.stopService = stopService;
        this.keyStoreInitializer = keyStoreInitializer;
        this.clientConfigurationProperties = clientConfigurationProperties;
        this.running = new AtomicBoolean(false);
    }

    @Override
    public void start() {
        LOGGER.info("Application is starting.");
        pingService.ping();
        keyStoreInitializer.injectCertificates(pingService.getLastTlsData());
        running.set(true);

        this.pingDaemon = PING_SERVICE.scheduleAtFixedRate(
            () -> {
                try {
                    pingService.ping();
                } catch (Throwable e) {
                    LOGGER.error("Could not ping control server.", e);
                }
            },
            15,  // initial delay (since we manually invoke it earlier, this is the second request)
            15,  // executed every X seconds hereafter
            TimeUnit.SECONDS
        );
    }

    @Override
    public void stop() {
        LOGGER.info("Application is shutting down.");
        try {
            if (pingDaemon != null) {
                pingDaemon.cancel(false);
                stopService.stop();
                LOGGER.info("Server acknowledged stop request! Shutting down...");
                newSingleThreadScheduledExecutor().schedule(
                    () -> LOGGER.info("Done waiting for graceful shutdown."),
                    clientConfigurationProperties.getGracefulShutdownSeconds(),
                    TimeUnit.SECONDS
                ).get();
            }
        } catch (Throwable e) {
            LOGGER.info("Failed graceful shutdown!", e);
        }
        running.set(false);
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

}
