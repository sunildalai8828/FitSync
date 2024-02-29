package com.example.fitsync;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminComplaintBoxActivity extends AppCompatActivity {

    ListView listView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    static String gym_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaint_box);

        listView = findViewById(R.id.complaint_list);
        getdata();
    }

    private void getdata(){
        firestore.collection("gymIDs/"+gym_id+"/Complaints")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<Map<String,Object>> data = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                Map<String,Object> documentData = documentSnapshot.getData();
                                data.add(documentData);
                            }
                            AdapterClass adapterClass = new AdapterClass(AdminComplaintBoxActivity.this,
                                    data);
                            listView.setAdapter(adapterClass);
                        }else {
                            Toast.makeText(getApplicationContext(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class AdapterClass extends ArrayAdapter<Map<String,Object>> {

        public AdapterClass(@NonNull Context context, List<Map<String,Object>> data) {
            super(context, R.layout.complaint_layout, data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView==null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.complaint_layout,parent,false);
            }

            TextView complaint = convertView.findViewById(R.id.complaint_display);
            TextView name = convertView.findViewById(R.id.name_display);

            Map<String,Object> currentData = getItem(position);

            complaint.setText("\"" + currentData.get("complaint").toString() + "\"");
            name.setText("- " + currentData.get("member").toString());

            return convertView;
        }
    }
}