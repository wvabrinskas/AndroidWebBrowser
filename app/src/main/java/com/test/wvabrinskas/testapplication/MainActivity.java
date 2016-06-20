package com.test.wvabrinskas.testapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.test.wvabrinskas.testapplication.ThreadObserver;

//Gradle library imports
import com.nikoyuwono.toolbarpanel.ToolbarPanelLayout;
import com.nikoyuwono.toolbarpanel.ToolbarPanelListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Scrolling";
    private static final JSONParser _jsonParser = JSONParser.getInstance();

    //temporary post ID
    private static final String postID = "1523295";

    //share buttons
    private Button _facebookShare;
    private Button _twitterShare;
    private Button _pinterestShare;
    private Button _fbMessengerShare;
    private Button _whatsappShare;
    private Button _smsShare;
    private Button _copyShare;
    private Button _browserShare;
    private Button _emailShare;

    //UI elements
    public ObservableWebView webView;
    public ProgressBar _loadingSpinner;
    public int progressStatus = 0;
    private Handler handler = new Handler();
    private TextView _titleLabel;
    private TextView _authorName;
    private TextView _categoryLabel;
    private TextView _toolbar_title;
    private ImageView _authorAvatarView;
    private Drawable _authorAvatar;
    private Typeface _icomoon;

    //custom ui elements
    private ToolbarPanelLayout _toolbarLayout;
    private Toolbar _toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        ThreadObserver.getInstance().addObserver("GotImageObserved", new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (data.getClass() == BitmapDrawable.class) {
                    final BitmapDrawable img = (BitmapDrawable) data;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            _authorAvatarView.setImageDrawable(img);
                        }
                    });
                }
            }
        });
        _loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        _toolbarLayout = (ToolbarPanelLayout) findViewById(R.id.sliding_down_toolbar_layout);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        _jsonParser.execute(new String[]{postID});
    }

    private JSONObject getPost() throws JSONException {
        JSONObject postObject = _jsonParser.currentPostObject;
        JSONObject post = postObject.getJSONObject("post");
        return post;
    }

    private String getPostContent() throws JSONException {

        JSONObject post = getPost();
        String app_content = post.getString("app_content");
        app_content = app_content.replace("//platform.", "https://platform.");
        return app_content;
    }

    public void setup() throws JSONException, IOException {
        setupShareButtons();
        _authorAvatarView = (ImageView) findViewById(R.id.authorAvatar);

        new Thread(new Runnable() {
            public void run() {
                try {
                    _authorAvatar = _jsonParser.drawableFromUrl(getPost().getJSONObject("author").getJSONObject("avatar").getString("url"));
                    ThreadObserver.getInstance().postNotification("GotImageObserved",_authorAvatar);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        //setup labels
        _titleLabel = (TextView) findViewById(R.id.title_text);
        _titleLabel.setText(getPost().getString("title"));
        _authorName = (TextView) findViewById(R.id.author_name);
        _authorName.setText(getPost().getJSONObject("author").getString("name"));
        _categoryLabel = (TextView) findViewById(R.id.category);
        JSONArray categories = getPost().getJSONArray("categories");
        JSONObject category = (JSONObject) categories.getJSONObject(0);
        _categoryLabel.setText("in " + category.getString("title"));
        _toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        _toolbar_title.setText(_titleLabel.getText());

        //setup toolbar
        _toolbarLayout.setToolbarPanelListener(new ToolbarPanelListener() {
            @Override
            public void onPanelSlide(Toolbar toolbar, View panelView, float slideOffset) {
                toolbar.getChildAt(0).setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelOpened(Toolbar toolbar, View panelView) {
            }

            @Override
            public void onPanelClosed(Toolbar toolbar, View panelView) {
            }
        });

        //setup webview
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

    private void setupShareButtons() {
        _icomoon = Typeface.createFromAsset(getAssets(),"fonts/icomoon.ttf");

        _facebookShare = (Button) findViewById(R.id.facebook_share);
        _facebookShare.setTypeface(_icomoon);
        _twitterShare = (Button) findViewById(R.id.twitter_share);
        _twitterShare.setTypeface(_icomoon);
        _pinterestShare = (Button) findViewById(R.id.pinterest_share);
        _pinterestShare.setTypeface(_icomoon);
        _fbMessengerShare = (Button) findViewById(R.id.fb_messenger_share);
        _fbMessengerShare.setTypeface(_icomoon);
        _whatsappShare = (Button) findViewById(R.id.whatsapp_share);
        _whatsappShare.setTypeface(_icomoon);
        _smsShare = (Button) findViewById(R.id.sms_share);
        _smsShare.setTypeface(_icomoon);
        _copyShare = (Button) findViewById(R.id.copy_share);
        _copyShare.setTypeface(_icomoon);
        _emailShare = (Button) findViewById(R.id.email_share);
        _emailShare.setTypeface(_icomoon);
        _browserShare = (Button) findViewById(R.id.browser_share);
        _browserShare.setTypeface(_icomoon);

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
