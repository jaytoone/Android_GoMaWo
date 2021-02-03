package com.project.gomawo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class ExitActivity extends Activity {

    private AdView mAdView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 팝업이 올라오면 배경 블러처리
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);

        // 레이아웃 설정
        setContentView(R.layout.activity_exit);
        //           광고 배너 달기         //
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.LARGE_BANNER);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));

//        // 사이즈조절
//        // 1. 디스플레이 화면 사이즈 구하기
//        Display dp = ((WindowManager) Objects.requireNonNull(getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
//
//        // 2. 화면 비율 설정
//        Point size = new Point();
//        dp.getSize(size);
//        int width = (int)(size.x*0.7);
//        int height = (int)(size.y*0.7);
//        // 3. 현재 화면에 적용
//        getWindow().getAttributes().width = width;
//        getWindow().getAttributes().height = height;

        // 액티비티 바깥화면이 클릭되어도 종료되지 않게 설정하기
        this.setFinishOnTouchOutside(false);

        Button exit_cancel_btn = (Button) findViewById(R.id.exit_cancle);
        Button exit_confirm_btn = (Button) findViewById(R.id.exit_confirm);

        exit_cancel_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        exit_confirm_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

    }
}
