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
