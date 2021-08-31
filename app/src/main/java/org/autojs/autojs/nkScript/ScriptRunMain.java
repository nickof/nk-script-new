package org.autojs.autojs.nkScript;


import android.os.Build;
import android.util.Log;

import com.stardust.autojs.core.accessibility.UiSelector;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;

import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.nkScript.functionInterface.FunInterThreadMethod;
import org.autojs.autojs.nkScript.interImp.InterMy;
import org.autojs.autojs.nkScript.interImp.SetNode;
import org.autojs.autojs.nkScript.interImp.SimpleActionAutomatorImp;
import org.autojs.autojs.nkScript.interImp.UiSelectorImp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptRunMain {

    private static final String TAG ="ScriptRunMain" ;
    final long waitTime = 1 * 1000;
    final long awaitTime = 8 * 1000;
    public Runnable runnableScriptTask;
    public ExecutorService poolMain;
    public ExecutorService poolSub;
    public AutoJs autoJs= AutoJs.getInstance();
    public ScriptRuntime scriptRuntime;
    public UiSelector uiSelector;
    public static List< ExecutorService > listExecutorService;

    //scriptInterface
    public SimpleActionAutomatorImp clk;
    public UiSelectorImp node;

    public long getAwaitTime() {
        return awaitTime;
    }
    public void main(){

        SetNode setNode= new SetNode();
        //Log.d(TAG, "main: SetNode="+setNode.  );
        initScriptInter();
       // threadInterrruptedForce();
        poolMain=Executors.newFixedThreadPool(1);
        poolSub=Executors.newFixedThreadPool(1);
        
        if (listExecutorService==null){
            listExecutorService=new ArrayList<>();
        }

        poolMain= InterMy.ThreadStart( ()->{ jk2(); },1 );
        listExecutorService.add(poolMain);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void threadInterrruptedForce(){
        Log.d(TAG, "ThreadInterrruptedForce: ");
        if ( listExecutorService!=null ){
            for ( ExecutorService e :
                    listExecutorService  ) {
                InterMy.threadInterruptedForce( e );
                listExecutorService.remove( e );
            }
        }

    }

    public void initScriptInter(){
//        scriptRuntime= autoJs.getRunTime();
//        scriptRuntime.init();
//        uiSelector=new UiSelector( scriptRuntime.accessibilityBridge );
        clk= new SimpleActionAutomatorImp();
        node=new UiSelectorImp();

    }

    public void jk2(){
        while (true){
            Log.d(TAG, "jk2: run");
            try {

                UiSelector uiSelector=node.newUiSelector();
                UiSelector uiSelector1=node.newUiSelector();
                UiGlobalSelector uiGlobalSelector=uiSelector.textMatches("click");
                uiGlobalSelector=uiSelector.idStartsWith("clickb");
                UiGlobalSelector uiGlobalSelector1=uiSelector1.textMatches("bb");
                uiGlobalSelector1=uiSelector1.idStartsWith("bbb");

                Log.d(TAG, "jk2: 0="+uiGlobalSelector.toString()  );
                Log.d(TAG, "jk2: 1="+uiGlobalSelector1.toString()  );

                //clk.swipe(100,10,2,1500,1000);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "jk2: InterruptedException");
                e.printStackTrace();
                break;
            }
        }
    }

    public void jk3(){
        while (true){
            Log.d(TAG, "jk3: run");
            try {
               // clk.swipe(100,10,2,1500,1000);
                clk.click(2,2);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "jk2: InterruptedException");
                e.printStackTrace();
                break;
            }
        }
    }

    public void jk( ExecutorService executorServiceSub ){
        try {

            scriptRuntime.automator.prepareForGesture();
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {

                UiGlobalSelector uiGlobalSelector=  uiSelector.textMatches ("CLICK");
                Log.d(TAG, "run: "+uiGlobalSelector.toString()  );
                UiObject uiObject= uiSelector.findOne(5000);
                if (uiObject!=null){
                    Log.d(TAG, "run: "+ uiObject.bounds().toString() );
                    uiObject.click();
                }else
                {
                    Log.d(TAG, "run: 未找到");
                }

                   /*
                    UiObjectCollection uiObjectCollection = uiSelector.find();
                   for (int i=0;i< uiObjectCollection.size();i++ ){
                        UiObject uiObject=uiObjectCollection.get(i);
                        Log.d(TAG, "run: "+ uiObject.bounds().toString() );
                        if ( uiObject.isClickable() )
                        {
                            uiObject.click();
                            Log.d(TAG, "run: 已点击");
                        }    else
                            Log.d(TAG, "run: 不能点击");
                    }*/

                   /* for (int i=0;i<10;i++){
                         Log.d(TAG, "run: "+i );
                         boolean b=   scriptRuntime.automator.click( 1,1 );
                         if (b)
                             Log.d(TAG, "run: 点击成功");
                         else
                             Log.d(TAG, "run: 点击失败");
                        try {
                            Thread.sleep(1000);
                        } catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }*/
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "jk: Exception-"+e.toString() );
        }
    }

    public void ThreadStart( ExecutorService executorService, FunInterThreadMethod functionalInterface  ){
        for (int i=0;i<1;i++){
            executorService.execute( ()->{ functionalInterface.excute(); } );
        }
    }

}
