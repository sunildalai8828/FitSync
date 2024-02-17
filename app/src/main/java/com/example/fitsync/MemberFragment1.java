package com.example.fitsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.AdminModel;
import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerSubscriptionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberFragment1 extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MemberFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberFragment1 newInstance(String param1, String param2) {
        MemberFragment1 fragment = new MemberFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView member_name_textview,gymid_textview,gymname_textview,trainer_subscription_textview,
            complaint_box_textview,logout_textview;
    FirebaseFirestore firebase=FirebaseFirestore.getInstance();
    static String gymId,username;
    String fullName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member1, container, false);

        member_name_textview = view.findViewById(R.id.member_name);
        gymname_textview = view.findViewById(R.id.gym_name);
        gymid_textview = view.findViewById(R.id.gym_id);
        trainer_subscription_textview = view.findViewById(R.id.trainer_subscription);
        complaint_box_textview = view.findViewById(R.id.complain_box_view);
        logout_textview = view.findViewById(R.id.logout_member);

        findGymName();

        firebase.collection("gymIDs").document(gymId).collection("Member")
                .document(username).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                            TrainerSubscriptionModel trainerSubscriptionModel = memberModel.getTrainerSubscriptionPlan();
                            member_name_textview.setText(memberModel.getFirstName()+" "+memberModel.getLastName());
                            gymid_textview.setText(gymId);
                            TrainerSubscriptionActivity.fullName = memberModel.getFirstName() + " " + memberModel.getLastName();
                            TrainerSubscriptionActivity.phone = memberModel.getMemberUsername();

                        }
                    }
                });

        trainer_subscription_textview.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), TrainerSubscriptionActivity.class);
            startActivity(intent);
        });

        complaint_box_textview.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), ComplaintBoxActivity.class);
            startActivity(intent);
        });

        logout_textview.setOnClickListener(view1 -> {
            LoginActivity.sharedPreferences.edit().remove("username").apply();
            LoginActivity.sharedPreferences.edit().remove("password").apply();
            LoginActivity.sharedPreferences.edit().remove("usertype").apply();
            LoginActivity.sharedPreferences.edit().remove("logged").apply();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }

    void findGymName() {
        firebase.collection("gyms").whereEqualTo("gymId",gymId).get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        AdminModel adminModel = documentSnapshot.toObject(AdminModel.class);
                        gymname_textview.setText("Gym Name : "+adminModel.getGymName());
                    }
                });
    }
}