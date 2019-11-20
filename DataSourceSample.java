import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

public class DataSourceSample {  
  // The recommended format of a connection URL is the long format with the
  // connection descriptor.
  final static String DB_URL= "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
  // For ATP and ADW - use the TNS Alias name along with the TNS_ADMIN when using 18.3 JDBC driver
  // final static String DB_URL="jdbc:oracle:thin:@wallet_dbname?TNS_ADMIN=/Users/test/wallet_dbname";
  // In case of windows, use the following URL 
  // final static String DB_URL="jdbc:oracle:thin:@wallet_dbname?TNS_ADMIN=C:\\Users\\test\\wallet_dbname";
  final static String DB_USER = "c##rweinreb";
  final static String DB_PASSWORD = "5379052";

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
      System.out.println("Driver Name: " + dbmd.getDriverName());
      System.out.println("Driver Version: " + dbmd.getDriverVersion());
      // Print some connection properties
      System.out.println("Default Row Prefetch Value is: " + 
         connection.getDefaultRowPrefetch());
      System.out.println("Database Username is: " + connection.getUserName());
      System.out.println();
      // Perform a database operation 
      //createTables(connection);
      //populate_customers("customers.csv",connection);
      //populate_accounts("accounts.csv",connection);
      try(Statement statement =connection.createStatement()){
      try (ResultSet resultSet = statement
          .executeQuery("select branch from Account")) {
        while (resultSet.next())
	    System.out.println(resultSet.getString(1));       
      }
      }
    }   
  }
    public static void createTables(Connection connection) throws SQLException {
    // Statement and ResultSet are AutoCloseable and closed automatically.
        String createCustomer = "CREATE TABLE Customer(" +
                            "name VARCHAR(40)," +
                            "taxid INTEGER," +
                            "address VARCHAR(40)," + 
                            "PIN INTEGER," +
                            "PRIMARY KEY(taxid))";

	String createAccount = "CREATE TABLE Account("+
                            "branch VARCHAR(100),"+
                            "aid INTEGER, "+
                            "acc_type VARCHAR(100), "+
                            "balance REAL, "+
                            "interest_rate  REAL, "+
                            "interest REAL, "+
                            "PRIMARY KEY(aid))";

	String createTransaction_Performed =" CREATE TABLE Transaction_Preformed("+
                                      "tid INTEGER, "+
                                      "tdate DATE, "+
                                      "trans_type VARCHAR(40),"+
                                      "Amount REAL,"+
                                      "tfee REAL,"+
                                      "Checknum INTEGER,"+
                                      "Acc_to INTEGER NOT NULL,"+
                                      "Acc_from INTEGER,"+
                                      "PRIMARY KEY(tid,Acc_to,Acc_from),"+
                                      "FOREIGN KEY(Acc_to) REFERENCES Account(aid), "+
                                      "FOREIGN KEY(Acc_from) REFERENCES Account(aid))";

       String createOwns = "CREATE TABLE Owns("+
                      "taxid INTEGER NOT NULL, "+
                      "aid INTEGER,"+
                      "PRIMARY KEY(aid),"+
                      "FOREIGN KEY(aid) REFERENCES Account(aid),"+
                      "FOREIGN KEY (taxid) REFERENCES Customer(taxid))";

      String createCo_owns= "CREATE TABLE Co_owns("+
                      "aid INTEGER,"+
                      "taxid INTEGER,"+
                      "PRIMARY KEY (aid, taxid),"+
                      "FOREIGN KEY(aid) REFERENCES Account(aid),"+
                      "FOREIGN KEY (taxid) REFERENCES Customer(taxid))";

      String createPocket = "CREATE TABLE Pocket("+ 
                      "aid INTEGER,"+
                      "pocket_fee REAL,"+
                      "PRIMARY KEY(aid),"+
                      "FOREIGN KEY(aid) REFERENCES Account(aid) ON DELETE CASCADE)";

      String createLinks= "CREATE TABLE Links("+
                    "paid INTEGER,"+
                    "aid INTEGER NOT NULL,"+
                    "PRIMARY KEY(paid),"+
                    "FOREIGN KEY (aid) REFERENCES Account(aid),"+
                    "FOREIGN KEY(paid) REFERENCES Pocket(aid))";
      
    try (Statement statement = connection.createStatement()) {
	statement.executeUpdate("DROP TABLE Customer");
	statement.executeUpdate(createCustomer);
	//statement.executeUpdate("DROP TABLE Account");
        statement.executeUpdate(createAccount);
	// statement.executeUpdate("DROP TABLE Transaction_Performed");
	statement.executeUpdate(createTransaction_Performed);
	//statement.executeUpdate("DROP TABLE Owns");
	statement.executeUpdate(createOwns);
	//	statement.executeUpdate("DROP TABLE Co_owns");
	statement.executeUpdate(createCo_owns);
	//statement.executeUpdate("DROP TABLE Pocket");
	statement.executeUpdate(createPocket);
	//statement.executeUpdate("DROP TABLE Links");
	statement.executeUpdate(createLinks);
	System.out.println("successfully created tables");
	//we need to drop the tables once they have been made, but calling drop before they exists results in an error 
	
      
    }   
  } 

    public static void populate_customers(String filename, Connection connection)throws SQLException{
	String line="";
	try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	    while ((line = br.readLine()) != null) {
		String[] columns = line.split(",");

		String name = parse(columns[0]);
		String taxID = parse(columns[1]);
		String address = parse(columns[2]);
		String pin = parse(columns[3]);
		// String pin = parse(columns[3]);

		String query = "insert into Customer (name, taxID, address, pin) values ("+
		    name+", " + taxID + ", " + address + ", " + pin + ")";
		try(Statement statement = connection.createStatement()){
		    statement.executeUpdate(query);
		    //System.out.println("successful insertion");
		}

      }
    }
    catch (IOException e) {
    e.printStackTrace();
    }
  }

 public static void populate_transactions(String filename, Connection connection)throws SQLException{
    String line="";
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      while ((line = br.readLine()) != null) {
        String[] columns = line.split(",");

        String tid = parse(columns[0]);
        String tdate = parse(columns[1]);
        String trans_type = parse(columns[2]);
        String amount = parse(columns[3]);
        String tfee = parse(columns[4]);
	String checknum = parse(columns[5]);
	String acc_to = parse(columns[6]);
	String acc_from = parse(columns[7]);

        String query = "insert into Transaction_Performed (tid, tdate, trans_type, Amount, tfee, Checknum, Acc_to, Acc_from) values ("+
	    tid+", " + tdate + ", " + trans_type + ", " + amount + ", " + tfee + ", " + checknum + ", " + acc_to + ", " + acc_from + ")";
	try(Statement statement = connection.createStatement()){
	    statement.executeUpdate(query);
	    System.out.println("successful insertion trans");
	}
        

      }
    }
    catch (IOException e) {
    e.printStackTrace();
    }
  }

    public static void populate_accounts(String filename, Connection connection)throws SQLException{
	String line="";
	try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	    while ((line = br.readLine()) != null) {
		String[] columns = line.split(",");

		String branch = parse(columns[0]);
		String aid = parse(columns[1]);
		String acc_type = parse(columns[2]);
		String balance = parse(columns[3]);
		String interest_rate = parse(columns[4]);
		String interest = parse(columns[5]);
	
		String query = "insert into Account (branch, aid, acc_type, balance, interest_rate, interest) values ("+
		    branch+", " + aid + ", " + acc_type + ", " + balance + ", " + interest_rate +", " + interest + ")";
		try(Statement statement = connection.createStatement()){
		    statement.executeUpdate(query);
		    //System.out.println("successful insertion");
		}
        

	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }    


    public static void populate_owns(String filename, Connection connection)throws SQLException{
	String line="";
	try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	    while ((line = br.readLine()) != null) {
		String[] columns = line.split(",");

		String taxid = parse(columns[0]);
		String aid = parse(columns[1]);
				
		String query = "insert into Owns (taxid, aid) values ("+
		    taxid+", " + aid + ")";
		try(Statement statement = connection.createStatement()){
		    statement.executeUpdate(query);
		    //System.out.println("successful insertion");
		}
        
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void populate_co_owns(String filename, Connection connection)throws SQLException{
	String line="";
	try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	    while ((line = br.readLine()) != null) {
		String[] columns = line.split(",");

		String taxid = parse(columns[1]);
		String aid = parse(columns[0]);
				
		String query = "insert into Co_owns (aid, taxid) values ("+
		    aid+", " + taxid + ")";
		try(Statement statement = connection.createStatement()){
		    statement.executeUpdate(query);
		    //System.out.println("successful insertion");
		}
        
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

     public static void populate_pocket(String filename, Connection connection)throws SQLException{
	String line="";
	try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	    while ((line = br.readLine()) != null) {
		String[] columns = line.split(",");

		String pocket_fee = parse(columns[1]);
		String aid = parse(columns[0]);
				
		String query = "insert into Pocket (aid, pocket_fee) values ("+
		    aid +", " + pocket_fee + ")";
		try(Statement statement = connection.createStatement()){
		    statement.executeUpdate(query);
		    //System.out.println("successful insertion");
		}
        
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void populate_links(String filename, Connection connection)throws SQLException{
	String line="";
	try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	    while ((line = br.readLine()) != null) {
		String[] columns = line.split(",");

		String paid = parse(columns[0]);
		String aid = parse(columns[1]);
				
		String query = "insert into Links (paid, aid) values ("+
		    paid+", " + aid + ")";
		try(Statement statement = connection.createStatement()){
		    statement.executeUpdate(query);
		    //System.out.println("successful insertion");
		}
        
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    public static String parse(String s){
	return "'" + s.replace("'", "''") + "'";
    }
}
