package com.project.gomawo;

import android.Manifest;
import android.app.AutomaticZenRule;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.RequiresApi;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;

//import net.gotev.uploadservice.MultipartUploadRequest;
//import net.gotev.uploadservice.UploadNotificationConfig;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "GoMaWo";
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK; // Camera.CameraInfo.CAMERA_FACING_FRONT


    private SurfaceView surfaceView;
    private ImageView imageView;
    private CameraPreview mCameraPreview;
    private AdView mAdView;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    Button uploadBtn, selectBtn;
    ImageView imageview;
    File f;

    private String userChoosenTask;
    Uri imageUri;
    String imagePath;

    boolean Cam_version;

    private Service mService;
    public static boolean TFLAG = false;


//    Long start_time;
//    Long end_time;

//    String imageurl;
//    String predict_value;

//    BackgroundTask task;
//    ProgressDialog dialog_progress;

//    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();
//    @Override
//    protected void onResume() {
//        super.onResume();
//        uploadReceiver.register(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        uploadReceiver.unregister(this);
//    }


    //    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 화면 켜진 상태를 유지합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationChannel notificationChannel = new NotificationChannel("channel1", "1번채널", NotificationManager.IMPORTANCE_DEFAULT);
//        notificationChannel.setDescription("1번채널입니다");
//        notificationChannel.enableLights(true);
//        notificationChannel.setLightColor(Color.GREEN);
//        notificationChannel.enableVibration(true);
//        notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
//        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        notificationManager.createNotificationChannel(notificationChannel);

//        TFLAG = true;
//        Intent backStartIntent = new Intent(MainActivity.this, TestBackgroundService.class);
//        backStartIntent.setAction("Action1");
//        startForegroundService(backStartIntent);


        //          DIALOG로 FOCUSING 안내하기        //
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.focusing_guide);

        TextView dialogClose = dialog.findViewById(R.id.dialog_close_btn);
        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

        Cam_version = true;
        if (Cam_version) {
            //          CAM VERSION & ADMOB BANNER        //
            setContentView(R.layout.activity_main);

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

            mLayout = findViewById(R.id.layout_main);
            surfaceView = findViewById(R.id.camera_preview_main);
            imageView = findViewById(R.id.imageView);

            //   런타임 퍼미션 완료될때 까지 화면에서 보이지 않게 해야합니다.
            surfaceView.setVisibility(View.GONE);

            final Button btn_to_cam = findViewById(R.id.button_main_capture);
            final Button btn_to_recam = findViewById(R.id.button_recam);
            final Button btn_to_result = findViewById(R.id.button_to_result);
            final TableLayout btn_container = findViewById(R.id.button_container);

            mLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mCameraPreview.focusing();
                }
            });

            btn_to_cam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagePath = mCameraPreview.takePicture();
    //                something = mCameraPreview.onMeasure();
                    System.out.println("imagePath in MainActivity : " + imagePath);
                    btn_to_cam.setVisibility(View.GONE);
                    btn_container.setVisibility(View.VISIBLE);
                }
            });
            // Recam Button 누를 시 초기화 작업을 진행해주면 된다. (앱 SurfaceCamera 시작 화면 띄워주기)
            btn_to_recam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            btn_to_result.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    TFLAG = true;
