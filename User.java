
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

public class User {
  // The recommended format of a connection URL is the long format with the
  // connection descriptor.
  final static String DB_URL= "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";

  final static String DB_USER = "c##manuelpartidagomez";
  final static String DB_PASSWORD = "9599663";
    //test
 /*
  * The method gets a database connection using
  * oracle.jdbc.pool.OracleDataSource. It also sets some connection
  * level properties, such as,
  * OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH,
  * OracleConnection.CONNECTION_PROPERTY_THIN_NET_CHECKSUM_TYPES, etc.,
  * There are many other connection related properties. Refer to
  * the OracleConnection interface to find more.
  */
  public static void main(String args[]) throws SQLException {
    Properties info = new Properties();
    info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
    info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);
    info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");


    OracleDataSource ods = new OracleDataSource();
    ods.setURL(DB_URL);
    ods.setConnectionProperties(info);

    // With AutoCloseable, the connection is closed automatically.
    try (OracleConnection connection = (OracleConnection) ods.getConnection()) {
      // Get the JDBC driver name and version
      DatabaseMetaData dbmd = connection.getMetaData();

      AddEmployees(connection);
    }
  }
 /*
  * Adds employee to the employees table.
  */
  public static void AddEmployees(Connection connection) throws SQLException {
    // Statement and ResultSet are AutoCloseable and closed automatically.
    String sql = "INSERT INTO Customer " +
                   "VALUES (42000,'Yovany Mejia',2020,'6749 Trigo Rd Unit B')";
    try (Statement statement = connection.createStatement()) {
    //ResultSet resultSet = statement;
    statement.executeUpdate(sql);
    System.out.println("successful insert value");

    }
  }
}
