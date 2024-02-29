package com.example.fitsync;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fitsync.models.AdminModel;
import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadActivity extends AppCompatActivity implements PaymentResultListener {

    String username,password,userType;
    Intent intent;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    static int createdYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        userType = getIntent().getStringExtra("usertype");

        checkSMSPermission();
    }

    void getAdminDetails() {
        firestore.collection("gyms").document(username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        AdminModel adminModel = documentSnapshot.toObject(AdminModel.class);

                        FirebaseMessaging.getInstance().subscribeToTopic(adminModel.getGymId());
                        FirebaseMessaging.getInstance().subscribeToTopic("admin");

                        MembersListFragment.gym_Id = adminModel.getGymId();
                        AdminProfileFragment.username=adminModel.getAdminUsername();
                        AdminComplaintBoxActivity.gym_id = adminModel.getGymId();
                        AdminAssignFragment.gym_id = adminModel.getGymId();
                        EarningsFragment.gymId = adminModel.getGymId();
                        AdminAddFragment.gymID = adminModel.getGymId();
                        SendMessageActivity.gymName = adminModel.getGymName();
                        SendMessageActivity.gymId = adminModel.getGymId();
                        getGymName(adminModel.getGymId());
                        createdYear = SignUpAdminActivity.createdYear;
                        intent = new Intent(LoadActivity.this,AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    void getMemberDetails() {
        firestore.collection("gymIDs").document(password)
                .collection("Member").document(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);

                        FirebaseMessaging.getInstance().subscribeToTopic(memberModel.getMemberPassword());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = dateFormat.format(new Date());

                        MemberProfileFragment.gymId = password;
                        MemberProfileFragment.username = memberModel.getMemberUsername();
                        MemberNoteFragment.gymid = password;
                        MemberNoteFragment.username = memberModel.getMemberUsername();

                        getGymName(password);

                        ComplaintBoxActivity.gym_id = password;
                        ComplaintBoxActivity.member_name = memberModel.getFirstName() +" "+ memberModel.getLastName();
                        TrainerSubscriptionActivity.gym_Id = password;

                        if (memberModel.getModeOfPayment().equals("Online") && memberModel.getPaymentStatus().equals(false)) {
                            payFirst(memberModel.getFirstName()+" "+memberModel.getLastName(),
                                    memberModel.getMemberUsername(),
                                    memberModel.getPayment());
                        }
                        else if (currentDate.equals(memberModel.getDateOfEnding())) {
                            Toast.makeText(this, "Your Member Subscription Ended", Toast.LENGTH_SHORT).show();
                            LoginActivity.sharedPreferences.edit().remove("logged").apply();
                            intent = new Intent(this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            intent = new Intent(this,MemberActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(LoadActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
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
        } else {
            ActivityCompat.requestPermissions(LoadActivity.this,new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
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
        } else {
            Toast.makeText(getApplicationContext(), "Notification Permission Denied", Toast.LENGTH_SHORT).show();
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
    }
    private void getGymName(String gymId) {
        firestore.collection("gyms").whereEqualTo("gymId",gymId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()) {
                            AdminModel adminModel = d.toObject(AdminModel.class);
                            AdminAddFragment.gymName = adminModel.getGymName();
                            ComplaintBoxActivity.gymName = adminModel.getGymName();
                        }
                    }
                });
    }

    void payFirst(String fullname,String phonenumber,String amount) {
        final Activity activity = this;

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_9V30Iow9nxYTdn");
        checkout.setImage(R.drawable.ic_launcher_background);

        double finalAmount = Float.parseFloat(amount)*100;

        try {
            JSONObject options = new JSONObject();
            options.put("name",fullname);
            options.put("image","https://s3.amazonaws.com./rzp-mobile/images/rzp.png");
            options.put("theme.color",R.color.main);
            options.put("currency","INR");
            options.put("amount",finalAmount + "");
            options.put("prefill.contact",phonenumber);

            checkout.open(activity,options);
        } catch (JSONException e) {
            Log.e("Payment","Error",e);
        }
    }

    void getTrainerDetails() {
        firestore.collection("gymIDs").document(password)
                .collection("Trainer").document(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        TrainerModel trainerModel = documentSnapshot.toObject(TrainerModel.class);

                        FirebaseMessaging.getInstance().subscribeToTopic(trainerModel.getTrainerPassword());

                        TrainerProfileFragment.gymId = password;
                        TrainerProfileFragment.username = trainerModel.getTrainerUsername();
                        ComplaintBoxActivity.gym_id = password;
                        ComplaintBoxActivity.member_name = trainerModel.getFirstName() +" "+ trainerModel.getLastName();
                        TrainerClientFragment.gymId = password;
                        TrainerClientFragment.username = trainerModel.getTrainerUsername();
                        getGymName(password);

                        intent = new Intent(this,TrainerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
        firestore.collection("gymIDs").document(password)
                .collection("Member")
                .document(username).update("paymentStatus", true)
                .addOnCompleteListener(task1 -> {
                    intent = new Intent(this,MemberActivity.class);
                    startActivity(intent);
                    finish();
                });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}