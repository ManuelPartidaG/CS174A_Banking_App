import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;


public class User{
//private String taxid;
//private byte pinHash[];
//private Arraylist<Account> accounts;

final static String DB_URL= "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";

final static String DB_USER = "c##manuelpartidagomez";
final static String DB_PASSWORD = "9599663";




  public static void main(String args[]) throws SQLException, ClassNotFoundException {

    try {Class.forName("oracle.jdbc.driver.OracleDriver");}
    catch (ClassNotFoundException e){
      System.out.println("Something went wrong1.");
    }
    Properties info = new Properties();
    info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
    info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);
    info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");


    OracleDataSource ods = new OracleDataSource();
    ods.setURL(DB_URL);
    ods.setConnectionProperties(info);


    try (OracleConnection connection = (OracleConnection) ods.getConnection()){
      printCustomers(connection);

    }

    catch (SQLException e) {
      System.out.println("Something went wrong2.");
    }
}


public static void printCustomers(Connection connection) throws SQLException {
  // Statement and ResultSet are AutoCloseable and closed automatically.

  try {
  PreparedStatement statement = connection.prepareStatement(" instert into customer (taxid,name,pin,address) values (?,'?',?,'?')");
    try {
      //ResultSet resultSet = statement
      //  .executeQuery("INSERT INTO Customer (TAXID,NAME,PIN,ADDRESS) VALUES (9599663,'Manuel Partida ',4546,'6749 Trigo Unit B');");



      statement.setInt (1, 9599663);
      statement.setString (2, "Manuel Partida Gomez");
      statement.setInt   (3, 1010);
      statement.setString(4, "6749 Trigo Rd Unit B");

      ResultSet answerSet = statement.executeQuery();
      connection.close();
    }

catch (SQLException e) {
  System.out.println("Something went wrong3.");

}

} catch (SQLException e) {
  System.out.println("Something went wrong4.");
}
}
}
