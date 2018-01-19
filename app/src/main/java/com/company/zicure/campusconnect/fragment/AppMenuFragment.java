package com.company.zicure.campusconnect.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.activity.BlocContentActivity;
import com.company.zicure.campusconnect.activity.LoginActivity;
import com.company.zicure.campusconnect.activity.MainMenuActivity;

import java.util.ArrayList;

import gallery.zicure.company.com.modellibrary.common.BaseActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppMenuFragment extends Fragment implements DownloadListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "url";

    // TODO: Rename and change types of parameters
    private String url;
    private String mParam2;
    private String token;

    /** Make: View **/
    public static WebView webView = null;
    private WebSettings webSettings = null;
    private ProgressBar progressBarLoading = null;
    private FrameLayout layoutProgress = null;

    /** Make: Properties **/
    public ValueCallback<Uri[]> mUploadMesssage = null;
    public Uri mCapturedImageURI = null;

    private String JAVASCRIPT_OBJ = "javascript_obj";
//    private String BASE_URL = "file:///android_asset/webview.html";
    private String BASE_URL = "http://connect06.pakgon.com/core";

    public AppMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url Parameter 1.
     * @return A new instance of fragment AppMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppMenuFragment newInstance(String url) {
        AppMenuFragment fragment = new AppMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_app_menu, container, false);

        bindView(root);
        return root;
    }

    private void bindView(View root) {
        webView = root.findViewById(R.id.appView);
        progressBarLoading = root.findViewById(R.id.progress_bar_webview);
        layoutProgress = root.findViewById(R.id.framelayout_loading);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBarLoading.setProgress(0);
        progressBarLoading.setMax(100);
        if (savedInstanceState == null) {
            setWebView();
        }
    }

    public void setWebView(){
        webView.setWebViewClient(new AppBrowser());
        webView.setWebChromeClient(new ChromeClient());
        webView.setVerticalScrollBarEnabled(true);
        webView.setClickable(true);
        webView.requestFocus(View.FOCUS_DOWN);
        webSettings = webView.getSettings();

        // improve webView performance
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(), JAVASCRIPT_OBJ);

        webView.loadUrl(url);
    }

    private void downloadFile(String url, String userAgent, String contentDisposition, String mimetype) {
        String[] currencyFile = url.split("/");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimetype);

        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Download file...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS,".rar");

        DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);

        Toast.makeText(getActivity(), "Download file...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        downloadFile(url, userAgent, contentDisposition, mimetype);
    }

    public class AppBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            layoutProgress.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.clearCache(true);
            webView.setEnabled(view.canGoBack());
            injectJavaScriptFunction();

            if (!MainMenuActivity.STACK_URL.get(0).equalsIgnoreCase(url)){
                MainMenuActivity.STACK_URL.add(url);
            }
        }
    }

    private void injectJavaScriptFunction(){
        StringBuilder script = new StringBuilder();
        script.append("javascript: ");
//        script.append("window.androidObj.textToAndroid = function(message) { ");
//        script.append(JAVASCRIPT_OBJ + ".textFromWeb(message) }");
        script.append("class Login { ");
        script.append("static onLogin(arg1, arg2, arg3) { ");
        script.append(JAVASCRIPT_OBJ + ".textFromLogin(arg1, arg2, arg3) }}");

        Log.d("TextFromWeb", script.toString());

        webView.loadUrl(script.toString());
    }

    public class ChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            layoutProgress.setVisibility(View.VISIBLE);
            progressBarLoading.setProgress(newProgress);

            if (newProgress == 100){
                layoutProgress.setVisibility(View.GONE);
            }

            super.onProgressChanged(view, newProgress);
        }
    }

    public class JavaScriptInterface{
        @JavascriptInterface
        public void textFromWeb(String fromWeb){
            Log.d("TextFromWeb", fromWeb);
        }

        @JavascriptInterface
        public void textFromLogin(String token, String url, String subscribeNoti){
            Log.d("TextFromWeb", token + url+ subscribeNoti);
            ((LoginActivity) getActivity()).store(token, url, subscribeNoti);
        }
    }
}
