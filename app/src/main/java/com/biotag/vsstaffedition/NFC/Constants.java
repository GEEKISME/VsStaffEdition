package com.biotag.vsstaffedition.NFC;

/**
 * Created by Lxh on 2017/8/3.
 */

public class Constants {
    public static final String TAG               = "Panpan";
    //正式服务器
//    public static final String MAINHOSTFORMAL          = "https://yl.shyule.org/webapi/";
    //测试服务器
    public static final String MAINHOST          = "http://211.152.45.196:8036/";

    public static final String KEYA              = "keyA";
    public static final String KEYB              = "keyB";
    public static final int IDGROUPIDCARDTYPE_SECTOR     = 1;
    public static final int IDCARD_SECTOR        = 2;
    public static final int STAFFNO_SECTOR       = 3;
    public static final int STAFFNAME_SECTORA    = 4;
    public static final int STAFFNAME_SECTORB    = 5;
    public static final int COMPANYNAME_SECTORA  = 6;
    public static final int COMPANYNAME_SECTORB  = 7;
    public static final int AREANOW_SECTOR              = 8;
    public static final int LASTMODIFIEDTIME_SECTOR     = 9;
    public static final int AREANO_SECTOR               = 10;
    public static final int SEAT_NO                     = 10;
    public static final int IMAGEURL_SECTOR             = 11;

    public static final int CHIP_INVITATION             = 1; //邀请函
    public static final int CHIP_EMPLOYEECARD           = 2; //工作证
    public static final int CHIP_TICKET                 = 3; //票
    public static final int CHIP_WRISTSTRAP             = 4; //手环

//    public static final String URL_GETSTAFFPHOTO = MAINHOST + "api/photo/getstaffphoto/{staffid}";
    public static final String URL_GETSTAFFPHOTO            =   MAINHOST          +    "api/photo/getstaffphoto/{staffid}";
    public static final String URL_GETSTAFFPHOTO2           =   MAINHOST          +    "uploadImage/{path}";

    public static final String URL_POSTPHOTO                =   MAINHOST          +    "api/Photo/LogPhoto";

    public static final String URL_GETALLHEADNUM            =   MAINHOST          +    "api/Photo/SyncPhoto/{id}";

    public static final String URL_GETSTAFFTHUMBPHOTO       =   MAINHOST          +    "api/Photo/GetStaffThumbPhoto/{id}";

    public static final String URL_POSTINOUT                =   MAINHOST          +    "api/InOut/Add";

    /**
     * 获取app的版本号，以决定是否下载新的版本
     */
    public static final String APP_VERSION                  =   MAINHOST    +    "";
}
