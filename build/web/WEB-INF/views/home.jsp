<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.tpsproject.model.CurrencyPackage" %>
<%@ page import="com.tpsproject.model.Game" %>
<%@ page import="com.tpsproject.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>TPS Version 1.0</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
    </head>
    <body>
        <%
            List<Game> games = (List<Game>) request.getAttribute("games");
            String databaseMessage = (String) request.getAttribute("databaseMessage");
            String username = (String) session.getAttribute(SessionUtil.USERNAME);
            String userRole = (String) session.getAttribute(SessionUtil.USER_ROLE);
            String flashType = (String) session.getAttribute(SessionUtil.FLASH_TYPE);
            String flashMessage = (String) session.getAttribute(SessionUtil.FLASH_MESSAGE);
            if (flashMessage != null) {
                session.removeAttribute(SessionUtil.FLASH_TYPE);
                session.removeAttribute(SessionUtil.FLASH_MESSAGE);
            }
        %>
        <div class="page-shell">
            <div class="container">
                <div class="topbar">
                    <div class="brand">
                        TPS Version 1.0
                        <small>Secure top-up gateway for online games</small>
                    </div>
                    <div class="nav-links">
                        <a class="button-ghost" href="<%= request.getContextPath() %>/home">Home</a>
                        <% if (username == null) { %>
                            <a class="button-secondary" href="<%= request.getContextPath() %>/login">Sign in</a>
                            <a class="button" href="<%= request.getContextPath() %>/register">Create account</a>
                        <% } else { %>
                            <a class="button-secondary" href="<%= request.getContextPath() + ("ADMIN".equals(userRole) ? "/admin/dashboard" : "/player/dashboard") %>">
                                <%= username %>
                            </a>
                            <form class="inline-form" method="post" action="<%= request.getContextPath() %>/logout">
                                <button class="button-ghost" type="submit">Sign out</button>
                            </form>
                        <% } %>
                    </div>
                </div>

                <% if (flashMessage != null) { %>
                    <div class="alert <%= "success".equals(flashType) ? "alert-success" : "alert-error" %>">
                        <%= flashMessage %>
                    </div>
                <% } %>

                <div class="panel">
                    <div class="hero">
                        <div class="hero-copy">
                            <h1><%= request.getAttribute("projectTitle") %></h1>
                            <p>
                                A short-lived payment window for game currency purchases. Each session uses a one-time
                                reference code and keeps only limited payment metadata after confirmation.
                            </p>
                            <div class="nav-links">
                                <% if (username == null) { %>
                                    <a class="button" href="<%= request.getContextPath() %>/register">Get started</a>
                                    <a class="button-secondary" href="<%= request.getContextPath() %>/login">Player sign in</a>
                                <% } else { %>
                                    <a class="button" href="<%= request.getContextPath() + ("ADMIN".equals(userRole) ? "/admin/dashboard" : "/player/dashboard") %>">
                                        Open dashboard
                                    </a>
                                <% } %>
                            </div>
                        </div>

                        <div class="hero-card">
                            <h2>How our TPS works</h2>
                            <ul>
                                <li>Select a game currency package</li>
                                <li>Start a temporary payment session</li>
                                <li>Use a single-use reference code</li>
                                <li>Confirm before the session expires</li>
                            </ul>
                        </div>
                    </div>

                    <div class="section-header">
                        <div>
                            <h2 class="section-title">Available packages</h2>
                            <p class="muted section-copy">Browse the supported game top-ups and launch a protected payment session when you are ready.</p>
                        </div>
                    </div>

                    <% if (games != null && !games.isEmpty()) { %>
                        <div class="catalog-grid">
                            <% for (Game game : games) { %>
                                <div class="game-card">
                                    <div class="game-media" style="background-image: linear-gradient(135deg, rgba(15,23,42,0.2), rgba(29,78,216,0.55))<%= game.getImagePath() == null ? "" : ", url('" + game.getImagePath() + "')" %>;">
                                        <div class="game-media-overlay">
                                            <div class="game-kicker"><%= game.getGenre() %></div>
                                            <h3><%= game.getTitle() %></h3>
                                            <p><%= game.getDescription() %></p>
                                            <% if (game.getOfficialUrl() != null) { %>
                                                <a class="button-ghost button-ghost-light" href="<%= game.getOfficialUrl() %>" target="_blank" rel="noopener noreferrer">Official game page</a>
                                            <% } %>
                                        </div>
                                    </div>

                                    <% if (!game.getCurrencyPackages().isEmpty()) { %>
                                        <div class="package-list">
                                            <% for (CurrencyPackage currencyPackage : game.getCurrencyPackages()) { %>
                                                <div class="package-item">
                                                    <div>
                                                        <strong><%= currencyPackage.getPackageName() %></strong><br>
                                                        <span class="muted"><%= currencyPackage.getCurrencyAmount() %> <%= currencyPackage.getCurrencyName() %></span>
                                                    </div>
                                                    <div class="package-actions">
                                                        <div class="package-price">$<%= currencyPackage.getPrice() %></div>
                                                        <% if (userRole == null) { %>
                                                            <a class="button-secondary" href="<%= request.getContextPath() %>/login">Sign in to buy</a>
                                                        <% } else if ("PLAYER".equals(userRole)) { %>
                                                            <form class="inline-form" method="post" action="<%= request.getContextPath() %>/player/purchase/start">
                                                                <input type="hidden" name="packageId" value="<%= currencyPackage.getId() %>">
                                                                <button class="button" type="submit">Buy now</button>
                                                            </form>
                                                        <% } else { %>
                                                            <span class="package-note">Player account required</span>
                                                        <% } %>
                                                    </div>
                                                </div>
                                            <% } %>
                                        </div>
                                    <% } else { %>
                                        <div class="alert alert-error">No currency packages are available for this game right now.</div>
                                    <% } %>
                                </div>
                            <% } %>
                        </div>
                    <% } else { %>
                        <div class="alert alert-error">
                            The catalog is not available right now.
                            <% if (databaseMessage != null) { %>
                                <br><br>
                                <strong>Details:</strong> <%= databaseMessage %>
                            <% } %>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </body>
</html>
