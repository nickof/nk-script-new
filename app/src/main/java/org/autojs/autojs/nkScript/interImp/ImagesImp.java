package org.autojs.autojs.nkScript.interImp;

import android.os.Build;
import android.util.Log;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.image.ColorFinder;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Images;

public class ImagesImp {

    private static final String TAG ="ImagesImp" ;
    public final ScriptRuntime scriptRuntime;
    public Images images;
    public ColorFinder colorFinder;

    public ImagesImp() {
         scriptRuntime= EnvScriptRuntime.getScriptRuntime();
         images = scriptRuntime.getImages();
        // colorFinder =new ColorFinder(scriptRuntime.getScreenMetrics());
         //waitPermission();
    }

    public void waitPermission() throws InterruptedException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           // images.initOpenCvIfNeeded();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               this.images.requestScreenCapture(3);
                while (this.images.getmScreenCapturer()==null ){
                    GlobalAppContext.toast("wait-permisson");
                    Log.d(TAG,"wait-permisson");
                    Thread.sleep(1000);
//                    try {
//                    } catch ( Exception e) {
//                        Log.d(TAG,"waitPermission-interrupted");
//                        throw  new Exception();
//                    }
                }
            }
        }

     /*   Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }*/

    }



}
