<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.tpsproject.model.AuditLogEntry" %>
<%@ page import="com.tpsproject.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Audit Logs | TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
    </head>
    <body>
        <%
            String username = (String) session.getAttribute(SessionUtil.USERNAME);
            List<AuditLogEntry> auditLogs = (List<AuditLogEntry>) request.getAttribute("auditLogs");
            String auditMessage = (String) request.getAttribute("auditMessage");
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        %>
        <div class="page-shell">
            <div class="container">
                <div class="topbar">
                    <div class="brand">
                        TPS Version 1.0
                        <small>Admin audit view</small>
                    </div>
                    <div class="nav-links">
                        <a class="button-ghost" href="<%= request.getContextPath() %>/admin/dashboard">Dashboard</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/admin/transactions">Transactions</a>
                        <form class="inline-form" method="post" action="<%= request.getContextPath() %>/logout">
                            <button class="button-ghost" type="submit">Sign out</button>
                        </form>
                    </div>
                </div>

                <div class="panel">
                    <h1 class="section-title">Audit logs</h1>
                    <p class="muted">Signed in as <strong><%= username %></strong>. Important actions are recorded here for review.</p>

                    <% if (auditMessage != null) { %>
                        <div class="alert alert-error"><%= auditMessage %></div>
                    <% } %>

                    <% if (auditLogs != null && !auditLogs.isEmpty()) { %>
                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Time</th>
                                        <th>User</th>
                                        <th>Action</th>
                                        <th>Description</th>
                                        <th>Target</th>
                                        <th>IP</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (AuditLogEntry item : auditLogs) { %>
                                        <tr>
                                            <td><%= formatter.format(item.getCreatedAt()) %></td>
                                            <td><%= item.getUsername() == null ? "System" : item.getUsername() %></td>
                                            <td><strong><%= item.getActionType() %></strong></td>
                                            <td><%= item.getDescription() %></td>
                                            <td><%= item.getTargetType() == null ? "-" : item.getTargetType() + (item.getTargetId() == null ? "" : " #" + item.getTargetId()) %></td>
                                            <td><%= item.getIpAddress() == null ? "-" : item.getIpAddress() %></td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else if (auditMessage == null) { %>
                        <div class="alert alert-info">No audit entries are available yet.</div>
                    <% } %>
                </div>
            </div>
        </div>
    </body>
</html>
