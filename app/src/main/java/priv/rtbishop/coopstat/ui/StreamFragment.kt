package priv.rtbishop.coopstat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import priv.rtbishop.coopstat.R
import priv.rtbishop.coopstat.vm.MainViewModel

class StreamFragment : Fragment() {

    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var viewModel: MainViewModel
    private lateinit var streamView: WebView
    private lateinit var webViewClient: WebViewClient
    private var scale: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
        webViewClient = WebViewClient()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

        val proxyUrl = viewModel.proxyUrl.value
        if (proxyUrl.isNullOrEmpty()) {
            swipeLayout.isRefreshing = true
            viewModel.setupProxyConnection()
        } else {
            loadUrl(proxyUrl)
        }

        viewModel.proxyUrl.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            loadUrl(it)
        })

        swipeLayout.setOnRefreshListener {
            viewModel.setupProxyConnection()
        }
    }

    private fun setupViews(view: View) {
        swipeLayout = view.findViewById(R.id.swipe_layout)
        streamView = view.findViewById(R.id.stream_view)
        val observer = streamView.viewTreeObserver
        if (observer.isAlive) {
            observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    streamView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    scale = (streamView.width / 1300f * 100).toInt()
                }
            })
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl(url: String) {
        streamView.post {
            streamView.settings.javaScriptEnabled = true
            streamView.webViewClient = webViewClient
            streamView.setInitialScale(scale)
            streamView.loadUrl("$url/stream_simple.html")
        }
    }
}