package org.autojs.autojs.nkScript.interImp;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.core.accessibility.SimpleActionAutomator;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequestActivity;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequester;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.app.AppUtils;

import org.autojs.autojs.nkScript.interImp.model.Photo;

import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AppUtilsImp {

    private  ScriptRuntime scriptRuntime;
    private  AccessibilityBridge accessibilityBridge;
    public  AppUtils appUtils;
    public SimpleActionAutomator simpleActionAutomator;
    public UiSelectorImp uiSelectorImp;

    private String TAG="nkScript-AppUtilsImp";
    private Uri URI_SMS_INBOX = Uri.parse("content://sms/");
    private SharedPreferences sharedPreferences;


    public AppUtilsImp() {
        scriptRuntime=EnvScriptRuntime.getScriptRuntime();
        accessibilityBridge=scriptRuntime.accessibilityBridge;
        appUtils = scriptRuntime.app;
        simpleActionAutomator = scriptRuntime.automator;

        //UiSelectorImp uiSelectorImp = new UiSelectorImp( new ImagesImp() ) ;
    }

    public void getSp() {
        if (sharedPreferences==null){
            sharedPreferences = appUtils.getmContext().getSharedPreferences("script", Context.MODE_PRIVATE );
        }
    }

    public void writeConfig(String key,String value){
        getSp();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public void writeConfig(String key,boolean value){
        getSp();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public void writeConfig(String key,int value){
        getSp();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public Object readConfig( String key,Object value){
        getSp();
        if ( value instanceof Integer ){
            return sharedPreferences.getInt(key, (Integer) value);
        }else if( value instanceof Boolean ){
            return sharedPreferences.getBoolean(key, (Boolean) value);
        }else if ( value instanceof String ){
            return sharedPreferences.getString(key, (String) value);
        }
        return null;
    }

    public void requestSmsPermission(){
        ScreenCaptureRequestActivity.requestPermission( appUtils.getmContext() );
        
        /*      final String REQUEST_CODE_ASK_PERMISSIONS = "123";
           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasReadSmsPermission = appUtils.getmContext().checkSelfPermission(Manifest.permission.READ_SMS);
            if (hasReadSmsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( new String[] {Manifest.permission.READ_SMS },REQUEST_CODE_ASK_PERMISSIONS );
                return;
            }
        }*/
    }

    public AppUtilsImp( AppUtils appUtils,UiSelectorImp uiSelectorImp ) {

        scriptRuntime=EnvScriptRuntime.getScriptRuntime();
        accessibilityBridge=scriptRuntime.accessibilityBridge;
        this.appUtils=appUtils;
        this.uiSelectorImp=uiSelectorImp;

    }

    public String getSms( String number ) throws InterruptedException {

        Log.d(TAG, "getSms: request");
        ScreenCaptureRequestActivity.requestGetSms( appUtils.getmContext(),number );

        long stTime=System.currentTimeMillis();
        while (true){

            toast( "getSms.wait.." );
            if ( ScreenCaptureRequestActivity.flagSms ){
                toast("getSms ok="+ScreenCaptureRequestActivity.smsText );
                return ScreenCaptureRequestActivity.smsText;
            }

            Thread.sleep( 1000 );
            if ( System.currentTimeMillis() - stTime>30*1000 ){
                toast("getSms over 30");
                return null;
            }
        }
    }


//    三、获取
//————————————————
//    版权声明：本文为CSDN博主「LXB-89」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/adminlxb89/article/details/81068419

    /**
     * 删除相册
     * @return
     * @throws InterruptedException
     */
    public boolean deleteAlbum() throws InterruptedException {
        ScreenCaptureRequestActivity.requestGetAllPhoto(appUtils.getmContext());
        long stTime=System.currentTimeMillis();
        while (true){

            toast( "deleteAlbuming.wait.." );
            if ( ScreenCaptureRequestActivity.flagBoolAlumdelete ){
                toast("clear album ok");
                return true;
            }

            Thread.sleep( 1000 );
            if ( System.currentTimeMillis() - stTime>30*1000 ){
                toast("deleteAlbum outtime over 30");
                return false;
            }
        }

    }



    public void toast( String text ){
        GlobalAppContext.toast(text);
    }

    public void openAlbum(String path ){
        ScreenCaptureRequestActivity.requestOpenAlbum( appUtils.getmContext(), new File(path));
        //(Activity)appUtils.startActivityForResult(intent,0);
        // (Activity)scriptRuntime.app.startActivityForResult(getAlbum, IMAGE_CODE);
    }

    public void openAlbum(File path ){
        ScreenCaptureRequestActivity.requestOpenAlbum( appUtils.getmContext(), path);
        //(Activity)appUtils.startActivityForResult(intent,0);
       // (Activity)scriptRuntime.app.startActivityForResult(getAlbum, IMAGE_CODE);
    }

    public void uninstall(String packageName) {
         appUtils.uninstall(packageName);
    }

    public boolean launchPackage(String packageName) {
        return appUtils.launchPackage(packageName);
    }

/*
    public void killAppBackGround( String packageName ) throws InterruptedException {
        launchPackage( packageName );
            Thread.sleep(5000);
        ScreenCaptureRequestActivity.requestKillApp(appUtils.getmContext(), packageName);
    }
*/

    public String getPackageName( String appName ){
        return appUtils.getPackageName( appName );
    }

    public String getAppName( String packageName ){
        return appUtils.getAppName( packageName );
    }

    public void showPackageDetail( String packageName ) throws Exception {
        appUtils.showPackageDetail( packageName );
    }

    public boolean stopApp( String packageName ) throws Exception {
      return   stopApp( packageName,10000 );
    }

    public boolean stopApp ( String packageName,long timeout ) throws Exception {

        appUtils.showPackageDetail( packageName );
        long stTime=System.currentTimeMillis();
        int boolS=0;

        Map<String,String> forceStop=new HashMap<>();
        forceStop.put( "id","com.android.settings:id/right_button" );

        Map<String,String> ok=new HashMap<>();
        ok.put( "id","android:id/button1" );
        ok.put("pack","com.android.settings");

        while (true){

            Log.d(TAG, "stopApp: "+boolS );

            if (boolS==0){
                if (uiSelectorImp.fnode( forceStop  )!=null )
                    boolS=1;

                if (boolS==1){

                    uiSelectorImp.waitTrueEx( forceStop,ok,3000 );
                    if ( uiSelectorImp.clickXy( ok )!=null ){
                        boolS=2;
                    };

                    if (boolS==2){
                        uiSelectorImp.waitTrueEx( ok,forceStop,3000 );
                        if ( uiSelectorImp.fnode( forceStop )!=null ){
                            GlobalAppContext.toast("stop"+packageName+"-suc." );
                            return true;
                        }
                    }
                }

            }

            if (System.currentTimeMillis()-stTime>timeout ){
                GlobalAppContext.toast("stop-app-"+packageName+"-fail" );
                Log.d(TAG, "stopApp: fail-"+packageName );
                return false;
            }
            Thread.sleep(500);
        }
       // return false;
    }

    @ScriptInterface
    public void openUrl(String url) {
            appUtils.openUrl(url);
    }


}
