package com.example.fitsync;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MembersListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MembersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MembersListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MembersListFragment newInstance(String param1, String param2) {
        MembersListFragment fragment = new MembersListFragment();
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

    ListView memberlistView;
    ListView trainerListView;
    static FirebaseFirestore db;
    static String gym_Id;
    ImageButton memberListButton,trainerListButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_members_list, container, false);
        memberlistView = view.findViewById(R.id.memberslist);
        trainerListView = view.findViewById(R.id.trainerslist);
        memberListButton = view.findViewById(R.id.member_list_button);
        trainerListButton = view.findViewById(R.id.trainer_list_button);
        db=FirebaseFirestore.getInstance();

        Toast.makeText(getContext(), "GYM : "+gym_Id, Toast.LENGTH_SHORT).show();
        getMembersData();
        getTrainersData();

        memberListButton.setOnClickListener(view1 -> {
            switch (memberlistView.getVisibility()) {
                case View.GONE:
                    memberlistView.setVisibility(View.VISIBLE);
                    break;
                case View.VISIBLE:
                    memberlistView.setVisibility(View.GONE);
                    break;
            }
        });
        trainerListButton.setOnClickListener(view1 -> {
            switch (trainerListView.getVisibility()) {
                case View.GONE:
                    trainerListView.setVisibility(View.VISIBLE);
                    break;
                case View.VISIBLE:
                    trainerListView.setVisibility(View.GONE);
                    break;
            }
        });
        return view;
    }

    private void getTrainersData() {
        db.collection("gymIDs/"+gym_Id+"/Trainer")
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
                            TrainerAdapterClass trainerAdapterClass = new TrainerAdapterClass(getContext(),data);
                            trainerListView.setAdapter(trainerAdapterClass);
                        }else {
                            Toast.makeText(getActivity(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getMembersData(){
        db.collection("gymIDs/"+gym_Id+"/Member")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<MemberModel> data = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                                data.add(memberModel);
                            }
                            MemberAdapterClass memberAdapterClass = new MemberAdapterClass(getContext(),data);
                            memberlistView.setAdapter(memberAdapterClass);
                        }else {
                            Toast.makeText(getActivity(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public static class MemberAdapterClass extends ArrayAdapter<MemberModel>{

        public MemberAdapterClass(@NonNull Context context, List<MemberModel>data) {
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

            if (currentMemberModel!=null){
                MemberName.setText("Member Name : "+currentMemberModel.getFirstName()+" "
                        +currentMemberModel.getLastName());
                Gender.setText("Gender : "+currentMemberModel.getGender());
                MobileNumber.setText("Phone Number : "+currentMemberModel.getMemberUsername());
                DateOfJoin.setText("Date Of Joining : "+currentMemberModel.getDateOfJoining().get("date"));
                DateOfEnd.setText("Date Of Ending : "+currentMemberModel.getDateOfEnding());
            }

            Remove.setOnClickListener(view -> {
                MembersListFragment.db.collection("gymIDs")
                        .document(MembersListFragment.gym_Id).collection("Member")
                        .document(currentMemberModel.getMemberUsername()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            return convertView;
        }
    }

    public static class TrainerAdapterClass extends ArrayAdapter<TrainerModel>{

        public TrainerAdapterClass(@NonNull Context context, List<TrainerModel>data) {
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
            TextView TrainerName = convertView.findViewById(R.id.member_name_view);
            TextView Gender = convertView.findViewById(R.id.member_gender_view);
            TextView MobileNumber = convertView.findViewById(R.id.member_mobile_view);
            TextView DateOfJoin = convertView.findViewById(R.id.member_doj_view);
            TextView DateOfEnd = convertView.findViewById(R.id.member_doe_view);
            Button Remove = convertView.findViewById(R.id.removeMember);

            TrainerModel currentTrainerModel = getItem(position);

            if (currentTrainerModel!=null){
                TrainerName.setText("Trainer Name : "+currentTrainerModel.getFirstName()+" "
                        +currentTrainerModel.getLastName());
                Gender.setText("Gender : "+currentTrainerModel.getGender());
                MobileNumber.setText("Phone Number : "+currentTrainerModel.getTrainerUsername());
                DateOfJoin.setText("Date Of Joining : "+currentTrainerModel.getDateOfJoining());
                DateOfEnd.setVisibility(View.GONE);
            }

            Remove.setOnClickListener(view -> {
                MembersListFragment.db.collection("gymIDs")
                        .document(MembersListFragment.gym_Id).collection("Member")
                        .document(currentTrainerModel.getTrainerUsername()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            return convertView;
        }
    }
}