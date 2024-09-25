package com.fjb.sunrise;

import com.fjb.sunrise.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(StorageProperties.class)
public class SunRiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SunRiseApplication.class, args);
    }

}
