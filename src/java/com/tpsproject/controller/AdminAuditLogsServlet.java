package com.tpsproject.controller;

import com.tpsproject.dao.AuditLogDao;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminAuditLogsServlet", urlPatterns = {"/admin/audit-logs"})
public class AdminAuditLogsServlet extends HttpServlet {

    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("auditLogs", auditLogDao.findRecentLogs(100));
        } catch (SQLException ex) {
            request.setAttribute("auditMessage", "Audit logs could not be loaded right now.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs.jsp");
        dispatcher.forward(request, response);
    }
}
