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
                            List<String> data = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                String complaint = documentSnapshot.getString("complaint");
                                data.add(complaint);
                            }
                            AdapterClass adapterClass = new AdapterClass(getApplicationContext(),data);
                            listView.setAdapter(adapterClass);
                        }else {
                            Toast.makeText(getApplicationContext(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public static class AdapterClass extends ArrayAdapter<String> {

        public AdapterClass(@NonNull Context context, List<String>data) {
            super(context, R.layout.complaint_layout,data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView==null){
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.complaint_layout,parent
                        ,false);
            }
            TextView complaint_textview = convertView.findViewById(R.id.complaint_display);

            String complaint = getItem(position);

            if (complaint!=null){
                complaint_textview.setText("\""+complaint+"\"");
            }
            return convertView;
        }
    }
}