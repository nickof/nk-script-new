package org.autojs.autojs.nkScript

import android.app.Application
import android.os.Build
import android.os.Environment
import android.util.Log
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.image.ColorFinder
import com.stardust.autojs.runtime.api.Images
import org.apache.log4j.lf5.viewer.LogFactor5InputDialog
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

  /*      AutoJs.initInstance(GlobalAppContext.get() as Application?);
        val autoJs = AutoJs.getInstance()
        val scriptRuntime = autoJs.runTime;
        scriptRuntime.init()
        images = scriptRuntime.images


        if (  scriptRuntime.app.currentActivity ==null )
            Log.d(TAG, "main: currentActivity="+null )
        else
            Log.d(TAG, "main: currentActivity="+scriptRuntime.app.currentActivity.toString() )
  */
        //  Log.d(TAG, "main: context ="+ )
        listExecutorService= ArrayList();
        imagesImp=ImagesImp(this);
        images=imagesImp.images;
        colorFinder=imagesImp.colorFinder;

        poolMain = InterMy.ThreadStart( { jk2() }, 1)
        listExecutorService.add( poolMain );

    }

    fun jk2() {

        //imagesImp.waitPermission();
        //images.requestScreenCapture(0);
        //imagesImp.waitPermission2();

        var i:Int=0;
        imagesImp.requestWaitPermission();
        while (true) {
            ++i;
            Log.d( TAG, "jk2: run")
            var ver="0901b"
            GlobalAppContext.toast("ver="+ver+i )
            Log.d( TAG,"ver="+ver);
            // String colorGroup,String rect,int threshold
     //         testFindPic();
    testFindColor()
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

    fun testFindColor(){
       //requestWaitPermission()
         //imagesImp.waitPermission2()
        var test= mapOf<String,String>( "c" to "48|21|d7ccf0,84|196|6200ee,147|418|ffffff")
        //simagesImp.colorTransformMultiColors("60|305|7a7a7a,95|383|6200ee,375|421|ffffff,873|122|6200ee,597|54|3700b3" )
         var point=imagesImp.findMultiColors(test);
   /*       var point = imagesImp.findMultiColors( "7a7a7a,35|78|6200ee,315|116|ffffff,813|-183|6200ee,537|-251|3700b3","[37,81,868,627]",
                     20);*/
        Log.d(TAG,"testFindColor")
            if (point!=null){
                   GlobalAppContext.toast("已找到"+point.toString())
            }else
                 GlobalAppContext.toast("未找到")
    }

    fun requestWaitPermission(){
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
    }

    fun testFindPic(){
        // imagesImp.waitPermission2()
       // requestWaitPermission()


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