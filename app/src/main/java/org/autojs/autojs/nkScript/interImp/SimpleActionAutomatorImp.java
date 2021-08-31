package org.autojs.autojs.nkScript.interImp;

import android.app.Application;
import android.os.Build;
import android.os.Environment;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.accessibility.SimpleActionAutomator;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.automator.simple_action.SimpleAction;

import org.autojs.autojs.autojs.AutoJs;

//点击接口
public class SimpleActionAutomatorImp {

    public AutoJs autoJs;
    private ScriptRuntime scriptRuntime;
    private SimpleActionAutomator simpleActionAutomator;

    public SimpleActionAutomatorImp( ) {
        scriptRuntime=EnvScriptRuntime.getScriptRuntime();
        simpleActionAutomator=scriptRuntime.automator;
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
