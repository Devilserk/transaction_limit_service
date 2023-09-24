package org.example;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SolvaServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SolvaServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}