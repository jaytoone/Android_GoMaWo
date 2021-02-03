package com.project.gomawo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate{

    private AdView mAdView;
    private ImageView resultView;
    // 52.78.189.194
    //  15.165.61.114
    String UPLOAD_URL = "http://52.78.189.194:80/predict/";

    String imageurl;
    String predict_value;
    String error_msg;
    boolean error;

    String imagePath;
    int value;

    TextView dialogClose, dialogErrorClose, percentage, text;

    CustomProgressDialog dialog_progress;
    Dialog dialog, dialog_error;

    BackgroundTask task;

    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();
    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    public static class CustomProgressDialog extends Dialog{
        CustomProgressDialog(Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
            setContentView(R.layout.progress_guide); // 다이얼로그에 박을 레이아웃
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //          CUSTOM PROGRESS DIALOG         //
        dialog_progress = new CustomProgressDialog(this);
        dialog_progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog_progress.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog_progress.setCancelable(false);
        dialog_progress.setCanceledOnTouchOutside(false);
        dialog_progress.show();

        setContentView(R.layout.activity_result);

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


        Button recamButton = (Button) findViewById(R.id.btn_recam);
        Button exitButton = (Button) findViewById(R.id.btn_exit);

        resultView = findViewById(R.id.ResultView);

        Intent intent = getIntent();
        imagePath = intent.getExtras().getString("imagePath");
        System.out.println("imagePath in ResultActivity :" + imagePath);

        //          에측값을 알릴 DIALOG SET        //
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.prediction);

        dialogClose = dialog.findViewById(R.id.dialog_close_btn);
        percentage = (TextView) dialog.findViewById(R.id.dialog_percentage);
        text = (TextView) dialog.findViewById(R.id.dialog_msg);

        //          ERROR 알릴 DIALOG SET        //
        dialog_error = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_error.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_error.setContentView(R.layout.error);

        dialogErrorClose = dialog_error.findViewById(R.id.dialog_close_btn);

        //          BACKTASK 결과 받아오기 시작          //
        error = false;
        task = new BackgroundTask();
        task.execute(imagePath);

//        uploadMultipart(imagePath);
//        while (imageurl == null || predict_value == null) {
//            this.onPause();
//            } this.onResume();


//        String imageurl = intent.getExtras().getString("imageurl");
//        String predict_value = intent.getExtras().getString("predict_value");
//        System.out.println("imageurl :" + imageurl);
//        System.out.println("predict_value :" + predict_value);

//        String p_value_percentage = (int) Float.parseFloat(predict_value) + "%";
//        percentage.setText(p_value_percentage);
//        text.setText("익었습니다.");
//        dialog.show();

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialogErrorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_error.dismiss();
            }
        });

        recamButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);
                ResultActivity.this.startActivity(mainIntent);
                finish();
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent exitIntent = new Intent(ResultActivity.this, ExitActivity.class);
                ResultActivity.this.startActivity(exitIntent);
            }
        });
    }

    Long start_time;
    Long end_time;

    public void uploadMultipart(String imagePath) {
        //getting name for the image
        String name = "test_image";

        //getting the actual path of the image

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(imagePath, "photo") //Adding file
                    .addParameter("title", name) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            start_time = System.currentTimeMillis();
            System.out.println("#             File Uploaded         #");


        } catch (Exception exc) {
//            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("GoMaWo", "Exception in uploadMultipart : " + exc.getMessage());
        }
    }

    @Override
    public void onProgress(int progress) {
        //your implementation
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        //your implementation
    }

    @Override
    public void onError(Exception exception) {
        //your implementation
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        //your implementation
        String response = new String(serverResponseBody, StandardCharsets.UTF_8).replace("\"", "");
        try {
            String[] response_list = response.split(",");

//            imageurl = "http://15.165.61.114:8080" + response_list[0];
            imageurl = response_list[0];
            predict_value = response_list[1];

            end_time = System.currentTimeMillis();
            System.out.println("#           RESPONSE            #");
            System.out.println("response :" + response);
            System.out.println("imageurl :" + imageurl);
            System.out.println("predict_value :" + predict_value);
            System.out.println("Elapsed time : " + (end_time - start_time) / 1000.0 + "seconds");
            System.out.println("#-------------------------------#");
        }
        catch (Exception e){
            error = true;
            error_msg = response;
            System.out.println("#           RESPONSE            #");
            System.out.println("Error occured :" + error_msg);
            System.out.println("#-------------------------------#");
        }
    }

    @Override
    public void onCancelled() {
        //your implementation
    }

    class BackgroundTask extends AsyncTask<String , Integer , Integer> {
        //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
        protected void onPreExecute() {
        }

        //스레드의 백그라운드 작업 구현
        //여기서 매개변수 Intger ... values란 values란 이름의 Integer배열이라 생각하면된다.
        //배열이라 여러개를 받을 수 도 있다. ex) excute(100, 10, 20, 30); 이런식으로 전달 받으면 된다.
        protected Integer doInBackground(String... values) {

            uploadMultipart(imagePath);
            while (imageurl == null && predict_value == null && !error) {
                Thread.yield();  // 예측값 반환될 때까지 Thread 대기 상태 만들기
            }
            dialog_progress.dismiss();

            return value;
        }
                //UI작업 관련 작업 (백그라운드 실행중 이 메소드를 통해 UI작업을 할 수 있다)
        //publishProgress(value)의 value를 값으로 받는다.values는 배열이라 여러개 받기가능
        protected void onProgressUpdate(Integer ... values) {
        }


        //이 Task에서(즉 이 스레드에서) 수행되던 작업이 종료되었을 때 호출됨
        protected void onPostExecute(Integer result) {


            // 에러가 아니면
            if (!error){
                String p_value_percentage = (int) Float.parseFloat(predict_value) + "%";
                percentage.setText(p_value_percentage);
                text.setText("익었습니다.");

                Picasso.get().load(imageurl).into(resultView);
                dialog.show();

//                Bitmap img = BitmapFactory.decodeStream(in);
//                in.close();
//                imageview.setImageBitmap(img);

            } else {
                dialog_error.show();
            }

        }

        //Task가 취소되었을때 호출
        protected void onCancelled() {
        }
    }

}
