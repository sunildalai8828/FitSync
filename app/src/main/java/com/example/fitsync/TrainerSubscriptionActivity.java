package com.example.fitsync;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerModel;
import com.example.fitsync.models.TrainerSubscriptionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrainerSubscriptionActivity extends AppCompatActivity implements PaymentResultListener {

    ListView listView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    static String gym_Id,fullName,phone;

    String modeOfPayment,amount;
    Boolean paymentStatus;
    TextView trainer_info;
    Button payment_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_subscription);
        listView = findViewById(R.id.trainers_list);
        trainer_info = findViewById(R.id.trainer_info);
        payment_button = findViewById(R.id.paymentButton);

        payment_button.setVisibility(View.GONE);

        firestore.collection("gymIDs/"+gym_Id+"/Trainer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<TrainerModel> data = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                TrainerModel trainerModel = documentSnapshot.toObject(TrainerModel.class);
                                data.add(trainerModel);
                            }
                            AdapterClass adapterClass = new AdapterClass(getApplicationContext(),data);
                            listView.setAdapter(adapterClass);
                        }else {
                            Toast.makeText(getApplicationContext(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        checkForPayment();

        payment_button.setOnClickListener(view -> {
            payFirst(fullName,phone,amount);
        });
    }

    void checkForPayment() {
        firestore.collection("gymIDs").document(gym_Id).collection("Member")
                .document(phone).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                        TrainerSubscriptionModel trainerSubscriptionModel = memberModel.getTrainerSubscriptionPlan();
                        if (trainerSubscriptionModel!=null) {
                            modeOfPayment = trainerSubscriptionModel.getModeOfPayment();
                            paymentStatus = trainerSubscriptionModel.getPaymentStatus();
                            amount = String.valueOf(trainerSubscriptionModel.getPayment());
                            if (modeOfPayment.equals("Online") && paymentStatus == false) {
                                trainer_info.setText("*Pay For Your Trainer Subscription*");
                                payment_button.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }


    void payFirst(String fullname, String phonenumber, String amount) {
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

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
        payment_button.setVisibility(View.GONE);
        firestore.collection("gymIDs").document(gym_Id).collection("Member")
                .document(phone).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                        TrainerSubscriptionModel trainerSubscriptionModel = memberModel.getTrainerSubscriptionPlan();
                        trainerSubscriptionModel.setPaymentStatus(true);
                        firestore.collection("gymIDs").document(gym_Id)
                                .collection("Member").document(phone)
                                .update("trainerSubscriptionPlan", trainerSubscriptionModel)
                                .addOnCompleteListener(task12 -> {
                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Payment Failed", Toast.LENGTH_SHORT).show();
    }

    public static class AdapterClass extends ArrayAdapter<TrainerModel> {

        public AdapterClass(@NonNull Context context, List<TrainerModel>data) {
            super(context, R.layout.trainer_list_layout,data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView==null){
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.trainer_list_layout,parent
                        ,false);
            }
            TextView trainerName = convertView.findViewById(R.id.trainer_name_display);
            TextView experience = convertView.findViewById(R.id.trainer_experience_display);
            TextView mobileNumber = convertView.findViewById(R.id.trainer_phone_display);
            RelativeLayout assignMemberLayout = convertView.findViewById(R.id.assign_member_layout);
            Button addMember = convertView.findViewById(R.id.add_member);

            TrainerModel currentTrainerModel = getItem(position);

            assignMemberLayout.setVisibility(View.GONE);
            addMember.setVisibility(View.GONE);

            if (currentTrainerModel!=null){
                trainerName.setText(currentTrainerModel.getFirstName()+" "+currentTrainerModel.getLastName());
                experience.setText(currentTrainerModel.getExperience());
                mobileNumber.setText(currentTrainerModel.getTrainerUsername());
            }
            return convertView;
        }
    }
}