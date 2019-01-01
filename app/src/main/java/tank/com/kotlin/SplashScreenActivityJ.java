package tank.com.kotlin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import tank.com.kotlin.model.VideoCacheBean;
import tank.com.kotlin.utils.VideoCacheDBUtil;

public class SplashScreenActivityJ extends AppCompatActivity {

    private static final Long MILLIS_IN_FUTURE = 5000l;

    private static final Long COUNT_DOWN_INTERVAL = 1000l;

    private static final Long MILLS_IN_SKIP = 3000l;

    private static final String TAG = SplashScreenActivityJ.class.getSimpleName();

    private final CountDownTimer countDownTimer = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {

        @Override
        public void onTick(long l) {
            Log.i(TAG, "onTick");
            if (MILLS_IN_SKIP > l) {
                remainTimeTV.setText("点击跳过");
                if (!remainTimeTV.hasOnClickListeners()) {
                    remainTimeTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startMainActivity();
                            cancel();
                        }
                    });
                }

            } else {
                remainTimeTV.setText(String.format(Locale.getDefault(), "剩余%d秒可跳过", (int) (l - MILLS_IN_SKIP) / 1000));
            }
        }

        @Override
        public void onFinish() {
            Log.i(TAG, "onFinish");
            startMainActivity();
        }
    };

    private TextView remainTimeTV = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        remainTimeTV = findViewById(R.id.remainTimeTv);
        countDownTimer.start();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isfullScreen(Activity activity) {
        int flags = activity.getWindow().getAttributes().flags;
        return (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;

    }



}
