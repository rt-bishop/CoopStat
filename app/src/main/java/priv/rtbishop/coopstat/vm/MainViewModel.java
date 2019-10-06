package priv.rtbishop.coopstat.vm;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import okhttp3.ResponseBody;
import priv.rtbishop.coopstat.R;
import priv.rtbishop.coopstat.data.Data;

public class MainViewModel extends AndroidViewModel {

    private OkHttpClient okHttpClient = new OkHttpClient();
    private Context mContext;
    private MutableLiveData<Data> data;
    private String proxyUrl;
    private boolean isConnected = false;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mContext = application;
    }

    public LiveData<Data> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.postValue(new Data("low", "low",
                    false, false, false));
            obtainNewData();
        }
        return data;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void obtainNewData() {
        String urlData = "https://api.thingspeak.com/channels/839994/feeds.json?results=1";
        Request request = new Request.Builder()
                .url(urlData)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray jsonArray = jsonObject.getJSONArray("feeds");
                        String currentHumid = jsonArray.getJSONObject(0).getString("field1");
                        String currentTemp = jsonArray.getJSONObject(0).getString("field2");
                        boolean isFanOn = jsonArray.getJSONObject(0).getString("field3").equals("1");
                        boolean isHeaterOn = jsonArray.getJSONObject(0).getString("field4").equals("1");
                        boolean isLightOn = jsonArray.getJSONObject(0).getString("field5").equals("1");
                        data.postValue(new Data(currentHumid, currentTemp, isFanOn, isHeaterOn, isLightOn));
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setupProxyConnection() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");
        String devKey = preferences.getString("devkey", "");

        if (username.equals("") && password.equals("") && devKey.equals("")) {
            Toast.makeText(mContext, R.string.credentials, Toast.LENGTH_LONG).show();
        } else if (!isConnected()) {
            obtainConnection(username, password, devKey);
        } else {
            Toast.makeText(mContext, R.string.connection_established, Toast.LENGTH_LONG).show();
        }
    }

    private void obtainConnection(String username, String password, final String devKey) {
        String urlLogin = "https://api.remot3.it/apv/v27/user/login";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("username", username);
        jsonBody.put("password", password);
        JSONObject jsonObject = new JSONObject(jsonBody);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .header("developerkey", devKey)
                .url(urlLogin)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        String devToken = jsonObject.getString("token");
                        obtainProxyUrl(devKey, devToken);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void obtainProxyUrl(String devKey, String devToken) {
        String urlConnect = "https://api.remot3.it/apv/v27/device/connect";
        String deviceAddr = "80:00:00:00:01:01:24:9A";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("deviceaddress", deviceAddr);
        jsonBody.put("wait", Boolean.TRUE.toString());
        JSONObject jsonObject = new JSONObject(jsonBody);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .addHeader("developerkey", devKey)
                .addHeader("token", devToken)
                .url(urlConnect)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try {
                        JSONObject jsonObjectMain = new JSONObject(responseBody.string());
                        JSONObject jsonObject = jsonObjectMain.getJSONObject("connection");
                        proxyUrl = jsonObject.getString("proxy");
                        isConnected = jsonObjectMain.getString("status").equals("true");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.connection_established, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
