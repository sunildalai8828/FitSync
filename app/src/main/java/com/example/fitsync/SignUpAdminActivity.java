package com.example.fitsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitsync.models.AdminModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpAdminActivity extends AppCompatActivity {
    EditText adminNameEditText,gymNameEditText,branchNameEditText,usernameEditText,passwordEditText;
    Button button;

    String adminName,gymName,branchName,username,password,gymId;

    FirebaseFirestore firestore;
    AdminModel adminModel;
//    SharedPreferences sharedPreferences=getSharedPreferences("mypreferences",MODE_PRIVATE);
//    SharedPreferences.Editor editor=sharedPreferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_admin);

        firestore = FirebaseFirestore.getInstance();
        adminNameEditText=findViewById(R.id.admin_name_edit_text);
        gymNameEditText=findViewById(R.id.gym_name_edit_text);
        branchNameEditText=findViewById(R.id.branch_name_edit_text);
        usernameEditText=findViewById(R.id.username_edit_text);
        passwordEditText=findViewById(R.id.password_edit_text);
        button=findViewById(R.id.sign_up_button);

        button.setOnClickListener(view->{
            adminName=adminNameEditText.getText().toString();
            gymName=gymNameEditText.getText().toString();
            branchName=branchNameEditText.getText().toString();
            username=usernameEditText.getText().toString();
            password=passwordEditText.getText().toString();
            gymId=createGymId(gymName,branchName,username);
            checkUserExistence(username);
        });
    }

    public String createGymId(String gymName,String branchName,String username){
        return gymName.substring(0,3)+branchName.substring(0,3)+username.substring(0,3);
    }

    public void checkUserExistence(String username){
        firestore.collection("gyms").document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Toast.makeText(getApplicationContext(),"User already exists!",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUpAdminActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else {
                                createUser(gymId,adminName,gymName,branchName,username,password);
                            }
                        }
                    }
                });
    }

    public void createUser(String gymId,String adminName,String gymName,String branchName,String username,String password){
        if(adminModel !=null){
            adminModel.setAdminName(adminName);
            adminModel.setGymName(gymName);
            adminModel.setGymId(gymId);
            adminModel.setBranch(branchName);
            adminModel.setAdminUsername(username);
            adminModel.setAdminPassword(password);
        }else {
            adminModel = new AdminModel(username,password,adminName,gymName,gymId,branchName);
        }
//        MembersListFragment.setgymid(gymId);

        firestore.collection("gyms").document(username).set(adminModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Account created!",Toast.LENGTH_LONG).show();
//                editor.putString("username",username);
//                editor.putString("control","admin");
//                editor.apply();
                Intent intent = new Intent(SignUpAdminActivity.this, AdminActivity.class);
                intent.putExtra("username",adminModel.getAdminUsername());
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

          }
        });
    }
}