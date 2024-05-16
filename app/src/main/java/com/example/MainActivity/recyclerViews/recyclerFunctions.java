package com.example.MainActivity.recyclerViews;

import static com.example.MouawiaPart.fragment_home.posts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.example.AhmedPart.fragment_blogs;
import com.example.DataBase.data.BlogsData;
import com.example.DataBase.data.PostData;
import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MouawiaPart.fragment_home;
import com.example.MouawiaPart.fragment_modifypost;
import com.example.ladyapp.R;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class recyclerFunctions {
    //Fix Infinite Likes glitch code
    //Buffer for likes when creating page
    public static ArrayList<Integer> getPostLikes(int userId, int postType) {
        ArrayList<Integer> postsLikedId;
        try {
            sqlConnector connect = new sqlConnector();
            postsLikedId = connect.getPostLikes(currentLoginData.getId(), postType);
            connect.closeConnection();
            return postsLikedId;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //used to update likes
    public static boolean updateDatabase(int postId, int newValue, String attr, String tag, String table) {
        PostData post = null; BlogsData blog = null;
        if(table.equals("post"))
            post = fragment_home.posts.get(postId-1); //current post
        else if (table.equals("blog")) {
            blog = fragment_home.blogs.get(postId-1); //current post
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //NOT RECOMMENDED IN PRODUCTION CODE
        try {
            sqlConnector connection  = new sqlConnector();

            //Update Post database
            connection.updateSpecificColumnPost(postId, newValue, attr, table);

            //Insert to X_likes database
            //TODO: DON'T FORGET TO CHANGE THE POSTTYPE WITH blogs
            if(tag.equals("unlike")) {
                //postId = adapter+1 = starting from 0 | posttype 0 for normal
                //Now this will differentiate between agency & normal user
                if(table.equals("post")) {
                    connection.addPostLike(currentLoginData.getId(), postId, 0);
                    post.setLikes(post.getLikes() + 1); //Position = postid - 1
                } else {
                    connection.addPostLike(currentLoginData.getId(), postId, 1);
                    blog.setLikes(blog.getLikes() + 1);
                }

                Log.d("Like", "like added");
            } else {
                if(table.equals("post")) {
                    connection.removePostLike(currentLoginData.getId(), postId, 0);
                    post.setLikes(post.getLikes() - 1); //Position = postid - 1
                }
                else {
                    connection.removePostLike(currentLoginData.getId(), postId, 1);
                    blog.setLikes(blog.getLikes() - 1);
                }

                Log.d("Like", "like removed");
            }

            connection.closeConnection();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static void editPost(int position, int posttype, View itemView) {
        Toast.makeText(itemView.getContext(), "Post id is " + (position), Toast.LENGTH_SHORT).show();
        //TODO: PASS INFORMATION TO NEXT FRAGMENT AMJAD
        sendPostData(position, posttype);
        fragment_modifypost.actionType = true;
        Navigation.findNavController(itemView).navigate(R.id.DestModifypost);

    }
    private static void sendPostData(int position, int posttype){
        fragment_modifypost.posttype = posttype;
        fragment_modifypost.position = position;
    }

    static void deletePost(final int position, Context context, int posttype) {
        //TODO: IMPLIMENT DELETE POST FUNCTION
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this post?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("dialogbox", "delete clicked");
                try {
                    sqlConnector con = new sqlConnector();
                    //TODO: UNHIGHLIGHT THE CODE ONCE CONSTRAINT DONE

                    //delete post in UI then update database
                    con.deletePost(position+1, posttype);
                    //Referesh Adapter
                    if(posttype == 0) {
                        fragment_home.adapter.notifyItemRemoved(position);
                        Log.d("Deleted", "postId = " + posts.remove(position).getPostId());
                    } else if(posttype == 1) {
                        fragment_blogs.adapter.notifyItemRemoved(position);
                        fragment_home.blogs.remove(position).getBlogid();
                    }

                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                    //Ids automatically displaced
                    con.closeConnection();
                } catch (SQLException e) {
                    Log.d("Delete", "Error from Database query");
                    throw new RuntimeException(e);
                }
            }
        });

        //Cancel Button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }


    //Calcules the date difference b/n post
    public static long findDifference(String start_date, String end_date) {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calculate time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calculate time difference in
            // seconds, minutes, hours, years,
            // and days
//            long difference_In_Seconds
//                    = (difference_In_Time
//                    / 1000)
//                    % 60;
//
//            long difference_In_Minutes
//                    = (difference_In_Time
//                    / (1000 * 60))
//                    % 60;
//
//            long difference_In_Hours
//                    = (difference_In_Time
//                    / (1000 * 60 * 60))
//                    % 24;
//
//            long difference_In_Years
//                    = (difference_In_Time
//                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            return difference_In_Days;
        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
