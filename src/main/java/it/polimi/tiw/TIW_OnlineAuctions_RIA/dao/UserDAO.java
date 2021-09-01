package it.polimi.tiw.TIW_OnlineAuctions_RIA.dao;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User checkCredentials(String email, String password) throws SQLException {
        String query =
                "SELECT user_id, email, first_name, last_name, street, city, province, zip_code, other_address_infos " +
                "FROM user " +
                "WHERE email = ? AND password = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (!result.isBeforeFirst())
                    return null;
                else {
                    result.next();
                    User user = new User();

                    user.setUser_id(result.getInt("user_id"));
                    user.setEmail(result.getString("email"));
                    user.setFirst_name(result.getString("first_name"));
                    user.setLast_name(result.getString("last_name"));
                    user.setStreet(result.getString("street"));
                    user.setCity(result.getString("city"));
                    user.setProvince(result.getString("province"));
                    user.setZip_code(result.getString("zip_code"));
                    user.setOther_address_infos(result.getString("other_address_infos"));

                    return user;
                }
            }
        }
    }

    public User authenticateUser(String email, String password) throws SQLException {
        User user = checkCredentials(email, password);

        connection.setAutoCommit(false);

        if(user != null) {

            String query1 =
                    "SELECT (last_login IS NULL) AS new_user " +
                    "FROM user " +
                    "WHERE user_id = ?;";

            String query2 =
                    "UPDATE user " +
                    "SET last_login = NOW() " +
                    "WHERE user_id = ?;";

            String query3 =
                    "SELECT last_login " +
                    "FROM user " +
                    "WHERE user_id = ?;";
            try {

                try (PreparedStatement preparedStatement = connection.prepareStatement(query1)) {
                    preparedStatement.setInt(1, user.getUser_id());

                    try (ResultSet result = preparedStatement.executeQuery()) {

                        if (!result.isBeforeFirst()) {
                            connection.setAutoCommit(true);
                            return null;
                        } else {
                            result.next();
                            user.setNew_user(result.getBoolean("new_user"));
                        }

                    }
                }

                try (PreparedStatement preparedStatement = connection.prepareStatement(query2)) {
                    preparedStatement.setInt(1, user.getUser_id());

                    if (preparedStatement.executeUpdate() != 1) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                        return null;
                    }
                }

                try (PreparedStatement preparedStatement = connection.prepareStatement(query3)) {
                    preparedStatement.setInt(1, user.getUser_id());

                    try (ResultSet result = preparedStatement.executeQuery()) {
                        if (!result.isBeforeFirst()) {
                            connection.rollback();
                            connection.setAutoCommit(true);
                            return null;
                        } else {
                            result.next();
                            user.setLast_login(result.getTimestamp("last_login").toInstant());

                            connection.commit();
                        }
                    }
                }
            }catch(SQLException e){
                connection.rollback();
                connection.setAutoCommit(true);
                throw e;
            }
        }

        connection.setAutoCommit(true);

        return user;
    }

    public User getUserDetails(int userID) throws SQLException {
        String query =
                "SELECT user_id, email, first_name, last_name, street, city, province, zip_code, other_address_infos, last_login " +
                "FROM user " +
                "WHERE user_id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                if(!result.isBeforeFirst())
                    return null;

                result.next();

                User user = new User();
                user.setUser_id(result.getInt("user_id"));
                user.setEmail(result.getString("email"));
                user.setFirst_name(result.getString("first_name"));
                user.setLast_name(result.getString("last_name"));
                user.setStreet(result.getString("street"));
                user.setCity(result.getString("city"));
                user.setProvince(result.getString("province"));
                user.setZip_code(result.getString("zip_code"));
                user.setOther_address_infos(result.getString("other_address_infos"));
                user.setLast_login(result.getTimestamp("last_login").toInstant());

                return user;
            }
        }
    }
}
