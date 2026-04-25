package com.tpsproject.controller;

import com.tpsproject.dao.GameDao;
import com.tpsproject.dao.PaymentWorkflowDao;
import com.tpsproject.dao.UserDao;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final GameDao gameDao = new GameDao();
    private final PaymentWorkflowDao paymentWorkflowDao = new PaymentWorkflowDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("playerCount", userDao.countActiveUsersByRole("PLAYER"));
            request.setAttribute("adminCount", userDao.countActiveUsersByRole("ADMIN"));
            request.setAttribute("gameCount", gameDao.countActiveGames());
            request.setAttribute("packageCount", gameDao.countActivePackages());
            request.setAttribute("sessionCount", paymentWorkflowDao.countAllPaymentSessions());
            request.setAttribute("pendingSessionCount", paymentWorkflowDao.countPendingPaymentSessions());
        } catch (SQLException ex) {
            request.setAttribute("dashboardMessage", "Some dashboard details are not available right now.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp");
        dispatcher.forward(request, response);
    }
}
