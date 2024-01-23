package com.example.fitsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MemberFragment2 extends Fragment {
CalendarView calendar;
EditText notepad;
Button addnote;
static String gymid,username,date;
FirebaseFirestore Db = FirebaseFirestore.getInstance();
Calendar cal = Calendar.getInstance();
    public MemberFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_member2, container, false);
        calendar=view.findViewById(R.id.calendar_view);
        notepad=view.findViewById(R.id.notepad_view);
        addnote=view.findViewById(R.id.addnote_view);

        date =  cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH)+1) +
                "-" + cal.get(Calendar.YEAR);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i2+"-"+(i1+1)+"-"+i;
                getNote();
            }
        });

        addnote.setOnClickListener(view1 -> {
            Map<String,Object> note = new HashMap<>();
            note.put("note",notepad.getText().toString());
            Db.collection("/gymIDs/"+gymid+"/Member")
                    .document(username).collection("notes").document(date).set(note)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Note Added", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        return view;
    }

    void getNote() {
        Db.collection("/gymIDs/"+gymid+"/Member")
                .document(username).collection("notes").document(date).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            notepad.setText(documentSnapshot.getString("note"));
                        }
                    }
                });
    }

    static void getdetails(String g,String u){
        gymid = g;
        username = u;
    };

}