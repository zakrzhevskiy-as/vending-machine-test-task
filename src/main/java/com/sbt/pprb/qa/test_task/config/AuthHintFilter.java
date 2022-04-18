package com.sbt.pprb.qa.test_task.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthHintFilter extends DefaultLoginPageGeneratingFilter {

    private final String failureUrl;
    private final String username;
    private final String password;

    public AuthHintFilter(String failureUrl, String username, String password) {
        this.failureUrl = failureUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        boolean loginFailed = loginFailed(httpRequest, this.failureUrl);

        if (loginFailed) {
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                String msg = "[401] Bad credentials: Inspect response headers of login page for credentials";
                BadCredentialsException exception = new BadCredentialsException(msg);
                session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
            }

            httpResponse.addHeader("username", username);
            httpResponse.addHeader("password", password);
        }

        super.doFilter(httpRequest, httpResponse, chain);
    }

    private boolean loginFailed(HttpServletRequest request, String url) {
        if (!"GET".equals(request.getMethod()) || url == null) {
            return false;
        }
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');
        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }
        if (request.getQueryString() != null) {
            uri += "?" + request.getQueryString();
        }
        if ("".equals(request.getContextPath())) {
            return uri.equals(url);
        }
        return uri.equals(request.getContextPath() + url);
    }
}
