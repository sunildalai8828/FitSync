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
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.MemberModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrainerFragment1 extends Fragment {

    public TrainerFragment1() {
        // Required empty public constructor
    }

    ListView listView;
    TextView noMembers;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trainer1, container, false);

        listView = view.findViewById(R.id.client_overview);
        noMembers = view.findViewById(R.id.noAssignedMemberTV);

        firestore.collection("gymIDs/"+TrainerFragment2.gymId+"/Trainer/"
                        +TrainerFragment2.username+"/AssignedMember")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            noMembers.setVisibility(View.GONE);
                            List<MemberModel> data = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                                data.add(memberModel);
                            }
                            AdapterClass adapterClass = new AdapterClass(getContext(),data);
                            listView.setAdapter(adapterClass);
                        }else {
                            Toast.makeText(getActivity(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }

    public static class AdapterClass extends ArrayAdapter<MemberModel> {

        public AdapterClass(@NonNull Context context, List<MemberModel>data) {
            super(context, R.layout.list_layout,data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView==null){
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_layout,parent
                        ,false);
            }
            TextView MemberName = convertView.findViewById(R.id.member_name_view);
            TextView Gender = convertView.findViewById(R.id.member_gender_view);
            TextView MobileNumber = convertView.findViewById(R.id.member_mobile_view);

            MemberModel currentMemberModel = getItem(position);

            if (currentMemberModel!=null){
                MemberName.setText(currentMemberModel.getFirstName()+" "+currentMemberModel.getLastName());
                Gender.setText(currentMemberModel.getGender());
                MobileNumber.setText(currentMemberModel.getMemberUsername());
            }
            return convertView;
        }
    }
}