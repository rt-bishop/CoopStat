package priv.rtbishop.coopstat.vm

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import priv.rtbishop.coopstat.R
import priv.rtbishop.coopstat.data.SensorData
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val service = Executors.newSingleThreadScheduledExecutor()
    private val webClient = OkHttpClient()
    private val _proxyUrl = MutableLiveData<String>()
    private val _debugMessage = MutableLiveData<String>()
    private val _sensorReadings = MutableLiveData<SensorData>().apply {
        postValue(SensorData("low", "low", isFanOn = false, isHeaterOn = false, isLightOn = false))
    }
    private val app = application

    val proxyUrl: LiveData<String> = _proxyUrl
    val debugMessage: LiveData<String> = _debugMessage
    val sensorReadings: LiveData<SensorData> = _sensorReadings

    init {
        service.scheduleAtFixedRate({ getNewData() }, 0, 20, TimeUnit.SECONDS)
    }

    fun setupProxyConnection() {
        val username = preferences.getString("username", "")!!
        val password = preferences.getString("password", "")!!
        val devKey = preferences.getString("devkey", "")!!

        if (username == "" || password == "" || devKey == "") {
            Toast.makeText(getApplication(), R.string.credentials, Toast.LENGTH_LONG).show()
        } else {
            viewModelScope.launch {
                try {
                    val token = obtainDevToken(username, password, devKey)
                    val url = obtainProxyUrl(devKey, token)
                    _proxyUrl.postValue(url)
                } catch (e: IOException) {
                    _debugMessage.postValue(app.resources.getString(R.string.no_internet))
                    _proxyUrl.postValue("")
                }
            }
        }
    }

    private fun getNewData() {
        val urlData = "https://api.thingspeak.com/channels/839994/feeds.json?results=1"
        val request = Request.Builder()
                .url(urlData)
                .build()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    webClient.newCall(request).execute().body?.let {
                        try {
                            val jsonObjectMain = JSONObject(it.string())
                            val jsonArrayFeeds = jsonObjectMain.getJSONArray("feeds")
                            val currentHumid = jsonArrayFeeds.getJSONObject(0).getString("field1")
                            val currentTemp = jsonArrayFeeds.getJSONObject(0).getString("field2")
                            val isFanOn = jsonArrayFeeds.getJSONObject(0).getString("field3") == "1"
                            val isHeaterOn = jsonArrayFeeds.getJSONObject(0).getString("field4") == "1"
                            val isLightOn = jsonArrayFeeds.getJSONObject(0).getString("field5") == "1"
                            _sensorReadings.postValue(SensorData(currentHumid, currentTemp, isFanOn, isHeaterOn, isLightOn))
                        } catch (e: JSONException) {
                            _debugMessage.postValue(e.toString())
                        }
                    } ?: _debugMessage.postValue(app.resources.getString(R.string.no_response))
                } catch (e: IOException) {
                    _debugMessage.postValue(app.resources.getString(R.string.no_internet))
                }
            }
        }
    }

    private suspend fun obtainDevToken(username: String, password: String, devKey: String): String {
        val urlLogin = "https://api.remot3.it/apv/v27/user/login"
        var devToken = String()

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

        withContext(Dispatchers.IO) {
            webClient.newCall(request).execute().body?.let {
                try {
                    devToken = JSONObject(it.string()).getString("token")
                } catch (e: JSONException) {
                    _debugMessage.postValue(e.toString())
                }
            } ?: _debugMessage.postValue(app.resources.getString(R.string.no_response))
        }
        return devToken
    }

    private suspend fun obtainProxyUrl(devKey: String, devToken: String): String {
        val urlConnect = "https://api.remot3.it/apv/v27/device/connect"
        val deviceAddr = "80:00:00:00:01:01:25:25"
        var proxyUrl = String()

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

        withContext(Dispatchers.IO) {
            webClient.newCall(request).execute().body?.let {
                try {
                    val mainObject = JSONObject(it.string())
                    val connObject = mainObject.getJSONObject("connection")
                    proxyUrl = connObject.getString("proxy")
                } catch (e: JSONException) {
                    _debugMessage.postValue(e.toString())
                }
            } ?: _debugMessage.postValue(app.resources.getString(R.string.no_response))
        }
        return proxyUrl
    }
}