/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package src.util.database;

/**
 *
 * @author tiago
 */

import java.sql.*;

public class DBtest {

public static void main(String[] argv) {
  System.out.println("Checking if Driver is registered with DriverManager.");

  try {
    Class.forName("org.postgresql.Driver");
  } catch (ClassNotFoundException cnfe) {
    System.out.println("Couldn't find the driver!");
    System.out.println("Let's print a stack trace, and exit.");
    cnfe.printStackTrace();
    System.exit(1);
  }

  System.out.println("Registered the driver ok, so let's make a connection.");

  Connection c = null;

  try {
    // The second and third arguments are the username and password,
    // respectively. They should be whatever is necessary to connect
    // to the database.
    c = DriverManager.getConnection("jdbc:postgresql://localhost/itsimple",
                                    "postgres", "administrator");
  } catch (SQLException se) {
    System.out.println("Couldn't connect: print out a stack trace and exit.");
    se.printStackTrace();
    System.exit(1);
  }

  if (c != null)
    System.out.println("Hooray! We connected to the database!");
  else
    System.out.println("We should never get here.");


  //Accessing the database
  System.out.println();

    Statement s = null;

    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
                         "that probably means we're no longer connected.");
      se.printStackTrace();
      System.exit(1);
    }
    ResultSet rs = null;
    try {
      rs = s.executeQuery("SELECT * FROM plan");
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
                         "that probably means our SQL is invalid");
      se.printStackTrace();
      System.exit(1);
    }

    int index = 0;

    try {
      while (rs.next()) {
          System.out.println("Here's the result of row " + index++ + ":");
          System.out.println(rs.getString(1) + " - " + rs.getString(2));
      }
    } catch (SQLException se) {
      System.out.println("We got an exception while getting a result:this " +
                         "shouldn't happen: we've done something really bad.");
      se.printStackTrace();
      System.exit(1);
    }

  }


}
