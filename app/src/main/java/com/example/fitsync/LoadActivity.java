package com.example.fitsync;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitsync.models.AdminModel;
import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerModel;
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
                getAdminDetails();
                break;

            case "Member":
                getMemberDetails();
                break;

            case "Trainer":
                getTrainerDetails();
                break;
        }
    }

    void getAdminDetails() {
        firestore.collection("gyms").document(username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        AdminModel adminModel = documentSnapshot.toObject(AdminModel.class);
                        MembersListFragment.gym_Id = adminModel.getGymId();
                        AdminFragment3.username=adminModel.getAdminUsername();
                        AdminComplaintBoxActivity.gym_id = adminModel.getGymId();
                        AdminFragment4.gym_id = adminModel.getGymId();
                        intent = new Intent(LoadActivity.this,AdminActivity.class);
                        startActivity(intent);
                    }
                });
    }

    void getMemberDetails() {
        firestore.collection("gymIDs").document(password)
                .collection("Member").document(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                        MemberFragment1.gymId = password;
                        MemberFragment1.username = memberModel.getMemberUsername();
                        ComplaintBoxActivity.gym_id = password;
                        intent = new Intent(this,MemberActivity.class);
                        startActivity(intent);
                    }
                });
    }

    void getTrainerDetails() {
        firestore.collection("gymIDs").document(password)
                .collection("Trainer").document(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        TrainerModel trainerModel = documentSnapshot.toObject(TrainerModel.class);
                        TrainerFragment2.gymId = password;
                        TrainerFragment2.username = trainerModel.getTrainerUsername();
                        ComplaintBoxActivity.gym_id = password;
                        intent = new Intent(this,TrainerActivity.class);
                        startActivity(intent);
                    }
                });
    }
}