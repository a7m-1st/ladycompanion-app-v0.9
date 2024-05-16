package com.example.DataBase;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;

public class InitializeConnection {
    public boolean connectToDatabase() {
        //Test connnection
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            //load the MySQL JDBC driver class dynamically at runtime
            Class.forName("com.mysql.jdbc.Driver");

            String connectionString = "jdbc:mysql://<Ip-addressOfCloudServer>:3306/mydatabase?useSSL=false";
            String username = "root", password = "ladies";

            //establish a connection to a MySQL database using the JDBC driver
            Connection con = DriverManager.getConnection(connectionString, username, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
