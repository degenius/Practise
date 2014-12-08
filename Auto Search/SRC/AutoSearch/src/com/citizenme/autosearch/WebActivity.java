package com.citizenme.autosearch;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String linkData;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.web_activity);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        linkData = getIntent()
                .getStringExtra(SearchResultActivity.KEY_LINK_URL);

        /** Load Url 1st Time */
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new CitizenMewebViewClient());
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setProgressBarVisibility(true);
                WebActivity.this.setTitle("Loading...");
                Log.d("The web account progress is " + progress);

                setProgress(progress * 100);

                if (progress > 80) {
                    WebActivity.this.setTitle(R.string.app_name);
                    setProgressBarVisibility(false);
                }
            }
        });

        Log.d("Gaurav the  url is " + linkData);
        if (linkData != null)
            webView.loadUrl(linkData);

    }

    private class CitizenMewebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
