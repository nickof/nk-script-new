package org.autojs.autojs.nkScript;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


import com.stardust.app.GlobalAppContext;
import com.stardust.util.UiHandler;

import org.autojs.autojs.R;
import org.autojs.autojs.nkScript.interImp.InterMy;

import java.util.concurrent.ExecutorService;


public class ScriptService extends Service {

    private static final String TAG = "ScriptService";
    public static final String CHANNEL_ID_STRING = "service_01";
    private Notification notification;
    ScriptRunMain scriptRunMain;
    Run run;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate: run++");
        super.onCreate();
  //      Context appCotext=GlobalAppContext.get();
        GlobalAppContext.set((Application) getApplicationContext());
        NotificationManager notificationManager = (NotificationManager)this.
                getSystemService( getApplicationContext().NOTIFICATION_SERVICE );
        NotificationChannel mChannel = null;
        Log.d(TAG, "onCreate: sdk ver="+android.os.Build.VERSION.SDK_INT );

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name ),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            Log.d(TAG, "onCreate: startForeground");
            startForeground(1, notification );
        }

//        scriptRunMain=   new ScriptRunMain();
//        scriptRunMain.main();

        run=new Run();
        run.main();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: run..");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForeground(1, notification);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if ( run.listExecutorService!=null ){
            Log.d(TAG, "onDestroy: stopThread");
            for ( ExecutorService ser: run.listExecutorService ) {
                Log.d(TAG, "onDestroy: stopSub");
                InterMy.threadInterruptedForce( ser )  ;
               // new UiHandler( this.getApplicationContext() ).toast("线程停止成功");
            }
            new UiHandler( this.getApplicationContext() ).toast("全部线程停止成功");
           // GlobalAppContext.toast_( "全部线程停止成功" );
        }

        super.onDestroy();

    }

}
