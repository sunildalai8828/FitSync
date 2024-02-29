package com.example.fitsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminOverviewFragment extends Fragment {
    public AdminOverviewFragment() {
        // Required empty public constructor
    }

    BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin1, container, false);
        bottomNavigationView = view.findViewById(R.id.stat_nav);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame,new MembersListFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.members) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame,new MembersListFragment()).commit();
            } else if (item.getItemId() == R.id.earnings) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame,new EarningsFragment()).commit();
            }
            return true;
        });


        return view;
    }

}