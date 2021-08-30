package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Bid;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.BidDAO;
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
import java.util.List;

@WebServlet(name = "GetBids", value = "/GetBids")
@MultipartConfig
public class GetBids extends HttpServlet {
    private TemplateEngine templateEngine;
    private Connection connection;


    public void init() throws UnavailableException {
        Pair<TemplateEngine, Connection> setupResult = ServletUtils.setupServlet(getServletContext());
        this.templateEngine = setupResult.getFirst();
        this.connection = setupResult.getSecond();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int auctionId;
        try {
            auctionId = Integer.parseInt(request.getParameter("auctionID"));
        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid request");
            return;
        }

        BidDAO bidDAO = new BidDAO(connection);
        List<Bid> bids;

        try {
            bids = bidDAO.getAuctionBids(auctionId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Failure in database fetch: failed to gather bids for auction #" + auctionId);
            return;
        }

        String serializedBids = JsonSerializer.getInstance().toJson(bids);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(serializedBids);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
