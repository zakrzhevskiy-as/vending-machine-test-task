package com.sbt.pprb.qa.test_task.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.APP_INFO;
import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.APP_INFO_PING;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${system.rest.auth.username}")
    private String username;
    @Value("${system.rest.auth.password}")
    private String password;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String failureUrl = "/login?error";
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**/*.{js,css,html}", "**/favicon.ico", "**" + APP_INFO + APP_INFO_PING).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().failureUrl(failureUrl).permitAll()
                .and()
                .logout().logoutUrl("/logout").invalidateHttpSession(true).permitAll()
                .and()
                .addFilterAfter(new RedirectToRegisterFilter(username), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AuthHintFilter(failureUrl, username, password), DefaultLoginPageGeneratingFilter.class)
                .httpBasic();
        http.headers().frameOptions().disable();
    }
}
