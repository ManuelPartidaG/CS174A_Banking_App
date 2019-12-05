
package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.math.RoundingMode;
import java.sql.*;
import java.lang.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Calendar;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

/**
* The most important class for your application.
* DO NOT CHANGE ITS SIGNATURE.
*/
public class App implements Testable
{
  private OracleConnection _connection;
  public static Scanner scan = new Scanner(System.in);                  // Example connection object to your DB.

  /**
   * Default constructor.
   * DO NOT REMOVE.
   */
  App()
  {
      // TODO: Any actions you need.
  }

  /**
   * This is an example access operation to the DB.
   */
  void exampleAccessToDB()
  {
      // Statement and ResultSet are AutoCloseable and closed automatically.
      try( Statement statement = _connection.createStatement() )
      {
          try( ResultSet resultSet = statement.executeQuery( "select owner, table_name from all_tables" ) )
          {
              while( resultSet.next() )
                  System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
          }
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
      }
  }

  ////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
  // Check the Testable.java interface for the function signatures and descriptions.

  @Override
  public String initializeSystem()
  {
      // Some constants to connect to your DB.
      final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
      final String DB_USER = "c##manuelpartidagomez";
      final String DB_PASSWORD = "9599663";

      // Initialize your system.  Probably setting up the DB connection.
      Properties info = new Properties();
      info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
      info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
      info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

      try
      {
          OracleDataSource ods = new OracleDataSource();
          ods.setURL( DB_URL );
          ods.setConnectionProperties( info );
          _connection = (OracleConnection) ods.getConnection();

          // Get the JDBC driver name and version.
          DatabaseMetaData dbmd = _connection.getMetaData();
          System.out.println( "Driver Name: " + dbmd.getDriverName() );
          System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

          // Print some connection properties.
          System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
          System.out.println( "Database Username is: " + _connection.getUserName() );
          System.out.println();

          return "0";
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
          return "1";
      }
  }


  /**
   * Destroy all of the tables in your DB.
   * @return a string "r", where r = 0 for success, 1 for error.
   */
  public String dropTables()
  {
      try (Statement statement = _connection.createStatement()) {
          statement.executeUpdate("DROP TABLE Transaction_Performed");
          statement.executeUpdate("DROP TABLE Co_owns");
          statement.executeUpdate("DROP TABLE Pocket");
          statement.executeUpdate("DROP TABLE Closed");
          statement.executeUpdate("DROP TABLE Initial_Balance");
          statement.executeUpdate("DROP TABLE Account_Owns");
          statement.executeUpdate("DROP TABLE Customer");
          statement.executeUpdate("DROP TABLE Current_Date");
          return "0";
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
          return "1";
      }
  }

  /**
   * Create all of your tables in your DB.
   * @return a string "r", where r = 0 for success, 1 for error.
   */
  public String createTables()
  {
      String createCustomer = "CREATE TABLE Customer(" +
              "name VARCHAR(100)," +
              "taxid VARCHAR(100)," +
              "address VARCHAR(100)," +
              "PIN INTEGER," +
              "PRIMARY KEY(taxid))";

      String createAccount_Owns = "CREATE TABLE Account_Owns("+
              "aid VARCHAR(100), "+
              "branch VARCHAR(100),"+
              "acc_type VARCHAR(100), "+
              "balance REAL, "+
              "interest_rate  REAL, "+
              "interest REAL, "+ //do not need that
              "taxid VARCHAR(100) NOT NULL,"+
              "PRIMARY KEY(aid),"+
              "FOREIGN KEY(taxid) REFERENCES Customer(taxid))";

      String createTransaction_Performed =" CREATE TABLE Transaction_Performed("+
              "tid VARCHAR(100), "+
              "tdate DATE, "+
              "trans_type VARCHAR(100),"+
              "amount REAL,"+
              "tfee REAL,"+
              "checknum VARCHAR(100),"+
              "acc_to VARCHAR(100) NOT NULL,"+
              "acc_from VARCHAR(100),"+
              "PRIMARY KEY(tid),"+
              "FOREIGN KEY(acc_to) REFERENCES Account_Owns(aid), "+
              "FOREIGN KEY(acc_from) REFERENCES Account_Owns(aid))";

      String createCo_owns= "CREATE TABLE Co_owns("+
              "aid VARCHAR(100),"+
              "taxid VARCHAR(100),"+
              "PRIMARY KEY (aid, taxid),"+
              "FOREIGN KEY(aid) REFERENCES Account_Owns(aid),"+
              "FOREIGN KEY (taxid) REFERENCES Customer(taxid))";

      String createPocket = "CREATE TABLE Pocket("+
              "paid VARCHAR(100),"+
              "aid VARCHAR(100) NOT NULL,"+
              "pocket_fee REAL,"+
              "PRIMARY KEY(paid),"+
              "FOREIGN KEY(paid) REFERENCES Account_Owns(aid) ON DELETE CASCADE," +
              "FOREIGN KEY(aid) REFERENCES Account_Owns(aid))";

      String createDate = "CREATE TABLE Current_Date("+
              "cdate DATE,"+
              "PRIMARY KEY (cdate))";

      String createClosed = "CREATE TABLE Closed("+
              "aid VARCHAR(100),"+
              "PRIMARY KEY(aid),"+
              "FOREIGN KEY(aid) REFERENCES Account_Owns(aid))";

      String createInitialBalance = "CREATE TABLE Initial_Balance("+
              "aid VARCHAR(100), "+
              "init_balance REAL,"+
              "PRIMARY KEY(aid),"+
              "FOREIGN KEY(aid) REFERENCES Account_Owns(aid))";

      String createAvgBalance="CREATE TABLE Avg_Balance("+
              "aid VARCHAR(100), "+
              "num_days INT,"+
              "avg_balance REAL,"+
              "PRIMARY KEY(aid, avg_balance),"+
              "FOREIGN KEY(aid) REFERENCES Account_Owns(aid))";

      try (Statement statement = _connection.createStatement()) {
          statement.executeUpdate(createCustomer);
          statement.executeUpdate(createAccount_Owns);
          statement.executeUpdate(createPocket);
          statement.executeUpdate(createCo_owns);
          statement.executeUpdate(createTransaction_Performed);
          statement.executeUpdate(createClosed);
          statement.executeUpdate(createDate);
          statement.executeUpdate(createInitialBalance);
          return "0";
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
          return "1";
      }
  }

