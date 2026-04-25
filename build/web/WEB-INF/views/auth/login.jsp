<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.tpsproject.util.SessionUtil" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Sign In | TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
        <script src="<%= request.getContextPath() %>/assets/js/site.js" defer></script>
    </head>
    <body>
        <%
            String flashType = (String) session.getAttribute(SessionUtil.FLASH_TYPE);
            String flashMessage = (String) session.getAttribute(SessionUtil.FLASH_MESSAGE);
            if (flashMessage != null) {
                session.removeAttribute(SessionUtil.FLASH_TYPE);
                session.removeAttribute(SessionUtil.FLASH_MESSAGE);
            }
            String errorMessage = (String) request.getAttribute("errorMessage");
            String identifier = (String) request.getAttribute("identifier");
        %>
        <div class="page-shell">
            <div class="container auth-shell">
                <div class="topbar">
                    <div class="brand">
                        TPS Version 1.0
                        <small>Secure top-up gateway for online games</small>
                    </div>
                    <div class="nav-links">
                        <a class="button-ghost" href="<%= request.getContextPath() %>/home">Home</a>
                        <a class="button-secondary" href="<%= request.getContextPath() %>/register">Create account</a>
                    </div>
                </div>

                <div class="auth-card">
                    <h1>Sign in</h1>
                    <p>Use your username or email to access your account.</p>

                    <% if (flashMessage != null) { %>
                        <div class="alert <%= "success".equals(flashType) ? "alert-success" : "alert-error" %>">
                            <%= flashMessage %>
                        </div>
                    <% } %>

                    <% if (errorMessage != null) { %>
                        <div class="alert alert-error"><%= errorMessage %></div>
                    <% } %>

                    <form method="post" action="<%= request.getContextPath() %>/login">
                        <div class="form-group">
                            <label class="form-label" for="identifier">Username or email</label>
                            <input class="form-input" type="text" id="identifier" name="identifier"
                                   value="<%= identifier == null ? "" : identifier %>" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="password">Password</label>
                            <div class="input-with-action">
                                <input class="form-input" type="password" id="password" name="password" required>
                                <button class="input-action" type="button" data-toggle-password="password">Show</button>
                            </div>
                        </div>

                        <button class="button" type="submit">Sign in</button>
                    </form>

                    <div class="auth-footer">
                        New here? <a href="<%= request.getContextPath() %>/register">Create a player account</a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
