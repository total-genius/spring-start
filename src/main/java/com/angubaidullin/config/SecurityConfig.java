package com.angubaidullin.config;

import com.angubaidullin.security.JwtAuthenticationFilter;
import com.angubaidullin.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**", "/oauth2/**").permitAll() // Разрешить доступ без аутентификации для JWT и OAuth2 входа
                .antMatchers("account/**").authenticated() // Требуется аутентификация для пользователей
                .antMatchers("/accounts/**").hasRole("ADMIN") // Только администратор
                .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT не использует сессии
                .and()
                .oauth2Login() // Включить OAuth2 Login
                .loginPage("/auth/login") // Страница входа (можно настроить кастомный контроллер)
                .defaultSuccessUrl("/dashboard", true); // После успешного входа перенаправить на dashboard

        // Добавляем фильтр для JWT до стандартной аутентификации
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
