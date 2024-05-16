package com.example.AmjadPart;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ladyapp.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class fragment_quickHelpSituations extends Fragment {
    ImageButton pregnancyBTN, menstruationBTN, sexualAssaultBTN;
    static int PERMISSION_CODE = 100;

    FloatingActionButton ambulanceBTN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quick_help_situations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pregnancyBTN = view.findViewById(R.id.pregnancySection);
        menstruationBTN = view.findViewById(R.id.menstruationSection);
        sexualAssaultBTN = view.findViewById(R.id.assaultSection);
        ambulanceBTN = view.findViewById(R.id.ambulance);

        pregnancyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestPregnancy);
            }
        });

        menstruationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestMenstruation);
            }
        });

        sexualAssaultBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestAssault);
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }

        ambulanceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_CALL);
                dialIntent.setData(Uri.parse("tel:" + 111));
                startActivity(dialIntent);
            }
        });
    }
}