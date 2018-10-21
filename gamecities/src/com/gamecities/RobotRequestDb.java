package com.gamecities;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class RobotRequestDb extends RequestDb {
  private Connection c;
  private Statement stmt = null;
  private String userSqlStmt = null;
  private ResultSet rs = null;
  private String robotCityName = null;
  private String bd = "cities";

  RobotRequestDb() throws Exception {
    c =  super.connectDb(bd);
  }

  synchronized String robotCityNameDb(String startName, String exceptionNames) throws Exception {
    stmt = c.createStatement();
    userSqlStmt = "SELECT DISTINCT name FROM city WHERE region_id<=45 AND " + startName + " AND name NOT LIKE ('%)%') AND name NOT LIKE  ('% %') AND name NOT IN " + exceptionNames + " LIMIT 1;";
    rs = stmt.executeQuery(userSqlStmt);

    while ( rs.next() ) {
    robotCityName = rs.getString("name");
    }

    return robotCityName;
  }

  protected synchronized void disconnectDb() throws Exception {
    rs.close();
    stmt.close();
    c.close();
  }
}