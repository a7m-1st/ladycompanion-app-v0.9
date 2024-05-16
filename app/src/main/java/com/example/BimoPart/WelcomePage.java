package com.example.BimoPart;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MainActivity.AndroidUtil;
import com.example.MainActivity.MainActivity;
import com.example.ladyapp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.sql.SQLException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class WelcomePage extends Fragment {
    Uri selectedImageUri;
    ImageView profilePic;
    TextView welcomeTxt;
    Button saveBtn, skipBtn;
    ActivityResultLauncher<Intent> imagePickLauncher;
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
        return inflater.inflate(R.layout.fragment_welcome_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        welcomeTxt = view.findViewById(R.id.welcomeTxt);
        saveBtn = view.findViewById(R.id.saveBtn);
        skipBtn = view.findViewById(R.id.skipBtn);
        profilePic = view.findViewById(R.id.profilePicSign);

        if(currentLoginData.userType == 0)
            welcomeTxt.setText(welcomeTxt.getText().toString() + currentLoginData.getUser().getFullName());
        else {
            welcomeTxt.setText(welcomeTxt.getText().toString() + currentLoginData.getAgency().getCompanyName());
        }


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

        saveBtn.setOnClickListener(v -> {
            updateProfilePicDB(selectedImageUri);

            //gO TO HOME PAGE
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        skipBtn.setOnClickListener(v -> {
            setDefaultPic(); //default image in case
            updateProfilePicDB(selectedImageUri);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void updateProfilePicDB(Uri selectedImageUri) {
        new Thread(() -> {
            Looper.prepare();
            try {
                sqlConnector connect = new sqlConnector();
                connect.updateSpecificColumnUser(selectedImageUri.toString(), "profilepic");
                connect.closeConnection();

                Toast.makeText(getContext(),"Profile Picture Set", Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Looper.loop();
        }).start();
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
}