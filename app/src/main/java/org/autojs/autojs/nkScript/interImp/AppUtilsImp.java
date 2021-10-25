package org.autojs.autojs.nkScript.interImp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AppUtilsImp {

    private  ScriptRuntime scriptRuntime;
    private  AccessibilityBridge accessibilityBridge;
    public   AppUtils appUtils;
    public SimpleActionAutomator simpleActionAutomator;
    public UiSelectorImp uiSelectorImp;
    private String TAG;

    public AppUtilsImp(  ) {

        scriptRuntime=EnvScriptRuntime.getScriptRuntime();
        accessibilityBridge=scriptRuntime.accessibilityBridge;
        appUtils = scriptRuntime.app;
        simpleActionAutomator = scriptRuntime.automator;
        //UiSelectorImp uiSelectorImp = new UiSelectorImp( new ImagesImp() ) ;

    }

    public AppUtilsImp( AppUtils appUtils,UiSelectorImp uiSelectorImp ) {
        this.appUtils=appUtils;
        this.uiSelectorImp=uiSelectorImp;
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
