package com.example.MouawiaPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.DataBase.data.BlogsData;
import com.example.DataBase.data.PostData;
import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class fragment_modifypost extends Fragment {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private LinearLayout linearLayout;
    private Button postButton;
    private PostData data;
    private BlogsData data2;
    public static int position;
    public static boolean actionType;
    private EditText editTextTitle, editTextContent, editTextLink;
    private TextView linkText, pageTitle, qTwoTitleTxt, qOneTitleTxt;
    public static int posttype;

    private Switch pollSwitch;
    EditText qTwoEditTxt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modifypost, container, false);

        // Retrieve post data
        if (actionType){
            if(posttype == 0)
                this.data = fragment_home.posts.get(position);
            else
                this.data2 = fragment_home.blogs.get(position);
        }
//        Bundle bundle = getArguments();
//        if (bundle != null){
//            actionType = bundle.getInt("Action Type");

//        }
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Edit Texts
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextContent = view.findViewById(R.id.editTextContent);
        pageTitle = view.findViewById(R.id.PageTitle);
        //Link
        editTextLink = view.findViewById(R.id.editTextLink);
        linkText = view.findViewById(R.id.linkText);

        //Poll
        pollSwitch = view.findViewById(R.id.pollSwitch);
        qTwoEditTxt = view.findViewById(R.id.editTextTitle2);
        qTwoTitleTxt = view.findViewById(R.id.textView7);
        qOneTitleTxt = view.findViewById(R.id.textView3);

        if(currentLoginData.userType == 1) { //set visible if editing or adding
            linkText.setVisibility(View.VISIBLE);
            editTextLink.setVisibility(View.VISIBLE);
            pollSwitch.setVisibility(View.VISIBLE);
        }
        pollSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            if(isChecked) {
                linkText.setVisibility(View.GONE);
                editTextLink.setVisibility(View.GONE);
                editTextContent.setVisibility(View.GONE);
                qTwoEditTxt.setVisibility(View.VISIBLE);
                qOneTitleTxt.setText("Question 1");
                qTwoTitleTxt.setText("Question 2");
                editTextContent.setText("$poll");
            } else {
                editTextContent.setVisibility(View.VISIBLE);
                qTwoEditTxt.setVisibility(View.GONE);
                qOneTitleTxt.setText("Title");
                qTwoTitleTxt.setText("Content");
                editTextContent.setText("");
            }
        });

        if(actionType){ //if editing
            pageTitle.setText("Editing Post");
            if(posttype == 0) {
                editTextTitle.setText(data.getPostTitle().toString());
                editTextContent.setText(data.getPostBody().toString());
            } else {
                try {
                if(data2.getLink() != null)
                    editTextLink.setText(data2.getLink().toString());
                } catch (Exception e) {e.printStackTrace();}

                editTextTitle.setText(data2.getBlogtitle().toString());
                editTextContent.setText(data2.getBlogbody().toString());
            }
        }

        postButton = view.findViewById(R.id.buttonSave);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionType){
                    if(posttype == 0)
                        editPostData(view, data);
                    else
                        editBlogData(view, data2);
                } else {
                    addNewPost(view);
                }
//                Toast.makeText(getActivity(), "Button Clicked", Toast.LENGTH_SHORT).show();
//                addNewPost(view);

                //Reset Action type once ended
                actionType = false;
            }
        });
    }

    private void addNewPost(View view){
        String postTitle = editTextTitle.getText().toString();
        String postBody = editTextContent.getText().toString();
        String link = editTextLink.getText().toString();
        int userID = currentLoginData.getId();
        int userType = currentLoginData.getUserType();
        int likes = 0;
        String dateofpost = java.time.LocalDate.now().toString();


        if (postTitle.isEmpty() || postBody.isEmpty()){
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            sqlConnector connect = new sqlConnector();
            if(userType == 0)
                 connect.createNewPost(postTitle, postBody, likes, dateofpost, userID, userType);
            else { //blogs
                if(!pollSwitch.isChecked())
                    connect.createNewPost(postTitle, postBody, likes, dateofpost, userID, userType, link);
                else if(pollSwitch.isChecked()) {
                    //Send data to database
                    connect.createNewPost(postTitle, qTwoEditTxt.getText().toString(), 0, dateofpost, userID, userType, "$poll");
                }
            }

            connect.closeConnection();

            // this.clearFormFields();
            Toast.makeText(getActivity(), "Post Added Successfully", Toast.LENGTH_LONG).show();

            if(userType == 0)
            Navigation.findNavController(view).navigate(R.id.ToDestHome);
            else Navigation.findNavController(view).navigate(R.id.DestBlogs);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private void editPostData(View view, PostData postData){
        String postTitle = editTextTitle.getText().toString();
        String postBody = editTextContent.getText().toString();
        String dateofpost = java.time.LocalDate.now().toString();

        if (postTitle.isEmpty() || postBody.isEmpty()){
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            sqlConnector connect = new sqlConnector();
            connect.editPost(position, postTitle, postBody, dateofpost, "");
            connect.closeConnection();

            postData.setPostBody(postBody);
            postData.setPostTitle(postTitle);
            postData.setDateOfPost(dateofpost);

            // this.clearFormFields();
            Toast.makeText(getActivity(), "Post Edited Successfully", Toast.LENGTH_LONG).show();


            Navigation.findNavController(view).navigate(R.id.ToDestHome);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void editBlogData(View view, BlogsData postData){
        String postTitle = editTextTitle.getText().toString();
        String postBody = editTextContent.getText().toString();
        String dateofpost = java.time.LocalDate.now().toString();
        String link = editTextLink.getText().toString();

        actionType = false;
        if (postTitle.isEmpty() || postBody.isEmpty()){
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            sqlConnector connect = new sqlConnector();
            connect.editPost(position, postTitle, postBody, dateofpost, link);
            connect.closeConnection();

            //Update in local array
            postData.setBlogbody(postBody);
            postData.setBlogtitle(postTitle);
            postData.setDateofpost(java.time.LocalDate.now().toString());
            postData.setLink(link);

            // this.clearFormFields();
            Toast.makeText(getActivity(), "Post Edited Successfully", Toast.LENGTH_LONG).show();


            Navigation.findNavController(view).navigate(R.id.DestBlogs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void receivePostData(PostData postData) {
        // Handle the data based on the button identifier

    }

    public void setActionType(boolean actionType) {
        if (actionType == true){
            this.actionType = true;
        } else this.actionType = false;
    }

    public void setPosition(int position, int posttype){
        this.position = position;
        this.posttype = posttype;
    }
}