package org.autojs.autojs.nkScript

import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.accessibility.SimpleActionAutomator
import com.stardust.autojs.core.image.ColorFinder
import com.stardust.autojs.runtime.api.Images
import com.stardust.autojs.runtime.app.AppUtils
import com.stardust.automator.UiObject
import dalvik.system.DexClassLoader
import org.autojs.autojs.MainActivity
import org.autojs.autojs.nkScript.interImp.*
import org.autojs.autojs.nkScript.scriptCollection.ss.newTest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class Run {

    var scriptName="scDemo.ScDemo"

    lateinit var listExecutorService: ArrayList<ExecutorService>;
    lateinit var poolMain: ExecutorService
    lateinit var poolSub: ExecutorService
    open var TAG:String="nkScript-Run"

    var setNode=SetNode();
    //var listExecutorService = arrayListOf<ExecutorService>();

    var clk: SimpleActionAutomatorImp? = null

    lateinit var node: UiSelectorImp;
    lateinit var images: Images;
    lateinit var imagesImp: ImagesImp;
    lateinit var colorFinder: ColorFinder
    lateinit var nod: SetNode
    lateinit var appUtils: AppUtilsImp;
    lateinit var sim:SimpleActionAutomatorImp ;

    constructor() {

        //this.scriptName = scriptName
        imagesImp=ImagesImp()
        images=imagesImp.images
        colorFinder=imagesImp.colorFinder

        nod= SetNode()
        node= UiSelectorImp( imagesImp )

        sim= SimpleActionAutomatorImp()
        appUtils = AppUtilsImp( node.scriptRuntime.app,node )
        // node.scriptRuntime.app

    }

//    lateinit var simpleActionAutomator: SimpleActionAutomator

    @RequiresApi(Build.VERSION_CODES.N)
    fun main(){
        api_init();
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun api_init(){

        //???????????????
        poolMain = Executors.newFixedThreadPool(1)
        poolSub = Executors.newFixedThreadPool(1)

        listExecutorService= ArrayList();
        //poolMain = InterMy.ThreadStart( { jk2() }, 1 )
       // val clazz = Class.forName("org.autojs.autojs.nkScript.scriptConllection.test.class")

        try {

      /*      var context=GlobalAppContext.get()
            var optfile = context.getDir("opt_dex",0);
            var  libfile=context.getDir("lib_path",0);
            var parentClassloader= MainActivity::class.java.classLoader

            var dexClassLoader=DexClassLoader("/sdcard/test2.dex",optfile.getAbsolutePath(),
                    libfile.getAbsolutePath(),parentClassloader );*/

           var clazz =  Class.forName("org.autojs.autojs.nkScript.scriptCollection."+scriptName  )
            //var clazz=dexClassLoader.loadClass("org.autojs.autojs.nkScript.scriptCollection."+scriptName)
           var method=clazz.getDeclaredMethod("script");
           var obj=clazz.newInstance()

            poolMain = InterMy.ThreadStart(  { method.invoke(obj); } , 1 )
          //  poolMain = InterMy.ThreadStart(  { scriptRun() } , 1 )
             poolSub = InterMy.ThreadStart(  { jk2() } , 1 )
            listExecutorService.add( poolMain );
            listExecutorService.add( poolSub );
            ThreadpoolScriptManager.setListExecutorServices( listExecutorService );

         } catch (e: Exception) {
                GlobalAppContext.toast( "??????"+scriptName+"?????????,???????????????"  );
            return;
        }


    }

    fun scriptRun(){
    // Looper.prepare()
//        var clazz =  Class.forName("org.autojs.autojs.nkScript.scriptCollection."+scriptName  )
//        var method=clazz.getDeclaredMethod("script");
//        var obj=clazz.newInstance()
        //method.invoke(obj)
        //Looper.loop()
    }

    fun requestPermission(){
//        appUtils.requestSmsPermission()
        imagesImp.requestWaitPermission()
    }

 /*   fun scriptRun(){

        var i:Int=0;
        //imagesImp2. requestWaitPermission ();
        GlobalAppContext.toast("script-run.")


        imagesImp.waitPermissionOnlyWait ();
        while (true) {

            ++i;
            Log.d( TAG, "scriptRun: run")
            var ver="0901b"
            GlobalAppContext.toast("ver="+ver+i )
            Log.d( TAG,"ver="+ver);
            testNode()
            Log.d(TAG,"script-run-loop"+i )
            try {
                Thread.sleep(1000)
            } catch (e: Exception) {
                throw java.lang.Exception("scriptRun-"+e.toString() ) ;
            }

        }
    }
*/

     fun testNode(){
         node.clickXy( nod.zz??????2 )
     }

    fun toast( string: String){
        GlobalAppContext.toast(string)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun jk2() {

        nod= SetNode()
        //imagesImp.requestPermissionOnce()
        //var uiObject:UiObject;
        for (index in 1..10 ){
          var uiObject=node.fnode( nod.permissionScreen )
              if ( uiObject!=null ){
                  var rect=uiObject.bounds();
                  rect.set( rect.left,rect.top,rect.left+5,rect.bottom )
                  sim.clickRect( rect );
                  node.waitFalse( nod.permissionScreen )
              }

            toast( "click-permission-"+(10- index) )
            Thread.sleep( 1000 )

        }

        //images.requestScreenCapture(0);
        //imagesImp.waitPermissionOnlyWait();
        var i:Int=0;

        while (true) {

            ++i;
            Log.d( TAG, "watch: run")
            var ver="0901b"
            GlobalAppContext.toast( "watch: run-ver="+ver +";time=" +i )
            Log.d( TAG,"ver="+ver);
            node.waitFalseEx( nod.permissionScreen )

            //imagesImp.captureSaveImageToGallery(
/*
            try {
                var ret= node.fnode(setNode.ss?????? );
                if (ret!=null){
                    Log.d(TAG,"bounds="+ret.bounds()   )
                    ret.click();
                }else
                    Log.d( TAG,"?????????"  );

                Log.d(TAG,ver)
                        Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Log.d(TAG, "jk2: InterruptedException")
                e.printStackTrace()
                break
            }*/

            try {
                Thread.sleep(5000)
            } catch (e: Exception) {
                return;
            }

        }
    }

    fun testFindColor(){

        //requestWaitPermission()
        // imagesImp.waitPermission2()
        var test= mapOf<String,String>( "c" to "48|21|d7ccf0,84|196|6200ee,147|418|ffffff")
        var point=imagesImp.findMultiColors(test);
   /*       var point = imagesImp.findMultiColors( "7a7a7a,35|78|6200ee,315|116|ffffff,813|-183|6200ee,537|-251|3700b3","[37,81,868,627]",
                     20);*/

        Log.d(TAG,"testFindColor-thread="+Thread.currentThread().name )
            if (point!=null){
                GlobalAppContext.toast("?????????"+point.toString()+"thr-name="+Thread.currentThread().name )
                Log.d(TAG, "?????????"+point.toString()+"thr-name="+Thread.currentThread().name  )
            }else
                 GlobalAppContext.toast("????????????"+"thr-name="+Thread.currentThread().name  )

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
                GlobalAppContext.toast("?????????"+point )
                Log.d(TAG,"?????????"+point)
            }else{
                GlobalAppContext.toast("?????????" )
                Log.d(TAG,"?????????")
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
        node = UiSelectorImp(imagesImp)

//        imagesImp=ImagesImp();
//        colorFinder=imagesImp.colorFinder;
//        images=imagesImp.images ;
//        images.initOpenCvIfNeeded();

    }


}