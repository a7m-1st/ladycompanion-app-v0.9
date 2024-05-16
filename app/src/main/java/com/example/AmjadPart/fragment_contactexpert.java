package com.example.AmjadPart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.DataBase.data.AgencyData;
import com.example.DataBase.data.UserData;
import com.example.DataBase.data.currentLoginData;
import com.example.ladyapp.R;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class fragment_contactexpert extends Fragment {
    public static String experPhoneNo , expertEmail;
    static int PERMISSION_CODE = 100;
    EditText emailET;
    Button sendEmailBTN, callExpertBTN;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contactexpert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailET = view.findViewById(R.id.inquiry);
        sendEmailBTN = view.findViewById(R.id.sendMessage);
        callExpertBTN = view.findViewById(R.id.callExpert);

        sendEmailBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inquiry = emailET.getText().toString();
                try {
                    sendInquiry(expertEmail, inquiry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }


        callExpertBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_CALL);
                dialIntent.setData(Uri.parse("tel:" + experPhoneNo));
                startActivity(dialIntent);
            }
        });

    }

    private void sendInquiry(String email, String inquiry) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("inquiry", inquiry);
        editor.apply();

        new Thread(() -> {
            try {
                sendEmailWithInquiry(email, inquiry);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Inquiry has been sent to the expert.", Toast.LENGTH_LONG).show();

                    Navigation.findNavController(getView()).navigate(R.id.DestHealthExpertsList);
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Error sending inquiry: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ContactExpert", "Error sending inquiry: ", e);
                });
            }
        }).start();
    }

    private void sendEmailWithInquiry(String email, String inquiry) {
        new Thread(() -> {
            try {
                final String username = ""; // Replace with your email you want to send from
                final String password = ""; // Replace with your email password

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
                String body = buildBody(inquiry);
                String emailSender = getSender(new currentLoginData());
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("herladycompanion@gmail.com")); // Replace with your email
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Lady Companion inquiry from " + emailSender);
                message.setText(body);

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getSender(currentLoginData currentLoginData) {
        String sender;
        if (currentLoginData.userType == 0){
            sender = currentLoginData.getUser().getFullName();
        }
        else sender = currentLoginData.getAgency().getCompanyName();
        return sender;
    }

    public String buildBody(String inquiry){
        String body = inquiry;
        if (new currentLoginData().userType == 0){
            UserData user = currentLoginData.getUser();
            body = inquiry
                    + "\n\nInformation:"
                    +"\nName: " + user.getFullName()
                    +"\nAge: " + user.getAge()
                    +"\nContact Email: " + user.getEmail()
                    +"\nContact No: " + user.getPhone();
        }
        else {
            AgencyData user = currentLoginData.getAgency();
            body = inquiry
                    + "\n\nInformation:"
                    +"\nName: " + user.getCompanyName()
                    +"\ncompany Type: " + user.getCompanyType()
                    +"\nContact Email: " + user.getEmail()
                    +"\nContact No: " + user.getPhone();
        }
        return body;
    }

}
