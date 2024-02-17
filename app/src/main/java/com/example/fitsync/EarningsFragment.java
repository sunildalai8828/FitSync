package com.example.fitsync;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.MemberModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class EarningsFragment extends Fragment {

    public EarningsFragment() {
        // Required empty public constructor
    }

    GraphView graphView;
    TextView yearSelection;
    String year;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    List<Integer> monthlyEarnings = new ArrayList<>();
    static String gymId;
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);
        yearSelection = view.findViewById(R.id.yearSelection);
        graphView = view.findViewById(R.id.earningsGraph);

        findEarnings();

        yearSelection.setOnClickListener(view1 -> {
            showNumberPickerDialog();
        });

        return view;
    }

    private void findEarnings() {
        firestore.collection("gymIDs").document(gymId).collection("Member")
                .whereEqualTo("dateOfJoining.year","2024").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            MemberModel memberModel = documentSnapshot.toObject(MemberModel.class);
                            monthlyEarnings.add(Integer.valueOf(memberModel.getPayment()));
                        }
                        Toast.makeText(getContext(), ""+monthlyEarnings.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setGraphView() {
        List<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < monthlyEarnings.size(); i++) {
            dataPoints.add(new DataPoint(i, monthlyEarnings.get(i)));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints.toArray(new DataPoint[0]));

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int index = (int) value;
                    if (index >= 0 && index < months.length) {
                        return months[index];
                    }
                }
                return super.formatLabel(value, isValueX);
            }
        });

        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(months.length - 1);
        graphView.getViewport().setXAxisBoundsManual(true);

        graphView.setTitle("Monthly Earnings");

        GridLabelRenderer gridLabelRenderer = graphView.getGridLabelRenderer();
        gridLabelRenderer.setHorizontalAxisTitle("Months");
        gridLabelRenderer.setVerticalAxisTitle("Earning");

        graphView.addSeries(series);
    }

    private void showNumberPickerDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.number_picker_dialog);
        NumberPicker yearPicker = dialog.findViewById(R.id.yearPicker);


        Instant instant = null;
        LocalDateTime currentDateTime = null;
        int currentYear = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = Instant.now();
            currentDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            currentYear = currentDateTime.getYear();
        }

        yearPicker.setMinValue(LoadActivity.createdYear);
        yearPicker.setMaxValue(currentYear);

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                year = String.valueOf(oldVal);
                year = String.valueOf(newVal);
                yearSelection.setText("Year : " + year);
                setGraphView();
            }
        });
        dialog.show();


    }

}