package com.example.HarIPart;

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
import android.widget.Button;
import android.widget.TextView;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import java.sql.SQLException;
import java.util.ArrayList;


public class agencyReviewUnsafeZoneRequests extends Fragment {

    public static RequestsRecyclerView adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agency_review_unsafe_zone_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Obtain request info from database
        ArrayList<unsafeRequestsFromDB> requestslist = new ArrayList<>();
        try {
            sqlConnector connect = new sqlConnector();
            requestslist = connect.getUnsafeZoneRequests();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (requestslist.size() == 0) {
            RecyclerView RVAdditionRequests = view.findViewById(R.id.RVAdditionRequests);
            RVAdditionRequests.setVisibility(View.GONE);
            TextView textView13 = view.findViewById(R.id.textView13);
            textView13.setVisibility(View.VISIBLE);
        }
        else {
            RecyclerView RVAdditionRequests = view.findViewById(R.id.RVAdditionRequests);
            RVAdditionRequests.setVisibility(View.VISIBLE);
            adapter = new RequestsRecyclerView(requireContext(), requestslist);
            RVAdditionRequests.setAdapter(adapter);
            RVAdditionRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        Button goBackToMapView = view.findViewById(R.id.backToAgencyMapView);
        goBackToMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_agencyReviewUnsafeZoneRequests_to_agencyUserUnsafeMapView);
            }
        });
    }
}