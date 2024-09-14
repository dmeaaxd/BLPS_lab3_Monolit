package org.example.blps_lab3_monolit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.ignoringRequestMatchers("/**"))
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.POST, "/clients/auth").permitAll())
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.POST, "/clients/register").permitAll())
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.POST, "/clients/request-change-password").permitAll())
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.PUT, "/clients/change-password").permitAll())
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.GET, "/shops/**").permitAll())
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.GET, "/categories/**").permitAll())
                .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.GET, "/shop_discounts/**").permitAll())
                .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider);
        return http.build();
    }
}
