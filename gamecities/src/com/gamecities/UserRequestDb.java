package com.gamecities;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class UserRequestDb extends RequestDb {
  private Connection c;
  private Statement stmt = null;
  private String userSqlStmt = null;
  private ResultSet rs = null;
  private String existResponse = null;
  private String bd = "cities";

  UserRequestDb() throws Exception {
    c =  super.connectDb(bd);
  }

  synchronized boolean cityExistsDb(String cityUser) throws Exception {
    stmt = c.createStatement();
    userSqlStmt = "SELECT true FROM city WHERE name = '" + cityUser + "' LIMIT 1;";
    rs = stmt.executeQuery(userSqlStmt);

    while ( rs.next() ) {
      existResponse = rs.getString("bool");
    }

    if ( existResponse != null) return true;
    else return false;
    }

  protected synchronized void disconnectDb() throws Exception {
    rs.close();
    stmt.close();
    c.close();
  }
}