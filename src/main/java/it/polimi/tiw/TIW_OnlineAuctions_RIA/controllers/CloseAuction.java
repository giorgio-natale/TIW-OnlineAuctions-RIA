package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
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

@WebServlet(name = "CloseAuction", value = "/CloseAuction")
@MultipartConfig
public class CloseAuction extends HttpServlet {

    private TemplateEngine templateEngine;
    private Connection connection;


    public void init() throws UnavailableException {
        Pair<TemplateEngine, Connection> setupResult = ServletUtils.setupServlet(getServletContext());
        this.templateEngine = setupResult.getFirst();
        this.connection = setupResult.getSecond();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);

        int auctionId;
        try {
            auctionId = Integer.parseInt(request.getParameter("auctionID"));
        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Incorrect param values");
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

        //TODO: close the auction in the database

        // check if auction exists
        if(auction == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Invalid request: the auction doesn't exist");
            return;
        }

        // check if the auction is expired
        if(!auction.isExpired()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Invalid request: you may not close a not yet expired auction");
            return;
        }

        // check if the auction is not closed
        if(auction.isClosed()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Invalid request: you may not close an already closed auction");
            return;
        }

        // check if the owner is closing
        if(auction.getUser_id() != ((User) request.getSession().getAttribute("user")).getUser_id()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Invalid request: only the owner of the auction may close it");
            return;
        }

        try {
            auctionDAO.closeAuction(auctionId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Failure in database fetch: failed to close auction");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("Auction closed successfully");
    }
}
