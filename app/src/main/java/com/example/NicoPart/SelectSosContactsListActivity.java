package com.example.NicoPart;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ladyapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectSosContactsListActivity extends AppCompatActivity implements ContactAdapter.OnContactSelectionChangedListener {

    private ContactAdapter contactAdapter;
    private RecyclerView recyclerView;
    private EditText contactSearch;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sos_contacts_list);

        recyclerView = findViewById(R.id.recycle_view_contacts);
        contactSearch = findViewById(R.id.contactSearch);
        searchIcon = findViewById(R.id.searchIcon);

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            List<Contact> allContacts = loadContacts();
            List<Contact> savedSelectedContacts = loadSelectedContacts();

            // Pass both lists to initializeRecyclerView
            initializeRecyclerView(allContacts, savedSelectedContacts);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        findViewById(R.id.select_text).setOnClickListener(v -> {
            List<Contact> selectedContacts = contactAdapter.getSelectedContacts();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("selectedContacts", new ArrayList<>(selectedContacts));
            setResult(RESULT_OK, returnIntent);
            finish();
        });

        searchIcon.setOnClickListener(v -> {
            String searchText = contactSearch.getText().toString();
            contactAdapter.filterContacts(searchText);
        });
    }

    private List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<>();

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                if (nameIndex != -1 && numberIndex != -1) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(nameIndex);
                        String phoneNumber = cursor.getString(numberIndex);
                        contacts.add(new Contact(name, phoneNumber, false));
                    }
                } else {
                    // Handle the case where the columns are not found
                }
                cursor.close();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

        // Sort contacts alphabetically
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });

        return contacts;
    }

    private void initializeRecyclerView(List<Contact> allContacts, List<Contact> savedSelectedContacts) {
        contactAdapter = new ContactAdapter(allContacts, savedSelectedContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);
        contactAdapter.setOnContactSelectionChangedListener(this);
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        TextView selectText = findViewById(R.id.select_text);
        String text = "Select(" + selectedCount + "/10)";
        selectText.setText(text);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted, refresh the contact list
            List<Contact> allContacts = loadContacts(); // Load all contacts
            List<Contact> savedSelectedContacts = loadSelectedContacts(); // Load previously selected contacts

            // Pass both lists to initializeRecyclerView
            initializeRecyclerView(allContacts, savedSelectedContacts);
        } else {
            // Permission was denied. Handle the failure.
            Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSelectedContacts(List<Contact> selectedContacts) {
        SharedPreferences prefs = getSharedPreferences("SelectedContacts", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selectedContacts);
        editor.putString("Contacts", json);
        editor.apply();
    }
    private List<Contact> loadSelectedContacts() {
        SharedPreferences prefs = getSharedPreferences("SelectedContacts", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("Contacts", null);
        if (json == null) {
            return new ArrayList<>(); // Return an empty list if no data found
        }
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        return gson.fromJson(json, type);
    }

}