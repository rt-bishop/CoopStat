package priv.rtbishop.coopstat.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import priv.rtbishop.coopstat.R;
import priv.rtbishop.coopstat.vm.MainViewModel;

public class StreamFragment extends Fragment {

    private MainViewModel mViewModel;
    private WebView mStreamView;
    private int mScale;
    private ImageButton mBtnStartStream;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(MainViewModel.class);
        setupViews(view);
        checkConnection();
    }

    private void setupViews(@NonNull View view) {
        mStreamView = view.findViewById(R.id.web_view_stream);

        ViewTreeObserver viewTreeObserver = mStreamView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mStreamView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScale = (int) ((mStreamView.getWidth() / 1300f) * 100);
                }
            });
        }

        mBtnStartStream = view.findViewById(R.id.btn_start_stream);
        mBtnStartStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnection();
            }
        });
    }

    private void checkConnection() {
        if (mViewModel.isConnected()) {
            mBtnStartStream.setVisibility(View.INVISIBLE);
            loadUrl();
        } else {
            mViewModel.obtainConnection();
        }
    }

    private void loadUrl() {
        mStreamView.post(new Runnable() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                mStreamView.getSettings().setJavaScriptEnabled(true);
                mStreamView.setWebViewClient(new WebViewClient());
                mStreamView.setInitialScale(mScale);
                mStreamView.loadUrl(mViewModel.getProxyUrl() + "/stream_simple.html");
            }
        });
    }
}