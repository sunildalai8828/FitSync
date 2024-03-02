package com.example.fitsync;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMessageActivity extends AppCompatActivity {

    static String gymId,gymName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.4));

        EditText messageText = findViewById(R.id.send_message_textview);
        Button sendButton = findViewById(R.id.send_button);


        sendButton.setOnClickListener(view2 -> {
            if (messageText.getText().toString().isEmpty()) {
                messageText.setError("Message should not be empty");
                return;
            }
            sendNotification(messageText.getText().toString());
            finish();
        });

    }
    public void sendNotification(String message) {
        JSONObject jsonObject = new JSONObject();
        JSONObject notificationObj = new JSONObject();
        try {
            notificationObj.put("title",gymName);
            notificationObj.put("body",message);

            jsonObject.put("notification",notificationObj);
            jsonObject.put("to","/topics/"+gymId);
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

        Toast.makeText(SendMessageActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
    }
}