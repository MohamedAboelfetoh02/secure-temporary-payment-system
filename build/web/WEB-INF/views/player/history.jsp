<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.tpsproject.model.PaymentSessionDetails" %>
<%@ page import="com.tpsproject.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Payment History | TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
    </head>
    <body>
        <%
            String username = (String) session.getAttribute(SessionUtil.USERNAME);
            List<PaymentSessionDetails> history = (List<PaymentSessionDetails>) request.getAttribute("history");
            String historyMessage = (String) request.getAttribute("historyMessage");
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        %>
        <div class="page-shell">
            <div class="container">
                <div class="topbar">
                    <div class="brand">
                        TPS Version 1.0
                        <small>Payment history</small>
                    </div>
                    <div class="nav-links">
                        <a class="button-ghost" href="<%= request.getContextPath() %>/home">Catalog</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/player/dashboard"><%= username %></a>
                        <form class="inline-form" method="post" action="<%= request.getContextPath() %>/logout">
                            <button class="button-ghost" type="submit">Sign out</button>
                        </form>
                    </div>
                </div>

                <div class="panel">
                    <h1 class="section-title">Payment history</h1>
                    <p class="muted">Review your previous payment sessions, saved payment summaries, and final outcomes.</p>

                    <% if (historyMessage != null) { %>
                        <div class="alert alert-error"><%= historyMessage %></div>
                    <% } %>

                    <% if (history != null && !history.isEmpty()) { %>
                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Game</th>
                                        <th>Package</th>
                                        <th>Amount</th>
                                        <th>Method</th>
                                        <th>Stored detail</th>
                                        <th>Reference</th>
                                        <th>Status</th>
                                        <th>Started</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (PaymentSessionDetails item : history) { %>
                                        <tr>
                                            <td><strong><%= item.getGameTitle() %></strong></td>
                                            <td><%= item.getPackageName() %> (<%= item.getCurrencyAmount() %> <%= item.getCurrencyName() %>)</td>
                                            <td>$<%= item.getAmount() %></td>
                                            <td><%= item.getPaymentMethod() == null ? "-" : item.getPaymentMethod() %></td>
                                            <td><%= item.getPaymentDetailSummary() == null ? "-" : item.getPaymentDetailSummary() %></td>
                                            <td><code><%= item.getReferenceCode() %></code></td>
                                            <td><span class="status-pill status-<%= item.getSessionStatus().toLowerCase() %>"><%= item.getSessionStatus() %></span></td>
                                            <td><%= formatter.format(item.getCreatedAt()) %></td>
                                            <td><a href="<%= request.getContextPath() %>/player/payment-session?reference=<%= item.getReferenceCode() %>">View</a></td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else if (historyMessage == null) { %>
                        <div class="alert alert-info">No payment sessions have been recorded for this account yet.</div>
                    <% } %>
                </div>
            </div>
        </div>
    </body>
</html>
