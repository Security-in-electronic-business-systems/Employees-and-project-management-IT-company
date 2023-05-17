package com.SIEBS.ITCompany.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/auth/*")		// dozvoljava cross-origin zahteve ka navedenim putanjama
                .allowedOrigins("https://localhost:3000")	// postavice Access-Control-Allow-Origin header u preflight zahtev
                .allowedMethods("*")
                .maxAge(3600);		// definise u sekundama koliko dugo se preflight response cuva u browseru
    }

}