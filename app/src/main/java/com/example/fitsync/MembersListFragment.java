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

    ListView listView;
    FirebaseFirestore db;
    static String gym_Id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_members_list, container, false);
        listView = view.findViewById(R.id.memberslist);
        db=FirebaseFirestore.getInstance();

        Toast.makeText(getContext(), "GYM : "+gym_Id, Toast.LENGTH_SHORT).show();
        getdata();
        return view;
    }

    private void getdata(){
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
                            AdapterClass adapterClass = new AdapterClass(getContext(),data);
                            listView.setAdapter(adapterClass);
                        }else {
                            Toast.makeText(getActivity(),"ERROR"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public static class AdapterClass extends ArrayAdapter<MemberModel>{

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