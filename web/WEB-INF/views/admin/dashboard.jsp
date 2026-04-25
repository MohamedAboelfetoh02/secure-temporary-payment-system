<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.tpsproject.util.SessionUtil" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin Dashboard | TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
    </head>
    <body>
        <%
            String username = (String) session.getAttribute(SessionUtil.USERNAME);
            String dashboardMessage = (String) request.getAttribute("dashboardMessage");
            Integer playerCount = (Integer) request.getAttribute("playerCount");
            Integer adminCount = (Integer) request.getAttribute("adminCount");
            Integer gameCount = (Integer) request.getAttribute("gameCount");
            Integer packageCount = (Integer) request.getAttribute("packageCount");
            Integer sessionCount = (Integer) request.getAttribute("sessionCount");
            Integer pendingSessionCount = (Integer) request.getAttribute("pendingSessionCount");
        %>
        <div class="page-shell">
            <div class="container">
                <div class="topbar">
                    <div class="brand">
                        TPS Version 1.0
                        <small>Admin workspace</small>
                    </div>
                    <div class="nav-links">
                        <a class="button-ghost" href="<%= request.getContextPath() %>/home">Catalog</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/admin/transactions">Transactions</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/admin/audit-logs">Audit logs</a>
                        <form class="inline-form" method="post" action="<%= request.getContextPath() %>/logout">
                            <button class="button-secondary" type="submit">Sign out</button>
                        </form>
                    </div>
                </div>

                <div class="panel dashboard-panel">
                    <h1 class="section-title">Admin dashboard</h1>
                    <p class="muted">Signed in as <strong><%= username %></strong>. Review activity, transactions, and recent audit events from one place.</p>

                    <% if (dashboardMessage != null) { %>
                        <div class="alert alert-error"><%= dashboardMessage %></div>
                    <% } %>

                    <div class="dashboard-grid">
                        <div class="stat-card">
                            <div>Players</div>
                            <div class="value"><%= playerCount == null ? 0 : playerCount %></div>
                        </div>
                        <div class="stat-card">
                            <div>Admins</div>
                            <div class="value"><%= adminCount == null ? 0 : adminCount %></div>
                        </div>
                        <div class="stat-card">
                            <div>Games</div>
                            <div class="value"><%= gameCount == null ? 0 : gameCount %></div>
                        </div>
                        <div class="stat-card">
                            <div>Currency packages</div>
                            <div class="value"><%= packageCount == null ? 0 : packageCount %></div>
                        </div>
                        <div class="stat-card">
                            <div>Payment sessions</div>
                            <div class="value"><%= sessionCount == null ? 0 : sessionCount %></div>
                        </div>
                        <div class="stat-card">
                            <div>Pending sessions</div>
                            <div class="value"><%= pendingSessionCount == null ? 0 : pendingSessionCount %></div>
                        </div>
                    </div>

                    <div class="action-strip">
                        <a class="button" href="<%= request.getContextPath() %>/admin/transactions">Review transactions</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/admin/audit-logs">Open audit logs</a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
