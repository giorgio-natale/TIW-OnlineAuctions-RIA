package it.polimi.tiw.TIW_OnlineAuctions_RIA.utils;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CookieManager {
    public static final String AUCTIONS_HISTORY_KEY = "auctionsHistory";

    private static Cookie getCookie(HttpServletRequest request, String key, String defaultValue) {
        return Arrays.stream(request.getCookies())
                .filter(c -> key.equals(c.getName()))
                .findAny()
                .orElse(new Cookie(key, defaultValue));
    }

    private static String encode(String str) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(str.getBytes());
    }

    private static String decode(String str) {
        return new String(Base64.getUrlDecoder().decode(str));
    }

    public static void addVisitedAuction(Connection connection, HttpServletRequest request, HttpServletResponse response, int auctionID) {
        List<Integer> auctions;

        Cookie oldCookie = getCookie(request, AUCTIONS_HISTORY_KEY, "W10"); // in base64: []
        String oldCookieContent = decode(oldCookie.getValue());

        try {
            List<String> list = new ArrayList<>(Arrays.asList(oldCookieContent.replaceAll("(\\[|]| )", "").split(",")));
            auctions = list.stream().map(Integer::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            auctions = new ArrayList<>();
        }

        auctions.remove((Integer) auctionID);
        auctions.add(0, auctionID);

        List<Integer> openAuctions;
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        try {
            openAuctions = auctionDAO.clearClosedAndExpiredAuctions(auctions);
        } catch (SQLException e) {
            openAuctions = new ArrayList<>();
        }

        auctions.retainAll(openAuctions);
        auctions = List.copyOf(new LinkedHashSet<>(auctions));

        String newCookieContent = encode(auctions.toString().replace(" ", ""));

        Cookie newCookie = new Cookie(AUCTIONS_HISTORY_KEY, newCookieContent);
        newCookie.setMaxAge(30 * 60 * 60 * 24);
        response.addCookie(newCookie);
    }

    public static void setLastAction(HttpServletRequest request, HttpServletResponse response, String action) {

    }
}