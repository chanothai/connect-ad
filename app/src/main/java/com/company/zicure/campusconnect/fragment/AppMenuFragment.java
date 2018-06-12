package com.company.zicure.campusconnect.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.activity.LoginActivity;
import com.company.zicure.campusconnect.nearby.DetectBeacon;
import com.company.zicure.campusconnect.network.ClientHttp;
import com.company.zicure.campusconnect.utility.StackURLController;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.company.zicure.campusconnect.utility.ModelCart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//commu-uat.connect.pakgon.com
    //connect-uat.pakgon.com
public class AppMenuFragment extends Fragment implements DownloadListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "url";
    private static final String state = "state";

    // TODO: Rename and change types of parameters
    private String url;
    public Boolean webviewState = null;

    /** Make: View **/
    public static WebView webView = null;
    private WebSettings webSettings = null;
    private ProgressBar progressBarLoading = null;
    private FrameLayout layoutProgress = null;

    /** Make: Properties **/
    public ValueCallback<Uri[]> mUploadMesssage = null;
    public Uri mCapturedImageURI = null;

    private String TAG = "TextFromWeb";

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
    public static AppMenuFragment newInstance(String url, Boolean webviewState) {
        AppMenuFragment fragment = new AppMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        args.putBoolean(state, webviewState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
            webviewState = getArguments().getBoolean(state);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void checkSession() {
        if (StackURLController.getInstance().getStackURL().size() == 4) {
            try{
                String[] splitURL = StackURLController.getInstance().getStackURL().get(3).split("/users");
                if (splitURL[0].equalsIgnoreCase(ClientHttp.URL_OAUTH) && ModelCart.getInstance().getKeyModel().getToken() != null) {
                    url = "http://connect-uat.pakgon.com/users/reautorize";
                    setWebView();
                    StackURLController.getInstance().getStackURL().clear();
                    StackURLController.getInstance().getStackURL().add(splitURL[0]);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public void setWebView(){
        webView.setWebViewClient(new AppBrowser());
        webView.setWebChromeClient(new AppBrowserChrome());
        webView.setVerticalScrollBarEnabled(true);
        webView.setClickable(true);
        webView.requestFocus(View.FOCUS_DOWN);

        webSettings = webView.getSettings();
        webView.setScrollY(webView.getScrollY() - 10);

        // improve webView performance
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new JavaScriptInterface(), "Login");
        webView.addJavascriptInterface(new JavaScriptInterface(), "Beacon");

        webView.loadUrl(url, getHeader());
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization","Bearer " + ModelCart.getInstance().getKeyModel().getToken());
        header.put("Accept-Language", ModelCart.getInstance().getKeyModel().getLanguage()+";q=1.0");
        return header;
    }

    private void downloadFile(String url, String userAgent, String contentDisposition, String mimetype) {
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

    public class AppBrowserChrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            layoutProgress.setVisibility(View.VISIBLE);
            progressBarLoading.setProgress(newProgress);

            if (newProgress == 100){
                layoutProgress.setVisibility(View.GONE);
            }

            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadMesssage = filePathCallback;

            File imageStoragePath = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Connect"
            );
            if (!imageStoragePath.exists()) {
                imageStoragePath.mkdirs();
            }

            try {
                File file = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", imageStoragePath);
                mCapturedImageURI = Uri.fromFile(file);

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePictureIntent });

                startActivityForResult(chooserIntent, 2500);
                return true;

            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public class AppBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webviewState = true;
            view.loadUrl(url, getHeader());
            layoutProgress.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.clearCache(true);
            webView.setEnabled(view.canGoBack());

            if (!StackURLController.getInstance().getStackURL().get(0).equalsIgnoreCase(url)){
                if (webviewState) {
                    if (StackURLController.getInstance().getStackURL().size() > 1) {
                        if (StackURLController.getInstance().getStackURL().get(1).equalsIgnoreCase(url)){
                            StackURLController.getInstance().getStackURL().set(1, url);
                        }else{
                            StackURLController.getInstance().getStackURL().add(url);
                        }
                    }else{
                        StackURLController.getInstance().getStackURL().add(url);
                    }
                }
            }

            Log.d("STACK_URL", StackURLController.getInstance().getStackURL().toString());

            checkSession();
        }
    }

    public class JavaScriptInterface{
        @JavascriptInterface
        public void textFromWeb(String fromWeb){
            Log.d("TextFromWeb", fromWeb);
        }

        @JavascriptInterface
        public void onLogin(final String token, final String url, final String subscribeNoti){
            Log.d("TextFromWeb", token + url+ subscribeNoti);
            webView.post(new Runnable() {
                @Override
                public void run() {
                    ((LoginActivity) getActivity()).store(token, url, subscribeNoti);
                }
            });
        }

        @JavascriptInterface
        public void getData(){
            String beacon = new Gson().toJson(DetectBeacon.getInstance(getContext()).getStackBeacon());
            final StringBuilder script = new StringBuilder();
            Log.d("TOKEN_USER", ModelCart.getInstance().getKeyModel().getToken());
            script.append("javascript: ");
            script.append("updateBeacon('"+ beacon +"', '"+ ModelCart.getInstance().getKeyModel().getToken() +"')");

            Log.d(TAG, script.toString());

            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.evaluateJavascript(script.toString(), null);
                }
            });
        }
    }
}
