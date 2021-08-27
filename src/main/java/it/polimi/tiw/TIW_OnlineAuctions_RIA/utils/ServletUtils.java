package it.polimi.tiw.TIW_OnlineAuctions_RIA.utils;

import it.polimi.tiw.TIW_OnlineAuctions_RIA.exceptions.UnsupportedExtensionException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import javax.ws.rs.core.UriBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ServletUtils {

    public static Connection getConnection(ServletContext context) throws UnavailableException {
        Connection connection;

        try {
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");

            Class.forName(driver);

            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }

        return connection;
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null)
            connection.close();
    }

    public static Pair<TemplateEngine, Connection> setupServlet(ServletContext servletContext) throws UnavailableException {

        TemplateEngine templateEngine = new TemplateEngine();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);

        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        Connection connection = ServletUtils.getConnection(servletContext);

        return new Pair<>(templateEngine, connection);
    }

    public static String appendParameter(String baseUrl, String key, Object value){
        return UriBuilder.fromUri(baseUrl).queryParam(key, value).build().toString();
    }

    public static String appendParameters(String baseUrl, Map<String, Object> parameters){
        for(Map.Entry<String, Object> entry : parameters.entrySet()){
            baseUrl = appendParameter(baseUrl, entry.getKey(), entry.getValue());
        }
        return baseUrl;
    }

    public static String getRecognizedExtension(String s) throws UnsupportedExtensionException{
        int lastFullStopIndex = s.lastIndexOf(".");

        //TODO: add all supported extensions
        Set<String> supportedExtensions = new HashSet<>(){{
            add(".jpg");
            add(".jpeg");
            add(".png");
        }};
        try{
            String extension = s.substring(lastFullStopIndex);
            extension = extension.toLowerCase();
            if(!supportedExtensions.contains(extension)){
                throw new UnsupportedExtensionException("Unsupported extension");
            }
            return extension;
        }catch(IndexOutOfBoundsException e){
            throw new UnsupportedExtensionException("The file name has no extension");
        }
    }

}
