package com.example.DataBase.data;

public class AgencyData {
    int agentUserId; //PK
    String profilePic;
    String companyType;
    String companyName;
    String phone;
    String email;
    String address;
    String password;
    String dateJoin;

    public AgencyData(int agentUserId, String profilePic, String companyType, String companyName, String phone, String email, String address, String password, String dateJoin) {
        this.agentUserId = agentUserId;
        this.profilePic = profilePic;
        this.companyType = companyType;
        this.companyName = companyName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.password = password;
        this.dateJoin = dateJoin;
    }

    public int getAgentUserId() {
        return agentUserId;
    }
    public String getProfilePhoto() {
        return profilePic;
    }
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
    public String getCompanyType() {
        return companyType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getDateJoin() {
        return dateJoin;
    }
}
