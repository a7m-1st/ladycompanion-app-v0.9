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

public class fragment_assault extends Fragment {

    private TabLayout assaultTL;
    private ViewPager assaultVP;
    private ExtendedFloatingActionButton visitNHSBTN;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assault, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assaultTL = view.findViewById(R.id.assaultTabLayout);
        assaultVP = view.findViewById(R.id.assaultPager);
        visitNHSBTN = view.findViewById(R.id.assaultVisitNHS);

        assaultTL.setupWithViewPager(assaultVP);

        viewPagerAdapter assaultAdapter = new viewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        assaultAdapter.addFragment(new fragment_assaultOverview(), "Overview");
        assaultVP.setAdapter(assaultAdapter);

        visitNHSBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nhs.uk/live-well/sexual-health/help-after-rape-and-sexual-assault/"));
                startActivity(intent);
            }
        });
    }
}