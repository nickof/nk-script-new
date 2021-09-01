package com.stardust.autojs.core.image.capture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.util.IntentExtras;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created by Stardust on 2017/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureRequestActivity extends Activity {
    private static final String TAG ="ScreenCapture" ;
    //openCV4Android 需要加载用到
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("ScreenCapture", "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentExtras extras = IntentExtras.fromIntentAndRelease(getIntent());
        if (extras == null) {
            finish();
            return;
        }
        mCallback = extras.get("callback");
        if (mCallback == null) {
            finish();
            return;
        }
        mScreenCaptureRequester = new ScreenCaptureRequester.ActivityScreenCaptureRequester(mOnActivityResultDelegateMediator, this);
        mScreenCaptureRequester.setOnActivityResultCallback(mCallback);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mOnActivityResultDelegateMediator.onActivityResult(requestCode, resultCode, data);
        finish();
    }

}