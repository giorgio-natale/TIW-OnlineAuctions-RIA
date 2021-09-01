package it.polimi.tiw.TIW_OnlineAuctions_RIA.controllers;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.beans.User;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.dao.UserDAO;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.JsonSerializer;
import it.polimi.tiw.TIW_OnlineAuctions_RIA.utils.ServletUtils;
import org.apache.commons.lang3.StringEscapeUtils;

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

@WebServlet(name = "CheckLogin", value = "/CheckLogin")
@MultipartConfig
public class CheckLogin extends HttpServlet {
    private Connection connection;

    public void init() throws UnavailableException {
        this.connection = ServletUtils.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email;
        String password;

        try {
            email = StringEscapeUtils.escapeJava(request.getParameter("email"));
            password = StringEscapeUtils.escapeJava(request.getParameter("password"));

            if (email == null || password == null || email.isEmpty() || password.isEmpty())
                throw new Exception("Email and password are required");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
            return;
        }

        UserDAO userDAO = new UserDAO(connection);
        User user;
        try {
            user = userDAO.authenticateUser(email, password);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().println("Failure in database credential checking");
            return;
        }

        if(user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Incorrect credentials");
        }
        else {
            request.getSession().setAttribute("user", user);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            User lightUser = new User();
            lightUser.setUser_id(user.getUser_id());
            lightUser.setFirst_name(user.getFirst_name());
            lightUser.setLast_name(user.getLast_name());
            lightUser.setLast_login(user.getLast_login());
            lightUser.setNew_user(user.isNew_user());

            String json = JsonSerializer.getInstance().toJson(lightUser, User.class);
            response.getWriter().println(json);
        }
    }

    public void destroy() {
        try {
            ServletUtils.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