//                    Intent backStartIntent = new Intent(MainActivity.this, TestBackgroundService.class);
//                    backStartIntent.setAction("Action1");
//                    startForegroundService(backStartIntent);
//                    if (Build.VERSION.SDK_INT >= 26) {
//                        System.out.println("version checked");
//                        startMyOwnForeground();
//                    }
//                    else {
//                        mService.startForeground(2, new Notification());
//                    }
                    Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                    resultIntent.putExtra("imagePath", imagePath);
                    MainActivity.this.startActivity(resultIntent);
    //                task = new BackgroundTask();
    //                task.execute(imagePath);
    //                task.cancel(true);
                }
            });
        } else {
            //          UPLOAD TESTING VERSION          //
            setContentView(R.layout.activity_main2);
            uploadBtn = (Button) findViewById(R.id.uploadBtn);
            selectBtn = (Button) findViewById(R.id.selectBtn);
            imageview = (ImageView) findViewById(R.id.imageview1);

            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                    resultIntent.putExtra("imagePath", imagePath);
                    MainActivity.this.startActivity(resultIntent);
                }
            });
            selectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                }
            });
        };

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);


            if (cameraPermission == PackageManager.PERMISSION_GRANTED
                    && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED
                    && readExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                if (Cam_version) {startCamera();};


            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {

                    Snackbar.make(mLayout, "이 앱을 실행하려면 카메라와 외부 저장소 접근 권한이 필요합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                                    PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();


                } else {
                    // 2. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }

            }

        } else {

            final Snackbar snackbar = Snackbar.make(mLayout, "디바이스가 카메라를 지원하지 않습니다.",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("확인", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }


    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void startMyOwnForeground() {
////        Intent notificationIntent = new Intent(this, MainActivity.class);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        String NOTIFICATION_CHANNEL_ID = "com.project.gomawo";
//        String channelName = "My Background Service";
//        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
//        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//        manager.createNotificationChannel(chan);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.pig_icon)
//                .setContentTitle("App is running in background")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        System.out.println("App is running in background");
//        mService.startForeground(2, notification);
//
//    }

    void startCamera() {

        // Create the Preview view and set it as the content of this Activity.
        mCameraPreview = new CameraPreview(this, this, CAMERA_FACING, surfaceView, imageView);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if (requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                if (Cam_version) {
                    startCamera();};
            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {

                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {

                    Snackbar.make(mLayout, "설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    imageview.setImageBitmap(img);

                    // 이미지 데이터를 파일 변수에 저장
                    imageUri = data.getData();
                    System.out.println("imageUri : " + imageUri);
                    imagePath = getPath(imageUri);
                    System.out.println("imagePath : " + imagePath);

                    f = new File(imagePath);
                    System.out.println("f.getName() : " + f.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

//    public void uploadMultipart(String imagePath) {
//        //getting name for the image
//        String name = "test_image";
//
//        //getting the actual path of the image
//
//        //Uploading code
//        try {
//            String uploadId = UUID.randomUUID().toString();
//            uploadReceiver.setDelegate(this);
//            uploadReceiver.setUploadID(uploadId);
//
//            //Creating a multi part request
//            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
//                    .addFileToUpload(imagePath, "photo") //Adding file
//                    .addParameter("title", name) //Adding text parameter to the request
//                    .setNotificationConfig(new UploadNotificationConfig())
//                    .setMaxRetries(2)
//                    .startUpload(); //Starting the upload
//            start_time = System.currentTimeMillis();
//            System.out.println("#             File Uploaded         #");
//
//
//        } catch (Exception exc) {
////            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Exception in uploadMultipart : " + exc.getMessage());
//        }
//    }
//
//    @Override
//    public void onProgress(int progress) {
//        //your implementation
//    }
//
//    @Override
//    public void onProgress(long uploadedBytes, long totalBytes) {
//        //your implementation
//    }
//
//    @Override
//    public void onError(Exception exception) {
//        //your implementation
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
//        //your implementation
//        String response = new String(serverResponseBody, StandardCharsets.UTF_8).replace("\"", "");
//        try {
//            String[] response_list = response.split(",");
//
//            imageurl = "http://52.78.189.194:8080" + response_list[0];
//            predict_value = response_list[1];
//
//            end_time = System.currentTimeMillis();
//            System.out.println("#           RESPONSE            #");
//            System.out.println("response :" + response);
//            System.out.println("imageurl :" + imageurl);
//            System.out.println("predict_value :" + predict_value);
//            System.out.println("Elapsed time : " + (end_time - start_time) / 1000.0 + "seconds");
//            System.out.println("#-------------------------------#");
//        }
//        catch (Exception e){
//            String error_msg = response;
//            System.out.println("#           RESPONSE            #");
//            System.out.println("Error occured :" + error_msg);
//            System.out.println("#-------------------------------#");
//        }
//    }
//
//    @Override
//    public void onCancelled() {
//        //your implementation
//    }
//
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


//    class BackgroundTask extends AsyncTask<String , Integer , Integer> {
//        //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
//        protected void onPreExecute() {
//        }
//
//        //스레드의 백그라운드 작업 구현
//        //여기서 매개변수 Intger ... values란 values란 이름의 Integer배열이라 생각하면된다.
//        //배열이라 여러개를 받을 수 도 있다. ex) excute(100, 10, 20, 30); 이런식으로 전달 받으면 된다.
//        protected Integer doInBackground(String ... values) {
//
//                    uploadMultipart(imagePath);
//                    while (imageurl==null || predict_value==null) {
//                        Thread.yield();  // 예측값 반환될 때까지 Thread 대기 상태 만들기
//                    }
//
//
//            return value;
//        }
//
//        //UI작업 관련 작업 (백그라운드 실행중 이 메소드를 통해 UI작업을 할 수 있다)
//        //publishProgress(value)의 value를 값으로 받는다.values는 배열이라 여러개 받기가능
//        protected void onProgressUpdate(Integer ... values) {
//        }
//
//
//        //이 Task에서(즉 이 스레드에서) 수행되던 작업이 종료되었을 때 호출됨
//        protected void onPostExecute(Integer result) {
//        }
//
//        //Task가 취소되었을때 호출
//        protected void onCancelled() {
//        }
//    }

}
