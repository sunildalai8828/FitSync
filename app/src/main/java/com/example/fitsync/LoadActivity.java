package com.example.fitsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitsync.models.AdminModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadActivity extends AppCompatActivity {

    String username,password,userType;
    Intent intent;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        userType = getIntent().getStringExtra("usertype");

        switch (userType) {
            case "Admin":
                getDetails();
                break;

            case "Member":
                intent = new Intent(this,MemberActivity.class);
                startActivity(intent);
                break;

            case "Trainer":
                intent = new Intent(this,TrainerActivity.class);
                startActivity(intent);
                break;
        }
    }

    void getDetails() {
        firestore.collection("gyms").document(username).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            AdminModel adminModel = documentSnapshot.toObject(AdminModel.class);
                            Toast.makeText(LoadActivity.this, ""+adminModel.getGymId(), Toast.LENGTH_SHORT).show();
                            MembersListFragment.gym_Id = adminModel.getGymId();
                            AdminFragment3.username=adminModel.getAdminUsername();
                            intent = new Intent(LoadActivity.this,AdminActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
}