package com.example.vaunceqr;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    private SurfaceView surfaceView;
    private QREader qrEader;
    TextView text_result;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //화면꺼짐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.activity_main);

//        textView = (TextView) findViewById(R.id.text);
//
//        String content = textView.getText().toString();
//        SpannableString spannableString = new SpannableString(content);
//
//        String word = "사전등록 후 발급된 \\n             QR코드";
//        int start = content.indexOf(word);
//        int end = start + word.length();

//        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#E94A7E")), start, end,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.setText(spannableString);


        //Request permission

        setupCamera();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (qrEader == null) {
                            return;
                        }
                        if (qrEader.isCameraRunning()) {
                            return;
                        }
                        qrEader.initAndStart(surfaceView);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "You must enable this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    }
                }).check();


    }

    private void setupCamera() {
        text_result = (TextView) findViewById(R.id.code_info);
        surfaceView = (SurfaceView) findViewById(R.id.camera_view);

        setupQREader();

    }

    boolean isSync = false;


    private void setupQREader() {
        qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                text_result.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isSync){
                            text_result.setText(data);
                            isSync = true;
                            showNewToast();

                        }
                        //핸들러 원래 위치

                    }
                });


            }
        }).facing(QREader.FRONT_CAM)
                .enableAutofocus(true)
                .height(surfaceView.getHeight())
                .width(surfaceView.getWidth())
                .build();

  }

    public void showNewToast (){
        Toast toast = new Toast(this);
        View customView = getLayoutInflater().inflate(R.layout.popup,null);
        toast.setView(customView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSync = false;
            }
        },5000);

        Log.e(TAG,"토스트실행");
        
    }



    @Override
    protected void onResume() {
        super.onResume();
        isSync = false;
        if (qrEader.isCameraRunning()) {
            return;
        }
        qrEader.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        qrEader.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrEader.releaseAndCleanup();
    }


}

