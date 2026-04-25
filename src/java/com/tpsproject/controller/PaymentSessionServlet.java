package com.tpsproject.controller;

import com.tpsproject.model.PaymentSessionDetails;
import com.tpsproject.service.PaymentWorkflowService;
import com.tpsproject.util.GamePresentationUtil;
import com.tpsproject.util.PaymentMethodUtil;
import com.tpsproject.util.SessionUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PaymentSessionServlet", urlPatterns = {"/player/payment-session"})
public class PaymentSessionServlet extends HttpServlet {

    private final PaymentWorkflowService paymentWorkflowService = new PaymentWorkflowService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);
        String referenceCode = request.getParameter("reference");

        if (userId == null || referenceCode == null || referenceCode.trim().isEmpty()) {
            SessionUtil.setFlashMessage(request, "error", "The payment session could not be opened.");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            PaymentSessionDetails paymentSession = paymentWorkflowService.getPaymentSessionDetails(
                    referenceCode.trim(), userId, request.getRemoteAddr());

            if (paymentSession == null) {
                SessionUtil.setFlashMessage(request, "error", "That payment session could not be found.");
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            request.setAttribute("paymentSession", paymentSession);
            GamePresentationUtil.applyToPaymentSession(paymentSession);
            request.setAttribute("paymentMethods", PaymentMethodUtil.getAvailableMethods());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/player/payment-session.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException ex) {
            SessionUtil.setFlashMessage(request, "error", "The payment session could not be loaded right now.");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
