package it.polimi.tiw.TIW_OnlineAuctions_RIA.dao;


import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Auction;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.exceptions.UnsupportedExtensionException;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO {
    private Connection connection;

    public AuctionDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Auction> searchOpenAuctions(String searchTerms) throws SQLException {
        String query =
                "SELECT auction.auction_id, auction.user_id, name, description, image, starting_price, min_price_gap, end_date, closed, (NOW() > end_date) AS expired, winning_price, auction_winner.user_id as winner_id " +
                "FROM auction_winner RIGHT JOIN auction ON auction_winner.auction_id = auction.auction_id " +
                "WHERE closed = false AND NOW() < end_date AND (name LIKE CONCAT('%', ?, '%') OR description LIKE CONCAT('%', ?, '%')) " +
                "ORDER BY end_date ASC;";   // TODO: change to DESC just in case

        List<Auction> auctions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, searchTerms);
            preparedStatement.setString(2, searchTerms);

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Auction auction = new Auction();
                    auction.setAuction_id(result.getInt("auction_id"));
                    auction.setUser_id(result.getInt("user_id"));
                    auction.setName(result.getString("name"));
                    auction.setDescription(result.getString("description"));
                    auction.setImage(result.getString("image"));
                    auction.setStarting_price(result.getDouble("starting_price"));
                    auction.setMin_price_gap(result.getDouble("min_price_gap"));
                    auction.setEnd_date(result.getTimestamp("end_date").toInstant());
                    auction.setClosed(result.getBoolean("closed"));
                    auction.setExpired(result.getBoolean("expired"));
                    auction.setWinning_price(result.getDouble("winning_price"));
                    auction.setWinner_id(result.getInt("winner_id"));

                    auctions.add(auction);
                }
            }
        }

        return auctions;
    }

    public List<Auction> getWonAuctions(int userID) throws SQLException {
        String query =
                "SELECT auction.auction_id, auction.user_id, name, description, image, starting_price, min_price_gap, end_date, closed, (NOW() > end_date) AS expired, winning_price, auction_winner.user_id as winner_id " +
                "FROM auction_winner RIGHT JOIN auction ON auction_winner.auction_id = auction.auction_id " +
                "WHERE closed = true AND auction_winner.user_id = ?;";

        List<Auction> auctions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userID);

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Auction auction = new Auction();
                    auction.setAuction_id(result.getInt("auction_id"));
                    auction.setUser_id(result.getInt("user_id"));
                    auction.setName(result.getString("name"));
                    auction.setDescription(result.getString("description"));
                    auction.setImage(result.getString("image"));
                    auction.setStarting_price(result.getDouble("starting_price"));
                    auction.setMin_price_gap(result.getDouble("min_price_gap"));
                    auction.setEnd_date(result.getTimestamp("end_date").toInstant());
                    auction.setClosed(result.getBoolean("closed"));
                    auction.setExpired(result.getBoolean("expired"));
                    auction.setWinning_price(result.getDouble("winning_price"));
                    auction.setWinner_id(result.getInt("winner_id"));

                    auctions.add(auction);
                }
            }
        }

        return auctions;
    }

    public List<Auction> getAuctionsByUser(int userID, boolean closedOrExpired) throws SQLException {
        String query =
                "SELECT auction.auction_id, auction.user_id, name, description, image, starting_price, min_price_gap, end_date, closed, (NOW() > end_date) AS expired, winning_price, auction_winner.user_id as winner_id " +
                "FROM auction_winner RIGHT JOIN auction ON auction_winner.auction_id = auction.auction_id " +
                "WHERE (? XOR (NOW() < end_date)) AND auction.user_id = ? " +
                "ORDER BY end_date ASC;";

        List<Auction> auctions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, closedOrExpired);
            preparedStatement.setInt(2, userID);

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Auction auction = new Auction();
                    auction.setAuction_id(result.getInt("auction_id"));
                    auction.setUser_id(result.getInt("user_id"));
                    auction.setName(result.getString("name"));
                    auction.setDescription(result.getString("description"));
                    auction.setImage(result.getString("image"));
                    auction.setStarting_price(result.getDouble("starting_price"));
                    auction.setMin_price_gap(result.getDouble("min_price_gap"));
                    auction.setEnd_date(result.getTimestamp("end_date").toInstant());
                    auction.setClosed(result.getBoolean("closed"));
                    auction.setExpired(result.getBoolean("expired"));
                    auction.setWinning_price(result.getDouble("winning_price"));
                    auction.setWinner_id(result.getInt("winner_id"));

                    auctions.add(auction);
                }
            }
        }

        return auctions;
    }

    public Auction getAuctionDetails(int auctionID) throws SQLException {
        String query =
                "SELECT auction.auction_id, auction.user_id, name, description, image, starting_price, min_price_gap, end_date, closed, (NOW() > end_date) AS expired, winning_price, auction_winner.user_id as winner_id " +
                "FROM auction_winner RIGHT JOIN auction ON auction_winner.auction_id = auction.auction_id " +
                "WHERE auction.auction_id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, auctionID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                if(!result.isBeforeFirst())
                    return null;

                result.next();

                Auction auction = new Auction();
                auction.setAuction_id(result.getInt("auction_id"));
                auction.setUser_id(result.getInt("user_id"));
                auction.setName(result.getString("name"));
                auction.setDescription(result.getString("description"));
                auction.setImage(result.getString("image"));
                auction.setStarting_price(result.getDouble("starting_price"));
                auction.setMin_price_gap(result.getDouble("min_price_gap"));
                auction.setEnd_date(result.getTimestamp("end_date").toInstant());
                auction.setClosed(result.getBoolean("closed"));
                auction.setExpired(result.getBoolean("expired"));
                auction.setWinning_price(result.getDouble("winning_price"));
                auction.setWinner_id(result.getInt("winner_id"));

                return auction;
            }
        }
    }

    public List<Auction> getAuctionDetails(List<Integer> auctionIDs) throws SQLException {
        StringBuilder sb = new StringBuilder();

        String sep = "";
        for(Integer id : auctionIDs){
            sb.append(sep).append(id);
            sep = ",";
        }

        String idListString = sb.toString();

        String query =
                "SELECT auction.auction_id, auction.user_id, name, description, image, starting_price, min_price_gap, end_date, closed, (NOW() > end_date) AS expired, winning_price, auction_winner.user_id as winner_id " +
                "FROM auction_winner RIGHT JOIN auction ON auction_winner.auction_id = auction.auction_id " +
                "WHERE closed = FALSE AND (NOW() > auction.end_date) = FALSE AND auction.auction_id IN (" + idListString+ ") ORDER BY FIELD(auction.auction_id, "+ idListString + ");";


        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            try (ResultSet result = preparedStatement.executeQuery()) {

                if(!result.isBeforeFirst())
                    return null;

                List<Auction> auctions = new ArrayList<>();
                while(result.next()) {
                    Auction auction = new Auction();
                    auction.setAuction_id(result.getInt("auction_id"));
                    auction.setUser_id(result.getInt("user_id"));
                    auction.setName(result.getString("name"));
                    auction.setDescription(result.getString("description"));
                    auction.setImage(result.getString("image"));
                    auction.setStarting_price(result.getDouble("starting_price"));
                    auction.setMin_price_gap(result.getDouble("min_price_gap"));
                    auction.setEnd_date(result.getTimestamp("end_date").toInstant());
                    auction.setClosed(result.getBoolean("closed"));
                    auction.setExpired(result.getBoolean("expired"));
                    auction.setWinning_price(result.getDouble("winning_price"));
                    auction.setWinner_id(result.getInt("winner_id"));
                    auctions.add(auction);
                }
                return auctions;
            }
        }

    }

    public String getImageName(int auctionID) throws SQLException{
        String query =
                "SELECT image " +
                "FROM auction " +
                "WHERE auction_id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, auctionID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                if(!result.isBeforeFirst())
                    return null;

                result.next();

                return result.getString("image");
            }
        }
    }

    public void closeAuction (int auctionID) throws SQLException {
        String query =
                "UPDATE auction " +
                "SET closed = true " +
                "WHERE auction_id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, auctionID);

            if(preparedStatement.executeUpdate() != 1)
                throw new SQLException("Error closing auction");
        }
    }

    public void addNewAuction(int userId, String name, String description, double startingPrice, double minPriceGap, Instant endDate, String uploadPath, Part filePart) throws SQLException, IOException {

        String query =
                "INSERT INTO auction (user_id, name, description, starting_price, min_price_gap, end_date, closed) " +
                "VALUES (?, ?, ?, ?, ?, ?, false)";

        connection.setAutoCommit(false);

        int newId;

        //create a new auction without the image
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setDouble(4, startingPrice);
            preparedStatement.setDouble(5, minPriceGap);
            preparedStatement.setTimestamp(6, Timestamp.from(endDate));

            if(preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Error closing auction");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    newId = generatedKeys.getInt(1);
                }else{
                    throw new SQLException("Retrieving the last created id failed");
                }
            }

            //add the image field with the correct id and extension
            String updateQuery = "UPDATE auction SET image = ? WHERE auction_id = ?;";
            try(PreparedStatement imageUpdateStatement = connection.prepareStatement(updateQuery)){
                String extension;
                if(filePart != null) {
                    try{
                        extension = ServletUtils.getRecognizedExtension(filePart.getSubmittedFileName());
                        imageUpdateStatement.setString(1, newId + extension);

                        //create the file
                        File file = new File(uploadPath + newId + extension);
                        try (InputStream fileContent = filePart.getInputStream()) {
                            Files.copy(fileContent, file.toPath());
                        }catch (IOException e) {
                            throw new IOException("Cannot create the file");
                        }

                    }catch(UnsupportedExtensionException e){
                        throw new IOException("The submitted file had an unsupported exception");
                    }
                }else{
                    imageUpdateStatement.setString(1, "default.png");
                }
                imageUpdateStatement.setInt(2, newId);

                if(imageUpdateStatement.executeUpdate() != 1){
                    throw new SQLException("Error while updating image field");
                }
            }

            connection.commit();

        }catch(IOException | SQLException e){
            connection.rollback();
            throw e;
        }

        finally{
            connection.setAutoCommit(true);
        }

    }

    public List<Integer> clearClosedAndExpiredAuctions (List<Integer> auctions) throws SQLException {
        StringBuilder str = new StringBuilder();

        String sep = "";
        for(int i : auctions) {
            str.append(sep).append(i);
            sep = ",";
        }

        String query =
                "SELECT DISTINCT auction_id " +
                "FROM auction " +
                "WHERE auction_id IN (" + str + ") AND closed = false AND (NOW() > end_date) = false " +
                "ORDER BY FIELD(auction_id," + str + ");";

        List<Integer> openAuctions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet result = preparedStatement.executeQuery()) {

                while (result.next()) {
                    openAuctions.add(result.getInt("auction_id"));
                }

                return openAuctions;
            }
        }
    }
}

