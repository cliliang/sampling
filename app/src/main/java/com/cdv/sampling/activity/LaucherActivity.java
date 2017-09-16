package com.cdv.sampling.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.cdv.sampling.R;
import com.cdv.sampling.repository.UserRepository;

public class LaucherActivity extends Activity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laucher);
        hideStatusBar();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if (!UserRepository.isLogin()) {
                    startActivity(LoginActivity.getStartIntent(LaucherActivity.this));
                    return;
                }
                startActivity(new Intent(LaucherActivity.this, MainActivity.class));
            }
        }, 3000);
    }

    private void hideStatusBar() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}
