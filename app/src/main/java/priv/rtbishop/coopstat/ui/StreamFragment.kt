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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import priv.rtbishop.coopstat.R
import priv.rtbishop.coopstat.vm.MainViewModel

class StreamFragment : Fragment() {

    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var viewModel: MainViewModel
    private lateinit var streamView: WebView
    private var proxyUrl = String()
    private var scale: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        if (proxyUrl.isNotEmpty()) loadUrl(proxyUrl)
        else {
            swipeLayout.isRefreshing = true
            viewModel.setupProxyConnection()
        }

        viewModel.proxyUrl.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                swipeLayout.isRefreshing = false
                proxyUrl = it
                loadUrl(proxyUrl)
            } else {
                swipeLayout.isRefreshing = false
                Toast.makeText(activity as MainActivity, "Problem was there", Toast.LENGTH_SHORT).show()
            }
        })

        swipeLayout.setOnRefreshListener {
            viewModel.setupProxyConnection()
        }
    }

    private fun setupViews(view: View) {
        swipeLayout = view.findViewById(R.id.swipe_layout)
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
    private fun loadUrl(url: String) {
        streamView.post {
            streamView.settings.javaScriptEnabled = true
            streamView.webViewClient = WebViewClient()
            streamView.setInitialScale(scale)
            streamView.loadUrl("$url/stream_simple.html")
        }
    }
}