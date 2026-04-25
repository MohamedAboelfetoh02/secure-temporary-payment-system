package com.tpsproject.controller;

import com.tpsproject.service.PaymentWorkflowService;
import com.tpsproject.util.SessionUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "StartPurchaseServlet", urlPatterns = {"/player/purchase/start"})
public class StartPurchaseServlet extends HttpServlet {

    private final PaymentWorkflowService paymentWorkflowService = new PaymentWorkflowService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);
        String packageIdValue = request.getParameter("packageId");

        if (userId == null) {
            SessionUtil.setFlashMessage(request, "error", "Please sign in to continue.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int packageId;
        try {
            packageId = Integer.parseInt(packageIdValue);
        } catch (NumberFormatException ex) {
            SessionUtil.setFlashMessage(request, "error", "The selected package could not be started.");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            String referenceCode = paymentWorkflowService.startPurchase(userId, packageId, request.getRemoteAddr());
            SessionUtil.setFlashMessage(request, "success", "Your payment session is ready.");
            response.sendRedirect(request.getContextPath() + "/player/payment-session?reference=" + referenceCode);
        } catch (SQLException ex) {
            SessionUtil.setFlashMessage(request, "error", "The payment session could not be created right now.");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
