package com.sex8.sinchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMMessageApplication extends Application {
    public static boolean Show_Log;
    public static IMMessageApplication application;
    public FirebaseAnalytics mFirebaseAnalytics;


    public static final int Request_Permission_Read = 1001;
    public static final int Request_Permission_Camera = 1002;
    public static final int Request_Crop_Image = 1003;
    public static final int Request_Edit_Nick = 1004;

    //App快取路徑
    public static File cacheDir;
    public static File tmpFile;


    private String uid="";
    private String imKey="";
    private String[] fileHosts;

    private int width=0;
    private int height=0;

    @Override
    public void onCreate() {
        super.onCreate();
        Show_Log = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        application = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        cacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), getString(R.string.app_name));
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    @Nullable
    public static Context getGlobalContext() {
        return application;
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public static void Toast(String text, int duration){
        Toast.makeText(application, text, duration).show();
    }

    public static void showDialog(Context context, @StringRes int titleId, DialogInterface.OnClickListener positiveClockListener) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(titleId)
                .setPositiveButton(R.string.dialog_ok, positiveClockListener)
                .create()
                .show();
    }

    private static Toast toast;
    public static void showToast(Context context, CharSequence text, int duration){
        if(toast != null) toast.cancel();
        toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void showToast(Context context, int resId, int duration) throws Resources.NotFoundException {
        showToast(context, context.getString(resId), duration);
    }



    public static boolean checkPermission(@NonNull Activity activity, @IntRange(from = 0L) int requestCode, @NonNull String... permissions){
        List<String> denied = new ArrayList<String>();
        for(String s: permissions){
            if(ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED){
                denied.add(s);
            }
        }

        if(denied.size() > 0) {
            ActivityCompat.requestPermissions(activity, Arrays.copyOf(denied.toArray(), denied.size(), String[].class), requestCode);
            return false;
        }else
            return true;
    }

    public static boolean checkPermissionsResult(@NonNull Context context, @NonNull String[] permissions, @NonNull int[] grantResults){
        for(int i: grantResults){
            if(i != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void hideKeyboard(Context context, View rootLayout){
        if(rootLayout != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
            rootLayout.requestFocus();
        }
    }


    /**使用者TOKEN**/
    public String getUid() {
        return uid;
    }

    public void setLoginUid(String uid) {
        this.uid = uid;
    }

    public String getImKey() {
        return imKey;
    }

    public void setImKey(String imKey) {
        this.imKey = imKey;
    }

    public String[] getFileHosts() {
        return fileHosts;
    }

    public void setFileHosts(String[] fileHosts) {
        this.fileHosts = fileHosts;
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean checkEmail(String email) {
        String regex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 驗證手機號碼
     *
     * 移動號碼段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 聯通號碼段:130、131、132、136、185、186、145
     * 電訊號碼段:133、153、180、189
     *
     * @param cellphone
     * @return
     */
    public static boolean checkCellphone(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ;
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(cellphone);
        return matcher.matches();
    }

    /**
     * 取得第一個字的拼音
     * 使用pinyin4j將中文字轉成拼音
     * http://pinyin4j.sourceforge.net/
     */
    public static String getFirstPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

//        char[] input = inputString.trim().toCharArray();
        char curchar = inputString.trim().charAt(0);
        String output = "";

        try {
//            for (char curchar : input) {
                if (java.lang.Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);
                    output += temp[0];
                } else
                    output += java.lang.Character.toString(curchar);
//            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return output;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
