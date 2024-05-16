package com.example.MainActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;

import com.example.ladyapp.R;

public class AboutUs extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);


        return view;
    }


    public void openAboutUsFragment() {
        // Navigate to the "ToDestAboutUs" destination using the NavController
        NavController navController = Navigation.findNavController(requireActivity(), R.id.NHFMain);
        navController.navigate(R.id.ToDestAboutUs);
    }
}
