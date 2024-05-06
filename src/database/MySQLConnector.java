package database;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnector {

    private static final String DATABASE = "library";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/" + DATABASE;
    private static final DataSource dataSource = getDataSource();

    public static DataSource getDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(CONNECTION_URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

}
