package moe.tristan.mdas.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import moe.tristan.mdas.configuration.logging.OutgoingRequestsMetricsClientInterceptor;
import moe.tristan.mdas.configuration.logging.WebRequestsLoggingConfiguration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ClientConfigurationProperties.class)
@Import(WebRequestsLoggingConfiguration.class)
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate(
        RestTemplateBuilder restTemplateBuilder,
        OutgoingRequestsMetricsClientInterceptor outgoingRequestsMetricsClientInterceptor
    ) {
        return restTemplateBuilder
            .additionalInterceptors(outgoingRequestsMetricsClientInterceptor)
            .build();
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }

}
