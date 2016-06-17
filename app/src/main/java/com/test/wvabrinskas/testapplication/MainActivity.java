package com.test.wvabrinskas.testapplication;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

//Gradle library imports
import com.nikoyuwono.toolbarpanel.ToolbarPanelLayout;
import com.nikoyuwono.toolbarpanel.ToolbarPanelListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Scrolling";
    private static final JSONParser _jsonParser = JSONParser.getInstance();
    //UI elements
    public ObservableWebView webView;
    public ProgressBar _loadingSpinner;
    public int progressStatus = 0;
    private Handler handler = new Handler();
    //custom ui elements
    private ToolbarPanelLayout _toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        _loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        _toolbar = (ToolbarPanelLayout) findViewById(R.id.sliding_down_toolbar_layout);
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        public void run() {
                            _loadingSpinner.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        _jsonParser.controllerActivity = this;
        _jsonParser.execute(new String[]{"1523295"});
    }

    private String getPostContent() throws JSONException {

        JSONObject postObject = _jsonParser.currentPostObject;
        JSONObject post = postObject.getJSONObject("post");
        String app_content = post.getString("app_content");

        app_content = app_content.replace("//platform.","https://platform.");

        return app_content;
    }

    public void setup() throws JSONException {
        //enable javascript in webview at start up
        webView = (ObservableWebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setHorizontalScrollBarEnabled(false);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                _loadingSpinner.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }
        });

        String app_content = getPostContent();

        String content = null;
        try {
            content = new Scanner(getAssets().open(String.format("PostContent.html"))).useDelimiter("\\A").next();
        } catch (IOException e) {
            Log.d(TAG,"post content not found =( ");
            e.printStackTrace();
        }
        if (content != null) {
            webView.loadDataWithBaseURL("file:///android_asset/PostContent.html",String.format(content,app_content),"text/html",null,null);
        }
    }

    private void hideKeyboard() {
        //we want to hide the keyboard after hitting go
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Toast getToast(String text) {
        Toast newToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        return newToast;
    }


}
