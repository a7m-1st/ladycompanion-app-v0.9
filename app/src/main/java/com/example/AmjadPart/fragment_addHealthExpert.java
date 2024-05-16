package com.example.AmjadPart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import java.sql.SQLException;

public class fragment_addHealthExpert extends Fragment {

    private EditText expertNameET, expertAgeET, expertPhoneNoET, expertEmailET, expertPositionET, expertBioET;
    private Button addExpertBTN;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_health_expert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initiate EditTexts
        expertNameET = view.findViewById(R.id.expertName);
        expertAgeET = view.findViewById(R.id.expertAge);
        expertPhoneNoET = view.findViewById(R.id.expertPhoneNo);
        expertEmailET = view.findViewById(R.id.expertEmail);
        expertPositionET = view.findViewById(R.id.expertPosition);
        expertBioET = view.findViewById(R.id.expertBio);

        addExpertBTN = view.findViewById(R.id.addHealthExpert);
        addExpertBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpert(v);
            }
        });
    }

    private void addExpert(View view){
        String expertName = expertNameET.getText().toString().trim();
        int expertAge = Integer.parseInt(expertAgeET.getText().toString());
        String expertPhoneNo = expertPhoneNoET.getText().toString().trim();
        String expertEmail = expertEmailET.getText().toString().trim();
        String expertPosition = expertPositionET.getText().toString().trim();
        String expertBio = expertBioET.getText().toString().trim();

        if (expertName.isEmpty() || expertPhoneNo.isEmpty() || expertEmail.isEmpty() || expertPosition.isEmpty()){
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            sqlConnector connect = new sqlConnector();
            connect.addHealthExpert(expertName, expertAge, expertPosition, expertBio, expertEmail, expertPhoneNo);
            connect.closeConnection();

            Toast.makeText(getActivity(), "New Health Expert Added", Toast.LENGTH_LONG).show();

            Navigation.findNavController(view).navigate(R.id.DestHealthExpertsList);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}