package com.tpsproject.controller;

import com.tpsproject.dao.AuditLogDao;
import com.tpsproject.dao.UserDao;
import com.tpsproject.model.User;
import com.tpsproject.util.PasswordUtil;
import com.tpsproject.util.SessionUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + resolveDashboardPath(request));
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String identifier = readValue(request.getParameter("identifier"));
        String password = request.getParameter("password");

        request.setAttribute("identifier", identifier);

        if (identifier == null || identifier.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Enter your username or email and password.");
            forwardToLogin(request, response);
            return;
        }

        try {
            User user = userDao.findByUsernameOrEmail(identifier);

            if (user == null || !"ACTIVE".equals(user.getStatus())
                    || !PasswordUtil.matches(password, user.getPasswordHash())) {
                request.setAttribute("errorMessage", "The sign in details are not correct.");
                forwardToLogin(request, response);
                return;
            }

            userDao.updateLastLogin(user.getId());
            SessionUtil.startUserSession(request, user);
            writeAuditLog(user.getId(), "LOGIN", "User signed in successfully.", request.getRemoteAddr(), "USER", user.getId());
            response.sendRedirect(request.getContextPath() + resolveDashboardPath(user.getRole()));
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "The sign in request could not be completed. Please try again.");
            forwardToLogin(request, response);
        }
    }

    private String readValue(String value) {
        return value == null ? null : value.trim();
    }

    private void forwardToLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp");
        dispatcher.forward(request, response);
    }

    private void writeAuditLog(Integer userId, String actionType, String description, String ipAddress,
            String targetType, Integer targetId) {
        try {
            auditLogDao.log(userId, actionType, description, ipAddress, targetType, targetId);
        } catch (SQLException ex) {
            log("Audit log could not be written.", ex);
        }
    }

    private String resolveDashboardPath(HttpServletRequest request) {
        return resolveDashboardPath(SessionUtil.getLoggedInRole(request));
    }

    private String resolveDashboardPath(String role) {
        return "ADMIN".equals(role) ? "/admin/dashboard" : "/player/dashboard";
    }
}
