package com.tpsproject.util;

import com.tpsproject.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class SessionUtil {

    public static final String USER_ID = "loggedInUserId";
    public static final String USERNAME = "loggedInUsername";
    public static final String USER_ROLE = "loggedInUserRole";
    public static final String FLASH_TYPE = "flashType";
    public static final String FLASH_MESSAGE = "flashMessage";

    private SessionUtil() {
    }

    public static void startUserSession(HttpServletRequest request, User user) {
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setMaxInactiveInterval(15 * 60);
        newSession.setAttribute(USER_ID, user.getId());
        newSession.setAttribute(USERNAME, user.getUsername());
        newSession.setAttribute(USER_ROLE, user.getRole());
    }

    public static void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static Integer getLoggedInUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(USER_ID);
        return value instanceof Integer ? (Integer) value : null;
    }

    public static String getLoggedInUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(USERNAME);
        return value instanceof String ? (String) value : null;
    }

    public static String getLoggedInRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(USER_ROLE);
        return value instanceof String ? (String) value : null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInUserId(request) != null;
    }

    public static void setFlashMessage(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(FLASH_TYPE, type);
        session.setAttribute(FLASH_MESSAGE, message);
    }
}
