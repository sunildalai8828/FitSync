package com.example.fitsync;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ComplaintBoxActivity extends AppCompatActivity {

    EditText complaint_edit_text;
    Button submit_button;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    static String gym_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_box);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.6));

        complaint_edit_text = findViewById(R.id.complaint_edit_text);
        submit_button = findViewById(R.id.submit_button);

        submit_button.setOnClickListener(view -> {
            Map<String,Object> complaint = new HashMap<>();
            complaint.put("complaint",complaint_edit_text.getText().toString());

            firestore.collection("gymIDs").document(gym_id).collection("Complaints")
                    .add(complaint).addOnCompleteListener(task -> {
                        Toast.makeText(this, "Complaint Sent", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        });
    }
}