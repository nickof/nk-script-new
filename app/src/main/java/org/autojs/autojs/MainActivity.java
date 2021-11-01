/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.autojs.autojs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.nk.nkscript.tinker.app.BaseBuildInfo;
import com.nk.nkscript.tinker.app.BuildInfo;
import com.nk.nkscript.tinker.util.Utils;
import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequestActivity;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import org.autojs.autojs.nkScript.interImp.EnvScriptRuntime;
import org.autojs.autojs.nkScript.interImp.AES;
import org.autojs.autojs.nkScript.interImp.webSocket.WebSocketImp;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "nkScript-Tinker.MainActivity";
    private TextView mTvMessage = null;
    List<String> mPermissionList = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_CODE = 10000;
    String[] permissions;

    //openCV4Android 需要加载用到
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @SuppressLint("LongLogTag")
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected( status );
                }
                break;
            }
        }
    };

    private void getPermissions() {
        mPermissionList.clear();                                    //清空已经允许的没有通过的权限
        for (int i = 0; i < permissions.length; i++) {          //逐个判断是否还有未通过的权限
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (mPermissionList.size() > 0) {                           //有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE);
        } else {
            Log.e("getPermissions() >>>", "已经授权");     //权限已经都通过了
        }
    }

    public void permissionInit(){
                permissions = new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
    }

    @SuppressLint("LongLogTag")
    public boolean killAppBackGround(String packageName) {

        Log.d(TAG, "killAppBackGround: 1");
        ActivityManager manager = (ActivityManager) this.getSystemService(this.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> activityes = ((ActivityManager) manager).getRunningAppProcesses();

        Log.d(TAG, "killAppBackGround: 2");

        for (int iCnt = 0; iCnt < activityes.size(); iCnt++) {
            Log.d(TAG, "killAppBackGround:  " + "APP: " + iCnt + " " + activityes.get(iCnt).processName);

            if (activityes.get(iCnt).processName.contains(packageName)) {
                android.os.Process.sendSignal(activityes.get(iCnt).pid, android.os.Process.SIGNAL_KILL);
                android.os.Process.killProcess(activityes.get(iCnt).pid);
                Log.d(TAG, "killAppBackGround: suc..");
                return true;
            }
        }
        return false;

    }

    TextView textViewConnection;
    TextView textViewLogReceive;
    Button buttonConnect;
    Button buttonSend;

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EnvScriptRuntime.getAutoJs();

//        Log.d(TAG, "onCreate: key="+AES256Util.DEFAULT_SECRET_KEY  );
//        Log.d(TAG, "aes="+ AES256Util.encode( AES256Util.DEFAULT_SECRET_KEY,"111" ) );
        permissionInit();
        getPermissions();

        boolean isARKHotRunning = ShareTinkerInternals.isArkHotRuning();
        Log.e(TAG, "ARK HOT Running status = " + isARKHotRunning);
        Log.e(TAG, "i am on onCreate classloader:" + MainActivity.class.getClassLoader().toString());
        //test resource change
        Log.e(TAG, "i am on onCreate string:" + getResources().getString(R.string.test_resource));

        try {
            Log.d(TAG, "onCreate: base64-2="+AES.encrypt("111") );
        } catch (Exception e) {
            e.printStackTrace();
        }
         mTvMessage = findViewById( R.id.tv_message  );


       // edit_tel.setOn

//        requestMyPermissions();
//        requestSmsPermission();

 /*       Button loadPatchButton = (Button) findViewById(R.id.loadPatch);
        loadPatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinkerInstaller.onReceiveUpgradePatch( getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
            }
        });*/

   /*     Button loadLibraryButton = ( Button ) findViewById( R.id.loadLibrary );
        loadLibraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // #method 1, hack classloader library path
                TinkerLoadLibrary.installNavitveLibraryABI( getApplicationContext(), "armeabi");
                System.loadLibrary("stlport_shared");

            }
        });*/

    /*    Button cleanPatchButton = (Button) findViewById(R.id.cleanPatch);
        cleanPatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tinker.with(getApplicationContext()).cleanPatch();
            }
        });*/

   /*     Button killSelfButton = (Button) findViewById(R.id.killSelf);
        killSelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareTinkerInternals.killAllOtherProcess( getApplicationContext() );
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
*/

    /*    Button buildInfoButton = (Button) findViewById(R.id.showInfo);
        buildInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo(MainActivity.this);
                ScreenCaptureRequestActivity.requestKillApp(getApplicationContext(),"org.autojs.autojs");
                //killAppBackGround("jp.naver.line.android");
            }
        });*/


        Button  button_open_accessibility = (Button) findViewById(R.id.open_accessibility );
        button_open_accessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showInfo(MainActivity.this);
                getApplication().startActivity( new Intent( Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        //new WebSocketImp( MainActivity.this );

    }

    public void saveTel(){
        EditText edit_tel=(EditText)findViewById( R.id.edit_tel );
        edit_tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    public void requestSmsPermission(){

        int REQUEST_CODE_ASK_PERMISSIONS = 123;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasReadSmsPermission = checkSelfPermission(Manifest.permission.READ_SMS);
            if (hasReadSmsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;      //有权限没有通过

        if (MY_PERMISSIONS_REQUEST_CODE == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;   //发现有未通过权限
                    break;
                }
            }
        }

        if (hasPermissionDismiss) {                //如果有没有被允许的权限
            //假如存在有没被允许的权限,可提示用户手动设置 或者不让用户继续操作
            Log.d(TAG, "onRequestPermissionsResult: 有权限未通过");
        } else {
            Log.d(TAG, "onRequestPermissionsResult: 已全部授权");
        }
    }

    private void askForRequiredPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if ( !hasRequiredPermissions() ) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    private void askForRequiredWritePermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有读SD权限");
        }
    }


    private boolean hasRequiredPermissions() {

        if (Build.VERSION.SDK_INT >= 16) {
            final int res = ContextCompat.checkSelfPermission( this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return res == PackageManager.PERMISSION_GRANTED;
        } else {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            final int res = ContextCompat.checkSelfPermission( this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return res == PackageManager.PERMISSION_GRANTED;
        }
    }

    private boolean hasRequiredWritePermissions() {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            final int res = ContextCompat.checkSelfPermission( this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return res == PackageManager.PERMISSION_GRANTED;

    }


    public boolean showInfo(Context context) {
        // add more Build Info
        final StringBuilder sb = new StringBuilder();
        Tinker tinker = Tinker.with(getApplicationContext());
        if (tinker.isTinkerLoaded()) {

            sb.append(String.format("[patch is loaded] \n"));
            sb.append(String.format("[buildConfig TINKER_ID] %s \n", BuildInfo.TINKER_ID));
            sb.append(String.format("[buildConfig BASE_TINKER_ID] %s \n", BaseBuildInfo.BASE_TINKER_ID));

            sb.append(String.format("[buildConfig MESSSAGE] %s \n", BuildInfo.MESSAGE));
            sb.append(String.format("[TINKER_ID] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName(ShareConstants.TINKER_ID)));
            sb.append(String.format("[packageConfig patchMessage] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName("patchMessage")));
            sb.append(String.format("[TINKER_ID Rom Space] %d k \n", tinker.getTinkerRomSpace()));

        } else {
            sb.append(String.format("[patch is not loaded] \n"));
            sb.append(String.format("[buildConfig TINKER_ID] %s \n", BuildInfo.TINKER_ID));
            sb.append(String.format("[buildConfig BASE_TINKER_ID] %s \n", BaseBuildInfo.BASE_TINKER_ID));

            sb.append(String.format("[buildConfig MESSSAGE] %s \n", BuildInfo.MESSAGE));
            sb.append(String.format("[TINKER_ID] %s \n", ShareTinkerInternals.getManifestTinkerID(getApplicationContext())));
        }
        sb.append(String.format("[BaseBuildInfo Message] %s \n", BaseBuildInfo.TEST_MESSAGE));

        final TextView v = new TextView(context);
        v.setText(sb);
        v.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        v.setTextColor(0xFF000000);
        v.setTypeface(Typeface.MONOSPACE);
        final int padding = 16;
        v.setPadding(padding, padding, padding, padding);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(v);
        final AlertDialog alert = builder.create();
        alert.show();
        return true;
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "i am on onResume");
//        Log.e(TAG, "i am on patch onResume");

        super.onResume();
   /*     if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }*/

        Utils.setBackground( false );
        if (hasRequiredPermissions()) {
            mTvMessage.setVisibility(View.GONE);
        } else {
            mTvMessage.setText(R.string.msg_no_permissions);
            mTvMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            mTvMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.setBackground( true );
    }

}
