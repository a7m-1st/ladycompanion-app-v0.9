package com.example.DataBase.data.people;

import com.example.DataBase.sqlConnector;

import java.sql.SQLException;

public class agencyUser {

    private String companyType, companyName, email, address;
    private int id;
    private long phonenumber;

    public agencyUser(String email) {

        new Thread(() -> {
            try {
                sqlConnector connect = new sqlConnector();
                int id = connect.getAgencyUserIDfromEmail(email);
                String info[] = connect.retrieveAgencyUserDetails(id);
                //Unpack string array with appropriate type casting
                this.companyType = info[0];
                this.companyName = info[1];
                this.phonenumber = Long.parseLong(info[2]);
                this.email = info[3];
                this.address = info[4];
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
