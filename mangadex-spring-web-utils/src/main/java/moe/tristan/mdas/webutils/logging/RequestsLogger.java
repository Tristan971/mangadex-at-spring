package moe.tristan.mdas.webutils.logging;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

public class RequestsLogger {

    private static final String INCOMING = "⬅";
    private static final String OUTGOING = "➡";

    private final Logger logger;

    public RequestsLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public long logStarted(HttpServletRequest httpServletRequest) {
        long start = Instant.now().toEpochMilli();
        logger.info("{} {} begin", INCOMING, getMethodAndPath(httpServletRequest));
        return start;
    }

    public long logStarted(HttpRequest clientHttpRequest) {
        long start = Instant.now().toEpochMilli();
        logger.info("{} {} begin", OUTGOING, getMethodAndPath(clientHttpRequest));
        return start;
    }

    public void logFinished(HttpServletRequest request, HttpServletResponse response, long start) {
        long end = Instant.now().toEpochMilli();
        long elapsed = end - start;

        int responseStatus = response.getStatus();
        HttpStatus statusCode = HttpStatus.resolve(responseStatus);
        requireNonNull(statusCode, "null status code resolved for: " + responseStatus);

        if (statusCode.is2xxSuccessful()) {
            logger.info("{} {} {} ({} ms)", OUTGOING, getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is4xxClientError()) {
            logger.warn("{} {} {} ({} ms)", OUTGOING, getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is5xxServerError()) {
            logger.error("{} {} {} ({} ms)", OUTGOING, getMethodAndPath(request), responseStatus, elapsed);
        }
    }

    public void logFinished(HttpRequest request, ClientHttpResponse response, long start) throws IOException {
        long end = Instant.now().toEpochMilli();
        long elapsed = end - start;

        int responseStatus = response.getRawStatusCode();
        HttpStatus statusCode = response.getStatusCode();

        if (statusCode.is2xxSuccessful()) {
            logger.info("{} {} {} ({} ms)", INCOMING, getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is4xxClientError()) {
            logger.warn("{} {} {} ({} ms)", INCOMING, getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is5xxServerError()) {
            logger.error("{} {} {} ({} ms)", INCOMING, getMethodAndPath(request), responseStatus, elapsed);
        }
    }

    private String getMethodAndPath(HttpServletRequest httpServletRequest) {
        return String.format(
            "%s %s://%s%s%s%s",
            httpServletRequest.getMethod(),
            httpServletRequest.isSecure() ? "https" : "http",
            httpServletRequest.getServerName(),
            httpServletRequest.getServerPort(),
            httpServletRequest.getContextPath(),
            httpServletRequest.getServletPath()
        );
    }

    private String getMethodAndPath(HttpRequest httpRequest) {
        return String.format(
            "%s %s",
            httpRequest.getMethod(),
            httpRequest.getURI()
        );
    }

}
