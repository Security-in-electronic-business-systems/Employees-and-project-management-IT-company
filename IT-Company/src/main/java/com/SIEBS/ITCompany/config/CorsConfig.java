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

        registry.addMapping("/api/v1/auth/**")		// dozvoljava cross-origin zahteve ka navedenim putanjama
                .allowedOrigins("https://localhost:3000")	// postavice Access-Control-Allow-Origin header u preflight zahtev
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);		// definise u sekundama koliko dugo se preflight response cuva u browseru

        registry.addMapping("/api/v1/demo/**")
                .allowedOrigins("https://localhost:3000")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);
        registry.addMapping("/api/v1/notif/**")
                .allowedOrigins("https://localhost:3000")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);

        registry.addMapping("/api/v1/user/**")
                .allowedOrigins("https://localhost:3000")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);

        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true);

    }

}
