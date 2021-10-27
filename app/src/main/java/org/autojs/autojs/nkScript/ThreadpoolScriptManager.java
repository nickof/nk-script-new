package org.autojs.autojs.nkScript;

import android.util.Log;

import com.stardust.app.GlobalAppContext;
import com.stardust.util.UiHandler;

import org.autojs.autojs.nkScript.interImp.InterMy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

//当前的脚本运行的线程池.关闭依据
public class ThreadpoolScriptManager {
    private static final String TAG = "nk-script-ThreadpoolScriptManager";
    private static volatile ArrayList<ExecutorService>  listExecutorServices;

    public static void setListExecutorServices( ArrayList<ExecutorService> listExecutorServicesScript ){
        synchronized ( "s" ){
            if (listExecutorServices==null)
                listExecutorServices=new ArrayList<>();

            for ( ExecutorService e :
                listExecutorServicesScript  ) {
                listExecutorServices.add(e);
            }
        }
    }


    public static ArrayList<ExecutorService> getListExecutorServices(){
        synchronized ( "s" ){
            if (listExecutorServices==null)
                listExecutorServices=new ArrayList<>();
            return listExecutorServices ;
        }
    }

    public static boolean isRunning(){
        getListExecutorServices();
        if ( listExecutorServices.size()>0 ){
            new UiHandler( GlobalAppContext.get()).toast("脚本线程已经在执行");
            return true;
        }else{
            new UiHandler( GlobalAppContext.get()).toast("没有线程运行");
            return false;
        }
    }

    public static void shutDownAll(){
        getListExecutorServices();
       if ( listExecutorServices.size()>0 ){
           for ( ExecutorService ser:  listExecutorServices ) {
               //Log.d(TAG, "onDestroy: stopSub");
               InterMy.threadInterruptedForce( ser )  ;
           }
           listExecutorServices.clear();
           new UiHandler(GlobalAppContext.get()).toast("全部线程停止成功");
       }else
           new UiHandler(GlobalAppContext.get()).toast("没有线程运行");

    }

}
