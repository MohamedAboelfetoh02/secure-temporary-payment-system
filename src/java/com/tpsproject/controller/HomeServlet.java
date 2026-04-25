package com.tpsproject.controller;

import com.tpsproject.dao.GameDao;
import com.tpsproject.model.Game;
import com.tpsproject.util.GamePresentationUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    private final GameDao gameDao = new GameDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("projectTitle", "Secure Temporary Payment System for Online Games");

        try {
            List<Game> games = gameDao.findActiveGamesWithPackages();
            GamePresentationUtil.applyToGames(games);
            request.setAttribute("games", games);
            request.setAttribute("databaseStatus", "connected");
        } catch (SQLException ex) {
            request.setAttribute("games", Collections.emptyList());
            request.setAttribute("databaseStatus", "unavailable");
            request.setAttribute("databaseMessage", ex.getMessage());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/home.jsp");
        dispatcher.forward(request, response);
    }
}
