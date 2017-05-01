package tube.floatView;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
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

import tube.DeveloperKey;

public class FloatingWindow extends Service {

    WindowManager wm;
    RelativeLayout ll;


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
        ll.setBackgroundColor(Color.RED);
        RelativeLayout.LayoutParams layoutParameteres = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400);
        ll.setBackgroundColor(Color.argb(66, 255, 0, 0));
        ll.setLayoutParams(layoutParameteres);

        WebView webView = new WebView(this);

        webView.loadUrl("file:///android_asset/index.html");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setScrollContainer(false);


        final LayoutParams parameters = new LayoutParams(
                600, 600, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        parameters.gravity = Gravity.CENTER | Gravity.CENTER;
        parameters.x = 0;
        parameters.y = 100;


        final LayoutParams webparameters = new LayoutParams(
                600, 500, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        webparameters.gravity = Gravity.CENTER | Gravity.CENTER;
        webparameters.x = 0;
        webparameters.y = 0;

        Button stop = new Button(this);

        stop.setText("X");
        ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);

        stop.setY(500f);

        ViewGroup.LayoutParams webParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);

        webView.setLayoutParams(webparameters);


        stop.setLayoutParams(btnParameters);
        //webView.setLayoutParams(webParameters);
        ll.addView(webView);
        ll.addView(stop);
        wm.addView(ll, parameters);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                System.out.println("hello");
                return true;
            }
        });


        ll.setOnTouchListener(new View.OnTouchListener() {
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
                System.exit(0);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

}