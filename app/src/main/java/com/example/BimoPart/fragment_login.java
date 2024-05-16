package com.example.BimoPart;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.DataBase.data.currentLoginData;
import com.example.DataBase.sqlConnector;
import com.example.MainActivity.MainActivity;
import com.example.ladyapp.R;

import java.sql.SQLException;

public class fragment_login extends Fragment {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView, forgotPasswordTextView;
    private ImageView logoImageView;  // THE LOGO
    private TextView appNameTextView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoImageView = view.findViewById(R.id.ic_logo);
        appNameTextView = view.findViewById(R.id.app_name);


        // Animate the logo
        animateLogo();
        animateAppName();
        animateShimmerEffect();

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login_button);
        signUpTextView = view.findViewById(R.id.sign_up);
        forgotPasswordTextView = view.findViewById(R.id.reset_password);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();

                return;
            }

            //1
            performLogin(email, password);

            //Navigate to main Activity
//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            startActivity(intent);
//            getActivity().finish();
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestSignup);
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestResetPassword);
            }
        });
    }

    private void animateLogo() {
        ObjectAnimator growX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1f, 1.7f);
        ObjectAnimator growY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1f, 1.7f);
        AnimatorSet growShrink = new AnimatorSet();
        growShrink.play(growX).with(growY);
        growShrink.setDuration(1000);  // 1 second
        growShrink.start();
    }

    private void animateAppName() {
        appNameTextView.setTranslationX(-1000f);
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(appNameTextView, "alpha", 0f, 1f);
        fadeInOut.setDuration(2000);  // 2 seconds
        ObjectAnimator translationX = ObjectAnimator.ofFloat(appNameTextView, "translationX", -1000f, 0);
        translationX.setDuration(1000);  // 1 second
        AnimatorSet textAnimationSet = new AnimatorSet();
        textAnimationSet.playTogether(fadeInOut, translationX);
        textAnimationSet.start();
    }

    private void animateShimmerEffect() {
        View shimmerView = getView().findViewById(R.id.shimmer_effect);
        shimmerView.setTranslationX(-100); // Start position off-screen

        View shimmerView1 = getView().findViewById(R.id.shimmer_effect1);
        shimmerView1.setTranslationX(-100); // Start position off-screen

        // Translate the shimmer effect across the logo
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(shimmerView, "translationX", -1100, 505, 0);
        translateAnimator.setDuration(2000); // Duration for the shine to pass over the logo

        // Translate the shimmer effect across the logo
        ObjectAnimator translateAnimator1 = ObjectAnimator.ofFloat(shimmerView1, "translationX", -1100, 505, 0);
        translateAnimator1.setDuration(2000); // Duration for the shine to pass over the logo

        // Fade out the shimmer effect after translation
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(shimmerView, "alpha", 5f, 0f);
        fadeOutAnimator.setDuration(300); // Half a second to fade out

        // Fade out the shimmer effect after translation
        ObjectAnimator fadeOutAnimator1 = ObjectAnimator.ofFloat(shimmerView1, "alpha", 5f, 0f);
        fadeOutAnimator1.setDuration(300); // Half a second to fade out


        // Set up the animator set to play the translation and then the fade out
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(translateAnimator, fadeOutAnimator );
        animatorSet.setInterpolator(new LinearInterpolator()); // For a smooth, linear animation
        animatorSet.setStartDelay(100);
        animatorSet.start();

        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1.playSequentially(translateAnimator1, fadeOutAnimator1 );
        animatorSet1.setInterpolator(new LinearInterpolator()); // For a smooth, linear animation
        animatorSet1.setStartDelay(100);
        animatorSet1.start();
    }

    private void performLogin(final String email, final String password) {
        new Thread(() -> {
            try {
                sqlConnector connector = new sqlConnector();
                if (connector.checkPassword(email, password)) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();

                        //Don't remove this pls
                        new currentLoginData(email, password);


                        String profilePic = "";
                        if(currentLoginData.userType == 0)
                            profilePic = currentLoginData.getUser().getProfilePic();
                        else
                            profilePic = currentLoginData.getAgency().getProfilePhoto();

                        // Navigate to ProfilePic or Home pge
                        if(profilePic == null || profilePic.isEmpty()) { //if profilepic is emtpy
                            Navigation.findNavController(getView()).navigate(R.id.welcomePage);
                        } else {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }

                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Incorrect User Credentials", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (final SQLException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }



//    private void performLogin2(final String email, final String password) {
//
//        new Thread(() -> {
//            try {
//                URL url = new URL("http://10.0.2.2/login.php"); // Replace with your server login URL
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setDoOutput(true);
//
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") +
//                        "&password=" + URLEncoder.encode(password, "UTF-8");
//
//                writer.write(data);
//                writer.flush();
//                writer.close();
//                os.close();
//
//                int responseCode = conn.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // Process the server's response here
//                    handleLoginResponse(conn);
//                } else {
//                    // Handle server error here
//                    getActivity().runOnUiThread(() -> {
//                        Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_LONG).show();
//                    });
//                }
//            } catch (final Exception e) {
//                // Handle network errors here
//                getActivity().runOnUiThread(() -> {
//                    Toast.makeText(getActivity(), "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//            }
//        }).start();
//    }
//
//    private void handleLoginResponse(HttpURLConnection conn) {
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//
//            reader.close();
//
//            String responseStr = response.toString();
//
//            getActivity().runOnUiThread(() -> {
//                try {
//                    JSONObject jsonResponse = new JSONObject(responseStr);
//                    boolean loginSuccess = jsonResponse.getBoolean("loginSuccess");
//                    String userType = jsonResponse.getString("userType"); // Assuming the server sends the user type
//
//                    if (loginSuccess) {
//                        // Store userType locally, e.g., in SharedPreferences
//
//                        // Navigate based on userType
//                        if ("citizen".equals(userType)) {
//                            // Navigate to Citizen specific activity/fragment
//                        } else if ("agency".equals(userType)) {
//                            // Navigate to Agency specific activity/fragment
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    Toast.makeText(getActivity(), "Response parsing error", Toast.LENGTH_LONG).show();
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            conn.disconnect();
//        }
//    }
}
