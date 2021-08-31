package org.autojs.autojs.nkScript

import android.app.Application
import android.util.Log
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.runtime.api.Images
import org.autojs.autojs.autojs.AutoJs
import org.autojs.autojs.nkScript.interImp.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.log

class Run {

    lateinit var listExecutorService: ArrayList<ExecutorService>;
    lateinit var poolMain: ExecutorService
    lateinit var poolSub: ExecutorService
    var TAG:String="Run"
    var setNode=SetNode();
    //var listExecutorService = arrayListOf<ExecutorService>();

    var clk: SimpleActionAutomatorImp? = null
    lateinit var node: UiSelectorImp;
    lateinit var images: Images;


    fun main(){
        init()
        // threadInterrruptedForce();
        poolMain = Executors.newFixedThreadPool(1)
        poolSub = Executors.newFixedThreadPool(1)

        poolMain = InterMy.ThreadStart( { jk2() }, 1)
        listExecutorService= ArrayList();
        listExecutorService.add( poolMain );


/*
        var scriptRuntime=AutoJs.getInstance().runTime;
        scriptRuntime.init()
       var images:Images= if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Images( GlobalAppContext.get(), scriptRuntime,
                    ScreenCaptureRequester.ActivityScreenCaptureRequester( OnActivityResultDelegate.Mediator(), GlobalAppContext.getActivity() ) )
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }

        images.requestScreenCapture(0);
        var imageWrapper=images.captureScreen();
        //images.requestScreenCapture()

        var path:String= Environment.getExternalStorageDirectory().toString()+"/1.png"
        var templateMatching=images.read( path);
        Log.d(TAG,path)

        var point= images.findImage( imageWrapper,templateMatching, 0.9F )
        Log.d(TAG,point.x.toString()+","+point.y)*/

    }

    fun jk2() {

        var i:Int=0;
        while (true) {

            ++i;
            Log.d(TAG, "jk2: run")
            var ver="0831"
            GlobalAppContext.toast("ver="+ver+i )
            Log.d(TAG,"ver="+ver);
/*
            try {
                var ret= node.fnode(setNode.ss设置 );
                if (ret!=null){
                    Log.d(TAG,"bounds="+ret.bounds()   )
                    ret.click();
                }else
                    Log.d( TAG,"未找到"  );

                Log.d(TAG,ver)
                        Thread.sleep(1000)

            } catch (e: InterruptedException) {
                Log.d(TAG, "jk2: InterruptedException")
                e.printStackTrace()
                break
            }*/
            try {
                Thread.sleep(1000)
            } catch (e: Exception) {
                    return;
            }

        }
    }

    fun init(){
        Log.d(TAG,"init")
        if (GlobalAppContext.get()==null)
            Log.d(TAG,"application null")
        else
            Log.d(TAG,"application not null")
        clk = SimpleActionAutomatorImp()
        node = UiSelectorImp()
        images=ImagesImp().images;
    }

}