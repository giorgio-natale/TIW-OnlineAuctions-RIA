package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.UserDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.JsonSerializer;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.Pair;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;
import org.thymeleaf.TemplateEngine;

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
import java.util.List;

@WebServlet(name = "GetAuctionWinner", value = "/GetAuctionWinner")
@MultipartConfig
public class GetAuctionWinner extends HttpServlet {
    private TemplateEngine templateEngine;
    private Connection connection;

    public void init() throws UnavailableException {
        Pair<TemplateEngine, Connection> setupResult = ServletUtils.setupServlet(getServletContext());
        this.templateEngine = setupResult.getFirst();
        this.connection = setupResult.getSecond();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        String strAuctionID = request.getParameter("auctionID");

        // check parameter
        if(strAuctionID == null || strAuctionID.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid request");
            return;
        }

        // check if int
        int auctionID;
        try {
            auctionID = Integer.parseInt(strAuctionID);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid request");
            return;
        }

        Auction auction;

        // tries to connect ot database
        try {
            auction = auctionDAO.getAuctionDetails(auctionID);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Failure in database fetch: failed to gather auction");
            return;
        }

        // check if auction exists and is open
        if(auction == null || (!auction.isClosed() && !auction.isExpired())) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Auction not found");
            return;
        }

        // check if the request is from the auction's owner or the winner
        int clientID = ((User) request.getSession().getAttribute("user")).getUser_id();
        if(auction.getUser_id() != clientID && auction.getWinner_id() != clientID) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Invalid request: only the owner or the winner of the auction can gather the winner");
            return;
        }

        User user;
        // tries to connect ot database
        try {
            user = userDAO.getUserDetails(auction.getWinner_id());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Failure in database fetch: failed to gather auction winner");
            return;
        }

        // check if user exists
        if(user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("User not found");
            return;
        }

        String serializedAuctions = JsonSerializer.getInstance().toJson(user);

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