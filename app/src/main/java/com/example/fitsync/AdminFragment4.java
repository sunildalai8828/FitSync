package com.example.fitsync;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerModel;
import com.example.fitsync.models.TrainerSubscriptionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFragment4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment4 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminFragment4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminFragment4.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminFragment4 newInstance(String param1, String param2) {
        AdminFragment4 fragment = new AdminFragment4();
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

    ListView listView;
    static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    static String gym_id;
    static List<String> memberNames = new ArrayList<>();
    static List<String> memberUsernames = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin4, container, false);

        listView = view.findViewById(R.id.trainer_list);

        if (!memberNames.contains("Select Member")) {
            memberNames.add("Select Member");
        }

        retriveMemberNames();
        retriveTrainers();

        return view;
    }

    private void retriveMemberNames() {
        firestore.collection("gymIDs/"+gym_id+"/Member")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                                String fullName = memberModel.getFirstName()+" "+memberModel.getLastName();
                                if (!memberNames.contains(fullName)) {
                                    memberNames.add(fullName);
                                }
                                if (!memberUsernames.contains(memberModel.getMemberUsername())) {
                                    memberUsernames.add(memberModel.getMemberUsername());
                                }
                            }
                        }
                    }
                });
    }

    private void retriveTrainers() {
        firestore.collection("gymIDs/"+gym_id+"/Trainer")
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
                            AdapterClass adapterClass = new AdapterClass(getContext(),data);
                            listView.setAdapter(adapterClass);
                        }else {
                            Toast.makeText(getActivity(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    static void findMember(TrainerSubscriptionModel trainerSubscriptionModel,String memberName,
                           TrainerModel trainerModel) {
        String memberNameParts[] = memberName.split(" ");
        firestore.collection("gymIDs/"+gym_id+"/Member")
                .whereEqualTo("firstName",memberNameParts[0])
                .whereEqualTo("lastName",memberNameParts[1])
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                                firestore.collection("gymIDs/"+gym_id+"/Member")
                                        .document(documentSnapshot.getId())
                                        .update("trainerSubscriptionPlan",trainerSubscriptionModel)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                assignMemberToTrainer(documentSnapshot.getId(),memberModel,trainerModel);
                            }
                        }
                    }
                });
    }

    static void assignMemberToTrainer(String documentId,MemberModel memberModel,TrainerModel trainerModel) {
        firestore.collection("gymIDs/").document(gym_id).collection("Trainer")
                .document(trainerModel.getTrainerUsername()).collection("AssignedMember")
                .document(documentId).set(memberModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
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
            Spinner membersList = convertView.findViewById(R.id.members_list);
            Button assignMember = convertView.findViewById(R.id.assign_member);
            RadioButton offlineButton = convertView.findViewById(R.id.offline_button);
            RadioButton onlineButton = convertView.findViewById(R.id.online_button);
            Spinner plans = convertView.findViewById(R.id.trainer_plans);
            final String[] modeOfPayment = {null};
            final Boolean[] paymentStatus = new Boolean[1];


            assignMemberLayout.setVisibility(View.GONE);
            TrainerModel currentTrainerModel = getItem(position);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item,memberNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            membersList.setAdapter(adapter);

            if (currentTrainerModel!=null){
                trainerName.setText(currentTrainerModel.getFirstName()+" "+currentTrainerModel.getLastName());
                experience.setText(currentTrainerModel.getExperience());
                mobileNumber.setText(currentTrainerModel.getTrainerUsername());
            }

            addMember.setOnClickListener(view -> {
                switch (assignMemberLayout.getVisibility()) {
                    case View.VISIBLE:
                        assignMemberLayout.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                        assignMemberLayout.setVisibility(View.VISIBLE);
                        break;
                }
            });


            assignMember.setOnClickListener(view -> {
                String memberName = membersList.getSelectedItem().toString();
                String trainerPlan = plans.getSelectedItem().toString();
                Double amount = calculatePayment(trainerPlan);
                if (memberName.equals("Select Member")) {
                    Toast.makeText(getContext(), "Please Select a Member", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (offlineButton.isChecked()) {
                    modeOfPayment[0] = offlineButton.getText().toString();
                    paymentStatus[0] = true;
                } else if (onlineButton.isChecked()) {
                    modeOfPayment[0] = onlineButton.getText().toString();
                    paymentStatus[0] = false;
                } else if (modeOfPayment[0].isEmpty()) {
                    Toast.makeText(getContext(), "Please Select Mode of Payment", Toast.LENGTH_SHORT).show();
                }


                TrainerSubscriptionModel trainerSubscriptionModel = new TrainerSubscriptionModel(currentTrainerModel,
                       trainerPlan,modeOfPayment[0],amount,paymentStatus[0]);

                findMember(trainerSubscriptionModel,memberName,currentTrainerModel);

                Toast.makeText(getContext(), "Trainer Added Successfully", Toast.LENGTH_SHORT).show();
            });
            return convertView;
        }

        private Double calculatePayment(String plan) {
            switch (plan) {
                case "1 Month":
                    return 1000.0;
                case "3 Month":
                    return 2000.0;
                case "6 Month":
                    return 5000.0;
                case "9 Month":
                    return 7000.0;
                case "12 Month":
                    return 9000.0;
                default:
                    Toast.makeText(getContext(), "Please Select Membership Plan", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }
}