package com.example.NicoPart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ladyapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class fragment_SeeSosContacts extends Fragment {

    private SelectedContactsAdapter adapter;
    private static final int YOUR_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__see_sos_contacts, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView clickText = view.findViewById(R.id.add_sos_contacts_text);

        clickText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Add Contacts Clicked", Toast.LENGTH_SHORT).show();
                Log.d("view", "clicked");
                Intent intent = new Intent(getContext(), SelectSosContactsListActivity.class);
                startActivityForResult(intent, YOUR_REQUEST_CODE); // Use YOUR_REQUEST_CODE here
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycle_view_chosen_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        //ConstraintLayout addContactsLayout = view.findViewById(R.id.addcontactsLayout);

    }

    private List<Contact> loadSelectedContacts() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("SelectedContacts", getContext().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("Contacts", null);
        if (json == null) {
            return new ArrayList<>(); // Return an empty list if no data is found
        }
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YOUR_REQUEST_CODE && resultCode == this.getActivity().RESULT_OK) {
            List<Contact> selectedContacts = (List<Contact>) data.getSerializableExtra("selectedContacts");
            adapter.updateContacts(selectedContacts);
            saveSelectedContacts(selectedContacts); // Save the updated list
        }
    }


    private void saveSelectedContacts(List<Contact> contacts) {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("SelectedContacts", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        editor.putString("Contacts", json);
        editor.apply();
    }
}