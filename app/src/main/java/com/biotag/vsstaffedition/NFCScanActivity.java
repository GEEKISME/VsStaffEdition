package com.biotag.vsstaffedition;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biotag.vsstaffedition.NFC.CardInfo;
import com.biotag.vsstaffedition.NFC.Constants;
import com.biotag.vsstaffedition.NFC.NFCTool;
import com.biotag.vsstaffedition.NFC.Utils;
import com.biotag.vsstaffedition.view.RadiationView;

import java.io.File;
import java.text.SimpleDateFormat;

public class NFCScanActivity extends BaseNFCActivity implements View.OnClickListener {
    private final int MSG_NFCREAD_FAIL = 1;
    private final int MSG_NFCREAD_SUCCESS = 2;
    private final int MSG_DENNIED = 3;
	private final int MSG_UPLOADSUC = 4;
    private final int MSG_DENIED_FAULT_AREA = 31;
    private final int MSG_DENIED_HAS_ENTERED = 32;
    private final String TAG = "tms";
    private NFCScanHandler mHandler = new NFCScanHandler();
    private ImageView iv_headImg, iv_rotate;
    private LinearLayout ll_credential, ll_access, ll_wrapinfo, ll_company, ll_name;
    //    private LinearLayout ll_lastmodified;
    private TextView tv_approved, tv_denied, tv_access, tv_access2,tv_credential, tv_company, tv_name;
    //    private TextView tv_lastmodified;
    private Button btn_back, btn_scanneron;
    private RadiationView rv;
    private FrameLayout fl_anim;
    private RelativeLayout rl, rl_wrong;
    private String staffphotourl = "";
    private RelativeLayout rl_title;
    private TextView tv_authority;

    private MydatabaseHelper dbStaffidHelper;
    private MyDataBaseInOutHelper dbInandOutHelper;
    private SQLiteDatabase   db,inandoutdb;

    private String imgpath;
    private String iscomein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nfcscan);
        DataBaseUtils.importdatabasefromassets(this,"StaffBook.db");//提前导入数据库,将asset中的db文件写入到该app的内存区，此段代码之后就可以正常的操作数据库了
        DataBaseUtils.importdatabasefromassets(this,"InandOut.db");//提前导入数据库,将asset中的db文件写入到该app的内存区，此段代码之后就可以正常的操作数据库了

        dbStaffidHelper = MydatabaseHelper.getInstance(this);
        dbInandOutHelper = MyDataBaseInOutHelper.getInstance(this);
        db = dbStaffidHelper.getReadableDatabase();
        inandoutdb = dbInandOutHelper.getReadableDatabase();
//        Cursor cursor = db.query("Book",new String[]{"staffid","imgpath","iscomein"},"staffid = ?",
//                new String[]{"DRC57688"},null,null,null);
//        if(cursor.moveToFirst()){
//            imgpath = cursor.getString(cursor.getColumnIndex("imgpath"));
//            iscomein = cursor.getString(cursor.getColumnIndex("iscomein"));
//            Log.i(Constants.TAG,"得到的路径是  "+ imgpath+ "  得到的iscomein是 "+iscomein);
//        }
//        cursor.close();
//
//        ContentValues values = new ContentValues();
//        values.put("iscomein","1");
//        db.update("Book",values,"staffid = ?",new String[]{"DRC57688"});
//        Log.i(Constants.TAG,"修改成功");
        //=================================
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        String[] techList = detectedTag.getTechList();
        android.util.Log.i(TAG, "onNewIntent: techList length = " + techList.length);
        for (int i = 0; i < techList.length; i++) {
            android.util.Log.i(TAG, "onNewIntent: techList[" + i + "] = " + techList[i]);
        }

        NFCToolThread nfcToolThread = new NFCToolThread(detectedTag);
        nfcToolThread.start();
    }

    private void initView() {
        rl_title = (RelativeLayout)findViewById(R.id.rl_title);
        tv_authority = (TextView)findViewById(R.id.tv_authority);
        rl = (RelativeLayout) findViewById(R.id.rl);
        rl_wrong = (RelativeLayout) findViewById(R.id.rl_wrong);
        rl_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rl_wrong.getVisibility() == View.VISIBLE) {
                    rl_wrong.setVisibility(View.GONE);
//                    fl_anim.setVisibility(View.VISIBLE);
//                    rl_title.setVisibility(View.GONE);
//                    rl.setVisibility(View.GONE);
                }
            }
        });
        iv_headImg = (ImageView) findViewById(R.id.iv_headImg);
        tv_name = (TextView) findViewById(R.id.tv_name);
        ll_name = (LinearLayout) findViewById(R.id.ll_name);
        tv_company = (TextView) findViewById(R.id.tv_company);
        ll_company = (LinearLayout) findViewById(R.id.ll_company);
        tv_credential = (TextView) findViewById(R.id.tv_credential);
        ll_credential = (LinearLayout) findViewById(R.id.ll_credential);
        tv_access = (TextView) findViewById(R.id.tv_access);
        tv_access2 = (TextView) findViewById(R.id.tv_access2);
        ll_access = (LinearLayout) findViewById(R.id.ll_access);
