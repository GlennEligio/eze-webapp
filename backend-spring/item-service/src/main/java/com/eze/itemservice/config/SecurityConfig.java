package com.eze.itemservice.config;

import com.eze.itemservice.filter.AuthRequestFilter;
import com.eze.itemservice.filter.ExceptionHandlerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;
import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthRequestFilter authRequestFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    public SecurityConfig(AuthRequestFilter authRequestFilter, ExceptionHandlerFilter exceptionHandlerFilter) {
        this.authRequestFilter  = authRequestFilter;
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/*/items", "/api/*/items/*").hasAnyRole("USER", "ADMIN", "SADMIN")
                .anyRequest().hasAnyRole("ADMIN", "SADMIN");
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, AuthRequestFilter.class);
        http.cors();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowedMethods(List.of("*"));
        corsConfig.setAllowedOrigins(List.of("*"));
        corsConfig.setMaxAge(Duration.ofMinutes(10));
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsFilter(source);
    }

}
