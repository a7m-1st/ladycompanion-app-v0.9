package com.example.NicoPart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ladyapp.R;

public class EditEmergencyContactActivity extends AppCompatActivity {

    Button saveButton;
    EditText editTextName1;
    EditText editTextName2;
    EditText editTextName3;
    EditText editTextNumber1;
    EditText editTextNumber2;
    EditText editTextNumber3;
    ImageView iconCross1;
    ImageView iconCross2;
    ImageView iconCross3;
    ImageView iconCross4;
    ImageView iconCross5;
    ImageView iconCross6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_emergency_contact);

        saveButton = findViewById(R.id.EmergencyContactsSavebutton);
        editTextName1 = findViewById(R.id.editTextContactName1);
        editTextName2 = findViewById(R.id.editTextContactName2);
        editTextName3 = findViewById(R.id.editTextContactName3);

        editTextNumber1 = findViewById(R.id.editTextContactNumber1);
        editTextNumber2 = findViewById(R.id.editTextContactNumber2);
        editTextNumber3 = findViewById(R.id.editTextContactNumber3);

        iconCross1 = findViewById(R.id.iconCross1);
        iconCross2 = findViewById(R.id.iconCross2);
        iconCross3 = findViewById(R.id.iconCross3);
        iconCross4 = findViewById(R.id.iconCross4);
        iconCross5 = findViewById(R.id.iconCross5);
        iconCross6 = findViewById(R.id.iconCross6);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        // Load data from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        editTextName1.setText(sharedPref.getString("CONTACT_NAME_1", ""));
        editTextName2.setText(sharedPref.getString("CONTACT_NAME_2", ""));
        editTextName3.setText(sharedPref.getString("CONTACT_NAME_3", ""));
        editTextNumber1.setText(sharedPref.getString("CONTACT_NUMBER_1", ""));
        editTextNumber2.setText(sharedPref.getString("CONTACT_NUMBER_2", ""));
        editTextNumber3.setText(sharedPref.getString("CONTACT_NUMBER_3", ""));

        iconCross1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact("CONTACT_NAME_1");
                editTextName1.setText("");
            }
        });

        iconCross2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact("CONTACT_NUMBER_1");
                editTextNumber1.setText("");
            }
        });

        iconCross3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact("CONTACT_NAME_2");
                editTextName2.setText("");
            }
        });

        iconCross4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact("CONTACT_NUMBER_2");
                editTextNumber2.setText("");
            }
        });

        iconCross5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact("CONTACT_NAME_3");
                editTextName3.setText("");
            }
        });

        iconCross6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContact("CONTACT_NUMBER_3");
                editTextNumber3.setText("");
            }
        });
    }

    private void saveData() {
        Intent returnIntent = new Intent();

        String name1 = editTextName1.getText().toString();
        String name2 = editTextName2.getText().toString();
        String name3 = editTextName3.getText().toString();

        String number1 = editTextNumber1.getText().toString();
        String number2 = editTextNumber2.getText().toString();
        String number3 = editTextNumber3.getText().toString();

        returnIntent.putExtra("CONTACT_NAME_1", name1);
        returnIntent.putExtra("CONTACT_NAME_2", name2);
        returnIntent.putExtra("CONTACT_NAME_3", name3);
        returnIntent.putExtra("CONTACT_NUMBER_1", number1);
        returnIntent.putExtra("CONTACT_NUMBER_2", number2);
        returnIntent.putExtra("CONTACT_NUMBER_3", number3);
        // Save data to SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("CONTACT_NAME_1", name1);
        editor.putString("CONTACT_NAME_2", name2);
        editor.putString("CONTACT_NAME_3", name3);
        editor.putString("CONTACT_NUMBER_1", number1);
        editor.putString("CONTACT_NUMBER_2", number2);
        editor.putString("CONTACT_NUMBER_3", number3);
        editor.apply();

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void removeContact(String key) {
        SharedPreferences sharedPref = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

}