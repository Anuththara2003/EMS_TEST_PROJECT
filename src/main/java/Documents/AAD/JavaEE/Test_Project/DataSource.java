package Documents.AAD.JavaEE.Test_Project;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;


@WebListener
public class DataSource implements ServletContextListener {

    BasicDataSource dataSource ;
    @Override
    public void contextInitialized(ServletContextEvent sec) {

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/EmployeeMangeSystem");
        dataSource.setUsername("root");
        dataSource.setPassword("sandaru2003");
        dataSource.setInitialSize(50);
        dataSource.setMaxTotal(100);

        ServletContext servletContext = sec.getServletContext();
        servletContext.setAttribute("dataSource", dataSource);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sec) {
        try {
            ServletContext servletContext = sec.getServletContext();
            BasicDataSource dataSource = (BasicDataSource) servletContext.getAttribute("dataSource");
            dataSource.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


}
