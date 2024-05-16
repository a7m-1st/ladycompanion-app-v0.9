package com.example.MainActivity.recyclerViews;

import static com.example.MainActivity.AndroidUtil.setProfilePic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DataBase.data.AgencyData;
import com.example.DataBase.data.BlogsData;
import com.example.DataBase.data.Polls;
import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MouawiaPart.fragment_home;
import com.example.ladyapp.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class blogsRecyclerAdapter extends RecyclerView.Adapter<blogsRecyclerAdapter.MyViewHolder> {
    static Context context;
    static ArrayList<BlogsData> posts;
    ArrayList<AgencyData> users;
    static ArrayList<Integer> postsLikedId;
    ArrayList<String> questions;
    public blogsRecyclerAdapter(Context context, ArrayList<BlogsData> posts, ArrayList<AgencyData> users) {
        this.context = context;
        this.posts = posts;
        this.users = users;
        questions = new ArrayList<>();
    }

    //Give the look & Inflating
    @NonNull
    @Override
    public blogsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.blogs_layout, parent, false);

        return new blogsRecyclerAdapter.MyViewHolder(view);
    }

    //Assign Value when in-stream | parameter of the variable template holder class
    @Override
    public void onBindViewHolder(@NonNull blogsRecyclerAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //User ID of current Data Row from Posts Table
        int userid = posts.get(position).getAgencyid();

        //Search for Matching User ID in Table to row of Post
        //In future; store alone in arraylist | User - Post relation
        for (int i = 0; i <= users.size()-1; i++)
            if (users.get(i).getAgentUserId() == userid) {
                setProfilePic(context, Uri.parse(users.get(i).getProfilePhoto()), holder.profilepic);

                { //Polls
                    String link = "";
                    try {
                        link = posts.get(position).getLink();
                    } catch (Exception e) {
                        e.printStackTrace();}

                    if(link == null || !link.equals("$poll")) {
                        holder.questionOne.setVisibility(View.GONE);
                        holder.resultOne.setVisibility(View.GONE);
                        holder.questionTwo.setVisibility(View.GONE);
                        holder.resultTwo.setVisibility(View.GONE);

                        holder.postbody.setVisibility(View.VISIBLE);
                        holder.posttitle.setVisibility(View.VISIBLE);
                        holder.posttitle.setText(posts.get(position).getBlogtitle());
                        holder.postbody.setText(posts.get(position).getBlogbody());
                    } else if(link.equals("$poll")) { //if its a poll
                        holder.questionOne.setVisibility(View.VISIBLE);
                        holder.resultOne.setVisibility(View.VISIBLE);
                        holder.questionTwo.setVisibility(View.VISIBLE);
                        holder.resultTwo.setVisibility(View.VISIBLE);

                        holder.postbody.setVisibility(View.GONE);
                        holder.posttitle.setVisibility(View.GONE);


                        int resultOne=0, resultTwo=0;
                        boolean flag = false;
                        for(int j=0; j<fragment_home.polls.size(); j++) {
                            Polls poll = fragment_home.polls.get(j);
                            if(poll.getQuestion().equals(posts.get(position).getBlogtitle()))
                                resultOne++;
                            if(poll.getQuestion().equals(posts.get(position).getBlogbody()))
                                resultTwo++;
                            if(poll.getUserId() == userid && poll.userType == currentLoginData.userType) {
                                flag = true;
                                questions.add(poll.getQuestion());
                            }
                            if(currentLoginData.userType == 0 && poll.getUserId() == currentLoginData.getUser().getUserId() && poll.userType == 0) {
                                questions.add(poll.getQuestion());
                                flag = true;
                            }
                        }
                        holder.questionOne.setText(posts.get(position).getBlogtitle());

                        holder.resultOne.setText(String.valueOf(resultOne));
                        holder.resultTwo.setText(String.valueOf(resultTwo));
                        holder.questionTwo.setText(posts.get(position).getBlogbody());


                        //Make it clickable
                        AtomicBoolean finalFlag = new AtomicBoolean(flag);
                        int finalResultOne = resultOne;
                        holder.questionOne.setOnClickListener( v -> {
                            if(questions.contains(posts.get(position).getBlogtitle()) || questions.contains(posts.get(position).getBlogbody())) {
                                Toast.makeText(context, "You already voted", Toast.LENGTH_SHORT).show();
                            } else {
                                finalFlag.set(true);
                                holder.resultOne.setText(String.valueOf( finalResultOne+1));
                                addVote(currentLoginData.userType, userid, posts.get(position).getBlogtitle());
                                questions.add(posts.get(position).getBlogtitle());
                            }
                        });

                        //Make it clickable
                        int finalResultTwo = resultTwo;
                        holder.questionTwo.setOnClickListener(v -> {
                            if(questions.contains(posts.get(position).getBlogbody()) || questions.contains(posts.get(position).getBlogtitle())) {
                                Toast.makeText(context, "You already voted", Toast.LENGTH_SHORT).show();
                            } else {
                                finalFlag.set(true);
                                holder.resultTwo.setText(String.valueOf(finalResultTwo +1));
                                addVote(currentLoginData.userType, userid, posts.get(position).getBlogbody());
                                questions.add(posts.get(position).getBlogbody());
                            }
                        });
                    }
                }


                holder.options.setImageResource(R.drawable.ic_more_vert);
                holder.username.setText(users.get(i).getCompanyName());
                holder.likes.setText(Integer.toString(posts.get(position).getLikes()));

                //Date calculation
                String dateOfPost = posts.get(position).getDateofpost(); //2023-12-15
                long dateDifference = recyclerFunctions.findDifference(dateOfPost, java.time.LocalDate.now().toString());
                String status = dateOfPost;
                if(dateDifference == 0) {
                    status = "Today  ";
                } else if(dateDifference > 0) {
                    status = dateDifference + " days ago ";
                }
                holder.time.setText(status);

                if (postsLikedId.contains(posts.get(position).getBlogid())) {
                    holder.likeIcon.setImageResource(R.drawable.ic_heart);
                    holder.likeIcon.setTag("like");
                }
                else {
                    holder.likeIcon.setImageResource(R.drawable.unlike_icon);
                    holder.likeIcon.setTag("unlike");
                }

                //Open to External Link to read more
                holder.openLink.setOnClickListener(v -> {
                    String link = "";
                    try {
                        link = posts.get(position).getLink();
                    } catch (Exception e) {e.printStackTrace();}

                    if(link != null && !link.isEmpty()) {
                        Toast.makeText(context, "Redirecting to Site", Toast.LENGTH_SHORT).show();
                        Uri website = Uri.parse(link);
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, website);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(context, "Invalid Link", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else Toast.makeText(context, "No Link for Blog", Toast.LENGTH_SHORT).show();

                });

                // Share functionality
                holder.shareIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharePost(position);
                    }
                });
            }
    }

    private void addVote(int userType,
    int userId, String question) {
        //sql
        new Thread(() -> {
            Looper.prepare();
            try {
                sqlConnector con = new sqlConnector();
                if(currentLoginData.userType == 1)
                    con.addPoll(userType, userId, question);
                else con.addPoll(userType, currentLoginData.getUser().getUserId(), question);
                Toast.makeText(context, "Poll Added", Toast.LENGTH_SHORT).show();
                //Ids automatically displaced
                con.closeConnection();
            } catch (SQLException e) {
                Log.d("Delete", "Error from Database query");
                throw new RuntimeException(e);
            }
            Looper.loop();
        }).start();
    }

    private void sharePost(int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this post: " + posts.get(position).getBlogbody());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    //overwrite View Holder |What data to update |Make variables
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, posttitle, postbody, likes, time;
        ImageView options, profilepic, likeIcon, shareIcon, openLink;
        TextView questionOne, questionTwo, resultOne, resultTwo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            postbody = itemView.findViewById(R.id.postBody);
            posttitle = itemView.findViewById(R.id.postTitle);
            likes = itemView.findViewById(R.id.likesCount);
            time = itemView.findViewById(R.id.time);
            shareIcon = itemView.findViewById(R.id.shareIcon);
            profilepic = itemView.findViewById(R.id.profilepic);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            options = itemView.findViewById(R.id.options);
            openLink = itemView.findViewById(R.id.openLinkIcon);
            //poll
            questionOne = itemView.findViewById(R.id.questionOne);
            questionTwo = itemView.findViewById(R.id.questionTwo);
            resultOne = itemView.findViewById(R.id.resultOne);
            resultTwo = itemView.findViewById(R.id.resultTwo);

            //Get User-PostLikes Table | Input: current login Id
            int userId = currentLoginData.getId();
            // todo update postType according to type of post liked ? Doubt, does postsLikedId get refreshed when post deleted?
            postsLikedId = recyclerFunctions.getPostLikes(userId, 1); //Posts

            // Handle options click
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Get postion from recycler view
                    int position = getAbsoluteAdapterPosition();
                    Toast.makeText(context, "Current Position is " + position, Toast.LENGTH_SHORT).show();
                    if(position != RecyclerView.NO_POSITION && (userId == posts.get(position).getAgencyid())) {
                        showOptionsDialog(position, itemView);
                    }

                }
            });

            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //local
                    int currentLikes = Integer.parseInt(likes.getText().toString());
                    int blogid = posts.get(getAbsoluteAdapterPosition()).getBlogid();

                    String tagUnlike = "unlike", tagLike = "like";
                    if(likeIcon.getTag().equals("unlike")) {
                        //TODO: Add likes in Database
                        likeIcon.setImageResource(R.drawable.ic_heart);
                        likeIcon.setTag(tagLike);

                        //Likes Counter
                        likes.setText(String.valueOf(currentLikes+1));
                        recyclerFunctions.updateDatabase(blogid, currentLikes+1, "likes", tagUnlike, "blog");

                    } else {
                        //TODO: Update Database
                        likeIcon.setImageResource(R.drawable.unlike_icon);
                        likeIcon.setTag("unlike");

                        likes.setText(String.valueOf(currentLikes-1));
                        recyclerFunctions.updateDatabase(blogid, currentLikes-1, "likes", tagLike, "blog");
                    }
                }
            });
        }

        private void showOptionsDialog(int position, View itemView) {
            PopupMenu popupMenu = new PopupMenu(context, options);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_post_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit) {
                        // ++ This handle the edit option and edit method need a logic to be implemented +_Cool Cool Cool+
                        //TODO this should return id of post / blog = -1 bcz assume adapterpos
                        recyclerFunctions.editPost(posts.get(position).getBlogid()-1, 1, itemView);

                        return true;
                    } else if (item.getItemId() == R.id.action_delete) {
                        //++ This handle the delete option +_Cool Cool Cool+
                        Toast.makeText(context, "Delete clicked", Toast.LENGTH_SHORT).show();
                        recyclerFunctions.deletePost(position, itemView.getContext(), 1);

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