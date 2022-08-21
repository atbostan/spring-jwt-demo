package com.bossware.jwtdemoapp.core.configurations;

import com.bossware.jwtdemoapp.core.constants.SecurityConstants;
import com.bossware.jwtdemoapp.core.security.JWTAccessDeniedHandler;
import com.bossware.jwtdemoapp.core.security.JWTAuthEntryPoint;
import com.bossware.jwtdemoapp.core.security.filters.JWTAuthFilter;
import com.bossware.jwtdemoapp.persistance.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigurations  {

    private JWTAuthFilter jwtAuthFilter;
    private  JWTAuthEntryPoint entryPoint;
    private  JWTAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfigurations(JWTAuthFilter jwtAuthFilter, JWTAuthEntryPoint entryPoint, JWTAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.entryPoint = entryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService customUserDetailsService() {
        return new UserServiceImpl();
    }

    @Bean
     SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {
        http.csrf().disable().cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and().authorizeRequests().antMatchers(SecurityConstants.PUBLIC_URLS).permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(entryPoint)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return  http.build();
    }





}
