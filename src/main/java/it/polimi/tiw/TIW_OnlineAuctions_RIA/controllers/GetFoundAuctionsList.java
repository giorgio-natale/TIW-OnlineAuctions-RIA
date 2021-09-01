package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.CookieManager;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.JsonSerializer;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;

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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GetFoundAuctionsList", value = "/GetFoundAuctionsList")
@MultipartConfig
public class GetFoundAuctionsList extends HttpServlet {
    private Connection connection;

    public void init() throws UnavailableException {
        this.connection = ServletUtils.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);

        String search = request.getParameter("search");

        if(search == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing search parameter");
            return;
        }

        search = search.trim();

        List<Auction> auctions = new ArrayList<>();

        if(!search.isEmpty()) {
            try {
                auctions = auctionDAO.searchOpenAuctions(search);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.getWriter().println("Failure in database fetch: failed to search through open auctions");
                return;
            }
        }

        CookieManager.setLastAction(request, response, "search", ((User) request.getSession().getAttribute("user")).getUser_id());

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

    public void destroy() {
        try {
            ServletUtils.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
