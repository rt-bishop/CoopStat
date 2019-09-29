package priv.rtbishop.coopstat.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import priv.rtbishop.coopstat.R;

public class ChartFragment extends Fragment {

    private WebView mWebView;
    private int mDaysToShow;
    private int mPosition;

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        mPosition = bundle.getInt("position");
        mDaysToShow = bundle.getInt("daysToShow");
        mWebView = view.findViewById(R.id.web_view_chart);
        loadUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadUrl() {
        String baseUrl = "https://thingspeak.com/channels/839994/charts/";
        String addUrl = (mPosition + 1) + "?" + getDays() + getSize();
        String endUrl = "&bgcolor=%23ffffff&color=%23d62020&dynamic=true&round=1&type=line&xaxis=+&yaxis=+";
        if (Locale.getDefault().getDisplayLanguage().equals("русский")) {
            addUrl += getTitleRusLocale();
        } else {
            addUrl += getTitleEngLocale();
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(baseUrl + addUrl + endUrl);
    }

    private String getDays() {
        switch (mDaysToShow) {
            case 1:
                return "&days=" + mDaysToShow + "&average=10";
            case 7:
                return "&days=" + mDaysToShow + "&timescale=240";
            default:
                return "&days=" + mDaysToShow + "&average=1440";
        }
    }

    private String getSize() {
        float density = getResources().getDisplayMetrics().density;
        int webViewWidth = (int) (mWebView.getWidth() / density);
        int webViewHeight = (int) (mWebView.getHeight() / density);
        return "&width=" + webViewWidth + "&height=" + webViewHeight;
    }

    private String getTitleRusLocale() {
        String[] titles = new String[]{
                "&title=%D0%92%D0%BB%D0%B0%D0%B6%D0%BD%D0%BE%D1%81%D1%82%D1%8C&yaxismax=100&yaxismin=0",
                "&title=%D0%A2%D0%B5%D0%BC%D0%BF%D0%B5%D1%80%D0%B0%D1%82%D1%83%D1%80%D0%B0&yaxismax=40&yaxismin=-10",
                "&title=%D0%92%D0%B5%D0%BD%D1%82%D0%B8%D0%BB%D1%8F%D1%86%D0%B8%D1%8F&yaxismax=1&yaxismin=0",
                "&title=%D0%9E%D0%B1%D0%BE%D0%B3%D1%80%D0%B5%D0%B2&yaxismax=1&yaxismin=0",
                "&title=%D0%A1%D0%B2%D0%B5%D1%82&yaxismax=1&yaxismin=0"};
        return titles[mPosition];
    }

    private String getTitleEngLocale() {
        String[] titles = new String[]{
                "&title=Humidity&yaxismax=100&yaxismin=0",
                "&title=Temperature&yaxismax=40&yaxismin=-10",
                "&title=Fan&yaxismax=1&yaxismin=0",
                "&title=Heater&yaxismax=1&yaxismin=0",
                "&title=Light&yaxismax=1&yaxismin=0"};
        return titles[mPosition];
    }
}