public void populateTables(){
        try (Statement statement = _connection.createStatement()) {
            //INSRTING CUSTOMERS


            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Alfred Hitchcock', '361721022', '6667 El Colegio #40', 1234)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Billy Clinton', '231403227', '5777 Hollister', 1468)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Cindy Laugher', '412231856', '7000 Hollister', 3764)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('David Copperfill', '207843218', '1357 State St', 8582)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Elizabeth Sailor', '122219876', '4321 State St', 3856)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Fatal Castro', '401605312', '3756 La Cumbre Plaza', 8193)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('George Brush', '201674933', '5346 Foothill Av', 9824)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Hurryson Ford', '212431965', '678 State St', 8471)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Ivan Lendme', '322175130', '1235 Johnson Dr', 1234)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Joe Pepsi', '344151573', '3210 State St', 3692)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Kelvin Costner', '209378521', 'Santa Cruz #3579', 4659)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Li Kung', '212116070', '2 People''s Rd Beijing', 9173)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Magic Jordon', '188212217', '3852 Court Rd', 7351)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Nam-Hoi Chung', '203491209', '1997 People''s St HK', 5340)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Olive Stoner', '210389768', '6689 El Colegio #151', 8452)");
            statement.executeUpdate("INSERT INTO Customer(name,taxid,address, PIN) VALUES ('Pit Wilson', '400651982', '911 State St', 1821)");
            //SETTING DATE


            this.setDate(2011,3,1);


            // CREATING ACCOUNTS+INITIAL DEPOSITS/TOPUPS


            this.accountUtility(AccountType.STUDENT_CHECKING, "17431", 1200, "344151573", "San Francisco");
            this.accountUtility(AccountType.STUDENT_CHECKING, "54321", 12000, "212431965", "Los Angeles");
            this.accountUtility(AccountType.STUDENT_CHECKING, "12121", 1200, "207843218", "Goleta");
            this.accountUtility(AccountType.INTEREST_CHECKING, "41725", 15000, "201674933", "Los Angeles");
            this.accountUtility(AccountType.INTEREST_CHECKING, "93156", 2000000, "209378521", "Goleta");
            this.accountUtility(AccountType.SAVINGS, "43942", 1289, "361721022", "Santa Barbara");
            this.accountUtility(AccountType.SAVINGS, "29107", 34000, "209378521", "Los Angeles");
            this.accountUtility(AccountType.SAVINGS, "19023", 2300, "412231856", "San Francisco");
            this.accountUtility(AccountType.SAVINGS, "32156", 1000, "188212217", "Goleta");
            this.accountUtility(AccountType.INTEREST_CHECKING, "76543", 8456, "212116070", "Santa Barbara");
            this.createPocketAccount( "53027", "12121", 50, "207843218", "Goleta" );
            this.createPocketAccount( "60413","43942", 20, "400651982", "Santa Cruz");
            this.createPocketAccount( "43947", "29107", 30, "212116070", "Isla Vista" );
            this.createPocketAccount( "67521","19023", 100, "401605312", "Santa Barbara" );

            //ADDING CO_OWNERS

            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('17431','412231856' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('17431','322175130' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('54321','412231856' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('54321','122219876' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('54321','203491209' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('41725','401605312' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('41725','231403227' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('76543','188212217' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('93156','188212217' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('93156','210389768' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('93156','122219876' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('93156','203491209' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('43942','400651982' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('43942','212431965' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('43942','322175130' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('29107','212116070' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('29107','210389768' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('19023','201674933' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('19023','401605312' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('32156','207843218' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('32156','122219876' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('32156','361721022' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('32156','203491209' )");
            statement.executeUpdate("INSERT INTO Co_owns (aid,taxid) VALUES('32156','210389768' )");


            //OTHER Transactions

            this.deposit("17431", 8800);
            this.withdrawal("54321",3000);
            this.withdrawal("76543",2000);
            this.purchase("53027",5);
            this.withdrawal("93156",1000000);
            this.writeCheck("93156","4887347632",950000);
            this.withdrawal("29107",4000);
            this.collect("43947","29107",10);
            this.topUp("43947",30);
            this.transfer("43942", "17431", "322175130", 289);
            this.withdrawal("43942",289);
            this.payFriend("60413","67521",10);
            this.deposit("93156",50000);
            this.writeCheck("12121","4327171423",200);
            this.transfer("41725","19023","201674933",1000);
            this.wire("32156", "41725", 4000,"401605312");
            this.payFriend("53027","60413",10);
            this.purchase("60413",15);
            this.withdrawal("93156",20000);
            this.writeCheck("76543","3984628746",456);
            this.topUp("67521",50);
            this.payFriend("67521","53027",20);
            this.collect("43947","29107",15);


        }
        catch( SQLException e )
        {
            System.err.println( e.getMessage() );

        }
    }
