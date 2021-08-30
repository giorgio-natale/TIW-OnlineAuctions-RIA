package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.UserDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.JsonSerializer;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.Pair;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "GetAuctionDetails", value = "/GetAuctionDetails")
@MultipartConfig
public class GetAuctionDetails extends HttpServlet {
    private TemplateEngine templateEngine;
    private Connection connection;


    public void init() throws UnavailableException {
        Pair<TemplateEngine, Connection> setupResult = ServletUtils.setupServlet(getServletContext());
        this.templateEngine = setupResult.getFirst();
        this.connection = setupResult.getSecond();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        int auctionId;
        try {
            auctionId = Integer.parseInt(request.getParameter("auctionID"));
        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Bad request");
            return;
        }

        Auction auction;
        try {
            auction = auctionDAO.getAuctionDetails(auctionId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Failure in database fetch: failed to gather auction");
            return;
        }

        if(auction == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Auction not found");
            return;
        }

        User owner;
        try {
            owner = userDAO.getUserDetails(auction.getUser_id());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Failure in database fetch: failed to gather user");
            return;
        }

        if(owner == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("User not found");
            return;
        }

        int clientID = ((User) request.getSession().getAttribute("user")).getUser_id();

        if((auction.isClosed() || auction.isExpired()) && clientID != auction.getWinner_id() && clientID != auction.getUser_id()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Unable to retrieve a closed or expired auction as neither the owner or the winner");
            return;
        }

        owner.setEmail(null);
        owner.setStreet(null);
        owner.setCity(null);
        owner.setProvince(null);
        owner.setZip_code(null);
        owner.setOther_address_infos(null);
        owner.setLast_login(null);

        HashMap<String, Object> auctionAndOwner = new HashMap<>();
        auctionAndOwner.put("auction", auction);
        auctionAndOwner.put("owner", owner);

        String serializedResponse = JsonSerializer.getInstance().toJson(auctionAndOwner);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(serializedResponse);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
