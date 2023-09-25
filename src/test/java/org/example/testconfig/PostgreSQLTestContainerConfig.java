package org.example.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class PostgreSQLTestContainerConfig {

    @Bean
    public static PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
                .withDatabaseName("mydb")
                .withUsername("myuser")
                .withPassword("mypassword")
                .withReuse(true);
    }
}
