package it.polimi.tiw.TIW_OnlineAuctions_RIA.dao;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.Bid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidDAO {
    private Connection connection;

    public BidDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Bid> getAuctionBids(int auctionID) throws SQLException {
        String query =
                "SELECT auction_id, user_id, first_name, last_name, bid_time, price " +
                "FROM bid NATURAL JOIN user " +
                "WHERE auction_id = ? " +
                "ORDER BY bid_time DESC;";

        List<Bid> bids = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, auctionID);

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Bid bid = new Bid();
                    bid.setAuction_id(result.getInt("auction_id"));
                    bid.setUser_id(result.getInt("user_id"));
                    bid.setUser_first_name(result.getString("first_name"));
                    bid.setUser_last_name(result.getString("last_name"));
                    bid.setBid_time(result.getTimestamp("bid_time").toInstant());
                    bid.setPrice(result.getDouble("price"));

                    bids.add(bid);
                }
            }
        }

        return bids;
    }

    public void addNewBid(int userID, int auctionID, double price) throws SQLException {
        String query =
                "INSERT INTO bid (auction_id, user_id, bid_time, price) VALUES (?, ?, NOW(), ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, auctionID);
            preparedStatement.setInt(2, userID);
            preparedStatement.setDouble(3, price);

            if(preparedStatement.executeUpdate() != 1)
                throw new SQLException("Error adding new bid");
        }
    }
}
