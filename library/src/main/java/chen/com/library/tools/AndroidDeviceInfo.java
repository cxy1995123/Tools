package chen.com.library.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by ${zhaojz} on 2017/8/31.
 */
//权限
//<uses-permission android:name="android.permission.READ_PHONE_STATE"/>

/**  throw new UnsupportedOperationException("you can't instantiate me...");
 *   可以代替return null  来提示你自己
 */
public class AndroidDeviceInfo {
    public static String androidID = null;//androidID
    public static String mSerial = null;//SIM卡的序列号
    public static String deviceID = null;//设备ID
    public static String androidUuidID = null;//Uuid加密的android设备唯一标识
    public static String phoneProducer = null;//手机厂商
    public static String IMEI = null;//手机IM
    public static String phoneModel = null;//手机型号
    public static String systemVersion = null;//手机系统版本号
    public static String SDKVersion = null;//SDK版本
    public static String versionName = null;//软件版本

    public static Context context;

    /**
     *   本工具类初始化
     * @param context
     */
    public static void  init(Context context){
        AndroidDeviceInfo.context= context;
    }
    /**
     * 未加密
     * @return 设备ID
     */
    public static String getAndroidID(){
        androidID = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return androidID;
    }
    @SuppressLint({"ByteOrderMark", "MissingPermission", "HardwareIds"})
    public static String getAndroidID2(){
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceID = "" + tm.getDeviceId();
        mSerial = "" + tm.getSimSerialNumber();
        return deviceID ;
    }

    /**
     * UUID 加密过后
     * @return  android唯一标识
     */
    public static String getUUIDAndroidID(String androidID,String deviceID,String tmSerial){
        UUID deviceUuid = new UUID(androidID.hashCode(), ((long)deviceID.hashCode() << 32) | tmSerial.hashCode());
        androidUuidID = deviceUuid.toString();
        return androidUuidID;
    }

    /**
     * 手机厂商
     * @return
     */
    public static String getPhoneProducer() {
        phoneProducer = android.os.Build.BRAND;
        return phoneProducer;
    }
    /**
     * 手机IM
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            IMEI = tm.getDeviceId();
            return IMEI;
        }

        return null;
    }

    /**
     *   获取手机型号
     * @return
     */
    public  static  String  getPhoneModel()  {
        phoneModel = android.os.Build.MODEL;
        return  phoneModel;
    }

    /**
     * 获取系统版本号
     * @return
     */
    public static String getSystemVersion() {
        systemVersion = android.os.Build.VERSION.RELEASE;
        return  systemVersion;
    }
    /**
     * SDK 版本
     * @return
     */
    public static String getSDKVersion() {
        SDKVersion = android.os.Build.VERSION.SDK ;
        return  SDKVersion;
    }

    /**
     * 当前软件版本
     * @return
     */
    private  String getAppVersionName() {
        try {
            PackageManager packageManager = context.getPackageManager();
//            context.getPackageName() 当前软件包名
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
//            packageInfo.versionCode
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }


}

