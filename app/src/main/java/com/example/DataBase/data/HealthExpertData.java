package com.example.DataBase.data;

public class HealthExpertData {
    int doctorid; //pk
    String expertName;
    int expertAge;
    String expertPosition;
    String expertBio;
    String expertEmail;
    String expertPhoneNo;

    public HealthExpertData(int doctorid, String expertName, int expertAge, String expertPosition, String expertBio, String expertEmail, String expertPhoneNo) {
        this.doctorid = doctorid;
        this.expertName = expertName;
        this.expertAge = expertAge;
        this.expertPosition = expertPosition;
        this.expertBio = expertBio;
        this.expertEmail = expertEmail;
        this.expertPhoneNo = expertPhoneNo;
    }

    public int getDoctorid() {
        return doctorid;
    }

    public String getExpertName() {
        return expertName;
    }

    public int getExpertAge() {
        return expertAge;
    }

    public String getExpertPosition() {
        return expertPosition;
    }

    public String getExpertBio() {
        return expertBio;
    }

    public String getExpertEmail() {
        return expertEmail;
    }

    public String getExpertPhoneNo() {
        return expertPhoneNo;
    }
}
