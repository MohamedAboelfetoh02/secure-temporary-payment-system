package com.tpsproject.controller;

import com.tpsproject.model.PaymentActionResult;
import com.tpsproject.model.PaymentInputData;
import com.tpsproject.model.PaymentSessionDetails;
import com.tpsproject.service.PaymentWorkflowService;
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

@WebServlet(name = "ConfirmPaymentServlet", urlPatterns = {"/player/payment/confirm"})
public class ConfirmPaymentServlet extends HttpServlet {

    private final PaymentWorkflowService paymentWorkflowService = new PaymentWorkflowService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);
        String referenceCode = request.getParameter("reference");
        PaymentInputData paymentInputData = buildPaymentInputData(request);

        if (userId == null || referenceCode == null || referenceCode.trim().isEmpty()) {
            SessionUtil.setFlashMessage(request, "error", "The payment session could not be updated.");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            PaymentActionResult result = paymentWorkflowService.confirmPayment(referenceCode.trim(), userId, paymentInputData, request.getRemoteAddr());

            if ("error".equals(result.getMessageType()) && result.getPaymentSessionDetails() != null
                    && result.getPaymentSessionDetails().isPending()) {
                request.setAttribute("paymentSession", result.getPaymentSessionDetails());
                request.setAttribute("paymentMethods", PaymentMethodUtil.getAvailableMethods());
                request.setAttribute("errorMessage", result.getMessage());
                attachInputValues(request, paymentInputData);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/player/payment-session.jsp");
                dispatcher.forward(request, response);
                return;
            }

            SessionUtil.setFlashMessage(request, result.getMessageType(), result.getMessage());
            response.sendRedirect(request.getContextPath() + "/player/payment-session?reference=" + referenceCode.trim());
        } catch (SQLException ex) {
            SessionUtil.setFlashMessage(request, "error", "The payment could not be confirmed right now.");
            response.sendRedirect(request.getContextPath() + "/player/payment-session?reference=" + referenceCode.trim());
        }
    }

    private PaymentInputData buildPaymentInputData(HttpServletRequest request) {
        PaymentInputData paymentInputData = new PaymentInputData();
        paymentInputData.setPaymentMethod(request.getParameter("paymentMethod"));
        paymentInputData.setPaypalEmail(request.getParameter("paypalEmail"));
        paymentInputData.setCardholderName(request.getParameter("cardholderName"));
        paymentInputData.setCardNumber(request.getParameter("cardNumber"));
        paymentInputData.setExpiryDate(request.getParameter("expiryDate"));
        paymentInputData.setCvv(request.getParameter("cvv"));
        paymentInputData.setWalletId(request.getParameter("walletId"));
        return paymentInputData;
    }

    private void attachInputValues(HttpServletRequest request, PaymentInputData paymentInputData) {
        request.setAttribute("selectedPaymentMethod", paymentInputData.getPaymentMethod());
        request.setAttribute("paypalEmail", paymentInputData.getPaypalEmail());
        request.setAttribute("cardholderName", paymentInputData.getCardholderName());
        request.setAttribute("cardNumber", paymentInputData.getCardNumber());
        request.setAttribute("expiryDate", paymentInputData.getExpiryDate());
        request.setAttribute("walletId", paymentInputData.getWalletId());
    }
}
