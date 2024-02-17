package com.example.fitsync;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fitsync.models.MemberModel;
import com.example.fitsync.models.TrainerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminFragment2 extends Fragment {
    Spinner spinner,plans,experience;
    EditText firstNameEditText,lastNameEditText,phoneNumberEditText,gymIDEditText,dateOfJoiningEditText;
    Button create;
    RadioGroup genderGroup,paymentGroup;
    String userType="",firstName,lastName,phoneNumber,gender="",
            dateOfJoining="",plan="",dateOfEnding,modeOfPayment="",amount,yearsOfExperience="";

    FirebaseFirestore firestore;
    MemberModel memberModel;
    Boolean paymentStatus;
    static String gymName="";
    Map<String, String> date = new HashMap<>();
    static String gymID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin2, container, false);

        firestore = FirebaseFirestore.getInstance();
        spinner = view.findViewById(R.id.spinner2);
        firstNameEditText = view.findViewById(R.id.first_name_edit_text);
        lastNameEditText = view.findViewById(R.id.last_name_edit_text);
        phoneNumberEditText = view.findViewById(R.id.phone_number_edit_text);
        gymIDEditText = view.findViewById(R.id.gym_id_edit_text);
        create = view.findViewById(R.id.create_btn);
        genderGroup = view.findViewById(R.id.gender_group);
        dateOfJoiningEditText = view.findViewById(R.id.date_join_edit_text);
        experience = view.findViewById(R.id.experience_spinner);

        plans = view.findViewById(R.id.plan_spinner);
        paymentGroup = view.findViewById(R.id.mode_of_payment_group);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userType = spinner.getSelectedItem().toString();
                if (userType.equals("Trainer")) {
                    plans.setVisibility(View.GONE);
                    paymentGroup.setVisibility(View.GONE);
                    experience.setVisibility(View.VISIBLE);
                } else {
                    plans.setVisibility(View.VISIBLE);
                    paymentGroup.setVisibility(View.VISIBLE);
                    experience.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = view.findViewById(i);
                modeOfPayment = radioButton.getHint().toString();
            }
        });

        dateOfJoiningEditText.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog d = new DatePickerDialog(
                    getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            dateOfJoining = i+"-"+(i1+1)+"-"+i2;
                            dateOfJoiningEditText.setText(dateOfJoining);
                            date.put("date", dateOfJoining);
                            date.put("year", String.valueOf(i));
                        }
                    },
                    year,month,day);
            d.show();
        });

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = view.findViewById(i);
                gender = radioButton.getHint().toString();
            }
        });

        create.setOnClickListener(v->{
            checkSMSPermission();
            plan = plans.getSelectedItem().toString();

            if (userType.equals("Trainer")) {
                yearsOfExperience = experience.getSelectedItem().toString();
                if(yearsOfExperience.isEmpty() || yearsOfExperience.equals("Select Years Of Experience")) {
                    Toast.makeText(getContext(), "Please select years of Experience!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if(plan.isEmpty()) {
                    Toast.makeText(getContext(), "Please select the plan!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(modeOfPayment.isEmpty()) {
                    Toast.makeText(getContext(), "Please select the mode of payment!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    calculatePayment();
                    if (modeOfPayment.equals("Offline")) {
                        paymentStatus = true;
                    } else if (modeOfPayment.equals("Online")) {
                        paymentStatus = false;
                    }
                }
                createDateOfEnding();
            }
            if (firstNameEditText.getText().toString().isEmpty()) {
                firstNameEditText.setError("First Name should not be empty!");
                return;
            }
            if (lastNameEditText.getText().toString().isEmpty()) {
                lastNameEditText.setError("Last Name should not be empty!");
                return;
            }
            if (phoneNumberEditText.getText().toString().isEmpty() ||
                    phoneNumberEditText.getText().toString().length()<10 ||
                    phoneNumberEditText.getText().toString().length()>10) {
                phoneNumberEditText.setError("Phone Number should not be empty!");
                phoneNumberEditText.setError("Phone Number should be proper!");
                return;
            }
            if (gymIDEditText.getText().toString().isEmpty()) {
                gymIDEditText.setError("Gym Id should not be empty!");
                return;
            }
            if(gender.isEmpty()) {
                Toast.makeText(getContext(), "Please select gender!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(userType.isEmpty()) {
                Toast.makeText(getContext(), "Please select who you want to select!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(dateOfJoining.isEmpty()) {
                Toast.makeText(getContext(), "Please select date of joining!", Toast.LENGTH_SHORT).show();
                return;
            }
            firstName = firstNameEditText.getText().toString().trim();
            lastName = lastNameEditText.getText().toString().trim();
            phoneNumber = phoneNumberEditText.getText().toString();
            checkUserExistence();
        });

        return view;
    }

    void calculatePayment() {
        switch (plan) {
            case "1 Month":
                amount = String.valueOf(1200);
                break;
            case "3 Month":
                amount = String.valueOf(2500);
                break;
            case "6 Month":
                amount = String.valueOf(4800);
                break;
            case "9 Month":
                amount = String.valueOf(7000);
                break;
            case "12 Month":
                amount = String.valueOf(10000);
                break;
            default:
                Toast.makeText(getContext(), "Please Select Membership Plan", Toast.LENGTH_SHORT).show();
        }
    }

    void createDateOfEnding() {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        try {
            startDate = sdf.parse(dateOfJoining);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        switch (plan) {
            case "1 Month":
                calendar.add(Calendar.DAY_OF_MONTH,30);
                break;
            case "3 Month":
                calendar.add(Calendar.DAY_OF_MONTH,90);
                break;
            case "6 Month":
                calendar.add(Calendar.DAY_OF_MONTH,180);
                break;
            case "9 Month":
                calendar.add(Calendar.DAY_OF_MONTH,270);
                break;
            case "12 Month":
                calendar.add(Calendar.DAY_OF_MONTH,360);
                break;
            default:
                Toast.makeText(getContext(), "Please Select Membership Plan", Toast.LENGTH_SHORT).show();
                return;
        }
        Date endDate = calendar.getTime();
        dateOfEnding = sdf.format(endDate);
    }


    public void checkUserExistence(){
        firestore.collection("/gymIDs/"+gymID+"/"+userType).document(phoneNumber)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Toast.makeText(getContext(),"User already exists!",Toast.LENGTH_LONG).show();
                            }else {
                                if (userType.equals("Trainer")) {
                                    createTrainerUser();
                                } else {
                                    createMemberUser();
                                }
                            }
                        }
                    }
                });
    }

    public void createMemberUser(){
        memberModel = new MemberModel(firstName,lastName,gender,phoneNumber,gymID,
                date,dateOfEnding,plan,modeOfPayment,amount,paymentStatus,null);

        firestore.collection("gymIDs/"+gymID+"/"+userType)
                .document(phoneNumber).set(memberModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"Account created!",Toast.LENGTH_LONG).show();
                        sendSMSToMember();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},
                    100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMSToMember() {
        String message = "Hello, " + firstName+" "+lastName + "\nGreetings From "+ gymName +
                "These are your Login Credentials for FitSync App" +
                "\nUsername : " +phoneNumber + "\nPassword : "+gymID;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null,message,null,null);
        Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
    }

    public void createTrainerUser(){
        TrainerModel trainerModel = new TrainerModel(firstName,lastName,gender,phoneNumber,gymID,dateOfJoining,yearsOfExperience);

        firestore.collection("gymIDs/"+gymID+"/"+userType)
                .document(phoneNumber).set(trainerModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"Account created!",Toast.LENGTH_LONG).show();
                        sendSMSToMember();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}