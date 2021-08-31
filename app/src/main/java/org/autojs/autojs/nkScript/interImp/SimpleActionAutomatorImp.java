package org.autojs.autojs.nkScript.interImp;

import android.os.Build;

import com.stardust.autojs.core.accessibility.SimpleActionAutomator;
import com.stardust.autojs.runtime.ScriptRuntime;

import org.autojs.autojs.autojs.AutoJs;

public class SimpleActionAutomatorImp {

    public AutoJs autoJs;
    public ScriptRuntime scriptRuntime;
    public  SimpleActionAutomator simpleActionAutomator;

    {
        if ( simpleActionAutomator==null ){
            autoJs= AutoJs.getInstance();
            ScriptRuntime scriptRuntime= autoJs.getRunTime();
            scriptRuntime.init();
            simpleActionAutomator=scriptRuntime.automator;
        }
    }

    public  boolean click(int x,int y){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return simpleActionAutomator.click(x,y);
        }else
            return false;
    }

    public boolean swipe(int x,int y,int x2,int y2, int delay  ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return simpleActionAutomator.swipe (x,y,x2,y2,delay);
        }else
            return false;
    }

}
