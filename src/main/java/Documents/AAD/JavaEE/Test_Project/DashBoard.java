package Documents.AAD.JavaEE.Test_Project;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

@WebServlet("/api/v1/dashboard")
public class DashBoard extends HttpServlet {
    @Resource(name = "jdbc/pool")
    private DataSource dataSource;


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



        try {
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection
                    .prepareStatement("SELECT * FROM employee").executeQuery();

            List<Map<String, String>> elist = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> event = new HashMap<>();
                event.put("id", resultSet.getString("id"));
                event.put("name", resultSet.getString("name"));
                event.put("email", resultSet.getString("email"));
                event.put("age", resultSet.getString("age"));
                event.put("address", resultSet.getString("address"));

                byte[] imageBytes = resultSet.getBytes("image");
                if (imageBytes != null && imageBytes.length > 0) {
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                    event.put("image", "data:image/png;base64," + base64Image);
                } else {
                    event.put("image", null);
                }
                elist.add(event);
            }

            resp.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getWriter(), elist);
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> event = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO employee (name,email,age,address,image) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setString(1, event.get("name"));
            stmt.setString(2, event.get("email"));
            stmt.setString(3, event.get("age"));
            stmt.setString(4, event.get("address"));

            String base64Image = event.get("image"); // You must send this from frontend
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                stmt.setBytes(5, imageBytes);
            } else {
                stmt.setNull(5, Types.BLOB);
            }


            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), rows);
            PrintWriter out = resp.getWriter();
            out.println(rows);
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> event = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE employee SET name=?, email=?, age=?, address=?, image=? WHERE id=?"
            );

            stmt.setString(1, event.get("name"));
            stmt.setString(2, event.get("email"));
            stmt.setString(3, event.get("age"));
            stmt.setString(4, event.get("address"));


            String base64Image = event.get("image");
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                stmt.setBytes(5, imageBytes);
            } else {
                stmt.setNull(5, Types.BLOB);
            }
            stmt.setString(6, event.get("id"));

            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), rows);
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> event = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM employee WHERE id=?"
            );
            stmt.setString(1, event.get("id"));

            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), rows);
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}