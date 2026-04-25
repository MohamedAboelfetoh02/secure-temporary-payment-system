<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.tpsproject.model.PaymentSessionDetails" %>
<%@ page import="com.tpsproject.util.SessionUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Payment Session | TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
        <script src="<%= request.getContextPath() %>/assets/js/site.js" defer></script>
    </head>
    <body>
        <%
            PaymentSessionDetails paymentSession = (PaymentSessionDetails) request.getAttribute("paymentSession");
            String username = (String) session.getAttribute(SessionUtil.USERNAME);
            String flashType = (String) session.getAttribute(SessionUtil.FLASH_TYPE);
            String flashMessage = (String) session.getAttribute(SessionUtil.FLASH_MESSAGE);
            String[] paymentMethods = (String[]) request.getAttribute("paymentMethods");
            String errorMessage = (String) request.getAttribute("errorMessage");
            String selectedPaymentMethod = (String) request.getAttribute("selectedPaymentMethod");
            String paypalEmail = (String) request.getAttribute("paypalEmail");
            String cardholderName = (String) request.getAttribute("cardholderName");
            String cardNumber = (String) request.getAttribute("cardNumber");
            String expiryDate = (String) request.getAttribute("expiryDate");
            String walletId = (String) request.getAttribute("walletId");
            if (flashMessage != null) {
                session.removeAttribute(SessionUtil.FLASH_TYPE);
                session.removeAttribute(SessionUtil.FLASH_MESSAGE);
            }
            if (selectedPaymentMethod == null) {
                selectedPaymentMethod = paymentSession.getPaymentMethod();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        %>
        <div class="page-shell">
            <div class="container">
                <div class="topbar">
                    <div class="brand">
                        TPS Version 1.0
                        <small>Temporary payment session</small>
                    </div>
                    <div class="nav-links">
                        <a class="button-ghost" href="<%= request.getContextPath() %>/home">Catalog</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/player/dashboard"><%= username %></a>
                        <form class="inline-form" method="post" action="<%= request.getContextPath() %>/logout">
                            <button class="button-ghost" type="submit">Sign out</button>
                        </form>
                    </div>
                </div>

                <% if (flashMessage != null) { %>
                    <div class="alert <%= "success".equals(flashType) ? "alert-success" : "alert-error" %>">
                        <%= flashMessage %>
                    </div>
                <% } %>

                <% if (errorMessage != null) { %>
                    <div class="alert alert-error"><%= errorMessage %></div>
                <% } %>

                <div class="panel">
                    <div class="payment-header">
                        <div>
                            <h1 class="section-title">Payment session</h1>
                            <p class="muted">Review the selected package, confirm the payment method, and complete the session before the timer ends.</p>
                        </div>
                        <div class="status-pill status-<%= paymentSession.getSessionStatus().toLowerCase() %>">
                            <%= paymentSession.getSessionStatus() %>
                        </div>
                    </div>

                    <div class="payment-grid">
                        <div class="payment-card payment-card-highlight">
                            <div class="payment-label">Reference code</div>
                            <div class="reference-code"><%= paymentSession.getReferenceCode() %></div>
                            <div class="muted">This one-time code identifies the current payment window and cannot be reused after the session closes.</div>
                        </div>

                        <div class="payment-card payment-game-card"<%= paymentSession.getGameImagePath() == null ? "" : " style=\"background-image: linear-gradient(180deg, rgba(15,23,42,0.08), rgba(15,23,42,0.18)), url('" + paymentSession.getGameImagePath() + "');\"" %>>
                            <div class="payment-label">Game</div>
                            <div class="payment-value"><%= paymentSession.getGameTitle() %></div>
                            <div class="muted"><%= paymentSession.getGameDescription() %></div>
                            <% if (paymentSession.getGameOfficialUrl() != null) { %>
                                <div class="link-row"><a href="<%= paymentSession.getGameOfficialUrl() %>" target="_blank" rel="noopener noreferrer">Visit official game page</a></div>
                            <% } %>
                        </div>

                        <div class="payment-card">
                            <div class="payment-label">Package</div>
                            <div class="payment-value"><%= paymentSession.getPackageName() %></div>
                            <div class="muted"><%= paymentSession.getCurrencyAmount() %> <%= paymentSession.getCurrencyName() %></div>
                        </div>

                        <div class="payment-card">
                            <div class="payment-label">Amount</div>
                            <div class="payment-value">$<%= paymentSession.getAmount() %></div>
                            <div class="muted">Transaction code: <%= paymentSession.getTransactionCode() %></div>
                        </div>
                    </div>

                    <div class="payment-summary-grid">
                        <div class="payment-card">
                            <div class="payment-label">Started</div>
                            <div class="payment-value-small"><%= formatter.format(paymentSession.getCreatedAt()) %></div>
                        </div>
                        <div class="payment-card">
                            <div class="payment-label">Expires</div>
                            <div class="payment-value-small"><%= formatter.format(paymentSession.getExpiresAt()) %></div>
                        </div>
                        <div class="payment-card">
                            <div class="payment-label">Payment method</div>
                            <div class="payment-value-small"><%= paymentSession.getPaymentMethod() == null ? "Not selected yet" : paymentSession.getPaymentMethod() %></div>
                            <div class="muted"><%= paymentSession.getPaymentDetailSummary() == null ? "Only minimal safe metadata will be retained after confirmation." : paymentSession.getPaymentDetailSummary() %></div>
                        </div>
                        <div class="payment-card">
                            <div class="payment-label">Time remaining</div>
                            <div class="payment-value-small">
                                <%= paymentSession.isPending() ? paymentSession.getSecondsRemaining() + " seconds" : "Session closed" %>
                            </div>
                        </div>
                    </div>

                    <% if (paymentSession.isPending()) { %>
                        <div class="alert alert-info">
                            This payment window stays open for 5 minutes. If you leave it unfinished, the session closes automatically and the reference code becomes invalid.
                        </div>

                        <form method="post" action="<%= request.getContextPath() %>/player/payment/confirm" data-payment-form>
                            <input type="hidden" name="reference" value="<%= paymentSession.getReferenceCode() %>">
                            <div class="payment-method-box">
                                <div class="payment-label">Choose a payment method</div>
                                <p class="muted">This session keeps only limited confirmation metadata. Full card numbers and CVV values are never stored.</p>
                                <div class="method-options">
                                    <% for (String method : paymentMethods) { %>
                                        <label class="method-option">
                                            <input type="radio" name="paymentMethod" value="<%= method %>"
                                                   <%= method.equals(selectedPaymentMethod) ? "checked" : "" %>>
                                            <span><%= method %></span>
                                        </label>
                                    <% } %>
                                </div>

                                <div class="method-form-grid" id="paypalFields">
                                    <div class="form-group compact-form-group">
                                        <label class="form-label" for="paypalEmail">PayPal email</label>
                                        <input class="form-input" type="email" id="paypalEmail" name="paypalEmail"
                                               value="<%= paypalEmail == null ? "" : paypalEmail %>" placeholder="player@example.com">
                                    </div>
                                </div>

                                <div class="method-form-grid" id="cardFields">
                                    <div class="form-group compact-form-group">
                                        <label class="form-label" for="cardholderName">Cardholder name</label>
                                        <input class="form-input" type="text" id="cardholderName" name="cardholderName"
                                               value="<%= cardholderName == null ? "" : cardholderName %>" placeholder="Name on card">
                                    </div>
                                    <div class="form-group compact-form-group">
                                        <label class="form-label" for="cardNumber">Card number</label>
                                        <input class="form-input" type="text" id="cardNumber" name="cardNumber"
                                               value="<%= cardNumber == null ? "" : cardNumber %>" placeholder="4242 4242 4242 4242">
                                    </div>
                                    <div class="form-group compact-form-group">
                                        <label class="form-label" for="expiryDate">Expiry date</label>
                                        <input class="form-input" type="text" id="expiryDate" name="expiryDate"
                                               value="<%= expiryDate == null ? "" : expiryDate %>" placeholder="MM/YY">
                                    </div>
                                    <div class="form-group compact-form-group">
                                        <label class="form-label" for="cvv">CVV</label>
                                        <input class="form-input" type="password" id="cvv" name="cvv" placeholder="123">
                                    </div>
                                </div>

                                <div class="method-form-grid" id="walletFields">
                                    <div class="form-group compact-form-group">
                                        <label class="form-label" for="walletId">Wallet name or ID</label>
                                        <input class="form-input" type="text" id="walletId" name="walletId"
                                               value="<%= walletId == null ? "" : walletId %>" placeholder="Main Wallet">
                                    </div>
                                </div>
                            </div>

                            <div class="action-row">
                                <button class="button" type="submit">Confirm payment</button>
                            </div>
                        </form>

                        <form class="inline-form" method="post" action="<%= request.getContextPath() %>/player/payment/cancel">
                            <input type="hidden" name="reference" value="<%= paymentSession.getReferenceCode() %>">
                            <% if (paymentSession.getPaymentMethod() != null) { %>
                                <input type="hidden" name="paymentMethod" value="<%= paymentSession.getPaymentMethod() %>">
                            <% } %>
                            <button class="button-danger" type="submit">Cancel payment</button>
                        </form>
                    <% } else if (paymentSession.isConfirmed()) { %>
                        <div class="alert alert-success">
                            Payment confirmed on <%= formatter.format(paymentSession.getConfirmedAt()) %>. The purchase record is complete and ready for the next delivery step.
                        </div>

                        <div class="payment-card fulfillment-card">
                            <div class="payment-label">Next step</div>
                            <div class="payment-value-small">Ready for delivery handoff</div>
                            <div class="muted">
                                In a live deployment, the confirmed order would now be handed to the game platform or store backend for currency delivery.
                            </div>
                        </div>
                    <% } else if (paymentSession.isCancelled()) { %>
                        <div class="alert alert-error">
                            Payment cancelled on <%= formatter.format(paymentSession.getCancelledAt()) %>.
                        </div>
                    <% } else if (paymentSession.isExpired()) { %>
                        <div class="alert alert-error">
                            This payment session expired on <%= formatter.format(paymentSession.getExpiredAt()) %>.
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </body>
</html>
