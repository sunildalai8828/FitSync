package com.example.fitsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.AdminModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminFragment3 extends Fragment {
    public AdminFragment3() {
        // Required empty public constructor
    }
    static String username;
    TextView gymname_textview , gymid_textview;
    FirebaseFirestore firebase=FirebaseFirestore.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin3, container, false);
        gymid_textview = view.findViewById(R.id.gym_id_text_view);
        gymname_textview = view.findViewById(R.id.gym_name_text_view);
        firebase.collection("gyms").document(username).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            AdminModel adminmodel = document.toObject(AdminModel.class);
                        }
                    }
                })

        return view;
    }
}