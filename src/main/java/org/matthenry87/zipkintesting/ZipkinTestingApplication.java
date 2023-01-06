package org.matthenry87.zipkintesting;

import io.micrometer.tracing.exporter.SpanExportingPredicate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ZipkinTestingApplication {

    public static void main(String[] args) {

        SpringApplication.run(ZipkinTestingApplication.class, args);
    }

    @Bean
    SpanExportingPredicate spanExportingPredicate() {

        return span -> {

            var uri = span.getTags().get("uri");

            return !"/actuator/health".equals(uri)
                    && !"/actuator/health/**".equals(uri)
                    && !"/actuator/prometheus".equals(uri);
        };
    }

}
