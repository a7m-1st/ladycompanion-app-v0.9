package com.example.MouawiaPart;

import static com.example.MainActivity.AndroidUtil.setProfilePic;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DataBase.data.BlogsData;
import com.example.DataBase.data.PostData;
import com.example.DataBase.data.currentLoginData;
import com.example.MainActivity.recyclerViews.blogsRecyclerAdapter;
import com.example.MainActivity.recyclerViews.postRecyclerAdapter;
import com.example.ladyapp.R;

import java.util.ArrayList;

public class fragment_profilepage extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profilepage, container, false);
    }

    ArrayList<PostData> profilePost;
    ArrayList<BlogsData> profileBlog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int sumOfLikes = 0;

        //Sieve the posts to same userid
        profilePost = new ArrayList<>();
        profileBlog = new ArrayList<>();
        ImageView profilePic = view.findViewById(R.id.profilePicture);


        if(currentLoginData.userType == 0) {
            for(PostData x: fragment_home.posts) {
                if(x.getUserId() == currentLoginData.getUser().getUserId()) {
                    profilePost.add(x);
                    sumOfLikes += x.getLikes();
                }
            }

            setProfilePic(getContext(), Uri.parse(currentLoginData.user.getProfilePic()), profilePic);


            TextView username = view.findViewById(R.id.username_profilepage);
            username.setText(currentLoginData.user.getFullName());

            TextView likes = view.findViewById(R.id.totalLikes_profilepage);
            likes.setText(Integer.toString(sumOfLikes));

            TextView dateJoin = view.findViewById(R.id.dateCreate_profilepage);
            dateJoin.setText(currentLoginData.user.getDateJoin());

            initializeRecyclerView(view, profilePost);
        } else {
            //AGENT USER
            for(BlogsData x: fragment_home.blogs) {
                if(x.getAgencyid() == currentLoginData.getAgency().getAgentUserId()) {
                    profileBlog.add(x);
                    sumOfLikes += x.getLikes();
                }
            }
            //set profile pic
            setProfilePic(getContext(), Uri.parse(currentLoginData.agency.getProfilePhoto()), profilePic);


            TextView username = view.findViewById(R.id.username_profilepage);
            username.setText(currentLoginData.getAgency().getCompanyName().toString());

            TextView likes = view.findViewById(R.id.totalLikes_profilepage);
            likes.setText(Integer.toString(sumOfLikes));

            TextView dateJoin = view.findViewById(R.id.dateCreate_profilepage);
            dateJoin.setText(currentLoginData.agency.getDateJoin());

            initializeRecyclerView2(view, profileBlog);
        }

        //Button
        Button editInfo = view.findViewById(R.id.editButton_profilepage);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                Navigation.findNavController(view).popBackStack();
                Navigation.findNavController(view).navigate(R.id.DestEditinfo);
            }
        });

        //TODO: Calculate total likes from posts table of user
//        TextView totalLikes = view.findViewById(R.id.likesCount);
//        totalLikes.setText("5233");

//        TextView userName = view.findViewById(R.id.username);
//        if ((new currentLoginData().userType == 0)) {
//            userName.setText(new currentLoginData().user.getDateJoin());
//        } else {
//            userName.setText(new currentLoginData().agency.getDateJoin());
//        }
//
//        TextView dateJoin = view.findViewById(R.id.dateCreate_profilepage);
//        dateJoin.setText(new currentLoginData().user.getDateJoin());
    }

    public static postRecyclerAdapter adapter;
    public static blogsRecyclerAdapter adapter2;
    public void initializeRecyclerView(View view, ArrayList<PostData> profilePost) {
        //Set into recycler view
        RecyclerView recyclerView = view.findViewById(R.id.profileRecyclerView);
        adapter = new postRecyclerAdapter(getActivity(), profilePost, fragment_home.users, "profilePage");
        recyclerView.setAdapter(adapter); //use Previous adapter |Assoc Post & User
    }

    public void initializeRecyclerView2(View view, ArrayList<BlogsData> profilePost) {
        //Set into recycler view
        RecyclerView recyclerView = view.findViewById(R.id.profileRecyclerView);
        adapter2 = new blogsRecyclerAdapter(getActivity(), profilePost, fragment_home.agency);
        recyclerView.setAdapter(adapter2); //use Previous adapter |Assoc Post & User
    }

}