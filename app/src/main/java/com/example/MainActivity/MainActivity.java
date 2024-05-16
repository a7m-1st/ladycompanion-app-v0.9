package com.example.MainActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.BimoPart.LoginActivity;
import com.example.DataBase.data.currentLoginData;
import com.example.HarIPart.AgencyUserUnsafeZone;
import com.example.HarIPart.RegularUserUnsafeZone;
import com.example.NicoPart.SeeSosContactsActivity;
import com.example.ladyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpActionBarAndMenu();
        setUpSearch();
    }


    private void setUpActionBarAndMenu() {

        //Instead of direct layout calling
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
        NavController navController = host.getNavController();

        //Set up the side navigation menu
        NavigationView sideMenu = findViewById(R.id.sideNav);
        NavigationUI.setupWithNavController(sideMenu, navController);

        //Set up menus
        setupNavMenus(navController);

        ImageView sideMenuIcon = findViewById(R.id.sideMenuIcon);
        sideMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.ActMain);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //Sets up the search bar
    public void setUpSearch() {
        searchView = findViewById(R.id.searchView);
        //For focus only
//        searchView.setFocusable(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Hide Keyboard
//        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(((Activity) this).getCurrentFocus().getWindowToken(), 0);

        //Remove fo
        findViewById(R.id.background).requestFocus();
    }

    //Inflate menu items
    private void setupNavMenus(NavController navController) {
        NavigationView sideNav = findViewById(R.id.sideNav);
        NavigationUI.setupWithNavController(sideNav, navController);
        //TODO: DIVIDE THE ONOPTIONSITEMSELECTED FOR SIDENAV
        sideNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //This is entirely for Unsafe Zone system
                if (item.getItemId() == R.id.ToDestSafetyZone) {


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //asking for bg location access now:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            //Asking for notification access now:
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

                                if (currentLoginData.getUserType() == 0) {
                                    startActivity(new Intent(MainActivity.this, RegularUserUnsafeZone.class));
                                    //Do not finish mainactivity, as we can get back to this
                                }
                                else if (currentLoginData.getUserType() == 1) {
                                    startActivity(new Intent(MainActivity.this, AgencyUserUnsafeZone.class));
                                    //Do not finish mainactivity, as we can get back to this
                                }
                            }
                            else {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                            }
                        }
                        else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
                        }
                    }
                    else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                    }


                }
                else if(item.getItemId() == R.id.DestWebScraping) {
                    if(currentLoginData.userType == 0) {
                        Toast.makeText(MainActivity.this, "Agency User Only", Toast.LENGTH_SHORT).show();
                    } else
                        Navigation.findNavController(MainActivity.this, R.id.NHFMain).navigate(R.id.DestWebScraping);
                }
                else if(item.getItemId() == R.id.LogOut) {
                    new onLogOut(true);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
                }
                else if (item.getItemId() == R.id.ToDestAboutUs) {
                    // Navigate to the "AboutUs" fragment
                    Navigation.findNavController(MainActivity.this, R.id.NHFMain).navigate(R.id.ToDestAboutUs);
                }
                //implement for other options as needed
                return true;
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    Log.d("Menu", "Menu Invoked");
                    //Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, String.format("User Type is "+new currentLoginData().userType), Toast.LENGTH_LONG).show();

                    //Navigation
                    //Rename the Menu Item Id to Navigation Fragments
                    Log.d("Menu Id", "Item Id taken is");


                    if(item.getTitle().equals("Contacts")) {
                        System.out.println("Destination clicked");
                        Intent intent = new Intent(MainActivity.this, SeeSosContactsActivity.class);
                        startActivity(intent);
                    } else
                        Navigation.findNavController(MainActivity.this, R.id.NHFMain).navigate(item.getItemId());

                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bottom, menu);
        return true;
    }


    //Bottom Navigation Menu & Side Menu | navigationId Deside
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            Log.d("Menu", "Menu Invoked");
            //Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, String.format("User Type is "+new currentLoginData().userType), Toast.LENGTH_LONG).show();

            //Navigation
            //Rename the Menu Item Id to Navigation Fragments
            Log.d("Menu Id", "Item Id taken is");


            if(item.getTitle().equals("Contacts")) {
                System.out.println("Destination clicked");
                Intent intent = new Intent(MainActivity.this, SeeSosContactsActivity.class);
                startActivity(intent);
            } else
                Navigation.findNavController(this, R.id.NHFMain).navigate(item.getItemId());

            return true;
        } catch (Exception ex) {
            return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Toast.makeText(this, "Main Activity Destroyed", Toast.LENGTH_SHORT).show();
//        Log.d("On Destroy Listener", "Activity Destroyed");
//        new currentLoginData().resetCurrentData();
//    }
}