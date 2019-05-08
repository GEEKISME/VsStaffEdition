package com.biotag.vsstaffedition;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biotag.vsstaffedition.NFC.CardInfo;
import com.biotag.vsstaffedition.NFC.Constants;
import com.biotag.vsstaffedition.NFC.NFCTool;
import com.biotag.vsstaffedition.view.RadiationView;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class WelcomActivity extends BaseNFCActivity {

    private ImageView VS;
    private Button btn_login;
    private MydatabaseHelper helper;
    private SQLiteDatabase db;
    private RelativeLayout rl_staffinfo;
    private TextView tv_name, tv_staffno, tv_tips;
    private FrameLayout fl_anim;
    private static final int MSG_NFCREAD_FAIL = 1;
    private static final int MSG_NFCREAD_SUCCESS = 2;
    private static final int MSG_NFCCARD_WRONGTYPE = 3;
    private ImageView iv_rotate;
    private RadiationView rv;
    private Tag detecttag;
    public static final  int NO_NEED     = 10;
    public static final  int NEED        = 11;
    private String areano;
    private String mTagText;

    static class NFCHandler extends Handler {

        private WeakReference<WelcomActivity> welcomActivityWeakReference;

        public NFCHandler (WelcomActivity welcomActivity){
            welcomActivityWeakReference = new WeakReference<WelcomActivity>(welcomActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WelcomActivity welcomActivity = welcomActivityWeakReference.get();
            if(welcomActivity!=null){
                switch (msg.what){
                    case MSG_NFCREAD_FAIL:
                        Toast.makeText(welcomActivity, "读取证件信息失败，再试一次吧 ！", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_NFCREAD_SUCCESS:
                        CardInfo cardInfo = (CardInfo) msg.obj;
                        welcomActivity.fl_anim.setVisibility(View.INVISIBLE);
                        welcomActivity.tv_tips.setVisibility(View.INVISIBLE);
                        welcomActivity.rl_staffinfo.setVisibility(View.VISIBLE);
                        welcomActivity.tv_name.setText(cardInfo.getStaffName());
                        welcomActivity.tv_staffno.setText(cardInfo.getIdCard());
                        welcomActivity.areano = cardInfo.getAreaNo();
                        break;
                    case MSG_NFCCARD_WRONGTYPE:
                        Toast.makeText(welcomActivity, "非工作证", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }
    private NFCHandler nfcHandler = new NFCHandler(this);

//    private int[] datas = new int[1024*1024*10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcom);
        //本机可分配的最大堆内存
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memorysize = am.getMemoryClass();
        Log.i(Constants.TAG,"the size is "+memorysize);
//        helper = new MydatabaseHelper(this, "StaffBook.db", null, 1);
//        db = helper.getReadableDatabase();
//
//        ContentValues values = new ContentValues();
//        //开始插入第一条数据
//        values.put("staffid", "DRC57688");
//        values.put("chipnumber", "0x52345");
//        values.put("imgpath", "/data/data/vsstaff/");
//        values.put("iscomein", "0");
//        values.put("approachtime", "2017.11.6");
//        db.insert("Book", null, values);
//        Log.i(Constants.TAG, "first data insert finish");

        initView();
    }
    private void initView() {
        VS = (ImageView) findViewById(R.id.VS);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomActivity.this,SettingDistrictActivity.class);
                intent.putExtra("areano",areano);
                Log.i(Constants.TAG,"areano is "+areano);
                startActivity(intent);
                finish();
            }
        });
        rl_staffinfo = (RelativeLayout)findViewById(R.id.rl_staffinfo);
        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_staffno = (TextView)findViewById(R.id.tv_staffno);
        tv_tips = (TextView)findViewById(R.id.tv_tips);
        fl_anim = (FrameLayout)findViewById(R.id.fl_anim);
        iv_rotate = (ImageView)findViewById(R.id.iv_rotate);
        rv = (RadiationView)findViewById(R.id.rv);
        rv.setMinRadius(70);
        rv.startRadiate();
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.rotate_circle_anim);
        iv_rotate.startAnimation(animation);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //====================================*********************
        detecttag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(detecttag);
        mTagText = ndef.getType() + "\nmaxsize:" + ndef.getMaxSize() + "bytes\n\n";
        readNfcTag(intent);
        Log.i("tms","mTagText is:  "+mTagText);
        //====================================*********************
        detecttag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        byte[] b = detecttag.getId();
        String s = b.toString();
        Log.i("tms","b is: "+s);
        String[] techList = detecttag.getTechList();
        Log.i("tms","onNewIntent: techList length = "+techList.length);
        for (int i = 0; i < techList.length; i++) {
            Log.i("tms","onNewIntent: techList[" + i + "] = " + techList[i]);
        }
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if(detecttag==null){
                    nfcHandler.sendEmptyMessage(MSG_NFCREAD_FAIL);
                    return;
                }
                NFCTool nfcToolread = new NFCTool();
                CardInfo cardInfo = nfcToolread.readTag(detecttag);
                if(cardInfo==null){
                    nfcHandler.sendEmptyMessage(MSG_NFCREAD_FAIL);
                }else {
                    Log.i(Constants.TAG, "run: cardinfo = " + cardInfo.toString());
                    if (cardInfo.getCardType() == Constants.CHIP_EMPLOYEECARD){
                        Message msg = nfcHandler.obtainMessage(MSG_NFCREAD_SUCCESS,cardInfo);
                        nfcHandler.sendMessage(msg);
                    }else{
                        Message msg = nfcHandler.obtainMessage(MSG_NFCCARD_WRONGTYPE,cardInfo);
                        nfcHandler.sendMessage(msg);
                    }
                }
            }
        });

    }

    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    mTagText += textRecord + "\n\ntext\n" + contentSize + " bytes";
                }
            } catch (Exception e) {
            }
        }
    }

    private String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constants.TAG,"ondestroy-----");
        nfcHandler.removeCallbacksAndMessages(null);
    }
}
