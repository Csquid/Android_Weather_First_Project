package com.example.rest_api;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView getWeatherTextView;
    private Button startButton;
    private Weather weaStart;
    private String getJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWeatherTextView = (TextView) findViewById(R.id.getWeatherTextView);
        getWeatherTextView.setMovementMethod(new ScrollingMovementMethod());
        startButton = (Button) findViewById (R.id.button);
        weaStart = new Weather(getWeatherTextView, getJson);

        //버튼을 클릭했을때 실행되는 함수
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weaStart.RunThread();
            }
        });
    }


}
