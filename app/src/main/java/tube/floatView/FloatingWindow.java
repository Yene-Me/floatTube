package tube.floatView;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.youtube.player.YouTubePlayerView;
import com.tube.R;

import tube.DeveloperKey;
import tube.util.helper;

public class FloatingWindow extends Service {

    WindowManager wm;
    RelativeLayout ll;
    String video_id = null;
    WebView webView = null;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub

        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        ll = new RelativeLayout(this);
        //ll.setBackgroundColor(Color.RED);
        RelativeLayout.LayoutParams layoutParameteres = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400);
        ll.setBackgroundColor(Color.argb(66, 160, 160, 160));
        ll.setLayoutParams(layoutParameteres);

        webView = new WebView(this);



        webView.getSettings().setJavaScriptEnabled(true);

        webView.setScrollContainer(false);


        final LayoutParams parameters = new LayoutParams(
                600, 650, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        parameters.gravity = Gravity.CENTER | Gravity.CENTER;
        parameters.x = 0;
        parameters.y = 100;


        final LayoutParams webParameters = new LayoutParams(
                LayoutParams.MATCH_PARENT, 550, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        webParameters.gravity = Gravity.CENTER | Gravity.CENTER;
        webParameters.x = 0;
        webParameters.y = 0;

        Button stop = new Button(this);
        stop.setText("X");

        Button move = new Button(this);
        move.setBackgroundResource(R.drawable.move);


        ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(100, 100);
        ViewGroup.LayoutParams moveButtonParameters = new ViewGroup.LayoutParams(100, 100);

        stop.setY(545f);
        stop.setX(510f);

        move.setY(545f);
        move.setX(200f);



        webView.setLayoutParams(webParameters);

        move.setLayoutParams(moveButtonParameters);
        stop.setLayoutParams(btnParameters);
        //webView.setLayoutParams(webParameters);
        ll.addView(webView);
        ll.addView(stop);
        ll.addView(move);
        wm.addView(ll, parameters);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                //System.out.println("hello");
                return true;
            }
        });


        move.setOnTouchListener(new View.OnTouchListener() {
            LayoutParams updatedParameters = parameters;
            double x;
            double y;
            double pressedX;
            double pressedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));

                        wm.updateViewLayout(ll, updatedParameters);

                    default:
                        break;
                }

                return false;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(ll);
                stopSelf();
                //System.exit(0);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        video_id = intent.getStringExtra(helper.VIDEO_ID);
        Log.e("video_id1234", video_id);
        webView.loadUrl("file:///android_asset/index.html?"+video_id);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

}