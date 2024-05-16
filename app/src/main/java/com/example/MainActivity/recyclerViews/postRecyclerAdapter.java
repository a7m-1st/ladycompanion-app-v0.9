package com.example.MainActivity.recyclerViews;

import static com.example.MainActivity.AndroidUtil.setProfilePic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DataBase.data.PostData;
import com.example.DataBase.data.UserData;
import com.example.DataBase.data.currentLoginData;
import com.example.ladyapp.R;

import java.util.ArrayList;

public class postRecyclerAdapter extends RecyclerView.Adapter<postRecyclerAdapter.MyViewHolder> {
    private static Context context;
    public static ArrayList<PostData> posts;
    private ArrayList<UserData> users;
    private static ArrayList<Integer> postsLikedId;
    private static String pageType;

    public postRecyclerAdapter(Context context, ArrayList<PostData> posts, ArrayList<UserData> users, String pageType) {
        this.context = context;
        this.posts = posts;
        this.users = users;
        this.pageType = pageType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int userId = posts.get(position).getUserId();

        for (int i = 0; i <= users.size() - 1; i++) {
            if (users.get(i).getUserId() == userId) {
                // Set user-related data
                setProfilePic(context, Uri.parse(users.get(i).getProfilePic()), holder.profilepic);
                holder.options.setImageResource(R.drawable.ic_more_vert);
                holder.username.setText(users.get(i).getFullName());

                // Set post-related data
                holder.postbody.setText(posts.get(position).getPostBody());
                holder.likes.setText(Integer.toString(posts.get(position).getLikes()));

                //Date calculation
                String dateOfPost = posts.get(position).getDateOfPost(); //2023-12-15
                String currentDate = java.time.LocalDate.now().toString();
                long dateDifference = recyclerFunctions.findDifference(dateOfPost, currentDate);
                String status = dateOfPost;
                if(dateDifference == 0) {
                    status = "Today  ";
                } else if(dateDifference > 0) {
                    status = dateDifference + " days ago";
                }
                holder.time.setText(status);

                // Set like icon based on whether the post is liked
                if (postsLikedId.contains(posts.get(position).getPostId()) && !postsLikedId.isEmpty()) {
                    holder.likeIcon.setImageResource(R.drawable.ic_heart);
                    holder.likeIcon.setTag("like");
                } else {
                    holder.likeIcon.setImageResource(R.drawable.unlike_icon);
                    holder.likeIcon.setTag("unlike");
                }

                // Share functionality
                holder.shareIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharePost(position);
                    }
                });

                // ... (your existing code)
            }
        }
    }

    private void sharePost(int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this post: " + posts.get(position).getPostBody());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, postbody, likes, time;
        ImageView options, profilepic, likeIcon, shareIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize your views
            username = itemView.findViewById(R.id.username);
            postbody = itemView.findViewById(R.id.postBody);
            likes = itemView.findViewById(R.id.likesCount);
            time = itemView.findViewById(R.id.time);
            shareIcon = itemView.findViewById(R.id.shareIcon);
            profilepic = itemView.findViewById(R.id.profilepic);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            options = itemView.findViewById(R.id.options);

            // Get User-PostLikes Table | Input: current login Id
            int userId = currentLoginData.getId();
            postsLikedId = recyclerFunctions.getPostLikes(userId, 0);

            // Handle options click
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    Toast.makeText(context, "Current Position is " + position, Toast.LENGTH_SHORT).show();
                    if (position != RecyclerView.NO_POSITION && (userId == posts.get(position).getUserId())) {
                        showOptionsDialog(position, itemView);
                    }
                }
            });

            // Handle like icon click
            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentLikes = Integer.parseInt(likes.getText().toString());
                    int postId = 0;

                    // See from which view it is
                    postId = posts.get(getAbsoluteAdapterPosition()).getPostId(); // if from the posts page

                    String tagUnlike = "unlike", tagLike = "like";
                    if (likeIcon.getTag().equals("unlike")) {
                        //TODO: Add likes in Database
                        likeIcon.setImageResource(R.drawable.ic_heart);
                        likeIcon.setTag(tagLike);

                        // Likes Counter
                        likes.setText(String.valueOf(currentLikes + 1));
                        // line 1 of postliked
                        // get by position = postid
                        // insert that
                        recyclerFunctions.updateDatabase(postId, currentLikes + 1, "likes", tagUnlike, "post");

                    } else {
                        //TODO: Update Database
                        likeIcon.setImageResource(R.drawable.unlike_icon);
                        likeIcon.setTag("unlike");

                        likes.setText(String.valueOf(currentLikes - 1));
                        recyclerFunctions.updateDatabase(postId, currentLikes - 1, "likes", tagLike, "post");
                    }
                }
            });
        }

        private void showOptionsDialog(int position, View itemView) {
            PopupMenu popupMenu = new PopupMenu(context, options);
            popupMenu.getMenuInflater().inflate(R.menu.menu_post_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit) {
                        // ++ This handle the edit option and edit method need a logic to be implemented +_Cool Cool Cool+
                        //TODO this should return id of post / blog = -1 bcz assume adapterpos
                        recyclerFunctions.editPost(posts.get(position).getPostId()-1, 0, itemView);
                        return true;
                    } else if (item.getItemId() == R.id.action_delete) {
                        //++ This handle the delete option +_Cool Cool Cool+
                        Toast.makeText(context, "Delete clicked", Toast.LENGTH_SHORT).show();
                        recyclerFunctions.deletePost(position, context, 0);
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            popupMenu.show();
        }
    }
}
