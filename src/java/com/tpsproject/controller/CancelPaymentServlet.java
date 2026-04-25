package com.tpsproject.controller;

import com.tpsproject.model.PaymentActionResult;
import com.tpsproject.model.PaymentInputData;
import com.tpsproject.service.PaymentWorkflowService;
import com.tpsproject.util.SessionUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CancelPaymentServlet", urlPatterns = {"/player/payment/cancel"})
public class CancelPaymentServlet extends HttpServlet {

    private final PaymentWorkflowService paymentWorkflowService = new PaymentWorkflowService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);
        String referenceCode = request.getParameter("reference");
        PaymentInputData paymentInputData = new PaymentInputData();
        paymentInputData.setPaymentMethod(request.getParameter("paymentMethod"));

        if (userId == null || referenceCode == null || referenceCode.trim().isEmpty()) {
            SessionUtil.setFlashMessage(request, "error", "The payment session could not be updated.");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            PaymentActionResult result = paymentWorkflowService.cancelPayment(referenceCode.trim(), userId, paymentInputData, request.getRemoteAddr());
            SessionUtil.setFlashMessage(request, result.getMessageType(), result.getMessage());
            response.sendRedirect(request.getContextPath() + "/player/payment-session?reference=" + referenceCode.trim());
        } catch (SQLException ex) {
            SessionUtil.setFlashMessage(request, "error", "The payment could not be cancelled right now.");
            response.sendRedirect(request.getContextPath() + "/player/payment-session?reference=" + referenceCode.trim());
        }
    }
}