//        tv_lastmodified = (TextView) findViewById(R.id.tv_lastmodified);
//        ll_lastmodified = (LinearLayout) findViewById(R.id.ll_lastmodified);
        ll_wrapinfo = (LinearLayout) findViewById(R.id.ll_wrapinfo);
        tv_approved = (TextView) findViewById(R.id.tv_approved);
        tv_denied = (TextView) findViewById(R.id.denied);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_scanneron = (Button) findViewById(R.id.btn_scanneron);
        fl_anim = (FrameLayout) findViewById(R.id.fl_anim);
        iv_rotate = (ImageView) findViewById(R.id.iv_rotate);
        rv = (RadiationView) findViewById(R.id.rv);
        rv.setMinRadius(70);
        rv.startRadiate();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate_circle_anim);
        iv_rotate.startAnimation(anim);


        btn_back.setOnClickListener(this);
        btn_scanneron.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_scanneron:
                rl_title.setVisibility(View.GONE);
                rl.setVisibility(View.GONE);
                fl_anim.setVisibility(View.VISIBLE);
                break;
        }
    }


    //=================+++++++++++++++++++++++++++++++++++++++

    class NFCToolThread extends Thread {

        private Tag tag = null;

        public NFCToolThread(Tag tag) {
            this.tag = tag;
        }

        @Override
        public void run() {
            if (tag == null) {
                mHandler.sendEmptyMessage(MSG_NFCREAD_FAIL);
                return;
            }
            NFCTool nfcRead = new NFCTool();
            CardInfo cardInfo = nfcRead.readTag(tag);
            if (cardInfo == null) {
                Log.i(TAG, "run: MSG_NFCREAD_FAIL");
                mHandler.sendEmptyMessage(MSG_NFCREAD_FAIL);
            } else {
                Log.i(TAG, "run: MSG_NFCREAD_SUCCESS");
                cardInfo.printInfo();

                boolean writeResult = false;
                boolean isDeniedWrongArea = false;
                boolean isDeniedHasEntered = false;

                String AreaNo = cardInfo.getAreaNo();
                String settingAreaNo = SharedPreferencesUtils.getString(NFCScanActivity.this,"dischosed","").split(" ")[0];
                String func = SharedPreferencesUtils.getString(NFCScanActivity.this,"funcchosed","");
                String AreaNow = cardInfo.getAreaNow();
                android.util.Log.i(TAG, "run: settingAreaNo = " + settingAreaNo);
                android.util.Log.i(TAG, "run: func = " + func);
                if (cardInfo.getCardType() == Constants.CHIP_WRISTSTRAP) {
                    mHandler.removeMessages(MSG_NFCREAD_SUCCESS);
                    Message msg = mHandler.obtainMessage(MSG_NFCREAD_SUCCESS, cardInfo);
                    mHandler.sendMessage(msg);
                }else{
                    mHandler.removeMessages(MSG_DENIED_FAULT_AREA);
                    Message msg = mHandler.obtainMessage(MSG_DENIED_FAULT_AREA, cardInfo);
                    mHandler.sendMessage(msg);
                }
//                if (AreaNow != null && AreaNo != null && Utils.checkArea(AreaNo,settingAreaNo)) {
//                    if(func.equals("检票")){
//                        if(cardInfo.getAreaNow().trim().equals("")){
//                            writeResult = nfcRead.WriteAreaNow(settingAreaNo);
//                            Log.i(Constants.TAG,"writeResult is "+writeResult);
//
//                        }else{
//                            isDeniedHasEntered = true;
//                        }
//                    }else{
//                        writeResult = true;
//                    }
//
//                }else{
//                    isDeniedWrongArea = true;
//                }
//                if(writeResult){
//                    mHandler.removeMessages(MSG_NFCREAD_SUCCESS);
//                    Message msg = mHandler.obtainMessage(MSG_NFCREAD_SUCCESS, cardInfo);
//                    mHandler.sendMessage(msg);
//                }else if(isDeniedWrongArea){
//                    mHandler.removeMessages(MSG_DENIED_FAULT_AREA);
//                    Message msg = mHandler.obtainMessage(MSG_DENIED_FAULT_AREA, cardInfo);
//                    mHandler.sendMessage(msg);
//                }else if(isDeniedHasEntered){
//                    mHandler.removeMessages(MSG_DENIED_HAS_ENTERED);
//                    Message msg = mHandler.obtainMessage(MSG_DENIED_HAS_ENTERED, cardInfo);
//                    mHandler.sendMessage(msg);
//                }
            }
        }


    }


    class NFCScanHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_NFCREAD_FAIL) {
                readCardFail();
            } else if (msg.what == MSG_NFCREAD_SUCCESS) {
                CardInfo cardInfo = (CardInfo) msg.obj;
                String areano = SharedPreferencesUtils.getString(NFCScanActivity.this,"dischosed","");
                try{
                    //=====对inandout表的操作
                    Cursor cursorinandout = inandoutdb.query("Inoutinfo",new String[]{"StaffID"},"StaffID = ?",
                            new String[]{cardInfo.getID().trim()},null,null,null);
                    long current = System.currentTimeMillis();
                    SimpleDateFormat sds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currenttime = sds.format(current);
                    if(cursorinandout.moveToFirst()){
                        //进行更新操作
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Action_Type",0);
                        contentValues.put("ActionTime",currenttime);
                        inandoutdb.update("Inoutinfo",contentValues,"StaffId = ?",new String[]{cardInfo.getID()});

                    }else {
                        ContentValues values = new ContentValues();
                        values.put("StaffID",cardInfo.getID());
                        values.put("ChipCode",cardInfo.getIdCard());
                        values.put("AreaNo",areano);
                        values.put("Action_Type","1");
                        values.put("ActionTime",currenttime);
                        inandoutdb.insert("Inoutinfo",null,values);
                    }
                    cursorinandout.close();
                    //先检查该卡的staffid 对应的iscomein  值是  0 还是1 ,  如果是1 代表这个员工已经入场则不可以再次入场
                    Cursor cursor = db.query("Book",new String[]{"staffid","iscomein"},"staffid = ?",
                            new String[]{cardInfo.getID().trim()},null,null,null);
                    if(cursor.moveToFirst()){
                        do {
                            iscomein = cursor.getString(cursor.getColumnIndex("iscomein"));
                            imgpath  = cursor.getString(cursor.getColumnIndex("imgpath"));

                        }while (cursor.moveToNext());
                    }
                    cursor.close();
                    if(iscomein!=null&&iscomein.equals("0")){//如果是0，则展示卡的信信息并将数据库中的iscomein 改为1
                        //showCardInfo(cardInfo, true,msg.what);
                        ContentValues values = new ContentValues();
                        values.put("iscomein","1");
                        db.update("Book",values,"staffid = ?",new String[]{cardInfo.getID()});
                    }else {
//                        mHandler.sendEmptyMessage(MSG_DENIED_HAS_ENTERED);
//                        showCardInfo(cardInfo,false,MSG_DENIED_HAS_ENTERED);
                    }
                    showCardInfo(cardInfo, true,msg.what);
                }catch (Exception e){
                    readCardFail();
                }

            } else if (msg.what == MSG_DENIED_FAULT_AREA || msg.what == MSG_DENIED_HAS_ENTERED) {
                CardInfo cardInfo = (CardInfo) msg.obj;
                try{
                    showCardInfo(cardInfo, false,msg.what);
                }catch (Exception e){
                    readCardFail();
                }
            } else if (msg.what == MSG_UPLOADSUC){
//                Toast.makeText(NFCScanActivity.this, "信息上传成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readCardFail() {
//        tv_name.setText("");
//        tv_company.setText("");
//        tv_credential.setText("");
//        tv_access.setText("");
//
//        tv_approved.setText("READ FAILED");
//        tv_approved.setTextColor(Color.BLACK);
        rl_wrong.setVisibility(View.VISIBLE);

    }

    private void showCardInfo(CardInfo cardInfo, boolean isAllow,int code) {
        Log.i(Constants.TAG, "cardinfo = " + cardInfo.toString());
        Log.i(Constants.TAG, "isallow shi " + isAllow);
        rl_wrong.setVisibility(View.GONE);
        fl_anim.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
        rl_title.setVisibility(View.VISIBLE);
        tv_authority.setText(SharedPreferencesUtils.getString(NFCScanActivity.this,"dischosed","ALL ACCESS"));
        tv_name.setText(cardInfo.getStaffName());
//        tv_company.setText(cardInfo.getCompanyName());
//        tv_credential.setText(cardInfo.getStaffNo());

        String companyName = cardInfo.getCompanyName();
        if(companyName == null || companyName.equals("")){
            ll_company.setVisibility(View.GONE);
        }else{
            ll_company.setVisibility(View.VISIBLE);
            tv_company.setText(companyName);
        }
        String staffNo = cardInfo.getStaffNo();
        if(staffNo == null || staffNo.equals("")){
            ll_credential.setVisibility(View.GONE);
        }else{
            ll_credential.setVisibility(View.VISIBLE);
            tv_credential.setText(staffNo);
        }

        String AreaNo = cardInfo.getAreaNo();
        AreaNo = Utils.convertAreaToDisplay(AreaNo);
//        if(AreaNo != null && AreaNo.equals("A")){
//            AreaNo = "H M B F S O";
//        }
        tv_access.setText(AreaNo);

        AreaNo = cardInfo.getAreaNo();
        AreaNo = Utils.dealAreaNo(AreaNo);
        tv_access2.setText(AreaNo);

        //=====+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        try{
            Cursor cursor = db.query("Book",new String[]{"staffid","imgpath"},"staffid = ?",
                    new String[]{cardInfo.getID().trim()},null,null,null);
            if(cursor.moveToFirst()){
                do {
                    imgpath = cursor.getString(cursor.getColumnIndex("imgpath"));
                    Log.i(Constants.TAG,"图片的路径是 "+ imgpath);
                }while (cursor.moveToNext());
            }
            cursor.close();

            File hesdImg = new File(imgpath);
            if(hesdImg.exists()){
                Bitmap bit = BitmapFactory.decodeFile(imgpath);
                iv_headImg.setImageBitmap(bit);
            }
        }catch(Exception e){

        }
        //================++++++++++++++++++++++++++++++++++++++++++++++++
        //加载图片时首先去VictoriaSecretBackUp文件夹找是否有对应id的图片，如果没有的话再去网络上去下载
