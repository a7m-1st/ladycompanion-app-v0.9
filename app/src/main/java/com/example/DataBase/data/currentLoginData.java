package com.example.DataBase.data;

import android.os.StrictMode;

import com.example.DataBase.sqlConnector;

//TODO: Add DateJoin to User & Agency User Tables (AS A RESULT OF LINES 19)
//TODO: Add profile Photo ability
//TODO: MODIFY SQL CONNECTOR DATABASE AS A RESULT

//this class stores the current LogIn data
public class currentLoginData {

    public static UserData user;
    public static AgencyData agency;
    public static int userType; //0 - normal | 1 - agency

    public currentLoginData() {
        //Used to create an object without invoking database
    }

    public static int getId() {
        if(userType == 0) {
            return user.getUserId();
        } else if(userType == 1) {
            return agency.getAgentUserId();
        }

        return -1;
    }

    //Initialize the UserData Object when Login
    //Constructor for Normal UserData
    public currentLoginData(int userId, String profilePic,
                            String fullName, String email, String idNumber, String homeAddress,
                            String phone, String password, int age, String dateJoin) {

        user = new UserData(userId,  profilePic,
                fullName,  email, password,  idNumber,  homeAddress, phone,    age, dateJoin);

    }

    //Constructor for Agency Data
    public currentLoginData(int agentUserId, String profilePic, String companyType,
                            String companyName, String phone, String email, String address, String password, String dateJoin) {

        agency = new AgencyData(agentUserId,  profilePic,  companyType,
                 companyName,  phone,  email,  address,  password, dateJoin);

    }

    //From Login page
    //Input -> email, password
    //Output -> modify current Login Information
    public currentLoginData(String email, String password) {
        //This makes sure the database works before the fragment generation
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //NOT RECOMMENDED IN PRODUCTION CODE
        try {
            sqlConnector connection  = new sqlConnector();
            int userType = connection.findUserType(email);
            this.userType = userType;

            if(userType == 0) {
                int id = connection.getRegularUserIDfromEmail(email);
                String[] data = connection.retrieveRegularUserDetails(id);

                //Set static Data
                user = new UserData(id,  data[6],
                        data[1],  email, password,  data[2],  data[3], data[4],
                          Integer.parseInt(data[5]), data[7]);

            } else if(userType == 1) {
                int id = connection.getAgencyUserIDfromEmail(email);
                String[] data = connection.retrieveAgencyUserDetails(id);

                agency = new AgencyData(id, data[5], data[0], data[1], data[2], data[3], data[4], password, data[6]);
            }


            connection.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static UserData getUser() {
        return user;
    }

    public static AgencyData getAgency() {
        return agency;
    }

    public static int getUserType() {
        return userType;
    }

    //Resets current login data
    public static boolean resetCurrentData() {
        user = null;
        agency = null;
        return true;
    }
}
