package com.example.BimoPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class fragment_forgotpassword extends Fragment {
    private EditText codeEditText, newPasswordEditText;
    private Button resetPasswordButton;
    private String userEmail, userType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgotpassword, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        codeEditText = view.findViewById(R.id.codeEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);

        if (getArguments() != null) {
            userEmail = getArguments().getString("email");
            userType = getArguments().getString("userType");
        }

        resetPasswordButton.setOnClickListener(v -> {
            String code = codeEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();

            if (code.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                return;
            }

            resetPassword(code, newPassword);
        });
    }

    private void resetPassword(final String code, final String newPassword) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                String storedOTP = sharedPref.getString("OTP", "");

                if (code.equals(storedOTP)) {
                    sqlConnector connector = new sqlConnector();
                    connector.resetPassword(userEmail, newPassword);

                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Password reset successfully", Toast.LENGTH_LONG).show();
                        NavController navController = Navigation.findNavController(getActivity(), R.id.resetPasswordButton);
                        navController.navigate(R.id.DestLogin);
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Incorrect OTP", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (final Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void handleResetPasswordResponse(HttpURLConnection conn) {
        // Read the server's response and act accordingly
        // For example, show a success message or handle errors
    }
}
