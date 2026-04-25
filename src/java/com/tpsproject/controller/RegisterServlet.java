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

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + resolveDashboardPath(request));
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = readValue(request.getParameter("username"));
        String email = readValue(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        request.setAttribute("username", username);
        request.setAttribute("email", email);

        String validationMessage = validateRegistration(username, email, password, confirmPassword);
        if (validationMessage != null) {
            request.setAttribute("errorMessage", validationMessage);
            forwardToRegister(request, response);
            return;
        }

        try {
            if (userDao.usernameExists(username)) {
                request.setAttribute("errorMessage", "That username is already in use.");
                forwardToRegister(request, response);
                return;
            }

            if (userDao.emailExists(email)) {
                request.setAttribute("errorMessage", "That email address is already registered.");
                forwardToRegister(request, response);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.hashPassword(password));

            int userId = userDao.createPlayer(user);
            writeAuditLog(userId, "REGISTER", "New player account created.", request.getRemoteAddr(), "USER", userId);

            SessionUtil.setFlashMessage(request, "success", "Account created successfully. You can sign in now.");
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "The account could not be created right now. Please try again.");
            forwardToRegister(request, response);
        }
    }

    private String validateRegistration(String username, String email, String password, String confirmPassword) {
        if (username == null || username.length() < 3 || username.length() > 30) {
            return "Username must be between 3 and 30 characters.";
        }

        if (email == null || email.length() > 100 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Please enter a valid email address.";
        }

        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        return null;
    }

    private String readValue(String value) {
        return value == null ? null : value.trim();
    }

    private void forwardToRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp");
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
        String role = SessionUtil.getLoggedInRole(request);
        return "ADMIN".equals(role) ? "/admin/dashboard" : "/player/dashboard";
    }
}
