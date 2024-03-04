package com.example.fitsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.fitsync.models.AdminModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminProfileFragment extends Fragment {
    public AdminProfileFragment() {
        // Required empty public constructor
    }
    static String username,gymName;
    TextView gymname_textview,gymid_textview,complaintbox_textview,sendmessage_textview,logout_textview;
    FirebaseFirestore firebase=FirebaseFirestore.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin3, container, false);

        gymid_textview = view.findViewById(R.id.gym_id_text_view);
        gymname_textview = view.findViewById(R.id.gym_name_text_view);
        complaintbox_textview = view.findViewById(R.id.complaint_box_text_view);
        sendmessage_textview = view.findViewById(R.id.send_message_text_view);
        logout_textview = view.findViewById(R.id.log_out_text_view);

        firebase.collection("gyms").document(username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        AdminModel adminmodel = document.toObject(AdminModel.class);
                        gymid_textview.setText("Gym Id : "+adminmodel.getGymId());
                        gymname_textview.setText(adminmodel.getAdminName());
                    }
                });

        complaintbox_textview.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), AdminComplaintBoxActivity.class);
            startActivity(intent);
        });

        sendmessage_textview.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), SendMessageActivity.class);
            getActivity().startActivity(intent);
        });

        logout_textview.setOnClickListener(view1 -> {
            LoginActivity.sharedPreferences.edit().remove("username").apply();
            LoginActivity.sharedPreferences.edit().remove("password").apply();
            LoginActivity.sharedPreferences.edit().remove("usertype").apply();
            LoginActivity.sharedPreferences.edit().remove("logged").apply();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(gymid_textview.getText().toString());
            FirebaseMessaging.getInstance().unsubscribeFromTopic(gymid_textview.getText().toString()+"admin");
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
        return view;
    }
    
}