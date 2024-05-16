package com.example.AmjadPart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ladyapp.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class fragment_pregnancySituations extends Fragment {
    private TabLayout pregnancyTL;
    private ViewPager pregnancyVP;
    private ExtendedFloatingActionButton visitNHSBTN;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pregnancy_situations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pregnancyTL = view.findViewById(R.id.pregnancyTabLayout);
        pregnancyVP = view.findViewById(R.id.pregnancyPager);
        visitNHSBTN = view.findViewById(R.id.pregnancyVisitNHS);

        pregnancyTL.setupWithViewPager(pregnancyVP);

        viewPagerAdapter pregnancyAdapter = new viewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pregnancyAdapter.addFragment(new fragment_pregnancyCommonSymptoms(), "Common Symptoms");
        pregnancyAdapter.addFragment(new fragment_pregnancyComplications(), "Complications");
        pregnancyAdapter.addFragment(new fragment_pregnancyExistingHealthConditions(), "Existing Health Conditions");
        pregnancyVP.setAdapter(pregnancyAdapter);

        visitNHSBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nhs.uk/pregnancy/"));
                startActivity(intent);
            }
        });
    }
}