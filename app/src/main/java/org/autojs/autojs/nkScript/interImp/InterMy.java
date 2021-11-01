package org.autojs.autojs.nkScript.interImp;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import com.stardust.app.GlobalAppContext;
import com.stardust.util.ClipboardUtil;

import org.autojs.autojs.nkScript.Service.ScriptService;
import org.autojs.autojs.nkScript.ThreadpoolScriptManager;
import org.autojs.autojs.nkScript.functionInterface.FunInterThreadMethod;
import org.autojs.autojs.nkScript.model.ShareDataScript;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InterMy {

    private static final String TAG = InterMy.class.getSimpleName();
    /***
     *  音量
     * @param event
     */
    public static void volEventDo(KeyEvent event ){

            Log.i(TAG, "onKeyEvent");
            int key = event.getKeyCode();
             Log.d(TAG, "volEventDo: "+event.toString() );
            Application application;
            Intent intent;
            switch(key){

                case KeyEvent.KEYCODE_VOLUME_DOWN:
          /*          Application application= (Application) GlobalAppContext.get();
                    ThreadpoolScriptManager.shutDownAll();*/
                    synchronized ( ShareDataScript.ScriptServiceLock ){
                        Log.i(TAG, "KEYCODE_VOLUME_DOWN");
                         application= (Application) GlobalAppContext.get();
                    /*     intent=new Intent( application, ScriptService.class );
                        application.stopService (intent);*/
                        ThreadpoolScriptManager.shutDownAll();
                        break;
                    }
                case KeyEvent.KEYCODE_VOLUME_UP:
             /*       application= (Application) GlobalAppContext.get();
                    Log.i(TAG, "KEYCODE_VOLUME_UP");
                    ThreadpoolScriptManager.shutDownAll();
                    new Run().main();*/
                    synchronized ( ShareDataScript.ScriptServiceLock){
                        application= (Application) GlobalAppContext.get();
                        if (application==null)
                            Log.d(TAG, "volEventDo: application is null");
                        //intent=new Intent( application, ScriptService.class );
                        intent=new Intent( application, ScriptService.class );
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            application.startForegroundService(intent);
                        } else {
                            application.startService(intent);
                        }
                        break;
                    }

            }
//————————————————
//        版权声明：本文为CSDN博主「法迪」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/su749520/article/details/79670220
        }

    /***
     *  利用线程池特性启动启动线程,taskNum多条可以在线程中断的时候,继续取队列的任务重启任务
     * @param funInterThreadMethod
     * @param taskNum
     * @return
     */
    public static ExecutorService ThreadStart(FunInterThreadMethod funInterThreadMethod, int taskNum  ){
        ExecutorService executorService= Executors.newFixedThreadPool(1);
        for (int i=0;i<taskNum;i++){
            executorService.execute( ()->{
                try {
                    funInterThreadMethod.excute();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Log.d(TAG, "ThreadStart: InterruptedException="+e.toString() );
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } );
        }
        return executorService;
    }

    /**
     * 用executorService发InterruptedException 停止线程
     * @param executorService
     * @return
     */
    public static ExecutorService threadInterrupted( ExecutorService executorService ){

        try {
            executorService.shutdownNow();
            boolean flag=executorService.awaitTermination(1000*10, TimeUnit.MILLISECONDS );
            Log.d(TAG, "threadInterrupted: flag="+flag );
            if(!flag){
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "ScriptExcute: " + "awaitTermination interrupted: " + e);
            System.out.println("awaitTermination interrupted: " + e);
            executorService.shutdownNow();
        }
        return executorService;
    }

    public static void writeConfig(Context context,String spKey,String key, String value){
        SharedPreferences sharedPreferences=context.getSharedPreferences(spKey,context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static String readConfig(Context context,String spKey,String key, String value){
        SharedPreferences sharedPreferences=context.getSharedPreferences(spKey,context.MODE_PRIVATE);
        return sharedPreferences.getString( key,value );
    }

    public static void setClip(String str){
        ClipboardUtil.setClip( GlobalAppContext.get(),str );
    }

    /***
     * 强行停止后才退出
     * @param executorService
     * @return
     */
    public static ExecutorService threadInterruptedForce( ExecutorService executorService ){
        try {
            executorService.shutdownNow();
            while ( !executorService.awaitTermination(1000*10, TimeUnit.MILLISECONDS ) ){
                    executorService.shutdownNow();
                Log.d(TAG, "threadInterrupted: fail " );
            }
            Log.d(TAG, "threadInterruptedForce: suc..");

        } catch (InterruptedException e) {
            Log.d(TAG, "ScriptExcute: " + "awaitTermination interrupted: " + e);
            System.out.println("awaitTermination interrupted: " + e);
            executorService.shutdownNow();
        }
        return executorService;
    }


    public void stopScriptService( Class clazz ){
        synchronized (ShareDataScript.ScriptServiceLock){
            Application application= (Application) GlobalAppContext.get();
            if (application==null){
                Log.d(TAG, "onMessage: application is null");
            }
            application.stopService( new Intent( application, ScriptService.class ));
        }
    }

    public void startScriptService( Class clazz  ){
        synchronized (ShareDataScript.ScriptServiceLock){
            Application application= (Application) GlobalAppContext.get();
            if (application==null)
                Log.d(TAG, "onMessage: application is null");
            //intent=new Intent( application, ScriptService.class );
            Intent intent=new Intent( application, clazz );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                application.startForegroundService(intent);
            } else {
                application.startService(intent);
            }
        }
    }


}

