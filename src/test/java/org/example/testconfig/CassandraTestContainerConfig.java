package org.example.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class CassandraTestContainerConfig {

    @Bean
    public static CassandraContainer<?> cassandraContainer() {
        CassandraContainer<?> cassandraContainer = new CassandraContainer<>(DockerImageName.parse("cassandra:latest"))
                .withExposedPorts(9042)
                .withInitScript("init-cassandra.cql");
        cassandraContainer.withCreateContainerCmdModifier(cmd -> {
            cmd.withEntrypoint("cassandra", "-f");
        });
        return cassandraContainer;
    }
}