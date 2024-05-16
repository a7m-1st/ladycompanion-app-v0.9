package com.example.AhmedPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ladyapp.R;

public class fragment_webScraping extends Fragment {
    EditText webLink;
    Button search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_scraping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webLink = view.findViewById(R.id.webLink);
        webLink.setText("https://www.unwomen.org/en/news-stories");
        search = view.findViewById(R.id.searchBtn);


        search.setOnClickListener(v -> {
            startAction(webLink.getText().toString(), view);
        });
    }

    public void startAction(String link, View view) {
        new JSoup().startScraping(link, view, getActivity()); //return arraylist
    }
}