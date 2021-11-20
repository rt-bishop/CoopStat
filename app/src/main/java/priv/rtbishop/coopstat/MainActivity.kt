package priv.rtbishop.coopstat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import priv.rtbishop.coopstat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val proxyUrl = "http://192.168.192.200:8554/?action=stream"
        binding.streamView.viewTreeObserver.apply {
            if (isAlive) {
                addOnGlobalLayoutListener {
                    val scale = (binding.streamView.width / 2592f * 100).toInt()
                    binding.streamView.setInitialScale(scale)
                    binding.streamView.loadUrl(proxyUrl)
                }
            }
        }
    }
}