//        File headImgLocal = new File(headImgBackUp, cardInfo.getID() + ".jpg");
////        File headImgLocal = new File(headImgBackUp, "EAB06189-2313-43FB-8641-5F56E2006849"+ ".jpg");
//        if (headImgLocal.exists()) {
//            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/VehiclenumberscanningBackUp/" + cardInfo.getID().trim()+ ".jpg");
//            iv_headImg.setImageBitmap(bitmap);
//            Log.i(Constants.TAG,"laiziwenjian");
//        } else {
////            staffphotourl = Constants.URL_GETSTAFFPHOTO;
////            staffphotourl = replacememgtUrl(staffphotourl, "{staffid}", cardInfo.getID().trim());
//
//            staffphotourl = Constants.URL_GETSTAFFPHOTO2;
//            staffphotourl = replacememgtUrl(staffphotourl, "{path}", cardInfo.getImageUrl());
//            Log.i(Constants.TAG, "url is" + staffphotourl);
//            Picasso.with(NFCScanActivity.this).load(staffphotourl).placeholder(R.mipmap.user).error(R.mipmap.user).into(iv_headImg);
//        }
        tv_approved.setVisibility(View.VISIBLE);
        if (isAllow) {
            tv_approved.setVisibility(View.VISIBLE);
            tv_approved.setText("Approved");
            tv_approved.setTextColor(Color.GREEN);
            tv_denied.setVisibility(View.INVISIBLE);
        } else {
            tv_approved.setVisibility(View.INVISIBLE);
            tv_denied.setVisibility(View.VISIBLE);
            if(code == MSG_DENIED_FAULT_AREA){
                tv_denied.setText("Denied(不是手环)");
            }
//            else if(code == MSG_DENIED_HAS_ENTERED){
//                tv_denied.setText("Denied(失效)");
//            }
            else {
                tv_denied.setText("Denied");
            }
        }

    }

}
