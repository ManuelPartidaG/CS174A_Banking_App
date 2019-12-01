package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.*;
import java.lang.*;
import java.time.LocalDate;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
    private OracleConnection _connection;                   // Example connection object to your DB.

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
                "interest REAL, "+
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

        try (Statement statement = _connection.createStatement()) {
            statement.executeUpdate(createCustomer);
            statement.executeUpdate(createAccount_Owns);
            statement.executeUpdate(createPocket);
            statement.executeUpdate(createCo_owns);
            statement.executeUpdate(createTransaction_Performed);
            statement.executeUpdate(createClosed);
            statement.executeUpdate(createDate);
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


    public void logTransaction(String trans_type, double amount, double tfee, String checknum, String acc_to, String acc_from){
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date tdate=new java.sql.Date(utilDate.getTime());
        String tid ="0";
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

    public void closeAccount(String aid){
        //1. check if account has a pocket account by checking if it exists in Pocket
        //2. if it does, insert pocket account into closed
        String checkForPocket = "SELECT P.aid FROM Pocket P WHERE P.aid = ?";
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

/**
 * Move a specified amount of money from one pocket account to another pocket account.
 * @param from Source pocket account ID.
 * @param to Destination pocket account ID.
 * @param amount Non-negative amount to pay.
 * @return a string "r fromNewBalance toNewBalance", where
 *         r = 0 for success, 1 for error.
 *         fromNewBalance is the new balance of the source pocket account, with up to 2 decimal places (e.g. with %.2f); and
 *         toNewBalance is the new balance of destination pocket account, with up to 2 decimal places.
 */

//TO DO: make a method that will test if a pocket account has appeared in a transaction this month
    public String payFriend( String from, String to, double amount ){
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
            //4. if new source balance is less than equal to 0.01, close account
            if(new_from_balance<=0.01)
                closeAccount(from);
            return "0 "+ new_from_balance + " "+ new_to_balance;
        }
        //else amount would set balance negative
        return response;

    }

    public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin, String branch )throws SQLException{

      String check = " SELECT A.AID"+
        " FROM Account_Owns A" +
        " WHERE A.AID = ?";

        try(PreparedStatement checkstatement= _connection.prepareStatement(check)){
          checkstatement.setString(1, linkedId);
          try (ResultSet resultSet = checkstatement.executeQuery()){
              if(resultSet.next()){

                  String insertAcc= "INSERT INTO Account_Owns (AID, BRANCH, ACC_TYPE, BALANCE, INTEREST_RATE, INTEREST, TAXID) VALUES (?,?,'POCKET',0,0,0,?)";
                  try(PreparedStatement s = _connection.prepareStatement(insertAcc)) {

                      System.out.println("NO ERROR YET");
                      s.setString(1, id);
                      s.setString(2, branch);
                      s.setString(3, tin);
                      s.executeUpdate();
                      System.out.println("NO ERROR YET");

                      String insertPocket = "INSERT INTO Pocket (PAID ,AID, POCKET_FEE) VALUES (?,?,0.00) ";
                      try (PreparedStatement s1 = _connection.prepareStatement(insertPocket)){
                        s1.setString(1, id);
                        s1.setString(2, linkedId);
                        s1.executeUpdate();
                        this.topUp(id, initialTopUp);
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




    //1. update the account
    //check that account is not in closed
    //2. check that accountId corresponds to a savings account or a checking account
    //3. insert into Transaction_Performed
    public String deposit( String accountId, double amount ) throws SQLException{
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
 		 }//1. update the account
    //check that account is not in closed
    //2. check that accountId corresponds to a savings account or a checking account
    //3. insert into Transaction_Performed


    public String showBalance( String accountId )throws SQLException {


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


    //NEW methods//

    //HELPER function
    //if op=minus subtract from balance
    //if op=plus add to balance
    public double checkBalance(String aid, double amount, String op){
        double balance=0;
        String checkBalance = "SELECT A.balance FROM Account_Owns A WHERE A.aid = ?";
        try (PreparedStatement statement = _connection.prepareStatement(checkBalance)) {
            statement.setString(1, aid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next())
                    balance = resultSet.getDouble(1);
            }
        }catch( SQLException e){
            System.err.println( e.getMessage() );
        }
        balance=Math.round(balance * 100.0)/100.0;
        if(op.equals("minus"))
            balance-=amount;
        else
            balance+=amount;
        return balance;
    }



    //USES checkBalance
    public String topUp( String accountId, double amount ){
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
                    if(newMainBalance > 0){
                        String updateBalance="UPDATE Account_Owns A SET A.balance = ? WHERE A.aid = ?";
                        try(PreparedStatement s = _connection.prepareStatement(updateBalance)) {
                            s.setDouble(1, newMainBalance);
                            s.setString(2, aid);
                            s.executeUpdate();
                        }
                        double newPocketBalance=this.checkBalance(accountId,amount, "plus");
                        //3. update the pocket account's balance in the Account_owns table to be +amount
                        try(PreparedStatement s = _connection.prepareStatement(updateBalance)) {
                            s.setDouble(1, newPocketBalance);
                            s.setString(2, accountId);
                            s.executeUpdate();
                        }
                         //4. make an entry in Transaction_Performed
                        this.logTransaction("Top Up",amount,0,null,accountId,aid);
                    }
                    if(newMainBalance <=0.01)
                        this.closeAccount(aid);
                }
            }
        }catch( SQLException e){
            System.err.println( e.getMessage() );
            return "1";
        }



        return "1";
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


    //checks if pocket account has had a transaction this month
  /*  public boolean checkPocketTransaction(String aid){
        //1. query transactions_owns table check if there is a row where the date's month is equal to the current date
        String checkBalance = "SELECT * FROM Account_Owns A WHERE A.aid = ? AND (SELECT EXTRACT(MONTH FROM A.tdate) = "+
                "(SELECT EXTRACT(MONTH FROM C.cdate) FROM Current_Date C))";
        try (PreparedStatement statement = _connection.prepareStatement(checkBalance)) {

            statement.setString(1, aid);
            System.out.println("hahahahhhahaaaha");
            try (ResultSet resultSet = statement.executeQuery()) {
              System.out.println("hahahahhhahaaaha");

                while (resultSet.next()){

                  String tmp= resultSet.getString(1);
                  System.out.println(tmp);
                    return true;
                  }
            }
        }catch( SQLException e){

            System.err.println( e.getMessage() );
            return false;
        }
        return false;
    }*/
}
