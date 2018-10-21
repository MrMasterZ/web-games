package com.gamecities;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class RequestDb {
  private Connection c = null;

protected synchronized Connection connectDb(String bd) throws Exception {
  c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + bd, "postgres", "12345678");
  c.setAutoCommit(false);
  return c;
}

protected abstract void disconnectDb() throws Exception;
}