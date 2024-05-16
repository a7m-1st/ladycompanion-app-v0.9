package com.example.MouawiaPart;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DataBase.data.AgencyData;
import com.example.DataBase.data.BlogsData;
import com.example.DataBase.data.Polls;
import com.example.DataBase.data.PostData;
import com.example.DataBase.data.UserData;
import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MainActivity.recyclerViews.postRecyclerAdapter;
import com.example.ladyapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class fragment_home extends Fragment {
    //Stand alone initailization inorder to use in another fragment
    public static ArrayList<PostData> posts = new ArrayList<>();
    public static ArrayList<UserData> users = new ArrayList<>();
    public static ArrayList<BlogsData> blogs = new ArrayList<>();
    public static ArrayList<AgencyData> agency;
    public static ArrayList<Polls> polls = new ArrayList<>();

    private FloatingActionButton addButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }


    public static postRecyclerAdapter adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpPostsAndUsers();

        RecyclerView recyclerView = view.findViewById(R.id.PostsRecycler);
        adapter = new postRecyclerAdapter(getActivity(), posts, users, "homePage");
        recyclerView.setAdapter(adapter);

        addButton = view.findViewById(R.id.add_BTN);
        if(currentLoginData.getUserType() == 0) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.DestModifypost);
                }
            });
        }
    }

    //Data from Database
    public void setUpPostsAndUsers() {
        //This makes sure the database works before the fragment generation
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //NOT RECOMMENDED IN PRODUCTION CODE
        try {
            sqlConnector connection  = new sqlConnector();
            posts = connection.retrievePostInformation();
            connection.closeConnection();

            connection  = new sqlConnector();
            fragment_home.users = connection.retrieveUserInformation();
            connection.closeConnection();

            connection  = new sqlConnector();
            fragment_home.blogs = connection.retrieveBlogInformation();
            connection.closeConnection();

            connection  = new sqlConnector();
            fragment_home.agency = connection.retrieveAgencyInformation();
            connection.closeConnection();

            connection  = new sqlConnector();
            fragment_home.polls = connection.retrievePollsInformation();
            connection.closeConnection();

//            connection  = new sqlConnector();
//            fragment_home.healthExpert = connection.retrieveExpertInformation();
//            connection.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}