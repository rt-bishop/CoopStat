package priv.rtbishop.coopstat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import priv.rtbishop.coopstat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            val proxyUrl = "http://192.168.192.200:8554/?action=stream"
            streamView.viewTreeObserver.apply {
                if (this.isAlive) {
                    addOnGlobalLayoutListener {
                        val scale = (streamView.width / 2592f * 100).toInt()
                        streamView.apply {
                            setInitialScale(scale)
                            loadUrl(proxyUrl)
                        }
                    }
                }
            }
        }
    }
}