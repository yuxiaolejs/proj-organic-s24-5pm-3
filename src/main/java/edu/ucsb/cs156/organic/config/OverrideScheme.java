package edu.ucsb.cs156.organic.config;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class OverrideScheme implements Filter {

    @Value("${FORCE_BACKEND_THINK_IT_IS_SECURE:false}")
    private boolean FORCE_BACKEND_THINK_IT_IS_SECURE;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (FORCE_BACKEND_THINK_IT_IS_SECURE || true) {
            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(req) {
                @Override
                public String getScheme() {
                    return "https";
                }

                @Override
                public boolean isSecure() {
                    return true;
                }
            };
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
