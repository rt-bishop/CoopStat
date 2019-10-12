package priv.rtbishop.coopstat.vm

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import priv.rtbishop.coopstat.R
import priv.rtbishop.coopstat.data.Data
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val okHttpClient: OkHttpClient = OkHttpClient()
    private val _data: MutableLiveData<Data> = MutableLiveData(Data("low",
            "low", isFanOn = false, isHeaterOn = false, isLightOn = false))
    val data: LiveData<Data> = _data
    var proxyUrl: String? = null
    var isConnected = false

    init {
        val service = Executors.newSingleThreadScheduledExecutor()
        service.scheduleAtFixedRate({ getNewData() }, 2, 20, TimeUnit.SECONDS)
    }

    fun setupProxyConnection() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val username = preferences.getString("username", "")
        val password = preferences.getString("password", "")
        val devKey = preferences.getString("devkey", "")

        if (username == "" || password == "" || devKey == "") {
            Toast.makeText(getApplication(), R.string.credentials, Toast.LENGTH_LONG).show()
        } else if (!isConnected) {
            obtainConnection(username!!, password!!, devKey!!)
        } else {
            Toast.makeText(getApplication(), R.string.connection_established, Toast.LENGTH_LONG).show()
        }
    }

    private fun getNewData() {
        val urlData = "https://api.thingspeak.com/channels/839994/feeds.json?results=1"
        val request = Request.Builder()
                .url(urlData)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody.string())
                        val jsonArray = jsonObject.getJSONArray("feeds")
                        val currentHumid = jsonArray.getJSONObject(0).getString("field1")
                        val currentTemp = jsonArray.getJSONObject(0).getString("field2")
                        val isFanOn = jsonArray.getJSONObject(0).getString("field3") == "1"
                        val isHeaterOn = jsonArray.getJSONObject(0).getString("field4") == "1"
                        val isLightOn = jsonArray.getJSONObject(0).getString("field5") == "1"
                        _data.postValue(Data(currentHumid, currentTemp, isFanOn, isHeaterOn, isLightOn))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        })
    }

    private fun obtainConnection(username: String, password: String, devKey: String) {
        val urlLogin = "https://api.remot3.it/apv/v27/user/login"

        val jSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonBody = HashMap<String, String>()
        jsonBody["username"] = username
        jsonBody["password"] = password
        val jsonObject = JSONObject(jsonBody as Map<*, *>)
        val requestBody = jsonObject.toString().toRequestBody(jSON)

        val request = Request.Builder()
                .header("developerkey", devKey)
                .url(urlLogin)
                .post(requestBody)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body
                if (responseBody != null) {
                    try {
                        val jsonObj = JSONObject(responseBody.string())
                        val devToken = jsonObj.getString("token")
                        obtainProxyUrl(devKey, devToken)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        })
    }

    private fun obtainProxyUrl(devKey: String, devToken: String) {
        val urlConnect = "https://api.remot3.it/apv/v27/device/connect"
        val deviceAddr = "80:00:00:00:01:01:24:9A"

        val jSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonBody = HashMap<String, String>()
        jsonBody["deviceaddress"] = deviceAddr
        jsonBody["wait"] = java.lang.Boolean.TRUE.toString()
        val jsonObject = JSONObject(jsonBody as Map<*, *>)
        val requestBody = jsonObject.toString().toRequestBody(jSON)

        val request = Request.Builder()
                .addHeader("developerkey", devKey)
                .addHeader("token", devToken)
                .url(urlConnect)
                .post(requestBody)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body
                if (responseBody != null) {
                    try {
                        val jsonObjectMain = JSONObject(responseBody.string())
                        val jsonObj = jsonObjectMain.getJSONObject("connection")
                        proxyUrl = jsonObj.getString("proxy")
                        isConnected = jsonObjectMain.getString("status") == "true"
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(getApplication(),
                                    R.string.connection_established,
                                    Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        })
    }
}