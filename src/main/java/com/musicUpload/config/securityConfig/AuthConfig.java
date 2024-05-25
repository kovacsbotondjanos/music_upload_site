package com.musicUpload.config.securityConfig;

import com.musicUpload.dataHandler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AuthConfig  {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
//                .csrf(AbstractHttpConfigurer::disable)
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/public/**").permitAll();
                    registry.requestMatchers("/register", "/album/**", "/home", "/images/**", "/music/**", "/users", "/songs/**", "/login").permitAll();
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
//                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(new AuthSuccessHandler())
                        .failureHandler(new AuthFailureHandler())
                        .permitAll())
                .logout(LogoutConfigurer::permitAll)
                //TODO: sessionmanager
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Allow your frontend origin
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public UserService userDetailsService() {
        return userService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}