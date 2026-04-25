package com.tpsproject.controller;

import com.tpsproject.service.PaymentWorkflowService;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminTransactionsServlet", urlPatterns = {"/admin/transactions"})
public class AdminTransactionsServlet extends HttpServlet {

    private final PaymentWorkflowService paymentWorkflowService = new PaymentWorkflowService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("history", paymentWorkflowService.getAllHistory());
        } catch (SQLException ex) {
            request.setAttribute("historyMessage", "Transactions could not be loaded right now.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin/transactions.jsp");
        dispatcher.forward(request, response);
    }
}
