package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.CookieManager;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.JsonSerializer;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.Pair;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;
import org.thymeleaf.TemplateEngine;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "GetAuctionsFromHistory", value = "/GetAuctionsFromHistory")
@MultipartConfig
public class GetAuctionsFromHistory extends HttpServlet {

    private Connection connection;

    public void init() throws UnavailableException {
        Pair<TemplateEngine, Connection> setupResult = ServletUtils.setupServlet(getServletContext());
        this.connection = setupResult.getSecond();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int userId = ((User) request.getSession().getAttribute("user")).getUser_id();

        List<Integer> idList = CookieManager.getAuctionIdsFromCookie(CookieManager.getCookie(request, CookieManager.HISTORY_KEY_PREFIX + userId, CookieManager.DEFAULT_ENCODED_HISTORY_VALUE).getValue());

        AuctionDAO auctionDAO = new AuctionDAO(connection);

        List<Auction> auctions;

        try {
            auctions = auctionDAO.getAuctionDetails(idList);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database fetch: failed to search through history auctions");
            return;
        }

        String serializedAuctions = JsonSerializer.getInstance().toJson(auctions);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(serializedAuctions);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
