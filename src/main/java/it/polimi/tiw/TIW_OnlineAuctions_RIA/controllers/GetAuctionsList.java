package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
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

@WebServlet(name = "GetAuctionsList", value = "/GetAuctionsList")
@MultipartConfig
public class GetAuctionsList extends HttpServlet {
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

        String type = request.getParameter("type");

        if(type == null || type.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid request");
            return;
        }

        List<Auction> auctions;

        switch (type) {
            case "open":
                try {
                    auctions = auctionDAO.getAuctionsByUser(
                            ((User) request.getSession().getAttribute("user")).getUser_id(),
                            false
                    );
                } catch (SQLException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                    response.getWriter().println("Failure in database fetch: failed to gather "  + type + " auctions");
                    return;
                }
                break;

            case "closed":
                try {
                    auctions = auctionDAO.getAuctionsByUser(
                            ((User) request.getSession().getAttribute("user")).getUser_id(),
                            true
                    );
                } catch (SQLException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                    response.getWriter().println("Failure in database fetch: failed to gather "  + type + " auctions");
                    return;
                }
                break;

            case "won":
                try {
                    auctions = auctionDAO.getWonAuctions(
                            ((User) request.getSession().getAttribute("user")).getUser_id()
                    );
                } catch (SQLException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                    response.getWriter().println("Failure in database fetch: failed to gather "  + type + " auctions");
                    return;
                }
                break;

            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid request");
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
