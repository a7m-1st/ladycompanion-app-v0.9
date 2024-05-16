package com.example.AhmedPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DataBase.data.currentLoginData;
import com.example.MainActivity.recyclerViews.blogsRecyclerAdapter;
import com.example.MouawiaPart.fragment_home;
import com.example.ladyapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class fragment_blogs extends Fragment {
    private FloatingActionButton addButton;
    public static blogsRecyclerAdapter adapter;
        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_blogs, container, false);
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.BlogsRecycler);
         adapter = new blogsRecyclerAdapter(getActivity(), fragment_home.blogs, fragment_home.agency);
        recyclerView.setAdapter(adapter); //use Previous adapter |Assoc Post & User

        addButton = view.findViewById(R.id.add_BTN);
        if(currentLoginData.getUserType() == 1) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: Add post by Agency
                    Navigation.findNavController(view).navigate(R.id.DestModifypost);
                }
            });
        }
    }
}