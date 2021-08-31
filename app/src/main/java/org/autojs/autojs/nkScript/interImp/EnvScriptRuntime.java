package org.autojs.autojs.nkScript.interImp;

import android.app.Application;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.runtime.ScriptRuntime;

import org.autojs.autojs.autojs.AutoJs;

public class EnvScriptRuntime {
    private  static volatile AutoJs autoJs;

    private static void  autoJsInit(){
        AutoJs.initInstance((Application) GlobalAppContext.get());
        autoJs=AutoJs.getInstance();
    }

    public  static ScriptRuntime getScriptRuntime(){
        getAutoJs();
        ScriptRuntime scriptRuntime=autoJs.getRunTime();
        scriptRuntime.init();
        return scriptRuntime;
    }

    public static AutoJs getAutoJs(){
        if (autoJs==null)
            autoJsInit();
        return autoJs;
    }

}
