package Documents.AAD.JavaEE.Test_Project;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/v1/signin")
public class SignInServlet extends HttpServlet {

    @Resource(name = "jdbc/pool")
    DataSource dataSource;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> credentials = mapper.readValue(req.getInputStream(), Map.class);

        String email = credentials.get("email");
        String password = credentials.get("password");

        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM user WHERE email = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();

            resp.setContentType("application/json");

            if (resultSet.next()) {
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("message", "Login successful");
                mapper.writeValue(resp.getWriter(), responseMap);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                mapper.writeValue(resp.getWriter(), Map.of("message", "Invalid email or password"));
            }

            stmt.close();
            resultSet.close();

        } catch (SQLException e) {
            new RuntimeException(e);
        }

    }
}
