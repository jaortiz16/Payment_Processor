package com.banquito.cards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir todos los orígenes durante desarrollo
        config.addAllowedOrigin("http://localhost:3000");
        
        // Permitir todos los métodos HTTP
        config.addAllowedMethod("*");
        
        // Permitir todos los headers
        config.addAllowedHeader("*");
        
        // Permitir credenciales
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 