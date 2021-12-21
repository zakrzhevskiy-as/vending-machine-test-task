package com.sbt.pprb.qa.test_task.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${system.auth.username}")
    private String username;
    @Value("${system.auth.password}")
    private String password;
    private String failureUrl = "/login?error";

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(username)
                .password(passwordEncoder.encode(password))
                .authorities("ROLE_USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/**/*.{js,css,html}", "**/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().failureUrl(failureUrl).permitAll()
                .and()
                .logout().logoutUrl("/logout").invalidateHttpSession(true).permitAll()
                .and()
                .addFilterBefore(authHintFilter(), DefaultLoginPageGeneratingFilter.class)
                .httpBasic();
    }

    public AuthHintFilter authHintFilter() {
        return new AuthHintFilter()
                .username(username)
                .password(password)
                .failureUrl(failureUrl);
    }
}
