package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Bid;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.BidDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.CookieManager;
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

@WebServlet(name = "AddBid", value = "/AddBid")
@MultipartConfig
public class AddBid extends HttpServlet {

    private Connection connection;

    public void init() throws UnavailableException {
        this.connection = ServletUtils.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        BidDAO bidDAO = new BidDAO(connection);

        double newBid;
        int auctionId;
        try {
            newBid = Double.parseDouble(request.getParameter("addBid-amount").replace(',', '.'));
            auctionId = Integer.parseInt(request.getParameter("addBid-auctionId"));
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

        List<Bid> bids;
        try {
            bids = bidDAO.getAuctionBids(auctionId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Failure in database fetch: failed to gather bids");
            return;
        }

        // check if auction exists
        if(auction == null) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Invalid request: the auction doesn't exist");
            return;
        }

        // check if the owner is not bidding
        if(auction.getUser_id() == ((User) request.getSession().getAttribute("user")).getUser_id()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Invalid request: the owner of the auction may not add a new bid");
            return;
        }

        // check if the auction is not closed
        if(auction.isClosed()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Invalid request: you may not add a new bid to a already closed auction");
            return;
        }

        // check if the auction is not expired
        if(auction.isExpired()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Invalid request: you may not add a new bid to an expired auction");
            return;
        }

        // check if bid is within limits
        if(newBid < 1.00 || newBid > 9_999_999.99) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bid must be within limits");
        }

        // check if bids are valid
        if(bids.isEmpty()) {
            if(newBid < auction.getStarting_price()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().println("Invalid request: you may not add a new bid lower than the starting price");
                return;
            }
        }
        else {
            if(newBid < bids.get(0).getPrice() + auction.getMin_price_gap()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().println("Invalid request: you may not add a new bid lower than the minimum rebid price");
                return;
            }
        }

        // add a new bid
        try {
            bidDAO.addNewBid(
                ((User) request.getSession().getAttribute("user")).getUser_id(),
                auctionId,
                newBid
            );
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Failure in database fetch: failed to update bids");
            return;
        }

        CookieManager.setLastAction(request, response, "bid", ((User) request.getSession().getAttribute("user")).getUser_id());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println("New bid added");
    }

    public void destroy() {
        try {
            ServletUtils.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
