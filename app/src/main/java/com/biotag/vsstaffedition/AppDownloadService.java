package com.biotag.vsstaffedition;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.biotag.vsstaffedition.NFC.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lxh on 2017/11/7.
 */

public class AppDownloadService extends Service {

    public static final String VSSTAFFEDITION_APK = "VsStaffEdition.apk";
    private OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String qppurl = intent.getStringExtra("appurl");
        final String newestVersion = intent.getStringExtra("newestVersion");
        Request apkdown = new Request.Builder().url(qppurl).build();
        client.newCall(apkdown).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                File apkfile = new File(Environment.getExternalStorageDirectory()+"/VsStaffEdition/", VSSTAFFEDITION_APK);
                FileOutputStream fos = new FileOutputStream(apkfile);
                byte[] buffer = new byte[1024];
                int num = -1;
                while ((num = is.read(buffer))!=-1){
                    fos.write(buffer,0,num);
                    fos.flush();
                }
                fos.close();
                is.close();
                Log.i(Constants.TAG,"apk 成功下载");
                SharedPreferencesUtils.saveString(getApplicationContext(),"newestVersion",newestVersion);
                SharedPreferencesUtils.saveString(getApplicationContext(),"apkstatus","1");
                Log.i(Constants.TAG,"apkstatus已经被置为1");
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
