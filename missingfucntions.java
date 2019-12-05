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
