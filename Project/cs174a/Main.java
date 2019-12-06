package cs174a;                         // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;
import java.sql.*;

/**
 * This is the class that launches your application.
 * DO NOT CHANGE ITS NAME.
 * DO NOT MOVE TO ANY OTHER (SUB)PACKAGE.
 * There's only one "main" method, it should be defined within this Main class, and its signature should not be changed.
 */
public class Main
{
	/**
	 * Program entry point.
	 * DO NOT CHANGE ITS NAME.
	 * DON'T CHANGE THE //!### TAGS EITHER.  If you delete them your program won't run our tests.
	 * No other function should be enclosed by the //!### tags.
	 */
	//!### COMENZAMOS
	public static void main( String[] args )
	{
		App app = new App();                        // We need the default constructor of your App implementation.  Make sure such
													// constructor exists.
		String r = app.initializeSystem();          // We'll always call this function before testing your system.
		if( r.equals( "0" ) )
		{

		//app.populateTables();

		//	app.exampleAccessToDB();                // Example on how to connect to the DB.
			   r= app.dropTables();
			   	System.out.println( r );
			  r = app.createTables();
			 		app.populateTables();
		 // 		r= app.collect("53027","12121",12.12);
		  System.out.println( r );

			 	 	System.out.println( r );
			 	   r= app.setDate( 2011, 8, 15 );
				  System.out.println( r );
				r= app.createCustomer( "17431", "9599663", "Manuel Partida", "6749 Trigo" );
				System.out.println( r );
				r=app.createCheckingSavingsAccount( AccountType.SAVINGS, "123456789", 10000, "9599663",null, null );
				System.out.println( r );
				r= app.showBalance( "93156" ) ;
				System.out.println( r );

				r=app.deposit( "93156", 34.54 );
				System.out.println( r );

				r=app.createPocketAccount( "90829", "93156", 22, "344151573" );
				System.out.println( r );

				r=app.topUp("67521", 22.22 );
				System.out.println( r );

				r=app.payFriend( "67521", "60413", 55.55 );
				System.out.println( r );

				r=app.listClosedAccounts();
				System.out.println( r );



			//System.out.println( r );


					app.atmOrteller();

// 		if ( app.checkPocketTransaction("908308")==true)
// 			r= "0";
// else
// 		r="1";

		 //app.startAtm();

		//	r= app.dropßß
			// Example tests.  We'll overwrite your Main.main() function with our final tests.


			//
			// // Another example test.
			 // r = app.showBalance("909090909");
			 // System.out.println( r );
		}



	}
	//!### FINALIZAMOS
}
