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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class fragment_menstruation extends Fragment {
    private TabLayout menstruationTL;
    private ViewPager menstruationVP;
    private ExtendedFloatingActionButton visitNHSBTN;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menstruation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menstruationTL = view.findViewById(R.id.menstruationTabLayout);
        menstruationVP = view.findViewById(R.id.menstruationPager);
        visitNHSBTN = view.findViewById(R.id.menstruationVisitNHS);

        menstruationTL.setupWithViewPager(menstruationVP);

        viewPagerAdapter menstruationAdapter = new viewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        menstruationAdapter.addFragment(new fragment_menstruationOverview(), "Overview");
        menstruationAdapter.addFragment(new fragment_menstruationStartingPeriod(), "Starting Period");
        menstruationAdapter.addFragment(new fragment_menstruationPeriodProblems(), "Period Problems");
        menstruationVP.setAdapter(menstruationAdapter);

        visitNHSBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nhs.uk/conditions/periods/"));
                startActivity(intent);
            }
        });
    }
}