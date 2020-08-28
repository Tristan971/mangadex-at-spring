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

    private final Logger logger;

    public RequestsLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public long logStarted(HttpServletRequest httpServletRequest) {
        long start = Instant.now().toEpochMilli();
        logger.info("{} begin", getMethodAndPath(httpServletRequest));
        return start;
    }

    public long logStarted(HttpRequest clientHttpRequest) {
        long start = Instant.now().toEpochMilli();
        logger.info("{} begin", getMethodAndPath(clientHttpRequest));
        return start;
    }

    public void logFinished(HttpServletRequest request, HttpServletResponse response, long start) {
        long end = Instant.now().toEpochMilli();
        long elapsed = end - start;

        int responseStatus = response.getStatus();
        HttpStatus statusCode = HttpStatus.resolve(responseStatus);
        requireNonNull(statusCode, "null status code resolved for: " + responseStatus);

        if (statusCode.is2xxSuccessful()) {
            logger.info("{} {} ({} ms)", getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is4xxClientError()) {
            logger.warn("{} {} ({} ms)", getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is5xxServerError()) {
            logger.error("{} {} ({} ms)", getMethodAndPath(request), responseStatus, elapsed);
        }
    }

    public void logFinished(HttpRequest request, ClientHttpResponse response, long start) throws IOException {
        long end = Instant.now().toEpochMilli();
        long elapsed = end - start;

        int responseStatus = response.getRawStatusCode();
        HttpStatus statusCode = response.getStatusCode();

        if (statusCode.is2xxSuccessful()) {
            logger.info("{} {} ({} ms)", getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is4xxClientError()) {
            logger.warn("{} {} ({} ms)", getMethodAndPath(request), responseStatus, elapsed);
        } else if (statusCode.is5xxServerError()) {
            logger.error("{} {} ({} ms)", getMethodAndPath(request), responseStatus, elapsed);
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
