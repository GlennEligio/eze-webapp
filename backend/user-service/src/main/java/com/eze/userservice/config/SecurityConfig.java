package com.eze.userservice.config;

import com.eze.userservice.filter.JwtRequestFilter;
import com.eze.userservice.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl service;
    private final PasswordEncoder passwordEncoder;
    private final JwtRequestFilter filter;

    public SecurityConfig(UserServiceImpl service, PasswordEncoder passwordEncoder, JwtRequestFilter jwtRequestFilter) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.filter = jwtRequestFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/api/v1/users/login", "/api/v1/users/register", "/api/v1/users/refresh/**", "/api/v1/users/validate/**", "/api/v1/users/*/avatar").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/users", "/api/v1/users/*").hasAnyRole("USER", "ADMIN", "SADMIN")
                .anyRequest().hasAnyRole("ADMIN", "SADMIN");
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        http.cors();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("GET", "PUT", "POST", "DELETE", "OPTIONS"));
        corsConfiguration.setExposedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setMaxAge(Duration.ofMinutes(10));
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
