package com.example.BimoPart;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private TextView forgotPasswordTextView;


    // Assuming you have hardcoded credentials for simplicity
    private final String correctEmail = "test";
    private final String correctPassword = "123";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Alternate Connection Method by Hari
        new Thread(() -> {
            Looper.prepare(); // To allow Toast to work on Thread
            try {
                sqlConnector sqlconnect = new sqlConnector();
                Toast.makeText(this, "Connection to DB Successful!", Toast.LENGTH_SHORT).show();
                sqlconnect.closeConnection(); // Closed, but can be opened in other places later.
                // Good Practice to close connection to ensure buffers flushed.

            } catch (SQLException e) {
                Toast.makeText(this, "Unable to Connect to Database", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                // Exits android app if connection error
                this.finishAffinity();
                System.exit(0);
            }
            Looper.loop(); // To allow Toast to work on Thread
        }).start();







//Older code, can be uncommented if the above thread doesnt work.
//        try {
//            sqlConnector sqlconnect = new sqlConnector();
//            Toast.makeText(this, "Connection to DB Successful!", Toast.LENGTH_SHORT).show();
//            sqlconnect.closeConnection(); //Closed, but can be opened in other places later.
//            //Good Practice to close connection to ensure buffers flushed.
//
//        } catch (SQLException e) {
//            Toast.makeText(this, "Unable to Connect to Database", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//            //Exits android app if connection error
//            this.finishAffinity();
//            System.exit(0);
//        }
    }
}