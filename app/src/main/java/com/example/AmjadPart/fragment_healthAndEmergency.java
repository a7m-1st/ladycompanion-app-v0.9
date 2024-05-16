package com.example.AmjadPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.NicoPart.SosDetailsActivity;
import com.example.ladyapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class fragment_healthAndEmergency extends Fragment {

    private static final int YOUR_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_and_emergency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton chatBotBtn = view.findViewById(R.id.chatBotBtn);
        ImageButton quickHelpBtn = view.findViewById(R.id.quickHelpBtn);
        ImageButton healthExpertsBtn = view.findViewById(R.id.healthExpertsBtn);
        FloatingActionButton modifyEmergencyBtn = view.findViewById(R.id.modifyEmergencyBtn);

        chatBotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.DestChatBot);
            }
        });

        quickHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.DestQuickHelp);
            }
        });

        healthExpertsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.DestHealthExpertsList);
            }
        });

        modifyEmergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), SosDetailsActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, YOUR_REQUEST_CODE);
                }
            }
        });

    }
}