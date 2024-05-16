package com.example.DataBase.data;

public class UserData {
    //Userdata from Posts page
    int UserId; //PK
    String profilePic;

    //Userdata from LogIn
    String fullName;
    String email;
    String idNumber;
    String homeAddress;
    String phone;
    String password;
    int age;
    String dateJoin;

    public UserData(int userId, String profilePic,
                    String fullName, String email, String password, String idNumber, String homeAddress,
                    String phone,  int age, String dateJoin) {
        UserId = userId;
        this.profilePic = profilePic;
        this.fullName = fullName;
        this.email = email;
        this.idNumber = idNumber;
        this.homeAddress = homeAddress;
        this.phone = phone;
        this.password = password;
        this.age = age;
        this.dateJoin = dateJoin;
    }

    public int getUserId() {
        return UserId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public String getDateJoin() {
        return dateJoin;
    }
}
