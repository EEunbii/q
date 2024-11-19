package com.example.shoppro.config;


import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@WebListener
public class SecurityConfig {

    @Bean





}
