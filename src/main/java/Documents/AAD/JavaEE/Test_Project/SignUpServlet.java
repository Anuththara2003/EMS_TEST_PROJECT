package Documents.AAD.JavaEE.Test_Project;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;



@WebServlet("/api/v1/signup")
public class SignUpServlet extends HttpServlet {

    @Resource(name = "jdbc/pool")
    private DataSource dataSource ;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //save user in database

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> user = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO user (name,email,password) VALUES (?, ?, ?)"
            );

            stmt.setString(1, user.get("name"));
            stmt.setString(2, user.get("email"));
            stmt.setString(3, user.get("password"));


            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), rows);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
