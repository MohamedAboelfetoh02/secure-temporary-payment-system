package com.tpsproject.controller;

import com.tpsproject.dao.AuditLogDao;
import com.tpsproject.util.SessionUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);

        if (userId != null) {
            try {
                auditLogDao.log(userId, "LOGOUT", "User signed out.", request.getRemoteAddr(), "USER", userId);
            } catch (SQLException ex) {
                log("Audit log could not be written.", ex);
            }
        }

        SessionUtil.clearSession(request);
        SessionUtil.setFlashMessage(request, "success", "You have been signed out.");
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
