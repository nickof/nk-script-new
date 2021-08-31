package org.autojs.autojs.nkScript.interImp;

import android.os.Build;
import android.util.Log;

import com.stardust.autojs.core.image.ColorFinder;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Images;

public class ImagesImp {

    private static final String TAG ="ImagesImp" ;
    public final ScriptRuntime scriptRuntime;
    public final Images images;
    public ColorFinder colorFinder;

    public ImagesImp() {
         scriptRuntime= EnvScriptRuntime.getScriptRuntime();
         images = scriptRuntime.getImages();
         colorFinder =new ColorFinder(scriptRuntime.getScreenMetrics());
         waitPermission();
    }

    public void waitPermission(){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //images.initOpenCvIfNeeded()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        images.requestScreenCapture(-1);
                        while (images.getmScreenCapturer()==null ){
                            Log.d(TAG,"wait-permisson");
                            try {
                                Thread.sleep(500);
                            } catch ( Exception e) {
                                Log.d(TAG,"interrupted");
                                return;
                            }
                        }
                    }
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

    }



}
