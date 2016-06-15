package com.test.wvabrinskas.testapplication;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Scrolling";

    //UI elements
    public ObservableWebView webView;
    public EditText urlNav;
    public Button backButton;
    public Button forwardButton;
    public Button goButton;

    //animations
    public Animation animTranslateOut;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        this.setup();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setup() {

        //set animations
        animTranslateOut = AnimationUtils.loadAnimation(this, R.anim.translate_out);

        //set buttons from activity_main.xml interface
        goButton = (Button) findViewById(R.id.navigate);
        forwardButton = (Button) findViewById(R.id.forward_button);
        backButton = (Button) findViewById(R.id.back_button);
        urlNav = (EditText) findViewById(R.id.browserBar);
        urlNav.setText("https://google.com");

        //enable javascript in webview at start up
        webView = (ObservableWebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                urlNav.setText(url);
                view.loadUrl(url);
                hideKeyboard();
                return false;
            }
        });


        webView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {

            int oldT = 0;

            public void onScroll(int l, int t) {

                if (t > oldT) {
                    //animate the UI here when scrolling down - hide
                    backButton.setAnimation(animTranslateOut);
                    forwardButton.setAnimation(animTranslateOut);
                    goButton.setAnimation(animTranslateOut);
                    urlNav.setAnimation(animTranslateOut);
                } else {
                    //animate the UI here when scrolling up - show
                }
                oldT = t;
            }
        });

        webView.loadUrl(urlNav.getText().toString());
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

    public void onButtonTap(View v) {
        Toast myToast = Toast.makeText(getApplicationContext(), "Navigating...", Toast.LENGTH_LONG);
        myToast.show();
        loadWebview();
        hideKeyboard();
    }

    public void loadWebview() {
        String url = urlNav.getText().toString();
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        webView.loadUrl(url);
    }

    public void forward(View v) {
        if (webView.canGoForward()) {
            webView.goForward();
        } else {
            getToast("Can't go forward").show();
        }
    }

    public void back(View v) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            getToast("Can't go back").show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.test.wvabrinskas.testapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.test.wvabrinskas.testapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