  public String setDate( int year, int month, int day ){
      String clear = "DELETE FROM Current_Date";
      //if table is full
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery("SELECT cdate FROM Current_Date")) {
              if(resultSet.next())
                  System.out.println(resultSet.getString(1));
                  statement.executeUpdate(clear);
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
          return "1 "+year+"-"+month+ "-" + day;
      }
      String insertDate = "INSERT INTO Current_Date (cdate) VALUES(?)";
      LocalDate currentDate = LocalDate.of(year,month, day);
      try (PreparedStatement statement = _connection.prepareStatement(insertDate)) {
          statement.setDate(1,java.sql.Date.valueOf(currentDate));
          statement.executeUpdate();
          return "0 "+year+"-"+month+ "-" + day;
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
          return "1 "+year+"-"+month+ "-" + day;
      }

  }

  public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
  {
      // check if initial balance is going to be enough
      if(initialBalance<1000)
          return "1 ";
      //1. check if Customer with taxid = tin exists in  Customer table
      String checkCustomer = "SELECT C.taxid FROM Customer C WHERE C.taxid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(checkCustomer)) {
          statement.setString(1,tin);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              if(!resultSet.next()) {
                  String createCustomer = "INSERT INTO Customer (name, taxid, address, PIN)"+
                          "VALUES(?,?,?,1234)";
                  try(PreparedStatement s = _connection.prepareStatement(createCustomer)) {
                      s.setString(1, name);
                      s.setString(2, tin);
                      s.setString(3, address);
                      s.executeUpdate();
                  }
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
          return "1";
      }
      //2. if customer with taxid =tin deos not exist, then call this.createCustomer(id,tin, name, address)
      //3. insert into transaction_performed the deposit of intial balance

      String createAccount = "INSERT INTO Account_Owns(aid, branch, acc_type, balance, interest_rate, interest, taxid)"+
              "VALUES(?, ?, ?, ?, ?, 0, ? )";
      try(PreparedStatement statement = _connection.prepareStatement(createAccount)){

          statement.setString(1,id);
          statement.setString(2,"Isla Vista");
          statement.setString(3,accountType.name());
          statement.setDouble(4, initialBalance);
          statement.setString(6, tin);
          if(accountType.equals("INTEREST_CHECKING")){
              statement.setDouble(5,3.0);
          }
          else if (accountType.equals("SAVINGS")){
              statement.setDouble(5, 4.8);
          }
          else{
              statement.setDouble(5,0.0);
          }
          statement.executeUpdate();
          this.logTransaction("Deposit",initialBalance,0,null,id, null );
          this.insertInitialBalance(id,initialBalance);
          return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
          return "1 ";
      }
  }


  public String createCustomer( String accountId, String tin, String name, String address ){
      //check that entry with aid=accountId exists in Account_Owns
      //1. check if there is an entry in Account_Owns where taxid=tin and aid=accountID
      String checkCustomer = "SELECT * FROM Account_Owns A WHERE A.taxid = ? AND A.aid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(checkCustomer)) {
          statement.setString(1,tin);
          statement.setString(2,accountId);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              if(!resultSet.next()) {
                  String createCustomer = "INSERT INTO Customer (name, taxid, address, PIN)"+
                          "VALUES(?,?,?,1234)";
                  try(PreparedStatement s = _connection.prepareStatement(createCustomer)) {
                      s.setString(1, name);
                      s.setString(2, tin);
                      s.setString(3, address);
                      s.executeUpdate();
                  }
                  String createCoOwner= "INSERT INTO Co_owns (aid, taxid) VALUES (?,?)";
                  try(PreparedStatement s = _connection.prepareStatement(createCoOwner)) {
                      s.setString(1, accountId);
                      s.setString(2, tin);
                      s.executeUpdate();
                  }
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
          return "1";
      }
      //2. if there is not, make an entry in Customer(name, ttin, address, 0)
      //3. then make an entry into Co_owns(accountId, tin)
      return "0";
  }



  public String listClosedAccounts(){
      String message="0";
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery("SELECT aid FROM Closed")) {
              while(resultSet.next()) {
                  String r = (resultSet.getString(1));
                  message=message+" "+r;
              }
          }
          return message;
      } catch( SQLException e){
          System.err.println( e.getMessage() );
          return "1 ";
      }
  }




  public String payFriend( String from, String to, double amount ){
      if(!(this.getAccountType(from).equals("POCKET")) || !(this.getAccountType(to).equals("POCKET")))
          return "1";
      double fee=0;
      double to_balance=0;
      double from_balance=0;
      //get to balance
      String checkBalance = "SELECT A.balance FROM Account_Owns A WHERE A.aid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(checkBalance)) {
          statement.setString(1, to);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while (resultSet.next())
                  to_balance = resultSet.getDouble(1);
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
      }

      //get from balance
      try (PreparedStatement statement = _connection.prepareStatement(checkBalance)) {
          statement.setString(1, from);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while (resultSet.next())
                  from_balance = resultSet.getDouble(1);
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
      }

      String response ="1 "+ (Math.round(from_balance * 100.0)/100.0) + " "+ (Math.round(to_balance * 100.0)/100.0);

      //check if either account is closed
      if(this.isClosed(to) || this.isClosed(from))
          return response;
      //1. check if there have been transaction
      //2. if amount is negative, return 1

      if(amount<0)
          return response;

      if(this.checkPocketTransaction(from)) {
          from_balance -= 5;
          fee=5.00;
      }
      if(this.checkPocketTransaction(to)) {
          to_balance -= 5;
          fee=5.00;
      }

      //3. if amount is equal to the amount less than or equal to source balance
      if(amount<=from_balance){
          //update from balance
          double new_from_balance = Math.round((from_balance - amount)*100.0)/100.0;
          String updateBalance = "UPDATE Account_Owns A SET A.balance = ? WHERE A.aid = ?";
          try(PreparedStatement s = _connection.prepareStatement(updateBalance)) {
              s.setDouble(1, new_from_balance);
              s.setString(2, from);
              s.executeUpdate();
          }
          catch( SQLException e){
              System.err.println( e.getMessage() );
              return response;
          }
          //update to balance
          double new_to_balance = Math.round((to_balance + amount)*100.0)/100.0;
          try(PreparedStatement s = _connection.prepareStatement(updateBalance)) {
              s.setDouble(1, new_to_balance);
              s.setString(2, to);
              s.executeUpdate();
          }
          catch( SQLException e){
              System.err.println( e.getMessage() );
              return "1 "+ (Math.round(new_from_balance * 100.0)/100.0) + " "+ (Math.round(to_balance * 100.0)/100.0);
          }
          //String trans_type, double amount, double tfee, String checknum, String acc_to, String acc_from
          this.logTransaction("Pay-Friend", amount, fee, null, to, from);
          //4. if new source balance is less than equal to 0.01, close account
          if(new_from_balance<=0.01)
              closeAccount(from);
          return "0 "+ new_from_balance + " "+ new_to_balance;
      }
      //else amount would set balance negative
      return response;

  }



  public String
  topUp( String accountId, double amount ){
      if(!(this.getAccountType(accountId).equals("POCKET"))){
          return "1";
      }
      double fee=0.00;
      boolean firstTrans=this.checkPocketTransaction(accountId);
      if(this.isClosed(accountId))
          return "1";
      //1. select aid from Pocket where paid=accountID
      String aid="";
      String getaid="SELECT P.aid FROM Pocket P WHERE P.paid=?";
      try (PreparedStatement statement = _connection.prepareStatement(getaid)) {
          statement.setString(1, accountId);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              if (resultSet.next()) {
                  aid = resultSet.getString(1);
                  if(this.isClosed(aid))
                      //check account is not closed
                      return "1";
                  //2. update the main account's balance to be -amount CHECK if the balance is above $0.01
                  double newMainBalance=this.checkBalance(aid,amount,"minus");
                  if(newMainBalance >= 0){
                      String updateBalance="UPDATE Account_Owns A SET A.balance = ? WHERE A.aid = ?";
                      //fee if it is the first transaction of the month
                      if(firstTrans) {
                          newMainBalance -= 5;
                          fee=5;
                      }
                      try(PreparedStatement s = _connection.prepareStatement(updateBalance)) {
                          s.setDouble(1, newMainBalance);
                          s.setString(2, aid);
                          s.executeUpdate();
                      }
                      double newPocketBalance=this.checkBalance(accountId,amount, "plus");
                      try(PreparedStatement s = _connection.prepareStatement(updateBalance)) {
                          s.setDouble(1, newPocketBalance);
                          s.setString(2, accountId);
                          s.executeUpdate();
                      }
                      this.logTransaction("Top Up",amount,fee,null,accountId,aid);
                  }
                  if(newMainBalance <=0.01)
                      this.closeAccount(aid);
                  return "0";
              }
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
          return "1";
      }

      //3. update the pocket account's balance in the Account_owns table to be +amount
      //4. make an entry in Transaction_Performed
      return "1";
  }



  public String deposit( String accountId, double amount ){
      String r;
      String res;

      float oldbalance=0;
      float  newbalance=0;

      String sql0= " SELECT A.BALANCE " +
              " FROM Account_Owns A WHERE A.AID = "+accountId;

      try( Statement select= _connection.createStatement()){
          ResultSet answer0 = select.executeQuery(sql0);
          if(answer0.next()){
              oldbalance= answer0.getFloat("BALANCE");
          }

          String sql = "UPDATE Account_Owns A" +
                  " SET A.BALANCE = A.BALANCE +" + amount +
                  " WHERE A.AID = "+ accountId +
                  " AND (A.ACC_TYPE= 'STUDENT_CHECKING' " +
                  "OR A.ACC_TYPE= 'INTEREST_CHECKING'"+
                  " OR A.ACC_TYPE= 'SAVINGS')"+
                  " AND  NOT EXISTS (SELECT DISTINCT Cl.AID"+
                  " FROM Closed Cl"+
                  " WHERE Cl.AID = A.AID)";

          try (Statement statement = _connection.createStatement()) {

              statement.executeUpdate(sql);


              String sql1 = " SELECT A.BALANCE " +
                      " FROM Account_Owns A WHERE A.AID = "+accountId;
              try (Statement selectstmnt = _connection.createStatement()){

                  ResultSet	answer = selectstmnt.executeQuery(sql1);
                  if (answer.next())
                      newbalance= answer.getFloat("BALANCE");
                  r="0";
                  res =  r  +" "+ oldbalance +" " +newbalance;
                  this.logTransaction("Deposit",amount,0,null,accountId, null );
                  return res;


              } catch (SQLException e){
                  r="1";
                  res =  r  +" "+ oldbalance +" " +newbalance;

              }

          }catch (SQLException e){
              r="1";
              res =  r  +" "+ oldbalance +" " +newbalance;

          }

      } catch (SQLException e){
          r="1";
          res =  r  +" "+ oldbalance +" " +newbalance;

      }

      return res;
  }


  public String showBalance( String accountId ) {


      String res;
      String r;
      float balance=0;
      float accid= Float.parseFloat(accountId);
      String sql0= " SELECT A.BALANCE " +
              " FROM Account_Owns A WHERE A.AID = "+accid;
      try( Statement select= _connection.createStatement()){

          ResultSet answer0 = select.executeQuery(sql0);
          if(answer0.next())
              balance= answer0.getFloat("BALANCE");
          r="0";


      }catch (SQLException e){
          r="1";
      }

      res= r +" "+  balance;

      return res;

      // 	//1. select balance from Account_Owns where aid=accountId
      // 	return "0";
  }

  public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin, String branch ){

      String check = " SELECT A.AID"+
              " FROM Account_Owns A" +
              " WHERE A.AID = ?";

      try(PreparedStatement checkstatement= _connection.prepareStatement(check)){
          checkstatement.setString(1, linkedId);
          try (ResultSet resultSet = checkstatement.executeQuery()){
              if(resultSet.next()){

                  String insertAcc= "INSERT INTO Account_Owns (AID, BRANCH, ACC_TYPE, BALANCE, INTEREST_RATE, INTEREST, TAXID) VALUES (?,?,'POCKET',0,0,0,?)";
                  try(PreparedStatement s = _connection.prepareStatement(insertAcc)) {


                      s.setString(1, id);
                      s.setString(2, branch);
                      s.setString(3, tin);
                      s.executeUpdate();


                      String insertPocket = "INSERT INTO Pocket (PAID ,AID, POCKET_FEE) VALUES (?,?,0.00) ";
                      try (PreparedStatement s1 = _connection.prepareStatement(insertPocket)){
                          s1.setString(1, id);
                          s1.setString(2, linkedId);
                          s1.executeUpdate();
                          this.topUp(id, initialTopUp);
                          this.insertInitialBalance(id,initialTopUp);
                      }catch (SQLException e){
                          System.out.println("Error 1");
                          return  "1";

                      }
                  }catch (SQLException e){
                      System.out.println(e.getMessage());
                      return  "1";

                  }


              }
              else return "1";
          }catch (SQLException e){
              System.out.println(e.getMessage());
              return  "1";

          }
      }catch (SQLException e){
          System.out.println(e.getMessage());
          return  "1";

      }
      //1. check that linkedId account exists in Account_owns
      //check that account is not closed
      //2. insert into Account_Owns
      //3. Insert into Pocket
      //4. Call topUp
      return "0";
  }



  //              !!!!!!
  //
  //          HELPER FUNCTIONS
  //
  //             !!!!!!!!!





  //returns true if account is closed
  //returns false if account is open
  public boolean isClosed(String aid){
      String checkForClosed = "SELECT C.aid FROM Closed C WHERE C.aid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(checkForClosed)) {
          statement.setString(1,aid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              if(resultSet.next()) {
                  return true;
              }
              return false;
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
          return false;
      }
  }

  //TO DO: fix it so that 13.5 will display as 13.50 in db
  public double checkBalance(String aid, double amount, String op){
      double balance=0;
      String checkBalance = "SELECT A.balance FROM Account_Owns A WHERE A.aid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(checkBalance)) {
          statement.setString(1, aid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while (resultSet.next())
                  balance = resultSet.getDouble(1);
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
      }

      if(op.equals("minus"))
          balance-=amount;
      else
          balance+=amount;
      BigDecimal bd = new BigDecimal(balance);
      bd = bd.setScale(2, RoundingMode.HALF_UP);
      return bd.doubleValue();
  }

  //checks if pocket account has had a transaction this month
  //returns true if first transaction
  public boolean checkPocketTransaction(String aid){
      //1. query transactions_owns table check if there is a row where the date's month is equal to the current date
      String checkBalance = "SELECT * FROM Transaction_Performed T WHERE (T.acc_to = ? OR T.acc_from = ? ) " +
              "AND EXTRACT(MONTH FROM T.tdate) = "+
              "(SELECT EXTRACT(MONTH FROM C.cdate) FROM Current_Date C)";
      try (PreparedStatement statement = _connection.prepareStatement(checkBalance)) {
          statement.setString(1, aid);
          statement.setString(2, aid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              if (resultSet.next())
                  return false;
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
          return false;
      }
      return true;
  }


  public String getAccountType(String aid){
      String acc_type="";
      String checkType = "SELECT A.acc_type FROM Account_Owns A WHERE A.aid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(checkType)) {
          statement.setString(1, aid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while (resultSet.next())
                  acc_type = resultSet.getString(1);
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
      return acc_type;
  }

  public void closeAccount(String aid){
      //1. check if account has a pocket account by checking if it exists in Pocket
      //2. if it does, insert pocket account into closed
      String checkForPocket = "SELECT P.paid FROM Pocket P WHERE P.aid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(checkForPocket)) {
          statement.setString(1,aid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              if(resultSet.next()) {
                  String paid=resultSet.getString(1);
                  String createCustomer = "INSERT INTO Closed (aid)"+
                          "VALUES(?)";
                  try(PreparedStatement s = _connection.prepareStatement(createCustomer)) {
                      s.setString(1, paid);
                      s.executeUpdate();
                  }
              }
          }
          String createCustomer = "INSERT INTO Closed (aid)"+
                  "VALUES(?)";
          try(PreparedStatement s = _connection.prepareStatement(createCustomer)) {
              s.setString(1, aid);
              s.executeUpdate();
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }

      //3. insert aid account into closed
  }


  public void logTransaction(String trans_type, double amount, double tfee, String checknum, String acc_to, String acc_from){
      java.util.Date utilDate = new java.util.Date();
      java.sql.Date tdate=new java.sql.Date(utilDate.getTime());
      String tid="0";
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery("SELECT cdate FROM Current_Date")) {
              while (resultSet.next())
                  tdate = resultSet.getDate(1);
          }
          try (ResultSet resultSet = statement
                  .executeQuery("SELECT tid FROM Transaction_Performed")) {
              while (resultSet.next()) {
                  String last_tid = resultSet.getString(1);
                  int n=Integer.parseInt(last_tid);
                  n++;
                  tid=Integer.toString(n);
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
      String insertTransaction= "INSERT INTO Transaction_Performed (tid, tdate, trans_type, amount, tfee, checknum, acc_to, acc_from)"+
              "VALUES (?,?,?,?,?,?,?,?)";
      try (PreparedStatement statement = _connection.prepareStatement(insertTransaction)) {
          statement.setString(1,tid);
          statement.setDate(2,tdate);
          statement.setString(3,trans_type);
          statement.setDouble(4,amount);
          statement.setDouble(5,tfee);
          statement.setString(6,checknum);
          statement.setString(7,acc_to);
          statement.setString(8,acc_from);
          statement.executeUpdate();
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
      }
  }

  public void updateBalance(String aid, double amount){
      String balance = "UPDATE Account_Owns A SET A.balance = ? WHERE A.aid = ?";
      try(PreparedStatement s = _connection.prepareStatement(balance)) {
          s.setDouble(1, amount);
          s.setString(2, aid);
          s.executeUpdate();
      }
      catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }



  public boolean isLastDay(){
      java.util.Date utilDate = new java.util.Date();
      java.sql.Date cdate=new java.sql.Date(utilDate.getTime());

      try (Statement statement = _connection.createStatement()) {
          try( ResultSet resultSet = statement.executeQuery( "SELECT C.cdate FROM Current_Date C" ) )
          {
              while( resultSet.next() )
                  cdate = resultSet.getDate(1);
          }
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
          return false;
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(cdate);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      if(month==1 && day==28) {
          return true;
      }
      else if((month==3 || month==5 || month==8 || month==10) && day==30){
          return true;
      }
      else if(day==31){
          return true;
      }
      return false;
  }



  ///
  ///         PART II
  ///

  //withdraw money from a checking or savings account
  public void withdrawal(String aid, double amount){
      //1.check if account is closed
      if(this.isClosed(aid)) {
          System.out.println("Sorry, account is closed");
          return;
      }
      //2.check if account is not a pocket account
      if(this.getAccountType(aid).equals("POCKET")) {
          System.out.println("Cannot perform Withdraw on Pocket account");
          return;
      }
      //3.check if account has enough money
      double balance=this.checkBalance(aid,amount,"minus");
      if(balance<0) {
          System.out.println("Insufficient funds");
          return;
      }
      //change balance
      this.updateBalance(aid,balance);
      //4.log transaction
      this.logTransaction("Withdrawl", amount, 0,null, aid, null);
      //5.check if account should close
      if(balance<=0.01) {
          this.closeAccount(aid);
          System.out.println("Withdrawal successful, account closed");
      }
      return;

  }

  public void purchase(String aid, double amount){
      //1.check if account is closed
      if(this.isClosed(aid)) {
          System.out.println("Sorry, account is closed");
          return;
      }
      //2.check if account is not a pocket account
      if(!this.getAccountType(aid).equals("POCKET")) {
          System.out.println("Cannot perform Purchase on Checking or Savings account");
          return;
      }
      //3.check if account has enough money
      double balance=this.checkBalance(aid,amount,"minus");
      if(balance<0) {
          System.out.println("Insufficient funds");
          return;
      }
      //change balance
      double fee=0;
      if(this.checkPocketTransaction(aid)) {
          fee = 5;
          balance-=5;
      }
      this.updateBalance(aid,balance);
      //4.log transaction
      this.logTransaction("Purchase", amount, fee,null, aid, null);
      //5.check if account should close
      if(balance<=0.01) {
          this.closeAccount(aid);
          System.out.println("Purchase successful, account closed");
      }
      return;

  }

  public void deleteTransactions(){
      String createTable=" CREATE TABLE Transaction_Performed("+
              "tid VARCHAR(100), "+
              "tdate DATE, "+
              "trans_type VARCHAR(100),"+
              "amount REAL,"+
              "tfee REAL,"+
              "checknum VARCHAR(100),"+
              "acc_to VARCHAR(100) NOT NULL,"+
              "acc_from VARCHAR(100),"+
              "PRIMARY KEY(tid),"+
              "FOREIGN KEY(acc_to) REFERENCES Account_Owns(aid), "+
              "FOREIGN KEY(acc_from) REFERENCES Account_Owns(aid))";
      if(!isLastDay()) {
          System.out.println("Sorry, it is not the last day of the month");
          return;
      }
      try (Statement statement = _connection.createStatement()) {
          statement.executeUpdate("DROP TABLE Transaction_Performed");
          System.out.println("Transactions deleted");
          statement.executeUpdate(createTable);
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
      }
  }


  public void createCustomerReport(String taxid){
      if(!this.isLastDay()){
          System.out.println("Sorry, it is not the last day of the month");
          return;
      }

      String query="SELECT A.aid FROM Account_Owns A WHERE A.taxid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(query)) {
          statement.setString(1, taxid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while (resultSet.next()) {
                  String aid = resultSet.getString(1);
                  System.out.print(aid);
                  if(this.isClosed(aid)){
                      System.out.println(" (closed)");
                  }
                  else{
                      System.out.println(" (open)");
                  }
              }
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }
  //also test everything again from scratch

  //for dter report maybe change integrity contraints to not have not null for accounts


  public void deleteClosedAccounts() {
      if (!isLastDay()) {
          System.out.println("Sorry, it is not the last day of the month");
          return;
      }
      String getClosed = "SELECT aid FROM Closed";
      try( Statement statement = _connection.createStatement() )
      {
          try( ResultSet resultSet = statement.executeQuery(getClosed) )
          {
              while( resultSet.next() ){
                  String r=resultSet.getString(1);
                  deleteAccount(r);
              }
          }
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
      }

      String getCustomer = "SELECT taxid FROM Customer";
      try( Statement statement = _connection.createStatement() )
      {
          try( ResultSet resultSet = statement.executeQuery(getCustomer) )
          {
              while( resultSet.next() ){
                  String taxid=resultSet.getString(1);
                  if(this.exists(taxid,"Co_owns","Customer")==false && this.exists(taxid, "Account_Owns", "Customer")==false)
                      deleteEntry(taxid, "Customer", "taxid");
              }
          }
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
      }
  }
  //2. for each customer, check if the taxid exists in Account_Owns or Co_Owns
  //if it does not exist then delete customer



  public void deleteAccount(String aid){
          //1. for each closed account, first check if there is a corresponding pocket account
      if(this.exists(aid, "Pocket", "Account")){
          String paid ="";
          String checkTable = "SELECT P.paid FROM Pocket P WHERE aid = "+aid;
          try( Statement statement = _connection.createStatement() )
          {
              try( ResultSet resultSet = statement.executeQuery(checkTable ) )
              {
                  while( resultSet.next() )
                      paid=resultSet.getString(1);
              }
          }
          catch( SQLException e )
          {
              System.err.println( e.getMessage() );
          }

          this.deleteEntry(aid, "Pocket","aid");
          this.deleteEntry(paid, "Transaction_Performed", "acc_to");
          this.deleteEntry(paid, "Transaction_Performed", "acc_from");
          this.deleteEntry(paid, "Closed", "aid");
          this.deleteEntry(paid,"Initial_Balance","aid");
          this.deleteEntry(paid, "Account_Owns","aid");
      }
          //a. delete the entry in pocket, then delete the entry in Account_Owns
      if(this.exists(aid, "Co_owns", "Account"))
          deleteEntry(aid,"Co_owns", "aid");
      this.deleteEntry(aid, "Closed","aid");
      this.deleteEntry(aid,"Initial_Balance","aid");
      this.deleteEntry(aid, "Transaction_Performed", "acc_to");
      this.deleteEntry(aid, "Transaction_Performed", "acc_from");
      if(this.exists(aid,"Account_Owns", "Account"))
          deleteEntry(aid,"Account_Owns","aid");
          // delete any tuple in Account_Owns, Co_Owns that contains aid


  }

  public boolean exists(String aid, String table, String checkType){
      if(checkType.equals("Customer")) {
          String checkTable = "SELECT * FROM " + table + " WHERE taxid = " + aid;
          try (Statement statement = _connection.createStatement()) {
              try (ResultSet resultSet = statement.executeQuery(checkTable)) {
                  if (resultSet.next())
                      return true;
              }
          } catch (SQLException e) {
              System.err.println(e.getMessage());
          }
      }
      else if(checkType.equals("Account")){
          String checkTable = "SELECT * FROM " + table + " WHERE aid = " + aid;
          try (Statement statement = _connection.createStatement()) {
              try (ResultSet resultSet = statement.executeQuery(checkTable)) {
                  if (resultSet.next())
                      return true;
              }
          } catch (SQLException e) {
              System.err.println(e.getMessage());
          }
      }
      return false;
  }

  public void deleteEntry(String aid, String table, String id){
      String r="DELETE FROM "+table+" WHERE "+id+" = "+aid;
      try( Statement statement = _connection.createStatement() )
      {
          statement.executeUpdate(r);
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
      }
  }


  public void generateMonthlyStatement(String taxid){
      if(!this.exists(taxid, "Account_Owns", "Customer")){
          System.out.println("Sorry, this customer is not a primary owner of an account");
          return;
      }

      double f=0;
      this.getMonth();
      String getPrimaryAccounts="SELECT A.aid FROM Account_Owns A WHERE A.taxid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(getPrimaryAccounts)) {
          statement.setString(1, taxid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while (resultSet.next()) {
                  String s=(resultSet.getString(1));
                  System.out.println("Account "+s);
                  this.getPrimaryOwner(s);
                  this.getCoOwner(s);
                  System.out.println();
                  System.out.println("Transactions");
                  this.listTransactions(s);
                  System.out.println();
                  f+=this.getInitialFinalBalance(s);
                  System.out.println("-----------------------");
                  System.out.println();
              }
          } catch (SQLException e) {
              System.err.println(e.getMessage());
          }
      } catch (SQLException e) {
          System.err.println(e.getMessage());
      }
      String getSecondaryAccounts="SELECT C.aid FROM Co_owns C WHERE C.taxid = ?";
      if(this.exists(taxid,"Co_owns","Customer")){
          try (PreparedStatement statement = _connection.prepareStatement(getSecondaryAccounts)) {
              statement.setString(1, taxid);
              try (ResultSet resultSet = statement
                      .executeQuery()) {
                  while (resultSet.next()) {
                      String s=(resultSet.getString(1));
                      System.out.println("Account "+s);
                      this.getPrimaryOwner(s);
                      this.getCoOwner(s);
                      System.out.println();
                      System.out.println("Transactions");
                      this.listTransactions(s);
                      System.out.println();
                      f+=this.getInitialFinalBalance(s);
                      System.out.println("-----------------------");
                      System.out.println();
                  }
              } catch (SQLException e) {
                  System.err.println(e.getMessage());
              }
          } catch (SQLException e) {
              System.err.println(e.getMessage());
          }
      }
      System.out.println("Total balance: "+String.format( "%.2f", f));
      if(f>100000)
          System.out.println("Warning: Limit of insurance reached");

  }

  public void getMonth() {
      String sql= "(SELECT EXTRACT(MONTH FROM C.cdate) FROM Current_Date C)";
      try( Statement statement = _connection.createStatement() ){
          try (ResultSet resultSet = statement
                  .executeQuery(sql)) {
              while (resultSet.next()) {
                  String s = (resultSet.getString(1));
                  if(s.equals("1"))
                      s="Janurary";
                  else if(s.equals("2"))
                      s="February";
                  else if(s.equals("3"))
                      s="March";
                  else if(s.equals("4"))
                      s="April";
                  else if(s.equals("5"))
                      s="May";
                  else if(s.equals("6"))
                      s="June";
                  else if(s.equals("7"))
                      s="July";
                  else if(s.equals("8"))
                      s="August";
                  else if(s.equals("9"))
                      s="September";
                  else if(s.equals("10"))
                      s="October";
                  else if(s.equals("11"))
                      s="November";
                  else if(s.equals("12"))
                      s="December";

                  System.out.println("Statement for month of "+s);
                  System.out.println();
              }
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }

  public void getCoOwner(String aid){
      String sql="SELECT C.name, C.address FROM Customer C WHERE C.taxid IN (SELECT Z.taxid FROM Co_owns Z  WHERE Z.aid = "+aid+")";
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery(sql)) {
              while(resultSet.next()) {
                  String s = (resultSet.getString(1));
                  String a = (resultSet.getString(2));
                  System.out.println("Co-Owner: "+s+", "+a);
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }

  public void getPrimaryOwner(String aid){
      String sql="SELECT C.name, C.address FROM Customer C WHERE C.taxid IN (SELECT A.taxid FROM Account_Owns A  WHERE A.aid = "+aid+")";
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery(sql)) {
              while(resultSet.next()) {
                  String s = (resultSet.getString(1));
                  String a = (resultSet.getString(2));
                  System.out.println("Primary Owner: "+s+", "+a);
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }

  public double getInitialFinalBalance(String aid){
      String init="SELECT I.init_balance FROM Initial_Balance I WHERE I.aid="+aid;
      String fin="SELECT A.balance FROM Account_Owns A WHERE A.aid="+aid;
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery(init)) {
              while(resultSet.next()) {
                  double i = (resultSet.getDouble(1));
                  System.out.println("Initial Balance: "+String.format( "%.2f", i ));
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery(fin)) {
              while(resultSet.next()) {
                  double f = (resultSet.getDouble(1));
                  System.out.println("Final Balance: "+String.format( "%.2f", f ));
                  return f;
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
      return 0;
  }

  public void insertInitialBalance(String aid, double amount){
      String insert = "INSERT INTO Initial_Balance (aid, init_balance) VALUES(?,?)";
      try (PreparedStatement statement = _connection.prepareStatement(insert)){
          statement.setString(1,aid);
          statement.setDouble(2,amount);
          statement.executeUpdate();
      }
      catch( SQLException e )
      {
          System.err.println( e.getMessage() );
          System.out.println("tried "+aid);
      }
  }

  public void populateInitialBalance(){
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery("SELECT aid, balance FROM Account_Owns")) {
              while(resultSet.next()) {
                  String s = (resultSet.getString(1));
                  double b = (resultSet.getDouble(2));
                  this.insertInitialBalance(s,b);
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }

  public void listTransactions(String aid){
      java.util.Date utilDate = new java.util.Date();
      java.sql.Date tdate=new java.sql.Date(utilDate.getTime());
      String sql="SELECT tdate, trans_type, amount FROM Transaction_Performed WHERE acc_to ="+aid;
      String sql2="SELECT tdate, trans_type, amount FROM Transaction_Performed WHERE acc_from ="+aid;
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery(sql)) {
              while(resultSet.next()) {
                  tdate=(resultSet.getDate(1));
                  String s = (resultSet.getString(2));
                  double b = (resultSet.getDouble(3));
                  if(s.equals("Withdrawal")||s.equals("Write-Check"))
                      System.out.println(tdate+" "+s+" - "+b);
                  else
                      System.out.println(tdate+" "+s+" + "+b);
              }
          }
          try (ResultSet resultSet = statement
                  .executeQuery(sql2)) {
              while(resultSet.next()) {
                  tdate=(resultSet.getDate(1));
                  String s = (resultSet.getString(2));
                  double b = (resultSet.getDouble(3));
                  System.out.println(tdate+" "+s+" - "+b);
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }

  //for each customer
  //1. SELECT A.aid FROM Account_Owns A WHERE A.taxid = ?
  ///for each one, SELECT T.amount FROM Transaction_Performed T WHERE acc_to =? and type=wire and deposit and transfer
  //add it to the totalIncomingFunds
  //2. SELECT C.aid FROM Co_owns C WHERE C.taxid=?
  //do the same thing and keep adding

  public double getIncomingFunds(String aid){
      double amount=0;
      String sql="SELECT T.amount FROM Transaction_Performed T WHERE T.acc_to =? " +
              "AND (T.trans_type='Wire' OR T.trans_type='Deposit' OR T.trans_type='Transfer')";
      try (PreparedStatement statement = _connection.prepareStatement(sql)) {
          statement.setString(1,aid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while(resultSet.next()) {
                  amount += (resultSet.getDouble(1));
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }
      return amount;
  }

  public void checkCustomerAmount(String taxid){
      String aid;
      double total=0;
      String sql = "SELECT A.aid FROM Account_Owns A WHERE A.taxid = ?";
      try (PreparedStatement statement = _connection.prepareStatement(sql)) {
          statement.setString(1,taxid);
          try (ResultSet resultSet = statement
                  .executeQuery()) {
              while(resultSet.next()) {
                  aid = (resultSet.getString(1));
                  total+=this.getIncomingFunds(aid);
              }
          }
      } catch( SQLException e){
          System.err.println( e.getMessage() );
      }

      if(total>10000)
          System.out.println("taxid: " +taxid);
  }

  public void generateDTER(){
      System.out.println("List of customers with more than $10,000 incoming to accounts");
      String sql= "SELECT C.taxid FROM Customer C";
      try (Statement statement = _connection.createStatement()) {
          try (ResultSet resultSet = statement
                  .executeQuery(sql)) {
              while (resultSet.next()) {
                  String taxid = (resultSet.getString(1));
                  this.checkCustomerAmount(taxid);
              }
          }
      }catch( SQLException e){
          System.err.println( e.getMessage() );
      }
  }


  String wire(String acc_to, String acc_from, double amount, String tin){

    double newtobalance = 0;
    double newmainbalance=0;
    String r = " ";
    String from= " ";
    String check = "SELECT A.AID FROM Account_Owns A, Customer C" +
                   " WHERE A.TAXID= C.TAXID" +
                   " AND (A.ACC_TYPE= 'STUDENT_CHECKING' OR A.ACC_TYPE= 'INTEREST_CHECKING' OR A.ACC_TYPE= 'SAVINGS')" +
                   " AND C.TAXID= ? AND A.AID = ? ";
      try(PreparedStatement checkstatement= _connection.prepareStatement(check)){
        checkstatement.setString(1, tin);
        checkstatement.setString(2, acc_from);
          try (ResultSet resultSet = checkstatement.executeQuery()){
            if (resultSet.next()){
              newmainbalance=this.checkBalance(acc_from,amount+ (amount*0.02), "minus");
              if(newmainbalance> 0.01){

                String update= "UPDATE Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";

                try (PreparedStatement updatemain= _connection.prepareStatement(update)){

                  updatemain.setDouble(1, newmainbalance);
                  updatemain.setString(2, acc_from);



                    updatemain.executeUpdate();
                    updatemain.close();

                  newtobalance = this.checkBalance(acc_to, amount,"plus");

                  String updateto= "UPDATE Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";
                  try(PreparedStatement updatetoacc = _connection.prepareStatement(updateto)){
                    updatetoacc.setDouble(1, newtobalance);
                    updatetoacc.setString(2, acc_to);
                    updatetoacc.executeUpdate();
                    updatetoacc.close();
                  //  this.logTransaction("Wire",  amount, amount*0.02, null,  acc_to,  acc_from);
                    r= "0";




                  }catch( SQLException e){
                      System.out.println("error 4");
                      System.err.println( e.getMessage() );
                      return "1";
                  }
                }catch( SQLException e){
                    System.out.println("error 3");
                    System.err.println( e.getMessage() );
                    return "1";
                }
              } else{
                r=  "1";
              }
            }


          }catch( SQLException e){
            System.out.println("error 2");
              System.err.println( e.getMessage() );
              return "1";
          }



      }catch( SQLException e){
        System.out.println("error 1");
          System.err.println( e.getMessage() );
          return "1";
      }
//1. check that tin corresponds to the TAXID for acc_from
//2. subtract amount from acc_from(check that it doesnt go below .01)
//3. add amout to acc_to
//4. call logTransaction
return r +" "+ newmainbalance+" "+newtobalance;

//       Subtract money from one savings or checking account and add it to another.The customer that
// requests this action must be an owner of the account from which the money is subtracted. There is a 2%
// fee for this action.
  }

  String collect(String pid,String mainid, double amount){
    double newpocketbalance=0;
    double newmainbalance=0;
    String r= " ";
    String selectpaid = "SELECT A.AID FROM Account_Owns A, Pocket P WHERE A.ACC_TYPE='POCKET' AND A.AID=P.PAID AND A.AID= ? ";
    try(PreparedStatement selectst= _connection.prepareStatement(selectpaid)){
        selectst.setString(1, pid);
          try (ResultSet resultSet = selectst.executeQuery()){
            if(resultSet.next()){
                newpocketbalance=this.checkBalance(pid,amount+ (amount*0.03), "minus");
                if(newpocketbalance> 0.01){
                  String update= "UPDATE Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";
                  try (PreparedStatement updatepocket= _connection.prepareStatement(update)){
                    updatepocket.setDouble(1, newpocketbalance);
                    updatepocket.setString(2, pid);
                    updatepocket.executeUpdate();
                    updatepocket.close();
                    newmainbalance= this.checkBalance(mainid, amount,"plus");
                      String updatemain= "Update Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";
                      try(PreparedStatement updatemainacc = _connection.prepareStatement(updatemain)){
                        updatemainacc.setDouble(1, newmainbalance);
                        updatemainacc.setString(2, mainid);
                        updatemainacc.executeUpdate();
                        updatemainacc.close();
                        //this.logTransaction("Collect",  amount, amount*0.03, null, pid, mainid);
                        r= "0";

                      }catch( SQLException e){
                          System.out.println("error 4");
                          System.err.println( e.getMessage() );
                          return "1";
                      }
                  }catch( SQLException e){
                      System.out.println("error 3");
                      System.err.println( e.getMessage() );
                      return "1";
                  }
                }else{
                  r= "1";
                }
            }else {
              r="1";
            }
          }catch( SQLException e){
              System.out.println("error 2");
              System.err.println( e.getMessage() );
              return "1";
          }

    }catch( SQLException e){
        System.out.println("error 1");
        System.err.println( e.getMessage() );
        return "1";
    }





return r +" " + newpocketbalance+" "+ newmainbalance;



//      Move a specified amount of money from the pocket account back to the linked checking/savings
// account, there will be a 3% fee assessed.
  }


 String writeCheck(String aid,String checknumber, double amount){
   double newmainbalance=0;
   String r= "1";
   String checkaid= "SELECT A.AID FROM Account_Owns A "+
                    "WHERE A.AID = ? AND (A.ACC_TYPE= 'STUDENT_CHECKING' OR A.ACC_TYPE= 'INTEREST_CHECKING')";
   try(PreparedStatement selectst= _connection.prepareStatement(checkaid)){
     selectst.setString(1, aid);
       try (ResultSet resultSet = selectst.executeQuery()){
         if(resultSet.next()){
           newmainbalance=this.checkBalance(aid,amount, "minus");
           String update= "Update Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";
           try (PreparedStatement updateacc= _connection.prepareStatement(update)){
             updateacc.setDouble(1, newmainbalance);
             updateacc.setString(2, aid);
             updateacc.executeUpdate();
             updateacc.close();
             this.logTransaction("Write-Check",  amount, 0, checknumber, aid, null);
             r= "0";
           }catch( SQLException e){
               System.out.println("error 3");
               System.err.println( e.getMessage() );
               return "1";
           }

         } else {
           r="1";
         }
       }catch( SQLException e){
           System.out.println("error 2");
           System.err.println( e.getMessage() );
           return "1";
       }
   }catch( SQLException e){
       System.out.println("error 1");
       System.err.println( e.getMessage() );
       return "1";
   }



   return r + " "+newmainbalance;


   //Subtract money from the checking account. Associated with a check transaction is a check
   // number. (Note that a check cannot be written from all account types.
 }

 String transfer(String acc_from, String acc_to, String tin, double amount){
   double newfrombalance= 0;
   double newtobalance = 0;
   String r= "1";
   if (amount>2000){
     return "1";
   } else {
   String check= "SELECT C.TAXID FROM Customer C, Account_Owns A"+
                            " WHERE A.TAXID= C.TAXID AND"+
                            " A.AID= ? AND C.TAXID= ?"+
                            " AND EXISTS"+
                            "  (SELECT A2.TAXID FROM Account_Owns A2 WHERE A2.TAXID= C.TAXID  AND  A2.AID= ?)";
   try(PreparedStatement checkst= _connection.prepareStatement(check)){
     checkst.setString(1, acc_from);
     checkst.setString(2, tin);
     checkst.setString(3, acc_to);
     try (ResultSet resultSet = checkst.executeQuery()){
       if(resultSet.next()){
         newfrombalance=this.checkBalance(acc_from,amount, "minus");
         String update= "Update Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";
         try(PreparedStatement updatefrom= _connection.prepareStatement(update)){
           updatefrom.setDouble(1, newfrombalance);
           updatefrom.setString(2, acc_from);

           updatefrom.executeUpdate();

           updatefrom.close();
           newtobalance= this.checkBalance(acc_to, amount, "plus");
           String update2= "Update Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";
           try( PreparedStatement updateto = _connection.prepareStatement(update2)){
             updateto.setDouble(1, newtobalance);
             updateto.setString(2, acc_to);
             updateto.executeUpdate();
             updateto.close();
             r = "0";
           }catch( SQLException e){
               System.out.println("error 4");
               System.err.println( e.getMessage() );
               return "1";
           }

         }catch( SQLException e){
             System.out.println("error 3");
             System.err.println( e.getMessage() );
             return "1";
         }
       }
     }catch( SQLException e){
         System.out.println("error 2");
         System.err.println( e.getMessage() );
         return "1";
     }

   }catch( SQLException e){
       System.out.println("error 1");
       System.err.println( e.getMessage() );
       return "1";
   }
 }
return r;
//       Subtract money from one savings or checking account and add it to another. A transfer can only
// occur between two accounts that have at least one owner in common. If the transfer was requested by a
// customer, she or he must be an owner of both accounts. Furthermore, the amount to be moved should
// not exceed $2,000.
 }

String enterCheckTransaction(String aid,String checknumber, double amount){
double newmainbalance=0;
String r= "1";
String checkaid= "SELECT A.AID FROM Account_Owns A "+
                 "WHERE A.AID = ? AND (A.ACC_TYPE= 'STUDENT_CHECKING' OR A.ACC_TYPE= 'INTEREST_CHECKING')";
try(PreparedStatement selectst= _connection.prepareStatement(checkaid)){
  selectst.setString(1, aid);
    try (ResultSet resultSet = selectst.executeQuery()){
      if(resultSet.next()){
        newmainbalance=this.checkBalance(aid,amount, "minus");
        String update= "Update Account_Owns A SET A.BAlANCE = ? WHERE A.AID = ?";
        try (PreparedStatement updateacc= _connection.prepareStatement(update)){
          updateacc.setDouble(1, newmainbalance);
          updateacc.setString(2, aid);
          updateacc.executeUpdate();
          updateacc.close();
          this.logTransaction("Write-Check",  amount, 0, checknumber, aid, null);
          r= "0";
        }catch( SQLException e){
            System.out.println("error 3");
            System.err.println( e.getMessage() );
            return "1";
        }

      } else {
        r="1";
      }
    }catch( SQLException e){
        System.out.println("error 2");
        System.err.println( e.getMessage() );
        return "1";
    }
}catch( SQLException e){
    System.out.println("error 1");
    System.err.println( e.getMessage() );
    return "1";
}



return r + " "+newmainbalance;


//Subtract money from the checking account. Associated with a check transaction is a check
// number. (Note that a check cannot be written from all account types.
}


public String SetPin(String taxid, int oldPin , int newPin){
      String r="1";
      int newhashpin=0;
      String getpin= "SELECT C.PIN FROM Customer C WHERE C.taxid= " + taxid;
      try (Statement checkpin= _connection.createStatement()){
          try (ResultSet resultSet = checkpin.executeQuery(getpin)){
              while(resultSet.next()) {
                  int pin = (resultSet.getInt(1));
                  if(pin==oldPin) {
                      newhashpin = reverseInteger(newPin);
                      String updatepin = "UPDATE CUSTOMER SET PIN = ? WHERE  taxid = ?  ";
                      try (PreparedStatement update = _connection.prepareStatement(updatepin)) {
                          update.setInt(1, newhashpin);
                          update.setString(2, taxid);
                          update.executeUpdate();
                          r = "0";

                      } catch (SQLException e) {
                          System.out.println("error 3");
                          System.err.println(e.getMessage());
                          return "1";
                      }
                  }
                  else{
                      System.out.println("Wrong pin, cannot change");
                  }
              }
          }catch( SQLException e){
              System.out.println("error 2");
              System.err.println( e.getMessage() );
              return "1";
          }

      }catch( SQLException e){
          System.out.println("error 1");
          System.err.println( e.getMessage() );
          return "1";
      }

      return r;

//   Add money to the checking or savings account. The amount added is the monthly interest
// rate times the average daily balance for the month (e.g., an account with balance $30 for 10 days and $60
// for 20 days in a 30-day month has an average daily balance of $50, not $45!). Interest is added at the end
// of each month.
  }

boolean VerifyPin(int pin, String taxid){

 int unhashedpin;
 String getpin= "SELECT C.PIN FROM Customer C WHERE C.taxid= " + taxid;
 try(Statement checkpin= _connection.createStatement()){
     try (ResultSet resultSet = checkpin.executeQuery(getpin)){
           while(resultSet.next()) {
               int resultpin = (resultSet.getInt(1));
               if(pin==reverseInteger(resultpin)){
                 System.out.println("0");

               }else{
                 System.out.println("cannot verify");
               }

           }
     }catch( SQLException e){
         System.out.println("error 1");
         System.err.println( e.getMessage() );
         return false;
     }


 }catch( SQLException e){
     System.out.println("error 1");
     System.err.println( e.getMessage() );
     return false ;
 }
  return true;
}

int reverseInteger(int number) {
      boolean isNegative = number < 0 ? true : false;
      if(isNegative){
          number = number * -1;
      }
      int reverse = 0;
      int lastDigit = 0;

      while (number >= 1) {
          lastDigit = number % 10; // gives you last digit
          reverse = reverse * 10 + lastDigit;
          number = number / 10; // get rid of last digit
      }

      return isNegative == true? reverse*-1 : reverse;
  }


String getAID(String tin){
String aid=" ";
String getaccountID= "SELECT A.AID FROM Account_Owns A WHERE A.TAXID= "+ tin;
try( Statement select= _connection.createStatement()){

  ResultSet answer0 = select.executeQuery(getaccountID);
  if(answer0.next())
   aid= answer0.getString("AID");
   return aid;


}catch (SQLException e){
    return "error";

}
}
int menu()
{
  int menuChoice;
  do
  {
      System.out.print("\nPlease Choose From the Following Options:"
              + "\n 1. Display Balance \n 2. Deposit"
              + "\n 3. Withdraw\n 4. Top Up\n 5.Purchase\n 6.Transfer"
              + "\n 7.Collect\n 8.Wire\n 9.Pay-Friend\n 10. Log Out\n\n");

      menuChoice = scan.nextInt();

      if (menuChoice < 1 || menuChoice > 10){
          System.out.println("error");
      }

  }while (menuChoice < 1 || menuChoice > 10);

  return menuChoice;
}
void startAtm()throws SQLException {
    String r;


    String tin, accountid;
    int pin;
    int count = 0, menuOption = 0;
    double depositAmt = 0, withdrawAmt = 0, currentBal=0;
    boolean  pinVerified= false;
    //loop that will count the number of login attempts
    //you make and will exit program if it is more than 3.
    //as long as oriBal equals an error.
    do{

        System.out.println("Please Enter Your Tax ID: ");
        tin = scan.next();

        System.out.println("Enter Your PIN: ");
        pin = scan.nextInt();

        pinVerified= this.VerifyPin(pin,tin);

        count++;

        if (count >= 3 && pinVerified== false){
            System.out.print("Maximum Login Attempts Reached.");
            System.exit(0);
        }




    }while(pinVerified== false);


    //this loop will keep track of the options that
    //the user inputs in for the menu. and will
    //give the option of deposit, withdraw, or logout.


    while (menuOption != 10)
    {
        menuOption=this.menu();
        switch (menuOption)
        {
        case 1:
        System.out.print("\nEnter Account ID: ");
        String id = scan.next();
            r =this.showBalance(id);
            System.out.println(r);
            break;
        case 2:
            System.out.print("\nEnter Account ID You Wish to Deposit From: ");
            String depositid= scan.next();
            System.out.print("\nEnter Amount You Wish to Deposit: ");
            depositAmt = scan.nextDouble();
            r =this.deposit(depositid, depositAmt );
            System.out.println(r);
            break;
        // case 3:
        //     System.out.print("\nEnter Amount You Wish to Withdrawl: ");
        //     double withdrawalAmt = scan.nextDouble();
        //     this.withdrawal(accountid, withdrawalAmt);
        //     break;
        case 4:
            System.out.print("\nEnter Pocket ID: ");
            String pocketid= scan.next();
            System.out.print("\nEnter Amount You Wish to Top-Up: ");
            double topUpAmt= scan.nextDouble();
            r =  this.topUp(pocketid,topUpAmt);
            System.out.println(r);

            break;
        // case 5:
        //     System.out.print("\nEnter Account ID");
        //     String accId = scan.next();
        //     System.out.print("\nEnter Amount to Purchase Item(s)");
        //     double purchaseAmt= scan.nextDouble();
        //     this.purchase(accId,purchaseAmt);
        //     break;
        case 6:
          System.out.print("\nEnter Account ID to Transfer From");
          String transferfromaccId = scan.next();
          System.out.print("\nEnter Account ID to Transfer To");
          String transfertoaccId = scan.next();
          System.out.print("\nEnter Amount");
          double transferAmt= scan.nextDouble();
          r=this.transfer(transferfromaccId, transfertoaccId, tin, transferAmt);
          System.out.println(r);
          break;
        case 7:
          System.out.print("\nEnter Main Account ID: ");
          String collectfromaccId = scan.next();
          System.out.print("\nEnter Pocket Account ID: ");
          String collecttoaccId = scan.next();
          System.out.print("\nEnter Amount to Collect: ");
          double collectAmt= scan.nextDouble();
          r=this.collect(collectfromaccId,collecttoaccId,collectAmt);
          System.out.println(r);
          break;
        case 8:
          System.out.print("\nEnter Account ID to Wire From");
          String wirefromaccId = scan.next();
          System.out.print("\nEnter Account ID to Wire To");
          String wiretoaccId = scan.next();
          System.out.print("\nEnter Amount");
          double wireAmt= scan.nextDouble();
          r=this.wire(wiretoaccId,wirefromaccId,wireAmt,tin);
          System.out.println(r);
          break;
        case 9:
        System.out.print("\nEnter Account ID to Pay Friend From");
        String pffromaccId = scan.next();
        System.out.print("\nEnter Friend's Account ID");
        String pftoaccId = scan.next();
        System.out.print("\nEnter Amount to Pay");
        double pfAmt= scan.nextDouble();
        r=this.payFriend(pffromaccId,pftoaccId,pfAmt);
        System.out.println(r);
        break;


        case 10:
            System.out.print("\nThank For Using My ATM.  Have a Nice Day.  Good-Bye!");
            System.exit(0);
            break;
    }
  }
}
  //public void accrueInterest(String aid){
    //  String sql="SELECT A.aid, SUM(A.num_days * A.balance)/30 FROM Avg_Balance GROUP BY A.aid";
  //}



}