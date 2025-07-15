package com.emsi.association.config;

import com.emsi.association.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**", "/register", "/login").permitAll()
                        .requestMatchers("/events", "/posts/**", "/gallery" ,"/comments").hasAnyRole("MEMBER", "ADMIN")
                        .requestMatchers("/dashboard", "/admin/**", "/gallery/**","/events/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL de déconnexion (POST)
                        .logoutSuccessUrl("/login?logout") // Redirection après déconnexion
                        .invalidateHttpSession(true) // Invalider la session
                        .deleteCookies("JSESSIONID") // Supprimer le cookie de session
                        .clearAuthentication(true) // Effacer l'authentification
                        .permitAll()
                )
                 // Désactiver CSRF pour les API REST (optionnel)
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
        return username -> memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}