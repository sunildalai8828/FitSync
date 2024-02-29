package com.example.fitsync;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerSubscriptionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrainerClientFragment extends Fragment {

    public TrainerClientFragment() {
        // Required empty public constructor
    }

    static String username,gymId;
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

        firestore.collection("gymIDs/"+gymId+"/Trainer/"
                        +username+"/AssignedMember")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<MemberModel> data = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                noMembers.setVisibility(View.GONE);
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

    public class AdapterClass extends ArrayAdapter<MemberModel> {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
            TextView DateOfJoin = convertView.findViewById(R.id.member_doj_view);
            TextView DateOfEnd = convertView.findViewById(R.id.member_doe_view);
            Button Remove = convertView.findViewById(R.id.removeMember);

            MemberModel currentMemberModel = getItem(position);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            if (currentMemberModel!=null){
                TrainerSubscriptionModel currentTrainerSubscriptionPlan = currentMemberModel.getTrainerSubscriptionPlan();
                MemberName.setText("Member Name : "+currentMemberModel.getFirstName()+" "
                        +currentMemberModel.getLastName());
                Gender.setText("Gender : "+currentMemberModel.getGender());
                MobileNumber.setText("Phone Number : "+currentMemberModel.getMemberUsername());
                if (currentTrainerSubscriptionPlan!=null) {
                    DateOfJoin.setText("Date Of Joining : "+currentTrainerSubscriptionPlan.getDateOfJoining());
                    DateOfEnd.setText("Date Of Ending : "+currentTrainerSubscriptionPlan.getDateOfEnding());
                }
                Remove.setVisibility(View.GONE);

                if (currentTrainerSubscriptionPlan!=null) {
                    if (currentTrainerSubscriptionPlan.getDateOfEnding().equals(currentDate)) {
                        firestore.collection("gymIDs").document(gymId).collection("Member")
                                .document(currentMemberModel.getMemberUsername())
                                .update("trainerSubscriptionPlan", null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        firestore.collection("gymIDs").document(gymId).collection("Trainer")
                                                .document(username).collection("AssignedMember")
                                                .document(currentMemberModel.getMemberUsername()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                    }
                                });
                    }
                }
            }
            return convertView;
        }
    }
}