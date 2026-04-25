package com.tpsproject.controller;

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

@WebServlet(name = "PlayerHistoryServlet", urlPatterns = {"/player/history"})
public class PlayerHistoryServlet extends HttpServlet {

    private final PaymentWorkflowService paymentWorkflowService = new PaymentWorkflowService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);

        if (userId == null) {
            SessionUtil.setFlashMessage(request, "error", "Please sign in to continue.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            request.setAttribute("history", paymentWorkflowService.getUserHistory(userId));
        } catch (SQLException ex) {
            request.setAttribute("historyMessage", "Your payment history could not be loaded right now.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/player/history.jsp");
        dispatcher.forward(request, response);
    }
}
