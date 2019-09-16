package com.example.rest_api;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Weather {
    private static final String TAG = "imagesearchexample";
    private static final int LOAD_SUCCESS = 101;
    private static final int CURRENT_FORECAST_API = 10001;
    private static final int FIVE_HOUR_THREE_FORECAST_API = 10002;


    //OpenWeather REST Api
    private static final String CURRENT_FORECAST_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String FIVE_DAY_THREE_HOUR_FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast?q=";

    private final String SEOUL   = "Seoul, kr";
    private final String DAEGU   = "Daegu, kr";
    private final String BUSAN   = "Busan, kr";
    private final String Gwangju = "Gwangju, kr";

    private final String APIKEY = "38708f5544158a8c68f3556312cc0fa2";

    private int checkAPI = FIVE_HOUR_THREE_FORECAST_API;

    private String SEARCH_URL;

    private String LOCATION   = DAEGU;
    private String COUNT      = "&cnt=10";
    private String APPID      = "&appid=" + APIKEY;
    private String UNITS      = "&units=metric";
    private String REQUEST_URL;

    private Thread thread;
    private TextView jsonText;

    private String returnStr;
    private String mainStr;
    private List<HashMap<String, String>> weatherInfoList = null;
    private HashMap<String, String> weatherInfoMap = null;

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case LOAD_SUCCESS:
                    String jsonString = (String)msg.obj;
                    jsonText.setText(jsonString);

                    jsonParser(jsonString, checkAPI);
                    Log.i("json: ", jsonString);
                    mainStr = jsonString;
                    break;
                default:
                    break;
            }
        }
    };

    //Constructor
    public Weather(TextView jsonTextView, String nStr) {
        this.jsonText = jsonTextView;
        this.mainStr = nStr;
    }

    public void RunThread() {
        thread = new Thread();
        thread.start();
    }

    class Thread extends java.lang.Thread {
        boolean stopped = false;
        int i = 0;

        public Thread() {
            this.stopped = false;
        }

        public void run() {
            super.run();

            try {
                if(checkAPI == CURRENT_FORECAST_API) {
                    SEARCH_URL = CURRENT_FORECAST_URL;
                } else if (checkAPI == FIVE_HOUR_THREE_FORECAST_API) {
                    SEARCH_URL = FIVE_DAY_THREE_HOUR_FORECAST_URL;
                }

                REQUEST_URL = SEARCH_URL + LOCATION + COUNT + APPID + UNITS;

                URL url = new URL(REQUEST_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(600);
                httpURLConnection.setConnectTimeout(600);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setUseCaches(false);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;

                if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                    inputStream = httpURLConnection.getInputStream();

                } else {

                    inputStream = httpURLConnection.getErrorStream();

                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;


                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                httpURLConnection.disconnect();

                returnStr = sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message message = handler.obtainMessage();

            message.what = LOAD_SUCCESS;
            message.obj = returnStr;
            handler.sendMessage(message);
        }
    }

    public boolean jsonParser(String jsonStringData, int checkAPI) {
        if(jsonStringData == null) return false;

        weatherInfoMap = new HashMap<String, String>();
        weatherInfoList = new ArrayList<HashMap<String, String>>();
        weatherInfoList.clear();

        try {
            JSONObject jsonObject = new JSONObject(jsonStringData);

            /*
             *  temp: 온도
             *  humidity: 습도
             *
             */
            switch (checkAPI) {
                case CURRENT_FORECAST_API:
                    JSONArray current_weather = jsonObject.getJSONArray("weather");
                    JSONObject current_main = jsonObject.getJSONObject("main");
                    JSONObject current_wind = jsonObject.getJSONObject("wind");
                    String current_clouds = jsonObject.getJSONObject("clouds").getString("all");

                    weatherInfoMap.put("temp", current_main.getString("temp"));
                    weatherInfoMap.put("humidity", current_main.getString("humidity"));
                    //wind.speed Wind speed. Unit Default: meter/sec, Metric:
                    weatherInfoMap.put("clouds", current_clouds);
                    weatherInfoMap.put("wind_speed", current_wind.getString("speed"));
                    break;
                case FIVE_HOUR_THREE_FORECAST_API:
                    //json데이터 특정 영역 list방식으로 가져오기
                    JSONArray list = jsonObject.getJSONArray("list");

                    for(int i = 0; i <list.length(); i++) {
                        JSONObject listInfo = list.getJSONObject(i);

                        String three_dt = listInfo.getString("dt");
                        JSONObject three_mainObj = listInfo.getJSONObject("main");
                        JSONArray three_weatherObj = listInfo.getJSONArray("weather");
                        String three_clouds = listInfo.getJSONObject("clouds").getString("all");
                        JSONObject three_wind = listInfo.getJSONObject("wind");

                        weatherInfoMap.put("dt", three_dt);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
