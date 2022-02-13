package com.sbt.pprb.qa.test_task.config;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectToRegisterFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            AppUser principal = (AppUser) authentication.getPrincipal();
            if (!(request.getContextPath() + "/login").equals(request.getRequestURI()) &&
                    "qa_engineer".equals(principal.getUsername())) {
                request = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getRequestURI() {
                        return this.getContextPath() + "/register";
                    }
                };
            }
        }
        filterChain.doFilter(request, response);
    }
}
