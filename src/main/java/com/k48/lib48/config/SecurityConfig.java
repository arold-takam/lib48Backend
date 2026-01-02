package com.k48.lib48.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService;
	
	@Value("${ALLOWED_ORIGINS:http://localhost:5500}")
	private String allowedOrigins;
	
	public SecurityConfig(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
//	------------------------------------------------------------------------------------
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			       .csrf(AbstractHttpConfigurer::disable)
			       .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 🔑 ajout ici
			       .authorizeHttpRequests(auth -> auth
				                                      .requestMatchers(
					                                      "/user/login",
					                                      "/user/register",
														  "user/get/byMail",
					                                      "/swagger-ui/**",
					                                      "/v3/api-docs/**"
				                                      ).permitAll()
				                                      .anyRequest().authenticated()
			       )
			       .httpBasic(httpBasic -> {})
			       .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			       .build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		
		return authenticationManagerBuilder.build();
	}
	
	@Bean
	public OpenAPI customOpenAPI(){
		final String SECURITY_SCHEME_BASIC = "basic-auth";
		
		return new OpenAPI()
		       .components(new Components()
			                   .addSecuritySchemes(SECURITY_SCHEME_BASIC, new SecurityScheme()
				                                                     .type(SecurityScheme.Type.HTTP)
				                                                     .scheme("basic")
				                                                     .description("Authentication HTTP Basic"))
			                   .addSecuritySchemes("noauth", new SecurityScheme()
				                                                 .type(SecurityScheme.Type.APIKEY)
				                                                 .in(SecurityScheme.In.HEADER)
				                                                 .name("no-auth")))
			                   .security(Collections.singletonList(new SecurityRequirement().addList("basic-auth")));
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	
}
