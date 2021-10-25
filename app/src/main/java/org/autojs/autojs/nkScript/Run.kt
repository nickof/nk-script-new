package org.autojs.autojs.nkScript

import android.os.Build
import android.os.Environment
import android.util.Log
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.image.ColorFinder
import com.stardust.autojs.runtime.api.Images
import com.stardust.autojs.runtime.app.AppUtils
import org.autojs.autojs.nkScript.interImp.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class Run {

    var scriptName="test"

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

    constructor() {

        //this.scriptName = scriptName
        imagesImp=ImagesImp()
        images=imagesImp.images
        colorFinder=imagesImp.colorFinder

        nod= SetNode()
        node= UiSelectorImp( imagesImp )

        appUtils = AppUtilsImp( node.scriptRuntime.app);
        // node.scriptRuntime.app

    }

//    lateinit var simpleActionAutomator: SimpleActionAutomator

    fun main(){
        api_init();
    }

    fun api_init(){

        //新建线程池
        poolMain = Executors.newFixedThreadPool(1)
        poolSub = Executors.newFixedThreadPool(1)
        listExecutorService= ArrayList();

        //poolMain = InterMy.ThreadStart( { jk2() }, 1 )
       // val clazz = Class.forName("org.autojs.autojs.nkScript.scriptConllection.test.class")

        try {
           var clazz =  Class.forName("org.autojs.autojs.nkScript.scriptCollection."+scriptName  )
           var method=clazz.getDeclaredMethod("script");
           var obj=clazz.newInstance()
            //Log.d(TAG, clazz.simpleName+"-className" );
           //{ method }
            poolSub = InterMy.ThreadStart(  { method.invoke(obj); } , 1 )
            listExecutorService.add( poolSub );
            listExecutorService.add( poolMain );
            ThreadpoolScriptManager.setListExecutorServices( listExecutorService );

         } catch (e: Exception) {
                GlobalAppContext.toast( "脚本"+scriptName+"未找到,请确认名称"  );
            return;
        }


    }

    fun scriptRun(){

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


     fun testNode(){
         //node.waitGrp( nod.testArrayFix );
         node.clickXy( nod.zz主页2 )
         //node.fnode (  nod.switch);
         //node.clkNodeWaitCorlor( nod.switch,3000,50);
     }

    fun toast( string: String){
        GlobalAppContext.toast(string)
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
            GlobalAppContext.toast( "ver="+ver+i+","+Thread.currentThread().name )
            Log.d( TAG,"ver="+ver);

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
        // imagesImp.waitPermission2()
        var test= mapOf<String,String>( "c" to "48|21|d7ccf0,84|196|6200ee,147|418|ffffff")
        var point=imagesImp.findMultiColors(test);
   /*       var point = imagesImp.findMultiColors( "7a7a7a,35|78|6200ee,315|116|ffffff,813|-183|6200ee,537|-251|3700b3","[37,81,868,627]",
                     20);*/

        Log.d(TAG,"testFindColor-thread="+Thread.currentThread().name )
            if (point!=null){
                GlobalAppContext.toast("已找到"+point.toString()+"thr-name="+Thread.currentThread().name )
                Log.d(TAG, "已找到"+point.toString()+"thr-name="+Thread.currentThread().name  )
            }else
                 GlobalAppContext.toast("未找到，"+"thr-name="+Thread.currentThread().name  )

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
        node = UiSelectorImp(imagesImp)

//        imagesImp=ImagesImp();
//        colorFinder=imagesImp.colorFinder;
//        images=imagesImp.images ;
//        images.initOpenCvIfNeeded();

    }


}