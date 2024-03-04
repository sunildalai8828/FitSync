package com.example.fitsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitsync.models.AdminModel;
import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    Spinner spinner;
    EditText usernameEditText, passwordEditText;
    Button button;
    TextView text;

    String userType, username, password;
    FirebaseFirestore firestore;
    AdminModel adminModel;
    MemberModel memberModel;
    TrainerModel trainerModel;
    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        spinner = findViewById(R.id.spinner);
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        button = findViewById(R.id.login_button);
        text = findViewById(R.id.sign_up_button);

        // Initialize SharedPreferences for storing login status
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("logged", false)) {
            login(sharedPreferences.getString("username", null),
                    sharedPreferences.getString("password", null),
                    sharedPreferences.getString("usertype", null));
        }

        // Spinner item selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get selected user type
                userType = spinner.getSelectedItem().toString();

                // Show/hide password visibility based on user type
                if (userType.equals("Member") || userType.equals("Trainer")) {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    text.setVisibility(View.GONE);
                } else if (userType.equals("Admin")) {
                    text.setVisibility(View.VISIBLE);
                } else {
                    text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected if needed
            }
        });

        // Login button click listener
        button.setOnClickListener(view -> {
            // Get user inputs
            userType = spinner.getSelectedItem().toString();
            username = usernameEditText.getText().toString().trim();
            password = passwordEditText.getText().toString();

            // Perform login based on user type
            switch (userType) {
                case "Admin":
                    loginAdmin(username, password);
                    break;
                case "Member":
                    loginMember(username, password);
                    break;
                case "Trainer":
                    loginTrainer(username, password);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Please select user type!", Toast.LENGTH_LONG).show();
                    break;
            }
        });

        // Sign up button click listener
        text.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpAdminActivity.class);
            startActivity(intent);
        });
    }

    // Method to perform admin login
    public void loginAdmin(String username, String password) {
        firestore.collection("gyms").document(username).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            adminModel = documentSnapshot.toObject(AdminModel.class);
                            if (username.equals(adminModel.getAdminUsername())
                                    && password.equals(adminModel.getAdminPassword())) {
                                // Store login credentials in SharedPreferences
                                sharedPreferences.edit().putString("usertype", "Admin").apply();
                                sharedPreferences.edit().putString("username", adminModel.getAdminUsername()).apply();
                                sharedPreferences.edit().putString("password", adminModel.getAdminPassword()).apply();
                                sharedPreferences.edit().putBoolean("logged", true).apply();
                                // Perform login
                                login(adminModel.getAdminUsername(), adminModel.getAdminPassword(), userType);
                                Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "User doesn't exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Method to perform member login
    public void loginMember(String username, String password) {
        firestore.collection("/gymIDs/" + password + "/Member").document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                memberModel = documentSnapshot.toObject(MemberModel.class);
                                if (username.equals(memberModel.getMemberUsername())
                                        && password.equals(memberModel.getMemberPassword())) {
                                    // Store login credentials in SharedPreferences
                                    sharedPreferences.edit().putString("usertype", "Member").apply();
                                    sharedPreferences.edit().putString("username", memberModel.getMemberUsername()).apply();
                                    sharedPreferences.edit().putString("password", memberModel.getMemberPassword()).apply();
                                    sharedPreferences.edit().putBoolean("logged", true).apply();
                                    // Perform login
                                    login(memberModel.getMemberUsername(), memberModel.getMemberPassword(), userType);
                                    Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "User doesn't exist", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    // Method to perform trainer login
    public void loginTrainer(String username, String password) {
        firestore.collection("/gymIDs/" + password + "/Trainer").document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                trainerModel = documentSnapshot.toObject(TrainerModel.class);
                                if (username.equals(trainerModel.getTrainerUsername())
                                        && password.equals(trainerModel.getTrainerPassword())) {
                                    // Store login credentials in SharedPreferences
                                    sharedPreferences.edit().putString("usertype", "Trainer").apply();
                                    sharedPreferences.edit().putString("username", trainerModel.getTrainerUsername()).apply();
                                    sharedPreferences.edit().putString("password", trainerModel.getTrainerPassword()).apply();
                                    sharedPreferences.edit().putBoolean("logged", true).apply();
                                    // Perform login
                                    login(trainerModel.getTrainerUsername(), trainerModel.getTrainerPassword(), userType);
                                    Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "User doesn't exist", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    // Method to start LoadActivity after successful login
    void login(String username, String password, String userType) {
        Intent intent = new Intent(LoginActivity.this, LoadActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("usertype", userType);
        startActivity(intent);
        finish();
    }
}
