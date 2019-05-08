package com.biotag.vsstaffedition;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class ScanningActivity extends AppCompatActivity {

    private ImageView iv_nfc;
    private ConstraintLayout ctl;
    private Context context = this;
    private String vehiclenumber;
    public static final int REQUEST_PERMISSION_CAMERA = 1;
    public static final int REQUEST_BARCODE = 2;

    private long mExittime;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scanning);

        //权限问题
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED) {
            //申请了两种权限：WRITE_EXTERNAL_STORAGE与 CAMERA 权限
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            return;
        }


        getAuthorityCamera();
        initView();
    }


    private void getAuthorityCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanningActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            return;
        }

    }


    private void initView() {

        iv_nfc = (ImageView)findViewById(R.id.iv_nfc);
        iv_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NFCScanActivity.class);
                startActivity(intent);
            }
        });
        ctl = (ConstraintLayout) findViewById(R.id.ctl);
        ctl.setBackgroundResource(R.color.pink);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            if(System.currentTimeMillis()-mExittime>2000){
                Toast.makeText(context, "再按一次退出App", Toast.LENGTH_SHORT).show();
                mExittime = System.currentTimeMillis();
            }else {
                Toast.makeText(context, "退出App", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
