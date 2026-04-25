package com.tpsproject.filter;

import com.tpsproject.util.SessionUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "RoleAccessFilter", urlPatterns = {"/player/*", "/admin/*"})
public class RoleAccessFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String role = SessionUtil.getLoggedInRole(httpRequest);
        String requestUri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        if (role == null) {
            SessionUtil.setFlashMessage(httpRequest, "error", "Please sign in to continue.");
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        if (requestUri.startsWith(contextPath + "/admin") && !"ADMIN".equals(role)) {
            SessionUtil.setFlashMessage(httpRequest, "error", "You do not have access to that page.");
            httpResponse.sendRedirect(contextPath + "/home");
            return;
        }

        if (requestUri.startsWith(contextPath + "/player") && !"PLAYER".equals(role)) {
            SessionUtil.setFlashMessage(httpRequest, "error", "You do not have access to that page.");
            httpResponse.sendRedirect(contextPath + "/home");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
