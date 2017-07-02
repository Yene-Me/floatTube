package org.player;

/**
 * Created by yeneneh.mulatu on 29/06/2017.
 */


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import hera.yene.org.R;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.DeveloperKey;
import org.util.helper;

public class VideoPlayer extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private String VIDEO_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        VIDEO_ID = getIntent().getStringExtra(helper.VIDEO_ID);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeplayerview);
        youTubePlayerView.initialize(DeveloperKey.DEVELOPER_KEY, this);

        startWebView();


    }

    private void startWebView()
    {
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl("https://www.google.com");

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });
    }

    @Override
    public void onInitializationFailure(Provider provider,
                                        YouTubeInitializationResult result)
    {
        Toast.makeText(getApplicationContext(),
                "onInitializationFailure()",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player,
                                        boolean wasRestored)
    {
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }

}
