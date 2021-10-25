package org.autojs.autojs.nkScript.interImp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.app.AppUtils;

public class AppUtilsImp {

    private  ScriptRuntime scriptRuntime;
    private  AccessibilityBridge accessibilityBridge;
    public   AppUtils appUtils;

    public AppUtilsImp() {
        scriptRuntime=EnvScriptRuntime.getScriptRuntime();
        accessibilityBridge=scriptRuntime.accessibilityBridge;
        appUtils = scriptRuntime.app;
    }

    public AppUtilsImp( AppUtils appUtils ) {
        this.appUtils=appUtils;
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

}
