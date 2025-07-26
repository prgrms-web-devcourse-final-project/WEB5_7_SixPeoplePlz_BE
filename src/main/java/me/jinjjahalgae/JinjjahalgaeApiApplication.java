package me.jinjjahalgae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@ConfigurationPropertiesScan
@EnableRetry
public class JinjjahalgaeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JinjjahalgaeApiApplication.class, args);
    }

}
