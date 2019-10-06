package priv.rtbishop.coopstat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import priv.rtbishop.coopstat.R
import priv.rtbishop.coopstat.vm.MainViewModel

class StreamFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var streamView: WebView
    private var scale: Int = 0

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        setupViews(view)

        if (viewModel.isConnected) {
            loadUrl()
        } else {
            Toast.makeText(activity, R.string.connection_not_found, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupViews(view: View) {
        streamView = view.findViewById(R.id.web_view_stream)

        val viewTreeObserver = streamView.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    streamView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    scale = (streamView.width / 1300f * 100).toInt()
                }
            })
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl() {
        streamView.post {
            streamView.settings.javaScriptEnabled = true
            streamView.webViewClient = WebViewClient()
            streamView.setInitialScale(scale)
            streamView.loadUrl(viewModel.proxyUrl + "/stream_simple.html")
        }
    }
}