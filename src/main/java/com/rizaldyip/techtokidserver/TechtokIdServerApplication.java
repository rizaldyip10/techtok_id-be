package com.rizaldyip.techtokidserver;

import com.rizaldyip.techtokidserver.configs.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
public class TechtokIdServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechtokIdServerApplication.class, args);
    }

}
