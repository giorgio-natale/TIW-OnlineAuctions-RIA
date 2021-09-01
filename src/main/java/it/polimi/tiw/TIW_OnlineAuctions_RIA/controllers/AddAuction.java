package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.CookieManager;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@WebServlet(name = "AddAuction", value = "/AddAuction")
@MultipartConfig
public class AddAuction extends HttpServlet {

    private Connection connection;

    public void init() throws UnavailableException {
        this.connection = ServletUtils.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        //get parameters
        String auctionName;
        String auctionDescription;

        try {
            auctionName = StringEscapeUtils.escapeJava(request.getParameter("newAuction-auctionName"));
            auctionDescription = StringEscapeUtils.escapeJava(request.getParameter("newAuction-auctionDescription"));

            if(auctionName == null || auctionName.isEmpty())
                throw new IllegalArgumentException("Missing name");

            if(auctionDescription == null || auctionDescription.isEmpty())
                throw new IllegalArgumentException("Missing description");

            if(auctionName.length() > 255)
                throw new IllegalArgumentException("Name too long");

            if(auctionDescription.length() > 6000)
                throw new IllegalArgumentException("Description too long");

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
            return;
        }

        double auctionStartingPrice, auctionPriceGap;
        try {
            auctionStartingPrice = Double.parseDouble(request.getParameter("newAuction-auctionStartingPrice"));
            auctionPriceGap = Double.parseDouble(request.getParameter("newAuction-auctionPriceGap"));

            if(auctionStartingPrice < 1.0 || auctionStartingPrice > 999_999.99)
                throw new IllegalArgumentException("Invalid starting price");

            if(auctionPriceGap < 1.0 || auctionPriceGap > 999_999.99)
                throw new IllegalArgumentException("Invalid price gap");

        } catch (NullPointerException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
            return;
        }

        //get date, hour and minute
        String dateString;
        try {
            dateString = StringEscapeUtils.escapeJava(request.getParameter("newAuction-auctionEndDate"));

            if (dateString == null || dateString.isEmpty())
                throw new IllegalArgumentException("End date is missing");

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
            return;
        }

        int hour, minute;
        try {
            hour = Integer.parseInt(request.getParameter("newAuction-auctionEndHour"));
            minute = Integer.parseInt(request.getParameter("newAuction-auctionEndMinute"));

            if(hour < 0 || hour > 23)
                throw new IllegalArgumentException("Invalid end hour");

            if(minute < 0 || minute > 59)
                throw new IllegalArgumentException("Invalid end minute");

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
            return;
        }

        Instant endDate;
        try {
            DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .toFormatter()
                .withZone(ZoneId.of("Europe/Rome"));
            endDate = FMT.parse(dateString + "T" + ((hour >= 10) ? hour : "0" + hour) + ":" + ((minute >= 10) ? minute : "0" + minute), Instant::from);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("End date cannot be parsed");
            return;
        }

        if(endDate.isBefore(Instant.now().plusSeconds(24 * 60 * 60)) || endDate.isAfter(Instant.now().plusSeconds(366 * 24 * 60 * 60))){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("End date must be between tomorrow and one year in the future");
            return;
        }

        //if the part is not specified, filePart is null
        Part filePart = request.getPart("newAuction-auctionImage");
        if(filePart != null && filePart.getSize() > 0) {
            String contentType = filePart.getContentType();

            if(!contentType.startsWith("image")){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Tried to upload a invalid file");
                return;
            }
        }
        else {
            filePart = null;
        }

        AuctionDAO auctionDao = new AuctionDAO(connection);
        String outputPath = getServletContext().getInitParameter("uploadFolder");

        try {
            auctionDao.addNewAuction(
                ((User) request.getSession().getAttribute("user")).getUser_id(),
                auctionName,
                auctionDescription,
                auctionStartingPrice,
                auctionPriceGap,
                endDate,
                outputPath,
                filePart
            );
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
            return;
        }

        CookieManager.setLastAction(request, response, "create", ((User) request.getSession().getAttribute("user")).getUser_id());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println("Auction created");
    }

    public void destroy() {
        try {
            ServletUtils.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
