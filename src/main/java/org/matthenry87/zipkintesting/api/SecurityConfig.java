package org.matthenry87.zipkintesting.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
class SecurityConfig {

    private final MonitoringConfigProps monitoringConfigProps;

    private static final String PROMETHEUS = "prometheus";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(GET, "/actuator/prometheus").hasAuthority(PROMETHEUS)
                        .anyRequest().permitAll())
                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        var userDetails = User.builder()
                .username(PROMETHEUS)
                .password(passwordEncoder.encode(monitoringConfigProps.getPrometheusPassword()))
                .authorities(PROMETHEUS)
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    PasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

}

@Getter
@Setter
@Component
@ConfigurationProperties("monitoring")
class MonitoringConfigProps {

    private String prometheusPassword;

}
