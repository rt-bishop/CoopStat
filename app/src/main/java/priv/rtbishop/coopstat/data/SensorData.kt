package priv.rtbishop.coopstat.data

data class SensorData(
        val currentHumid: String,
        val currentTemp: String,
        val isFanOn: Boolean,
        val isHeaterOn: Boolean,
        val isLightOn: Boolean)