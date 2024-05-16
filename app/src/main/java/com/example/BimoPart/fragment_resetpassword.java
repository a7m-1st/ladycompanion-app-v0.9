package com.example.BimoPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

public class fragment_resetpassword extends Fragment {
    private EditText emailAddressEditText;
    private Button resetPasswordButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resetpassword, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailAddressEditText = view.findViewById(R.id.forgot_password_email);
        resetPasswordButton = view.findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailAddressEditText.getText().toString();
            Log.d("ResetPassword", "Button clicked.");
            Log.d("ResetPassword", "Email entered: " + email);

            if (email.isEmpty() || !isValidEmail(email)) {
                Toast.makeText(getActivity(), "Please enter a valid email address", Toast.LENGTH_LONG).show();
            } else {
                Log.d("ResetPassword", "Checking if email exists...");
                checkEmailExists(email);
            }
        });
    }

    private void checkEmailExists(final String email) {
        new Thread(() -> {
            try {
                sqlConnector connector = new sqlConnector();
                Log.d("ResetPassword", "Attempting to connect to database...");
                if (connector.isUserEmailPresent(email)) {
                    Log.d("ResetPassword", "Email found in database. Sending OTP.");
                    sendOTP(email);
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Email is not in the database/invalid", Toast.LENGTH_LONG).show();
                    });
                    Log.d("ResetPassword", "Email not found in database.");
                }
            } catch (final Exception e) {
                Log.e("ResetPassword", "Database check error: ", e);
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void sendOTP(String email) {
        String otp = generateOTP();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("OTP", otp);
        editor.apply();

        new Thread(() -> {
            try {
                sendEmailWithOTP(email, otp);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "OTP has been sent to your email", Toast.LENGTH_LONG).show();

                    // Navigation to the forgotpassword fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email); // Pass the email to the next fragment if needed
                    Navigation.findNavController(getView()).navigate(R.id.DestForgotPassword, bundle);
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Error sending OTP: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ResetPassword", "Error sending OTP: ", e);
                });
            }
        }).start();
    }


    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendEmailWithOTP(String email, String otp) {
        new Thread(() -> {
            try {
                final String username = "herladycompanion@gmail.com"; // Replace with your email
                final String password = "bkdz hwar nnun omgz"; // Replace with your email password

                Properties prop = new Properties();
                prop.put("mail.smtp.host", "smtp.gmail.com");
                prop.put("mail.smtp.port", "587");
                prop.put("mail.smtp.auth", "true");
                prop.put("mail.smtp.starttls.enable", "true"); //TLS

                Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("herladycompanion@gmail.com")); // Replace with your email
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Lady Companion Password Reset");
                message.setText("Dear user,\n\n Your OTP for password reset is: " + otp);

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void processEmailCheckResponse(String response, String email) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean emailExists = jsonResponse.getBoolean("emailExists");
            String userType = jsonResponse.getString("userType"); // Assume userType is returned from the server

            if (emailExists) {
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                bundle.putString("userType", userType); // Pass userType to the next fragment
                Navigation.findNavController(getView()).navigate(R.id.DestForgotPassword, bundle);
            } else {
                Toast.makeText(getActivity(), "Email does not exist", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error processing response", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
