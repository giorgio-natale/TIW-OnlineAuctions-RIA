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
    public static final String HISTORY_KEY_PREFIX = "auctionsHistory.";
    public static final int MAX_HISTORY_SIZE = 50;

    public static final String LAST_ACTION_KEY_PREFIX = "lastAction.";
    public static final List<String> ACCEPTED_ACTIONS = Arrays.asList("search", "bid", "create", "close");

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

    public static List<Integer> getAuctionIdsFromCookie(String cookieContent) {
        String decodedCookieContent = decode(cookieContent);

        try {
            List<String> list = new ArrayList<>(Arrays.asList(decodedCookieContent.replaceAll("(\\[|]| )", "").split(",")));
            return list.stream().map(Integer::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void addVisitedAuction(Connection connection, HttpServletRequest request, HttpServletResponse response, int auctionID, int userID) {
        List<Integer> auctions;

        Cookie oldCookie = getCookie(request, HISTORY_KEY_PREFIX + userID, "W10"); // in base64: []

        auctions = getAuctionIdsFromCookie(oldCookie.getValue());

        auctions.remove((Integer) auctionID);
        auctions.add(0, auctionID);

        auctions = List.copyOf(new LinkedHashSet<>(auctions));

        AuctionDAO auctionDAO = new AuctionDAO(connection);
        try {
            auctions = auctionDAO.clearClosedAndExpiredAuctions(auctions);
        } catch (SQLException e) {
            auctions = new ArrayList<>();
        }

        if(auctions.size() > MAX_HISTORY_SIZE)
            auctions.subList(MAX_HISTORY_SIZE, auctions.size()).clear();

        String newCookieContent = encode(auctions.toString().replace(" ", ""));

        Cookie newCookie = new Cookie(HISTORY_KEY_PREFIX + userID, newCookieContent);
        newCookie.setMaxAge(30 * 60 * 60 * 24);
        response.addCookie(newCookie);
    }

    public static void setLastAction(HttpServletRequest request, HttpServletResponse response, String action, int userID) {
        Cookie newCookie;

        if(!ACCEPTED_ACTIONS.contains(action))
            newCookie = new Cookie(LAST_ACTION_KEY_PREFIX + userID, ACCEPTED_ACTIONS.get(0));
        else
            newCookie = new Cookie(LAST_ACTION_KEY_PREFIX + userID, action);

        newCookie.setMaxAge(30 * 60 * 60 * 24);
        response.addCookie(newCookie);
    }
}