package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.AuctionDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;

import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "GetImage", value = "/GetImage")
@MultipartConfig
public class GetImage extends HttpServlet {
    private Connection connection;

    public void init() throws UnavailableException {
        this.connection = ServletUtils.getConnection(getServletContext());
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int auctionId;
        try {
            auctionId = Integer.parseInt(request.getParameter("auctionId"));
        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Auction id not specified");
            return;
        }

        AuctionDAO auctionDAO = new AuctionDAO(connection);

        String fileName;

        try {
            fileName = auctionDAO.getImageName(auctionId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().write("Failure in database fetch: get image from auction");
            return;
        }

        if(fileName == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Can't find the auction with the specified id");
            return;
        }

        String folderPath = getServletContext().getInitParameter("uploadFolder");

        File file = new File(folderPath, fileName);

        if(!file.exists() || file.isDirectory()){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("File not found");
            return;
        }

        //set headers of the response
        response.setHeader("Content-Type", getServletContext().getMimeType(fileName));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

        Files.copy(file.toPath(), response.getOutputStream());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
