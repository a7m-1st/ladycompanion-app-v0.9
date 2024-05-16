package com.example.MouawiaPart;

import static com.example.MainActivity.AndroidUtil.setProfilePic;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MainActivity.AndroidUtil;
import com.example.ladyapp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.sql.SQLException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class fragment_editinfo extends Fragment {
    private RadioGroup userTypeRadioGroup;
    private LinearLayout citizenLayout, agencyLayout;
    private EditText fullNameEditText, emailEditText, idEditText, homeAddressEditText, phoneNumberEditText, passwordEditText, confirmPasswordEditText, ageEditText;
    private EditText companyTypeEditText, companyNameEditText, companyPhoneNumberEditText, companyEmailEditText, companyAddressEditText, companyPasswordEditText, companyConfirmPasswordEditText;
    private Button signUpButton;

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    ImageView profilePic;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                        }
                    }
                }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_editinfo, container, false);

        //Differentiate between user & agent to switch data
        if(new currentLoginData().userType == 0) {
            rootView.findViewById(R.id.agencyLayout).setVisibility(View.GONE);
            rootView.findViewById(R.id.citizenLayout).setVisibility(View.VISIBLE);
            profilePic = rootView.findViewById(R.id.profilePicEditInfo);

            setDefaultPic(); //default image in case

            profilePic.setOnClickListener((v) -> {
                ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });
            });


        }
        else if(new currentLoginData().userType == 1) {
            rootView.findViewById(R.id.citizenLayout).setVisibility(View.GONE);
            rootView.findViewById(R.id.agencyLayout).setVisibility(View.VISIBLE);

            profilePic = rootView.findViewById(R.id.profilePicEditInfoAgency);

            setDefaultPic(); //default image in case

            profilePic.setOnClickListener((v) -> {
                ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });
            });
        }

        return rootView;
    }

    public void setDefaultPic() {
        int resourceId = R.mipmap.default_profile;
        Resources resources = getContext().getResources();
        Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build();
        selectedImageUri = uri;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Initialize EditTexts for Citizen
        fullNameEditText = view.findViewById(R.id.sign_up_full_name);
        idEditText = view.findViewById(R.id.sign_up_id);
        homeAddressEditText = view.findViewById(R.id.sign_up_home_address);
        phoneNumberEditText = view.findViewById(R.id.sign_up_phone_number);
        ageEditText = view.findViewById(R.id.sign_up_age);

        // Initialize EditTexts for Agency
        companyTypeEditText = view.findViewById(R.id.sign_up_company_type);
        companyNameEditText = view.findViewById(R.id.sign_up_company_name);
        companyPhoneNumberEditText = view.findViewById(R.id.sign_up_company_phone_number);
        companyAddressEditText = view.findViewById(R.id.sign_up_company_address);

        //save the data
        Button saveButton = view.findViewById(R.id.save_button);

        //Populate texts
        initializeInfo();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoginData.userType == 0) {
                    updateCitizenData(view);
                }
                else if (currentLoginData.userType == 1) {
                    updateAgencyData(view);
                }
            }
        });
    }

    private void initializeInfo() {
        if (currentLoginData.userType == 0) {
                setProfilePic(getContext(), Uri.parse(currentLoginData.getUser().getProfilePic()), profilePic);
                fullNameEditText.setText(currentLoginData.getUser().getFullName().toString());
                idEditText.setText(currentLoginData.getUser().getIdNumber().toString());
                homeAddressEditText.setText(currentLoginData.getUser().getHomeAddress().toString());
                phoneNumberEditText.setText(currentLoginData.getUser().getPhone().toString());
                ageEditText.setText(String.valueOf(currentLoginData.getUser().getAge()));

        }
        else if (currentLoginData.userType == 1) {
                setProfilePic(getContext(), Uri.parse(currentLoginData.getAgency().getProfilePhoto()), profilePic);
                companyTypeEditText.setText(currentLoginData.getAgency().getCompanyType().toString());
                companyNameEditText.setText(currentLoginData.getAgency().getCompanyName().toString());
                companyPhoneNumberEditText.setText(currentLoginData.getAgency().getPhone().toString());
                companyAddressEditText.setText(currentLoginData.getAgency().getAddress().toString());

        }
    }


    private void updateCitizenData(View view) {
        String fullName = fullNameEditText.getText().toString();
        String idNumber = idEditText.getText().toString();
        String homeAddress = homeAddressEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String age = ageEditText.getText().toString();

        if (fullName.isEmpty() || idNumber.isEmpty() || homeAddress.isEmpty() || phoneNumber.isEmpty() || age.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            Looper.prepare();
            try {
                sqlConnector connect = new sqlConnector();

                //todo add profile pic here
                connect.editRegularUser(fullName, selectedImageUri.toString(), idNumber, homeAddress, phoneNumber, age);
                connect.closeConnection();
                this.clearFormFields();
                new currentLoginData(currentLoginData.getUser().getEmail(), currentLoginData.getUser().getPassword());

                Toast.makeText(getActivity(), "Changes Saved Successfully!", Toast.LENGTH_LONG).show();
                //Navigate to Nav Controller once done
                getActivity().runOnUiThread(() -> {
                    Navigation.findNavController(view).navigate(R.id.ToDestProfile);
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }
            Looper.loop();
        }).start();
    }

    private void updateAgencyData(View view) {
        String companyType = companyTypeEditText.getText().toString();
        String companyName = companyNameEditText.getText().toString();
        String companyPhoneNumber = companyPhoneNumberEditText.getText().toString();
        String companyAddress = companyAddressEditText.getText().toString();

        if (companyType.isEmpty() || companyName.isEmpty() || companyPhoneNumber.isEmpty() ||  companyAddress.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            Looper.prepare();
            try {
                sqlConnector connect = new sqlConnector();

                { //todo add profile pic here
                    connect.editAgencyUser(selectedImageUri.toString(), companyType, companyName, companyPhoneNumber, companyAddress);
                    connect.closeConnection();
                    this.clearFormFields();

                    new currentLoginData(currentLoginData.getAgency().getEmail(), currentLoginData.getAgency().getPassword());
                    Toast.makeText(getActivity(), "Changes Saved Successfully!", Toast.LENGTH_LONG).show();
                    //Navigate to Nav Controller once done
                    getActivity().runOnUiThread(() -> {
                        Navigation.findNavController(view).navigate(R.id.ToDestProfile);
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Looper.loop();
        }).start();
    }

    private void clearFormFields() {
        // Clear EditText fields for Citizen
        fullNameEditText.setText("");
        idEditText.setText("");
        homeAddressEditText.setText("");
        phoneNumberEditText.setText("");
        ageEditText.setText("");

        // Clear EditText fields for Agency
        companyTypeEditText.setText("");
        companyNameEditText.setText("");
        companyPhoneNumberEditText.setText("");
        companyAddressEditText.setText("");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}