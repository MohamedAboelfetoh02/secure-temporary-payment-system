<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.tpsproject.util.SessionUtil" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Player Dashboard | TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
    </head>
    <body>
        <%
            String username = (String) session.getAttribute(SessionUtil.USERNAME);
            String dashboardMessage = (String) request.getAttribute("dashboardMessage");
            Integer gameCount = (Integer) request.getAttribute("gameCount");
            Integer packageCount = (Integer) request.getAttribute("packageCount");
            Integer historyCount = (Integer) request.getAttribute("historyCount");
        %>
        <div class="page-shell">
            <div class="container">
                <div class="topbar">
                    <div class="brand">
                        TPS Version 1.0
                        <small>Player workspace</small>
                    </div>
                    <div class="nav-links">
                        <a class="button-ghost" href="<%= request.getContextPath() %>/home">Catalog</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/player/history">History</a>
                        <form class="inline-form" method="post" action="<%= request.getContextPath() %>/logout">
                            <button class="button-secondary" type="submit">Sign out</button>
                        </form>
                    </div>
                </div>

                <div class="panel dashboard-panel">
                    <h1 class="section-title">Welcome back, <%= username %></h1>
                    <p class="muted">
                        Manage your recent payment sessions, review top-up records, and jump back into the catalog when you want to start a new order.
                    </p>

                    <% if (dashboardMessage != null) { %>
                        <div class="alert alert-error"><%= dashboardMessage %></div>
                    <% } %>

                    <div class="dashboard-grid">
                        <div class="stat-card">
                            <div>Available games</div>
                            <div class="value"><%= gameCount == null ? 0 : gameCount %></div>
                        </div>
                        <div class="stat-card">
                            <div>Currency packages</div>
                            <div class="value"><%= packageCount == null ? 0 : packageCount %></div>
                        </div>
                        <div class="stat-card">
                            <div>Payment sessions</div>
                            <div class="value"><%= historyCount == null ? 0 : historyCount %></div>
                        </div>
                    </div>

                    <div class="action-strip">
                        <a class="button" href="<%= request.getContextPath() %>/home">Browse packages</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/player/history">Open payment history</a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
