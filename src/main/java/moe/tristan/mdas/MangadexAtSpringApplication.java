package moe.tristan.mdas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import moe.tristan.mdas.configuration.MangadexConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MangadexConfigurationProperties.class)
public class MangadexAtSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangadexAtSpringApplication.class, args);
    }

}
