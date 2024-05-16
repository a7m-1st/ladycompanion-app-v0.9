package com.example.AhmedPart;

import android.app.Activity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.example.DataBase.data.BlogsData;
import com.example.DataBase.data.BlogsDataWeb;
import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MouawiaPart.fragment_home;
import com.example.ladyapp.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class JSoup {
    ProgressBar progressBar;
    ArrayList<BlogsDataWeb> blogs;
    ArrayList<String> titles;
    public ArrayList<BlogsDataWeb> startScraping(String link, View view, Activity activity) {
        blogs = new ArrayList<>();
        titles = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));

        new Thread(() -> {
            //Add list of titles here
            for(BlogsData b: fragment_home.blogs) {
                titles.add(b.getBlogtitle());
            }
            // do your stuff
            try {
                String url = link; //website
                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("span.field-content").select("a");
                //System.out.println(data.toString());

                int size = data.size();
                for(int i = 0; i < size; i++) {
                    String blogUrl = "https://www.unwomen.org"+data.select("a")
                            .eq(i).attr("href");

                    String blogTitle = data.select("a")
                            .eq(i+1).text();

                    //Get Blog body
                    String blogBody = getBlogBody(blogUrl);

                    if(blogs != null) {
                        boolean flag = false;
                        for (BlogsDataWeb x : blogs) {
                            if (x.getBlogUrl().equals(blogUrl)) {
                                System.out.println("ALREADY CONTAINED***&&^^");
                                flag = true;
                            }
                        }
                        if(!flag)
                            blogs.add(new BlogsDataWeb(blogUrl, blogTitle, blogBody));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            activity.runOnUiThread((Runnable) () -> {
                if(!blogs.isEmpty())
                    addToDataBase(activity, view);
                else {
                    Toast.makeText(activity, "All Blogs Added", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

        return blogs;
    }

    private void addToDataBase(Activity activity, View view) {

        // do onPostExecute stuff
        try {
            sqlConnector connect = new sqlConnector();
            //Add all posts
            for(int i =0, j=0; i<2; i++, j++) {
                BlogsDataWeb b = blogs.get(j);

                while(titles.contains(b.getBlogTitle())) {
                    b = blogs.get(j++);
                }
                //Create new blog
                String link = b.getBlogUrl();
                connect.createNewPost(b.getBlogTitle(), b.getBlogBody(), 0, java.time.LocalDate.now().toString(), currentLoginData.getId(), 1, link);
                j++;
            }
            connect.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Finish Progress after saving all
        progressBar.setVisibility(View.GONE);
        progressBar.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
        //fragment_blogs.adapter.notifyDataSetChanged();
        // this.clearFormFields();
        Toast.makeText(activity, "Blogs Added Successfully", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.DestBlogs);
    }

    public String getBlogBody(String link) {
        try {
            Document doc = Jsoup.connect(link).get();

            Elements data = doc.select("div.col-md-8");
            //System.out.println("data is "+data.toString());

            int size = data.size();

            String blogBody = data.select("div.col-md-8")
                    .select("p")
                    .text();
            //System.out.println("Blog body for p is " + blogBody + "\n");

            return blogBody;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
