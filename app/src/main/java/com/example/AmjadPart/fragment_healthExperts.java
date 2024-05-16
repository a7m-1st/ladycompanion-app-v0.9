package com.example.AmjadPart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.DataBase.data.HealthExpertData;
import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MainActivity.recyclerViews.HealthExpertsAdapter;
import com.example.MouawiaPart.fragment_home;
import com.example.ladyapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.SQLException;
import java.util.ArrayList;

public class fragment_healthExperts extends Fragment {
    public static HealthExpertsAdapter adapter;
    public static ArrayList<HealthExpertData> healthExpert;
    private FloatingActionButton addExpertBTN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_health_expert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initiateExperts();

        RecyclerView recyclerView = view.findViewById(R.id.healthExpertsRecycler);
        adapter = new HealthExpertsAdapter(getActivity(), healthExpert);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        addExpertBTN = view.findViewById(R.id.addExpertBTN);
        if(currentLoginData.getUserType() == 1) {
            addExpertBTN.setVisibility(View.VISIBLE);
            addExpertBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.DestAddHealthExpert);
                }
            });
        }

    }

    private void initiateExperts() {
        sqlConnector connection  = null;
        try {
            connection = new sqlConnector();
            healthExpert = connection.retrieveExpertInformation();
            connection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}