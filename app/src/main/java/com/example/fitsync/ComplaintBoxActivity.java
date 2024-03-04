package com.example.fitsync;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ComplaintBoxActivity extends AppCompatActivity {

    EditText complaint_edit_text;
    Button submit_button;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    static String gym_id,member_name,gymName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_box);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.6));

        complaint_edit_text = findViewById(R.id.complaint_edit_text);
        submit_button = findViewById(R.id.submit_button);

        submit_button.setOnClickListener(view -> {
            Map<String,String> complaint = new HashMap<>();
            complaint.put("complaint",complaint_edit_text.getText().toString());
            complaint.put("member",member_name);

            firestore.collection("gymIDs").document(gym_id).collection("Complaints")
                    .add(complaint).addOnCompleteListener(task -> {
                        Toast.makeText(this, "Complaint Sent", Toast.LENGTH_SHORT).show();
                        sendNotification("New Complaint");
                        finish();
                    });
        });
    }

    public void sendNotification(String message) {
        JSONObject jsonObject = new JSONObject();
        JSONObject notificationObj = new JSONObject();
        try {
            notificationObj.put("title",gymName);
            notificationObj.put("body",message);

            jsonObject.put("notification",notificationObj);
            jsonObject.put("to","/topics/"+gym_id+"admin");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        callApi(jsonObject);
    }

    public void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer "+BuildConfig.notificationApiKey)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

        Toast.makeText(ComplaintBoxActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
    }
}