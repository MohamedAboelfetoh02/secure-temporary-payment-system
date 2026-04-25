package com.tpsproject.controller;

import com.tpsproject.dao.GameDao;
import com.tpsproject.service.PaymentWorkflowService;
import com.tpsproject.util.SessionUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PlayerDashboardServlet", urlPatterns = {"/player/dashboard"})
public class PlayerDashboardServlet extends HttpServlet {

    private final GameDao gameDao = new GameDao();
    private final PaymentWorkflowService paymentWorkflowService = new PaymentWorkflowService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer userId = SessionUtil.getLoggedInUserId(request);
            request.setAttribute("gameCount", gameDao.countActiveGames());
            request.setAttribute("packageCount", gameDao.countActivePackages());
            if (userId != null) {
                request.setAttribute("historyCount", paymentWorkflowService.getUserHistory(userId).size());
            }
        } catch (SQLException ex) {
            request.setAttribute("dashboardMessage", "Some dashboard details are not available right now.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/player/dashboard.jsp");
        dispatcher.forward(request, response);
    }
}
