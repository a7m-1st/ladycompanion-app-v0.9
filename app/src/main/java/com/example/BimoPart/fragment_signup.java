package com.example.BimoPart;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class fragment_signup extends Fragment {
    // Define member variables for all the views
    private RadioGroup userTypeRadioGroup;
    private LinearLayout citizenLayout, agencyLayout;
    private EditText fullNameEditText, emailEditText, idEditText, homeAddressEditText, phoneNumberEditText, passwordEditText, confirmPasswordEditText, ageEditText;
    private EditText companyTypeEditText, companyNameEditText, companyPhoneNumberEditText, companyEmailEditText, companyAddressEditText, companyPasswordEditText, companyConfirmPasswordEditText;
    private Button signUpButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupListeners(view);
    }

    private void initializeViews(View view) {
        userTypeRadioGroup = view.findViewById(R.id.userTypeRadioGroup);
        citizenLayout = view.findViewById(R.id.citizenLayout);
        agencyLayout = view.findViewById(R.id.agencyLayout);

        // Initialize EditTexts for Citizen
        fullNameEditText = view.findViewById(R.id.sign_up_full_name);
        emailEditText = view.findViewById(R.id.sign_up_email);
        idEditText = view.findViewById(R.id.sign_up_id);
        homeAddressEditText = view.findViewById(R.id.sign_up_home_address);
        phoneNumberEditText = view.findViewById(R.id.sign_up_phone_number);
        passwordEditText = view.findViewById(R.id.sign_up_password);
        confirmPasswordEditText = view.findViewById(R.id.sign_up_password_again);
        ageEditText = view.findViewById(R.id.sign_up_age);

        // Initialize EditTexts for Agency
        companyTypeEditText = view.findViewById(R.id.sign_up_company_type);
        companyNameEditText = view.findViewById(R.id.sign_up_company_name);
        companyPhoneNumberEditText = view.findViewById(R.id.sign_up_company_phone_number);
        companyEmailEditText = view.findViewById(R.id.sign_up_company_email);
        companyAddressEditText = view.findViewById(R.id.sign_up_company_address);
        companyPasswordEditText = view.findViewById(R.id.sign_up_company_password);
        companyConfirmPasswordEditText = view.findViewById(R.id.sign_up_company_password_again);

        signUpButton = view.findViewById(R.id.sign_up_button);
    }

    private void setupListeners(View view) {
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonCitizen) {
                citizenLayout.setVisibility(View.VISIBLE);
                agencyLayout.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioButtonAgency) {
                citizenLayout.setVisibility(View.GONE);
                agencyLayout.setVisibility(View.VISIBLE);
            }
        });

        signUpButton.setOnClickListener(v -> {
            if (userTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioButtonCitizen) {
                performCitizenSignUp2(view);
            } else {
                performAgencySignUp2(view);
            }
        });
    }

    private void performCitizenSignUp2(View view) {
        String fullName = fullNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String idNumber = idEditText.getText().toString();
        String homeAddress = homeAddressEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String hashedPassword = hashPassword(password);

        if (fullName.isEmpty() || email.isEmpty() || idNumber.isEmpty() || homeAddress.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || age.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isValidEmail(email) || !password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Invalid email format or passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            Looper.prepare();
            try {
                sqlConnector connect = new sqlConnector();
                if (connect.isUserEmailPresent(email)) {
                    Toast.makeText(getActivity(),"This Email is already in use!", Toast.LENGTH_LONG).show();
                    this.clearFormFields();
                    return;
                }
                else { //todo add profile pic here
                    connect.signUpRegularUser(fullName, email, "", idNumber, homeAddress, phoneNumber, hashedPassword, age, java.time.LocalDate.now().toString());
                    connect.closeConnection();
                    this.clearFormFields();
                    Toast.makeText(getActivity(), "Sign Up Successful!", Toast.LENGTH_LONG).show();
                    //Navigate to Nav Controller once done
                    getActivity().runOnUiThread(() -> {
                        Navigation.findNavController(view).navigate(R.id.DestLogin);
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Looper.loop();
        }).start();
    }

    // In fragment_signup.java
    private void performAgencySignUp2(View view) {
        String companyType = companyTypeEditText.getText().toString();
        String companyName = companyNameEditText.getText().toString();
        String companyPhoneNumber = companyPhoneNumberEditText.getText().toString();
        String companyEmail = companyEmailEditText.getText().toString();
        String companyAddress = companyAddressEditText.getText().toString();
        String companyPassword = companyPasswordEditText.getText().toString();
        String companyConfirmPassword = companyConfirmPasswordEditText.getText().toString();

        if (companyType.isEmpty() || companyName.isEmpty() || companyPhoneNumber.isEmpty() || companyEmail.isEmpty() || companyAddress.isEmpty() || companyPassword.isEmpty() || companyConfirmPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidEmail(companyEmail)) {
            Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_LONG).show();
            return;
        }

        if (!companyPassword.equals(companyConfirmPassword)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        String hashedPassword = hashPassword(companyPassword);

        new Thread(() -> {
            Looper.prepare();
            try {
                sqlConnector connect = new sqlConnector();
                if (connect.isUserEmailPresent(companyEmail)) {
                    Toast.makeText(getActivity(),"This Email is already in use!", Toast.LENGTH_LONG).show();
                } else { //todo add profile pic here
                    connect.signUpAgencyUser(companyType,"",  companyName, companyPhoneNumber, companyEmail, companyAddress, hashedPassword);
                    connect.closeConnection();
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Sign Up Successful!", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(view).navigate(R.id.DestLogin);
                    });
                }
            } catch (SQLException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
            Looper.loop();
        }).start();
    }


    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

//    private void performCitizenSignUp() {
//        String fullName = fullNameEditText.getText().toString();
//        String email = emailEditText.getText().toString();
//        String idNumber = idEditText.getText().toString();
//        String homeAddress = homeAddressEditText.getText().toString();
//        String phoneNumber = phoneNumberEditText.getText().toString();
//        String password = passwordEditText.getText().toString();
//        String confirmPassword = confirmPasswordEditText.getText().toString();
//        String age = ageEditText.getText().toString();
//
//        if (fullName.isEmpty() || email.isEmpty() || idNumber.isEmpty() || homeAddress.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || age.isEmpty()) {
//            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        if (!isValidEmail(email) || !password.equals(confirmPassword)) {
//            Toast.makeText(getActivity(), "Invalid email format or passwords do not match", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        sendSignUpDataToServer(fullName, email, idNumber, homeAddress, phoneNumber, password, confirmPassword, age, "citizen");
//    }
//
//    private void performAgencySignUp() {
//        String companyType = companyTypeEditText.getText().toString();
//        String companyName = companyNameEditText.getText().toString();
//        String companyPhoneNumber = companyPhoneNumberEditText.getText().toString();
//        String companyEmail = companyEmailEditText.getText().toString();
//        String companyAddress = companyAddressEditText.getText().toString();
//        String companyPassword = companyPasswordEditText.getText().toString();
//        String companyConfirmPassword = companyConfirmPasswordEditText.getText().toString();
//
//        if (companyType.isEmpty() || companyName.isEmpty() || companyPhoneNumber.isEmpty() || companyEmail.isEmpty() || companyAddress.isEmpty() || companyPassword.isEmpty() || companyConfirmPassword.isEmpty()) {
//            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        if (!isValidEmail(companyEmail) || !companyPassword.equals(companyConfirmPassword)) {
//            Toast.makeText(getActivity(), "Invalid email format or passwords do not match", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        sendSignUpDataToServer(companyType, companyName, companyPhoneNumber, companyEmail, companyAddress, companyPassword, companyConfirmPassword, "", "agency");
//    }
//
//    private void sendSignUpDataToServer(String name, String email, String idNumber, String address, String phoneNumber, String password, String confirmPassword, String age, String userType) {
//        final String postData = preparePostData(name, email, idNumber, address, phoneNumber, password, confirmPassword, age, userType);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://10.0.2.2/signup.php");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("POST");
//                    conn.setDoOutput(true);
//
//                    OutputStream os = conn.getOutputStream();
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                    writer.write(postData);
//                    writer.flush();
//                    writer.close();
//                    os.close();
//
//                    int responseCode = conn.getResponseCode();
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        handleServerResponse(conn);
//                    } else {
//                        handleServerError("Server responded with code: " + responseCode);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    handleNetworkError(e);
//                }
//            }
//        }).start();
//    }
//
//    private String preparePostData(String name, String email, String idNumber, String address, String phoneNumber, String password, String confirmPassword, String age, String userType) {
//        try {
//            return "name=" + URLEncoder.encode(name, "UTF-8") +
//                    "&email=" + URLEncoder.encode(email, "UTF-8") +
//                    "&idNumber=" + URLEncoder.encode(idNumber, "UTF-8") +
//                    "&address=" + URLEncoder.encode(address, "UTF-8") +
//                    "&phoneNumber=" + URLEncoder.encode(phoneNumber, "UTF-8") +
//                    "&password=" + URLEncoder.encode(password, "UTF-8") +
//                    "&confirmPassword=" + URLEncoder.encode(confirmPassword, "UTF-8") +
//                    "&age=" + URLEncoder.encode(age, "UTF-8") +
//                    "&userType=" + URLEncoder.encode(userType, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    private void handleServerResponse(HttpURLConnection conn) {
//        try {
//            InputStream is = conn.getInputStream();
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader br = new BufferedReader(isr);
//
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) {
//                response.append(line);
//            }
//
//            br.close();
//            isr.close();
//
//            String responseStr = response.toString();
//
//            getActivity().runOnUiThread(() -> {
//                if (responseStr.contains("Success")) {
//                    clearFormFields();
//                    Toast.makeText(getActivity(), "Sign Up Successful", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getActivity(), "Sign Up Failed: " + responseStr, Toast.LENGTH_LONG).show();
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            conn.disconnect();
//        }
//    }

    private void clearFormFields() {
        // Clear EditText fields for Citizen
        fullNameEditText.setText("");
        emailEditText.setText("");
        idEditText.setText("");
        homeAddressEditText.setText("");
        phoneNumberEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        ageEditText.setText("");

        // Clear EditText fields for Agency
        companyTypeEditText.setText("");
        companyNameEditText.setText("");
        companyPhoneNumberEditText.setText("");
        companyEmailEditText.setText("");
        companyAddressEditText.setText("");
        companyPasswordEditText.setText("");
        companyConfirmPasswordEditText.setText("");
    }
//
//    private void handleServerError(String errorDetails) {
//        getActivity().runOnUiThread(() -> {
//            Log.e("SignupError", "Server Error: " + errorDetails);
//            Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_LONG).show();
//        });
//    }
//
//    private void handleNetworkError(Exception exception) {
//        getActivity().runOnUiThread(() -> {
//            Log.e("SignupError", "Network Error: " + exception.getMessage());
//            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
//        });
//    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

