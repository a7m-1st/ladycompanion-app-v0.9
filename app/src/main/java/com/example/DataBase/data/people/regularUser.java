package com.example.DataBase.data.people;

import android.os.Parcelable;

import com.example.DataBase.sqlConnector;

import java.sql.SQLException;

public class regularUser {

    private String name, emailAddress, idnum, homeaddress;
    private long phonenumber;
    private int age, id;

    public regularUser(String email) {
        new Thread(() -> {
            try {
                sqlConnector connect = new sqlConnector();
                int id = connect.getRegularUserIDfromEmail(email);
                String info[] = connect.retrieveRegularUserDetails(id);
                //Unpacking String array here with appropriate type casting
                this.id = id;
                this.emailAddress = info[0];
                this.name = info[1];
                this.idnum = info[2];
                this.homeaddress = info[3];
                this.phonenumber = Long.parseLong(info[4]);
                this.age = Integer.parseInt(info[5]);
                connect.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //More methods can be implemented here as needed when details are to be extracted of user

    public int getID() {
        return this.id;
    }
}
