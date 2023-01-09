package com.example.lab8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView resultView;
    private EditText editText;
    private RadioButton usdButton, eurButton, gbpButton, mdlButton;
    private String jsonURL = "http://www.floatrates.com/daily/ron.json";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = findViewById(R.id.resultView);
        editText = findViewById(R.id.editTextNumber);
        usdButton = findViewById(R.id.usdButton);
        eurButton = findViewById(R.id.eurButton);
        gbpButton = findViewById(R.id.gbpButton);
        mdlButton = findViewById(R.id.mdlButton);
    }

    public void onClickExchange(View view) {

        if(editText.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "No value to exchange", Toast.LENGTH_SHORT).show();
        } else {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(jsonURL).build();


            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String recData = response.body().string();
                    Log.i(TAG, "onResponse: " + recData);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject json = new JSONObject(recData);
                                Double value = 0.0;
                                String currency = "";
                                if (usdButton.isChecked()) {
                                    currency = "USD";
                                    value = Double.parseDouble(json.getJSONObject("usd").getString("rate")) * Double.parseDouble(editText.getText().toString());
                                }
                                if (eurButton.isChecked()) {
                                    currency = "EUR";
                                    value = Double.parseDouble(json.getJSONObject("eur").getString("rate")) * Double.parseDouble(editText.getText().toString());
                                }
                                if (gbpButton.isChecked()) {
                                    currency = "GBP";
                                    value = Double.parseDouble(json.getJSONObject("gbp").getString("rate")) * Double.parseDouble(editText.getText().toString());
                                }
                                if (mdlButton.isChecked()) {
                                    currency = "MDL";
                                    value = Double.parseDouble(json.getJSONObject("mdl").getString("rate")) * Double.parseDouble(editText.getText().toString());
                                }
                                if(value != 0.0) {
                                    int temp = (int) (value * 100.0);
                                    value = ((double) temp) / 100.0;
                                    resultView.setText(value.toString() + " " + currency);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }
}