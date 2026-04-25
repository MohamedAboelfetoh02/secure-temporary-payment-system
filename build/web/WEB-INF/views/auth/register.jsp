<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Create Account | TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
        <script src="<%= request.getContextPath() %>/assets/js/site.js" defer></script>
    </head>
    <body>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            String username = (String) request.getAttribute("username");
            String email = (String) request.getAttribute("email");
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
                        <a class="button-secondary" href="<%= request.getContextPath() %>/login">Sign in</a>
                    </div>
                </div>

                <div class="auth-card">
                    <h1>Create your account</h1>
                    <p>Register as a player to continue to game currency purchases.</p>

                    <% if (errorMessage != null) { %>
                        <div class="alert alert-error"><%= errorMessage %></div>
                    <% } %>

                    <form method="post" action="<%= request.getContextPath() %>/register">
                        <div class="form-group">
                            <label class="form-label" for="username">Username</label>
                            <input class="form-input" type="text" id="username" name="username"
                                   value="<%= username == null ? "" : username %>" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="email">Email address</label>
                            <input class="form-input" type="email" id="email" name="email"
                                   value="<%= email == null ? "" : email %>" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="password">Password</label>
                            <div class="input-with-action">
                                <input class="form-input" type="password" id="password" name="password" required>
                                <button class="input-action" type="button" data-toggle-password="password">Show</button>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="confirmPassword">Confirm password</label>
                            <div class="input-with-action">
                                <input class="form-input" type="password" id="confirmPassword" name="confirmPassword" required>
                                <button class="input-action" type="button" data-toggle-password="confirmPassword">Show</button>
                            </div>
                        </div>

                        <button class="button" type="submit">Create account</button>
                    </form>

                    <div class="auth-footer">
                        Already have an account? <a href="<%= request.getContextPath() %>/login">Sign in here</a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
