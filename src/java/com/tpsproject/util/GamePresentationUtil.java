package com.tpsproject.util;

import com.tpsproject.model.Game;
import com.tpsproject.model.PaymentSessionDetails;
import java.util.List;

public final class GamePresentationUtil {

    private GamePresentationUtil() {
    }

    public static void applyToGames(List<Game> games) {
        if (games == null) {
            return;
        }

        for (Game game : games) {
            if (game != null) {
                apply(game);
            }
        }
    }

    public static void applyToPaymentSession(PaymentSessionDetails paymentSessionDetails) {
        if (paymentSessionDetails == null) {
            return;
        }

        String title = paymentSessionDetails.getGameTitle();
        if (title == null) {
            return;
        }

        if ("Warframe".equalsIgnoreCase(title)) {
            paymentSessionDetails.setGameImagePath("https://www-static.warframe.com/images/landing/warframe-metacard.png");
            paymentSessionDetails.setGameOfficialUrl("https://www.warframe.com/");
        } else if ("Marvel Rivals".equalsIgnoreCase(title)) {
            paymentSessionDetails.setGameImagePath("https://cdn2.unrealengine.com/01-1920x1080-1920x1080-88255a697e4f.jpg");
            paymentSessionDetails.setGameOfficialUrl("https://www.marvelrivals.com/");
        }
    }

    private static void apply(Game game) {
        if (game.getTitle() == null) {
            return;
        }

        if ("Warframe".equalsIgnoreCase(game.getTitle())) {
            game.setImagePath("https://www-static.warframe.com/images/landing/warframe-metacard.png");
            game.setOfficialUrl("https://www.warframe.com/");
        } else if ("Marvel Rivals".equalsIgnoreCase(game.getTitle())) {
            game.setImagePath("https://cdn2.unrealengine.com/01-1920x1080-1920x1080-88255a697e4f.jpg");
            game.setOfficialUrl("https://www.marvelrivals.com/");
        }
    }
}
