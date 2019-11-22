package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		final String DB_USER = "c##YoutNetID";
		final String DB_PASSWORD = "YourPassword";

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
	String dropTables()
	{
		try (Statement statement = _connection.createStatement()) {
			statement.executeUpdate("DROP TABLE Transaction_Performed");
			statement.executeUpdate("DROP TABLE Co_owns");
			statement.executeUpdate("DROP TABLE Pocket");
			statement.executeUpdate("DROP TABLE Account_Owns");
			statement.executeUpdate("DROP TABLE Customer");
			statement.executeUpdate("DROP TABLE Date");
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
	String createTables()
	{
		String createCustomer = "CREATE TABLE Customer(" +
                            "name VARCHAR(100)," +
                            "taxid INTEGER," +
                            "address VARCHAR(100)," + 
                            "PIN INTEGER," +
                            "PRIMARY KEY(taxid))";

		String createAccount_Owns = "CREATE TABLE Account_Owns("+
							"aid INTEGER, "+
                            "branch VARCHAR(100),"+
                            "acc_type VARCHAR(100), "+
                            "balance REAL, "+
                            "interest_rate  REAL, "+
                            "interest REAL, "+
                            "taxid INT NOT NULL"
                            "PRIMARY KEY(aid)"+
                            "FOREIGN KEY(taxid) REFERENCES Customer(taxid))";

		String createTransaction_Performed =" CREATE TABLE Transaction_Performed("+
                                      "tid INTEGER, "+
                                      "tdate DATE, "+
                                      "trans_type VARCHAR(100),"+
                                      "amount REAL,"+
                                      "tfee REAL,"+
                                      "checknum INTEGER,"+
                                      "acc_to INTEGER NOT NULL,"+
                                      "acc_from INTEGER,"+
                                      "PRIMARY KEY(tid),"+
                                      "FOREIGN KEY(acc_to) REFERENCES Account(aid), "+
                                      "FOREIGN KEY(acc_from) REFERENCES Account(aid))";

      	String createCo_owns= "CREATE TABLE Co_owns("+
                      "aid INTEGER,"+
                      "taxid INTEGER,"+
                      "PRIMARY KEY (aid, taxid),"+
                      "FOREIGN KEY(aid) REFERENCES Account(aid),"+
                      "FOREIGN KEY (taxid) REFERENCES Customer(taxid))";

      	String createPocket = "CREATE TABLE Pocket("+ 
                      "paid INTEGER,"+
                      "aid INTEGER NOT NULL,"+
                      "pocket_fee REAL,"+
                      "PRIMARY KEY(paid),"+
                      "FOREIGN KEY(paid) REFERENCES Account(aid) ON DELETE CASCADE)," +
                      "FOREIGN KEY(aid) REFERENCES Account(aid)";

        String createDate = "CREATE TABLE Date("+
        				"year INT,"+
        				"month INT,"+
        				"day INT,"+
        				"tasksCompleted INT,"+
        				"PRIMARY KEY (year, month, day));"

		String createClosed = "CREATE TABLE Closed("+
							"aid INT,"+
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

	/**
	 * Set system's date.
	 * @param year Valid 4-digit year, e.g. 2019.
	 * @param month Valid month, where 1: January, ..., 12: December.
	 * @param day Valid day, from 1 to 31, depending on the month (and if it's a leap year).
	 * @return a string "r yyyy-mm-dd", where r = 0 for success, 1 for error; and yyyy-mm-dd is the new system's date, e.g. 2012-09-16.
	 */
	String setDate( int year, int month, int day ){
		String insertDate = "INSERT INTO Date(year, month, day)"+
									"VALUES(?,?,?");
		try (PreparedStatement statement = _connection.prepareStatement()) {
			statement.executeUpdate(insertDate);
			statement.setInt(3,day);
			if()
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	
	}


	/**
	 * Example of one of the testable functions.
	 */
	@Override
	public String listClosedAccounts()
	{
		return "0 it works!";
	}

/**
	 * Create a new checking or savings account.
	 * If customer is new, then their name and address should be provided.
	 * @param accountType New account's checking or savings type.
	 * @param id New account's ID.
	 * @param initialBalance Initial account balance.
	 * @param tin Account's owner Tax ID number - it may belong to an existing or new customer.
	 * @param name [Optional] If customer is new, this is the customer's name.
	 * @param address [Optional] If customer is new, this is the customer's address.
	 * @return a string "r aid type balance tin", where
	 *         r = 0 for success, 1 for error;
	 *         aid is the new account id;
	 *         type is the new account's type (see the enum codes above, e.g. INTEREST_CHECKING);
	 *         balance is the account's initial balance with 2 decimal places (e.g. 1000.34, as with %.2f); and
	 *         tin is the Tax ID of account's primary owner.
	 */
	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		// check if initial balance is going to be enough 
		//1. check if Customer with taxid = tin exists in  Customer table 
		//2. if customer with taxid =tin deos not exist, then call this.createCustomer(id,tin, name, address)
		//3. insert into transaction_performed the deposit of intial balance 


		String createAccount = "INSERT INTO Account_Owns(aid, branch, acc_type, balance, interest_rate, interest, taxid)"+
								"VALUES(?, ?, ?, ?, ?, 0, ? )";
		try(PreparedStatement statement = _connection.prepareStatement()){
			statement.executeUpdate(createAccount);
			statement.setInt(1,id);
			statement.setString("Isla Vista");
			statement.setString(3,accountType);
			statement.setDouble(4, intialBlance);
			tatement.setInt(7, tin);
			if(accountType.equals("Interest-Checking")){
				statement.setDouble(5,3.0);
			}
			else if (accountType.equals("Savings")){
				statement.setDouble(5, 4.8);
			}
			else{
				statement.setDouble(5,0.0);
			}
			return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
		}

	}
	/**
	 * Create a new customer and link them to an existing checking or saving account.
	 * @param accountId Existing checking or saving account.
	 * @param tin New customer's Tax ID number.
	 * @param name New customer's name.
	 * @param address New customer's address.
	 * @return a string "r", where r = 0 for success, 1 for error.
	 */
	String createCustomer( String accountId, String tin, String name, String address ){
		//check that entry with aid=accountId exists in Account_Owns
		//1. check if there is an enry in Account_Owns where taxid=tin and aid=accountID
		//2. if there is not, make an entry in Customer(name, ttin, address, 0)
		//3. then make an entry into Co_owns(accountId, tin)
		return "0";

	}

/**
	 * Create a new pocket account.
	 * @param id New account's ID.
	 * @param linkedId Linked savings or checking account ID.
	 * @param initialTopUp Initial balance to be deducted from linked account and deposited into new pocket account.
	 * @param tin Existing customer's Tax ID number.  He/She will become the new pocket account's owner.
	 * @return a string "r aid type balance tin", where
	 *         r = 0 for success, 1 for error;
	 *         aid is the new account id;
	 *         type is the new account's type (see the enum codes above);
	 *         balance is the account's initial balance with up to 2 decimal places (e.g. 1000.12, as with %.2f); and
	 *         tin is the Tax ID of account's primary owner.
	 */
	String createPocketAccount( String id, String linkedId, double initialTopUp, String tin ){
		//1. check that linkedId account exists in Account_owns
		//check that account is not closed
		//2. call topUp(id, linkedinitalTopUP)
		//3. insert into Account_Owns
		return "0";
	}

	/**
	 * Move a specified amount of money from the linked checking/savings account to the pocket account.
	 * @param accountId Pocket account ID.
	 * @param amount Non-negative amount to top up.
	 * @return a string "r linkedNewBalance pocketNewBalance", where
	 *         r = 0 for success, 1 for error;
	 *         linkedNewBalance is the new balance of linked account, with up to 2 decimal places (e.g. with %.2f); and
	 *         pocketNewBalance is the new balance of the pocket account.
	 */
	String topUp( String accountId, double amount ){
		//1. select aid from Pocket where paid=accountID 
		//check account is not closed
		//2. update the main account's balance to be -amount CHECK if the balance is above $0.01 
		//3. update the pocket account's balance in the Account_owns table to be +amount 
		//4. make an entry in Transaction_Performed
		return "0";
	}

	/**
	 * Deposit a given amount of dollars to an existing checking or savings account.
	 * @param accountId Account ID.
	 * @param amount Non-negative amount to deposit.
	 * @return a string "r old new" where
	 *         r = 0 for success, 1 for error;
	 *         old is the old account balance, with up to 2 decimal places (e.g. 1000.12, as with %.2f); and
	 *         new is the new account balance, with up to 2 decimal places.
	 */
	String deposit( String accountId, double amount ){
		//1. update the account 
		//check that account is not in closed
		//2. check that accountId corresponds to a savings account or a checking account 
		//3. insert into Transaction_Performed
		return "0";
	}

		/**
	 * Show an account balance (regardless of type of account).
	 * @param accountId Account ID.
	 * @return a string "r balance", where
	 *         r = 0 for success, 1 for error; and
	 *         balance is the account balance, with up to 2 decimal places (e.g. with %.2f).
	 */
	String showBalance( String accountId ){
		//1. select balance from Account_Owns where aid=accountId
		return "0";
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
	String payFriend( String from, String to, double amount ){
		//CHECK if account has enough balance and that account is not closed
		//check balance after transaction, see if it should be closed
		//1. update Account_Owns tables for both accounts
		//2. insert into Transaction_Performed 
		return "0";
	}
	/**
	 * Generate list of closed accounts.
	 * @return a string "r id1 id2 ... idn", where
	 *         r = 0 for success, 1 for error; and
	 *         id1 id2 ... idn is a list of space-separated closed account IDs.
	 */
	String listClosedAccounts(){
		//1. select from closed 
		return "0";
	}

	String closeAccount(String aid){
		//1. check if account has a pocket account by checking if it exists in Pocket
		//2. if it does, insert pocket account into closed
		//3. insert aid account into closed
		return  "0"; 
	}

}
