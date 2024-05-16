package com.example.NicoPart;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ladyapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SeeSosContactsActivity extends AppCompatActivity {
    private SelectedContactsAdapter adapter;
    private static final int YOUR_REQUEST_CODE = 1;

    //Gets most of permissions
    public void getPermissions() {
        //Asking for notification access now:
        if (ActivityCompat.checkSelfPermission(SeeSosContactsActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(SeeSosContactsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(SeeSosContactsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(SeeSosContactsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {


                    }
                    else {
                        ActivityCompat.requestPermissions(SeeSosContactsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 104);
                    }

                }
                else {
                    ActivityCompat.requestPermissions(SeeSosContactsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 103);
                }

            }
            else {
                ActivityCompat.requestPermissions(SeeSosContactsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
            }

        }
        else {
            ActivityCompat.requestPermissions(SeeSosContactsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 101);
        }
    }

    private List<Contact> loadSelectedContacts() {
        SharedPreferences prefs = getSharedPreferences("SelectedContacts", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("Contacts", null);
        if (json == null) {
            return new ArrayList<>(); // Return an empty list if no data is found
        }
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        return gson.fromJson(json, type);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_sos_contacts);

        getPermissions();

        ConstraintLayout addContactsLayout = findViewById(R.id.addcontactsLayout);
        addContactsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ButtonClick", "Button clicked");
                Intent intent = new Intent(SeeSosContactsActivity.this, SelectSosContactsListActivity.class);
                startActivityForResult(intent, YOUR_REQUEST_CODE); // Use YOUR_REQUEST_CODE here
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recycle_view_chosen_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectedContactsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        List<Contact> contacts = loadSelectedContacts();
        if (contacts != null) {
            adapter.updateContacts(contacts);
        }

        adapter.setContactChangeCallback(new SelectedContactsAdapter.ContactChangeCallback() {
            @Override
            public void onContactListChanged(List<Contact> updatedList) {
                saveSelectedContacts(updatedList);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YOUR_REQUEST_CODE && resultCode == RESULT_OK) {
            List<Contact> selectedContacts = (List<Contact>) data.getSerializableExtra("selectedContacts");
            adapter.updateContacts(selectedContacts);
            saveSelectedContacts(selectedContacts); // Save the updated list
        }
    }


    private void saveSelectedContacts(List<Contact> contacts) {
        SharedPreferences prefs = getSharedPreferences("SelectedContacts", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        editor.putString("Contacts", json);
        editor.apply();
    }

}