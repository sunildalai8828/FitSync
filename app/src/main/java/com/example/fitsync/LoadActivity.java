package com.example.fitsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

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
                        EarningsFragment.gymId = adminModel.getGymId();
                        AdminFragment2.gymID = adminModel.getGymId();
                        getGymName(adminModel.getGymId());
                        createdYear = SignUpAdminActivity.createdYear;
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
                        TrainerSubscriptionActivity.gym_Id = password;

                        if (memberModel.getModeOfPayment().equals("Online") && memberModel.getPaymentStatus().equals(false)) {
                            payFirst(memberModel.getFirstName()+" "+memberModel.getLastName(),
                                    memberModel.getMemberUsername(),
                                    memberModel.getPayment());
                        } else {
                            intent = new Intent(this,MemberActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void getGymName(String gymId) {
        firestore.collection("gyms").whereEqualTo("gymId",gymId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()) {
                            AdminModel adminModel = d.toObject(AdminModel.class);
                            AdminFragment2.gymName = adminModel.getGymName();
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
                        TrainerFragment2.gymId = password;
                        TrainerFragment2.username = trainerModel.getTrainerUsername();
                        ComplaintBoxActivity.gym_id = password;
                        intent = new Intent(this,TrainerActivity.class);
                        startActivity(intent);
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
                });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}