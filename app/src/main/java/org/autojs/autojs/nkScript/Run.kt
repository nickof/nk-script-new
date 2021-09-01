package org.autojs.autojs.nkScript

import android.app.Application
import android.os.Build
import android.os.Environment
import android.util.Log
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.image.ColorFinder
import com.stardust.autojs.runtime.api.Images
import org.autojs.autojs.autojs.AutoJs
import org.autojs.autojs.nkScript.interImp.*
import org.opencv.android.OpenCVLoader
import org.opencv.core.Point
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
    lateinit var imagesImp: ImagesImp;
    lateinit var colorFinder: ColorFinder



    fun main(){
        //  init()

        // threadInterrruptedForce();
        poolMain = Executors.newFixedThreadPool(1)
        poolSub = Executors.newFixedThreadPool(1)

        AutoJs.initInstance(GlobalAppContext.get() as Application?);
        val autoJs = AutoJs.getInstance()
        val scriptRuntime = autoJs.runTime;
        scriptRuntime.init()
        images = scriptRuntime.images
//        if ( images.mContext==null )
//            Log.d(TAG, "main: context="+null )
//        else
//            Log.d(TAG, "main: context="+images.mContext.toString() )

        if (  scriptRuntime.app.currentActivity ==null )
            Log.d(TAG, "main: currentActivity="+null )
        else
            Log.d(TAG, "main: currentActivity="+scriptRuntime.app.currentActivity.toString() )

        //  Log.d(TAG, "main: context ="+ )
        //images.initOpenCvIfNeeded();
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

        //imagesImp.waitPermission();

        var i:Int=0;
        while (true) {

            ++i;
            Log.d( TAG, "jk2: run")
            var ver="0901b"
            GlobalAppContext.toast("ver="+ver+i )
            Log.d( TAG,"ver="+ver);
            // String colorGroup,String rect,int threshold
            //imagesImp.ks();
            testFindPic();

            /*         var point:Point= imagesImp.findMultiColors( "ff7716,42|29|fefefe,87|51|ff7716,208|35|fb8db1,254|59|fb9493","[37,81,868,627]",
                     0);
                     if (point!=null){
                         GlobalAppContext.toast("已找到"+point.toString())
                     }else
                         GlobalAppContext.toast("未找到")*/
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

    fun testFindPic(){
        // imagesImp.waitPermission2()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //images.initOpenCvIfNeeded()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                images.requestScreenCapture(3)
                while (images.getmScreenCapturer()==null ){
                    Log.d(TAG,"wait-permisson")
                    try {
                        Thread.sleep(500)
                    } catch (e: Exception) {
                        Log.d(TAG,"interrupted")
                        return;
                    }
                }
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var imagesScree=  images.captureScreen()
            var  path:String= Environment.getExternalStorageDirectory().toString()+"/1.png";
            var imagesTemplates=images.read( path )
            var point= images.findImage( imagesScree,imagesTemplates,0.95f );
            imagesScree.recycle();
            imagesTemplates.recycle();
            if (point!=null){
                GlobalAppContext.toast("找到了"+point )
                Log.d(TAG,"找到了"+point)
            }else{
                GlobalAppContext.toast("没找到" )
                Log.d(TAG,"没找到")
            }
            Log.d(TAG,"path="+path);

        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        };
    }

    fun init(){

        Log.d(TAG,"init")
        if (GlobalAppContext.get()==null)
            Log.d(TAG,"application null")
        else
            Log.d(TAG,"application not null")
        clk = SimpleActionAutomatorImp()
        node = UiSelectorImp()
//        imagesImp=ImagesImp();
//        colorFinder=imagesImp.colorFinder;
//        images=imagesImp.images ;
//        images.initOpenCvIfNeeded();

    }

}