package priv.rtbishop.coopstat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import priv.rtbishop.coopstat.R
import java.util.*

class ChartFragment : Fragment() {

    private lateinit var webView: WebView
    private var daysToShow: Int = 0
    private var position: Int = 0

    private val days: String
        get() = if (daysToShow == 1) {
            "&days=$daysToShow&average=10"
        } else "&days=$daysToShow&average=1440"

    private val size: String
        get() {
            val density = resources.displayMetrics.density
            val webViewWidth = (webView.width / density).toInt()
            val webViewHeight = (webView.height / density).toInt()
            return "&width=$webViewWidth&height=$webViewHeight"
        }

    private val titleRusLocale: String
        get() {
            val titles = arrayOf(
                    "&title=%D0%92%D0%BB%D0%B0%D0%B6%D0%BD%D0%BE%D1%81%D1%82%D1%8C&yaxismax=100&yaxismin=0",
                    "&title=%D0%A2%D0%B5%D0%BC%D0%BF%D0%B5%D1%80%D0%B0%D1%82%D1%83%D1%80%D0%B0&yaxismax=40&yaxismin=-10",
                    "&title=%D0%92%D0%B5%D0%BD%D1%82%D0%B8%D0%BB%D1%8F%D1%86%D0%B8%D1%8F&yaxismax=1&yaxismin=0",
                    "&title=%D0%9E%D0%B1%D0%BE%D0%B3%D1%80%D0%B5%D0%B2&yaxismax=1&yaxismin=0",
                    "&title=%D0%A1%D0%B2%D0%B5%D1%82&yaxismax=1&yaxismin=0")
            return titles[position]
        }

    private val titleEngLocale: String
        get() {
            val titles = arrayOf(
                    "&title=Humidity&yaxismax=100&yaxismin=0",
                    "&title=Temperature&yaxismax=40&yaxismin=-10",
                    "&title=Fan&yaxismax=1&yaxismin=0",
                    "&title=Heater&yaxismax=1&yaxismin=0",
                    "&title=Light&yaxismax=1&yaxismin=0")
            return titles[position]
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        position = bundle!!.getInt("position")
        daysToShow = bundle.getInt("daysToShow")
        webView = view.findViewById(R.id.web_view_chart)
        loadUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl() {
        val baseUrl = "https://thingspeak.com/channels/839994/charts/"
        var addUrl = (position + 1).toString() + "?" + days + size
        val endUrl = "&bgcolor=%23ffffff&color=%23d62020&dynamic=true&round=1&type=line&xaxis=+&yaxis=+"

        addUrl += if (Locale.getDefault().displayLanguage == "русский") {
            titleRusLocale
        } else {
            titleEngLocale
        }

        webView.settings.javaScriptEnabled = true
        webView.loadUrl(baseUrl + addUrl + endUrl)
    }

    companion object {
        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }
}