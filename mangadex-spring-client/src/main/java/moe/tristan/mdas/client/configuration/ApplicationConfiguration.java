package moe.tristan.mdas.client.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import moe.tristan.mdas.webutils.logging.OutgoingRequestsMetricsClientInterceptor;
import moe.tristan.mdas.webutils.logging.WebRequestsLoggingConfiguration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {
    ClientConfigurationProperties.class,
    ServerConfigurationProperties.class
})
@Import(WebRequestsLoggingConfiguration.class)
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public String applicationVersion(@Value("${spring.application.version}") String version) {
        return version;
    }

    @Bean
    public RestTemplate restTemplate(
        RestTemplateBuilder restTemplateBuilder,
        OutgoingRequestsMetricsClientInterceptor outgoingRequestsMetricsClientInterceptor
    ) {
        return restTemplateBuilder
            .additionalInterceptors(outgoingRequestsMetricsClientInterceptor)
            .build();
    }

}
