package org.autojs.autojs.nkScript.interImp;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.stardust.autojs.core.accessibility.SimpleActionAutomator;
import com.stardust.autojs.runtime.ScriptRuntime;

import org.autojs.autojs.autojs.AutoJs;

//点击接口
public class SimpleActionAutomatorImp {

    public AutoJs autoJs;
    private ScriptRuntime scriptRuntime;
    private SimpleActionAutomator simpleActionAutomator;
    public final String TAG="nkScript-SimpleActionAutomatorImp";

    public SimpleActionAutomatorImp( ) {
        scriptRuntime=EnvScriptRuntime.getScriptRuntime();
        simpleActionAutomator=scriptRuntime.automator;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean press(int x, int y, int time ){
        return simpleActionAutomator.press( x,y,time );
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("LongLogTag")
    public boolean clickRect(Rect rect ){
        int x=r_( rect.left,rect.right );
        int y=r_(rect.top,rect.bottom);
        //Log.d(TAG, "clickXy: rect="+rect.toString()+"--x,y="+x+","+y  );
        Log.d(TAG, "clickXy: "+x+","+y );
        return simpleActionAutomator.click(x,y);
    }

    public int r_(int min, int max) {
        int randomNumber;
        randomNumber = (int) (((max - min + 1) * Math.random() + min));
        return randomNumber;
    }

}
