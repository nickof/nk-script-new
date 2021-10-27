package com.stardust.autojs.core.image.capture;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.util.IntentExtras;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Stardust on 2017/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureRequestActivity extends Activity {

    private static final String TAG ="nkScript-ScreenCapture" ;
    public static boolean flagBoolAlumdelete=false;
    private static boolean flagUpdateAlum=false;
    //openCV4Android 需要加载用到
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            switch (status) {

                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("ScreenCapture", "OpenCV loaded successfully" );
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }

        }
    };

    private OnActivityResultDelegate.Mediator mOnActivityResultDelegateMediator = new OnActivityResultDelegate.Mediator();
    private ScreenCaptureRequester mScreenCaptureRequester;
    private ScreenCaptureRequester.Callback mCallback;

    public static void request(Context context, ScreenCaptureRequester.Callback callback) {
        Intent intent = new Intent(context, ScreenCaptureRequestActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentExtras.newExtras()
                .put("callback", callback)
                .putInIntent(intent);
        context.startActivity(intent);
    }

    public static void requestUpdateAlum(Context context,String path  ){
        Intent intent = new Intent(context, ScreenCaptureRequestActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentExtras.newExtras()
                .put("updateAlbum", path)
                .putInIntent(intent);
        context.startActivity(intent);
    }

    public static void requestOpenAlbum(Context context,File path) {

        if (context==null)
            Log.d(TAG, "requestOpenAlbum: context-null");
        Intent intent = new Intent(context, ScreenCaptureRequestActivity.class )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentExtras.newExtras()
                .put("album", "1")
                .put("path",path)
                .putInIntent( intent );
        context.startActivity( intent );

    }

    public static void requestKillApp(Context context,String packageName) {

        if (context==null){
            Log.d(TAG, "requestKillApp: context-null");
            return ;
        }

        Intent intent = new Intent(context, ScreenCaptureRequestActivity.class )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentExtras.newExtras()
                .put("packageName", packageName)
                .putInIntent( intent );
        context.startActivity( intent );

    }

    public static void requestGetAllPhoto(Context context ){
              if (context==null){
            Log.d(TAG, "requestKillApp: context-null");
            return ;
        }

        Intent intent = new Intent(context, ScreenCaptureRequestActivity.class )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentExtras.newExtras()
                .put("getAllPhoto", "1")
                .putInIntent( intent );
        context.startActivity( intent );
    }

    /**
     * 删除文件后更新数据库  通知媒体库更新文件夹
     *
     * @param context
     * @param filepath 文件路径（要求尽量精确，以防删错）
     */
    public static void updateFileFromDatabase(Context context, String filepath) {
        String where = MediaStore.Audio.Media.DATA + " like \"" + filepath + "%" + "\"";
        int i = context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, null);
        //int i = context.getContentResolver().de
        /*
        if(i>0){
            Log.e("[msg]", "媒体库更新成功！");
        }
         */
    }

    /**
     * 删除文件后更新数据库  通知媒体库更新文件夹
     *
     * @param context
     * @param filepath 文件路径（要求尽量精确，以防删错）
     */
    public static void updateAlum(Context context, String filepath) throws FileNotFoundException {

        flagUpdateAlum=false;
        File filePath2=new File( filepath );

        MediaStore.Images.Media.insertImage( context.getContentResolver(  ),
                filePath2.getAbsolutePath(), System.currentTimeMillis()+".jpg", null );
        // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile( filePath2
        ) ) );

        flagUpdateAlum=true;
        //int i = context.getContentResolver().de
        /*
        if(i>0){
            Log.e("[msg]", "媒体库更新成功！");
        }
         */


    }

    public void getAllPhotoAndDelete() {

        Log.d(TAG, "getAllPhoto: ");
        flagBoolAlumdelete=false;
        //读取手机中的相片
        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
       // List<Photo> mPhotoList = new ArrayList<Photo>();
        int i=0;
        while (cursor.moveToNext()) {
            //获取图片的路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            if (path != null && path.length() > 0) {
                deleteSingleFile( path );
                Log.d(TAG, "getAllPhotoAndDelete: "+path );
                updateFileFromDatabase(ScreenCaptureRequestActivity.this, path);

/*                //获取图片的名称
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                //获取图片最后修改的日期
                //byte[] date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File file = new File(path);
                long modifieTime = file.lastModified();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = sdf.format(new Date(modifieTime));
                //获取图片的大小
                long size = cursor.getLong(cursor.getColumnIndex( MediaStore.Images.Media.SIZE ) );
                Photo photo = new Photo(name, date, size, path);
                Log.d(TAG, "getAllPhoto: photo="+photo );
                mPhotoList.add(photo);*/
                i++;
               // mPhotoList.add(new Photo("name","date",11,path));
            }
        }
        flagBoolAlumdelete=true;
        Log.d(TAG, "getAllPhoto: 照片数="+i  );

    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                //Log.e("[Method]", "FileTools.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                //Log.e("[Method]", "FileTools.deleteSingleFile: 删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            //Log.e("[Method]", "FileTools.deleteSingleFile: 删除单个文件" + filePath$Name + "不存在！");
            return false;
        }
    }

    public boolean killAppBackGround( String packageName ){

        ActivityManager mActivityManager = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = null;
        try {
            method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.setAccessible(true);
            method.invoke(mActivityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "killAppBackGround: 1");
        ActivityManager manager =  (ActivityManager) this.getSystemService(this.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> activityes = ((ActivityManager)manager).getRunningAppProcesses();

        Log.d(TAG, "killAppBackGround: 2");
        for (int iCnt = 0; iCnt < activityes.size(); iCnt++){
            Log.d(TAG, "killAppBackGround:  "+"APP: "+iCnt +" "+ activityes.get(iCnt).processName);
            if (activityes.get(iCnt).processName.contains( packageName )){
                android.os.Process.sendSignal(activityes.get(iCnt).pid, android.os.Process.SIGNAL_KILL);
                android.os.Process.killProcess(activityes.get(iCnt).pid);
                Log.d(TAG, "killAppBackGround: suc..");
                return true;
            }
        }
        return false;

    }

    @Override
    protected void onResume() {
        super.onResume();
  /*      if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }*/
    }

    @ScriptInterface
    public boolean launchPackage(String packageName) {
        try {
            PackageManager packageManager = getApplication().getPackageManager();
            getApplication().startActivity(packageManager.getLaunchIntentForPackage( packageName )
                    .addFlags( Intent.FLAG_ACTIVITY_NEW_TASK) );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentExtras extras = IntentExtras.fromIntentAndRelease(getIntent());
        if (extras == null) {
            finish();
            return;
        }


        if (extras.get("updateAlbum")!=null){
            //updateFileFromDatabase(  );
            try {
                updateAlum(ScreenCaptureRequestActivity.this,extras.get("updateAlbum")  );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG, "onCreate: updateAlbum exception"+e.toString() );
            }
        }

        if (extras.get("getAllPhoto")!=null){
            getAllPhotoAndDelete ();
            finish();
            return;
        }

        if (extras.get ("album")!=null){
                openAlbum( extras.get("path") );
                finish();
                return;
        }

        if (extras.get("packageName")!=null ){
            this.setVisible(false);
            launchPackage( "jp.naver.line.android" );

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            killAppBackGround(extras.get("packageName"));
            finish();
            killAppBackGround(extras.get("packageName"));
            return;
        }

        mCallback = extras.get("callback");
        if (mCallback == null) {
            finish();
            return;
        }

        mScreenCaptureRequester = new ScreenCaptureRequester.ActivityScreenCaptureRequester(mOnActivityResultDelegateMediator, this);
        mScreenCaptureRequester.setOnActivityResultCallback( mCallback );
        mScreenCaptureRequester.request();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallback = null;
        if (mScreenCaptureRequester == null)
            return;
        mScreenCaptureRequester.cancel();
        mScreenCaptureRequester = null;
    }



    public void openAlbum( File mPath ){

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile( mPath ), "image/*");
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mOnActivityResultDelegateMediator.onActivityResult(requestCode, resultCode, data);
        finish();
    }

}