package com.example.MainActivity;

import com.example.DataBase.data.currentLoginData;

public class onLogOut {
    onLogOut(boolean flag) {
        if(flag){
            resetCurrentData();

        }
    }
    public boolean resetCurrentData() {
        return currentLoginData.resetCurrentData();
    }
}
