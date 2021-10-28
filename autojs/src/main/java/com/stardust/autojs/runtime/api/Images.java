package com.stardust.autojs.runtime.api;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.RequiresApi;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.annotation.ScriptVariable;
import com.stardust.autojs.core.image.ColorFinder;
import com.stardust.autojs.core.image.ImageWrapper;
import com.stardust.autojs.core.image.TemplateMatching;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequester;
import com.stardust.autojs.core.image.capture.ScreenCapturer;
import com.stardust.autojs.core.opencv.Mat;
import com.stardust.autojs.core.opencv.OpenCVHelper;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.util.ScriptPromiseAdapter;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Stardust on 2017/5/20.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Images {

    private static final String TAG = "aujs.image";
    private ScriptRuntime mScriptRuntime;
    private ScreenCaptureRequester mScreenCaptureRequester;
    private ScreenCapturer mScreenCapturer;
    private Context mContext;
    private Image mPreCapture;
    private ImageWrapper mPreCaptureImage;
    private ScreenMetrics mScreenMetrics;
    private volatile boolean mOpenCvInitialized = false;

    @ScriptVariable
    public final ColorFinder colorFinder;

    public Images(Context context, ScriptRuntime scriptRuntime, ScreenCaptureRequester screenCaptureRequester) {
        mScriptRuntime = scriptRuntime;
        mScreenCaptureRequester = screenCaptureRequester;
        mContext = context;
        mScreenMetrics = mScriptRuntime.getScreenMetrics();
        colorFinder = new ColorFinder(mScreenMetrics);
    }

    public ScreenCapturer getmScreenCapturer() {
        return mScreenCapturer;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScriptPromiseAdapter requestScreenCapture(int orientation) {

        ScriptRuntime.requiresApi(21);
        ScriptPromiseAdapter promiseAdapter = new ScriptPromiseAdapter();
        if (mScreenCapturer != null) {
            mScreenCapturer.setOrientation(orientation);
            promiseAdapter.resolve(true);
            return promiseAdapter;
        }
        Looper servantLooper = mScriptRuntime.loopers.getServantLooper();
        mScreenCaptureRequester.setOnActivityResultCallback((result, data) -> {
            if (result == Activity.RESULT_OK) {
                mScreenCapturer = new ScreenCapturer(mContext, data, orientation, ScreenMetrics.getDeviceScreenDensity(),
                        new Handler(servantLooper));
                promiseAdapter.resolve(true);
            } else {
                promiseAdapter.resolve(false);
            }
        });
        mScreenCaptureRequester.request();
        return promiseAdapter;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public synchronized ImageWrapper captureScreen() {
        ScriptRuntime.requiresApi(21);
        if (mScreenCapturer == null) {
            throw new SecurityException("No screen capture permission");
        }
        Image capture = mScreenCapturer.capture();
        if (capture == mPreCapture && mPreCaptureImage != null) {
            return mPreCaptureImage;
        }
        mPreCapture = capture;
        if (mPreCaptureImage != null) {
            mPreCaptureImage.recycle();
        }
        mPreCaptureImage = ImageWrapper.ofImage(capture);
        return mPreCaptureImage;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean captureScreen(String path) {
        path = mScriptRuntime.files.path(path);
        ImageWrapper image = captureScreen();
        if (image != null) {
            //String uriString = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),image.getBitmap(), "截图-20141121", "这是我的截图");
            image.saveTo(path);
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean captureScreenToAlbum() {
        //path = mScriptRuntime.files.path(path);
        ImageWrapper image = captureScreen();
        if (image != null) {
            String uriString = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),image.getBitmap(), "截图-20141121", "这是我的截图");
            return true;
        }
        return false;
    }

    public ImageWrapper copy(ImageWrapper image) {
        return image.clone();
    }

    public boolean save(ImageWrapper image, String path, String format, int quality) throws IOException {
        Bitmap.CompressFormat compressFormat = parseImageFormat(format);
        if (compressFormat == null)
            throw new IllegalArgumentException("unknown format " + format);
        Bitmap bitmap = image.getBitmap();
        FileOutputStream outputStream = new FileOutputStream(mScriptRuntime.files.path(path));
        return bitmap.compress(compressFormat, quality, outputStream);
    }

    public static int pixel(ImageWrapper image, int x, int y) {
        if (image == null) {
            throw new NullPointerException("image = null");
        }
        return image.pixel(x, y);
    }

    /**
        截图保存相册
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public File captureSaveImageToGallery() {
        //Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        ImageWrapper image = captureScreen();
        Bitmap bitmap=image.getBitmap();

        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "nkScreen-" + format.format(new Date())+".png";
        // 保存图片至指定路径
        String storePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"LS" ;
        Log.d(TAG, "captureSaveImageToGallery: path="+storePath );
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //发送广播通知系统图库刷新数据
            Log.d(TAG, "captureSaveImageToGallery: 发送广播通知系统图库刷新数据");
            Uri uri = Uri.fromFile(file);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            //mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri ) );

            if (isSuccess) {
                GlobalAppContext.toast("图片已保存至+"+file);
                return file;
            } else {
                GlobalAppContext.toast("图片已保存失败+"+file);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//————————————————
//    版权声明：本文为CSDN博主「weixin_38934440」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/weixin_38934440/article/details/106619501

    public static ImageWrapper concat(ImageWrapper img1, ImageWrapper img2, int direction) {
        if (!Arrays.asList(Gravity.LEFT, Gravity.RIGHT, Gravity.TOP, Gravity.BOTTOM).contains(direction)) {
            throw new IllegalArgumentException("unknown direction " + direction);
        }
        int width;
        int height;
        if (direction == Gravity.LEFT || direction == Gravity.TOP) {
            ImageWrapper tmp = img1;
            img1 = img2;
            img2 = tmp;
        }
        if (direction == Gravity.LEFT || direction == Gravity.RIGHT) {
            width = img1.getWidth() + img2.getWidth();
            height = Math.max(img1.getHeight(), img2.getHeight());
        } else {
            width = Math.max(img1.getWidth(), img2.getHeight());
            height = img1.getHeight() + img2.getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        if (direction == Gravity.LEFT || direction == Gravity.RIGHT) {
            canvas.drawBitmap(img1.getBitmap(), 0, (height - img1.getHeight()) / 2, paint);
            canvas.drawBitmap(img2.getBitmap(), img1.getWidth(), (height - img2.getHeight()) / 2, paint);
        } else {
            canvas.drawBitmap(img1.getBitmap(), (width - img1.getWidth()) / 2, 0, paint);
            canvas.drawBitmap(img2.getBitmap(), (width - img2.getWidth()) / 2, img1.getHeight(), paint);
        }
        return ImageWrapper.ofBitmap(bitmap);
    }

    public ImageWrapper rotate(ImageWrapper img, float x, float y, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree, x, y);
        return ImageWrapper.ofBitmap(Bitmap.createBitmap(img.getBitmap(), 0, 0, img.getWidth(), img.getHeight(), matrix, true));
    }

    public ImageWrapper clip(ImageWrapper img, int x, int y, int w, int h) {
        return ImageWrapper.ofBitmap(Bitmap.createBitmap(img.getBitmap(), x, y, w, h));
    }

    public ImageWrapper read(String path) {
        //modify
        path = mScriptRuntime.files.path(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return ImageWrapper.ofBitmap(bitmap);
    }

    public ImageWrapper read2(String path) {
        path = mScriptRuntime.files.path(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return ImageWrapper.ofBitmap(bitmap);
    }

    public ImageWrapper fromBase64(String data) {
        return ImageWrapper.ofBitmap(Drawables.loadBase64Data(data));
    }

    public String toBase64(ImageWrapper wrapper, String format, int quality) {
        return Base64.encodeToString(toBytes(wrapper, format, quality), Base64.NO_WRAP);
    }

    public byte[] toBytes(ImageWrapper wrapper, String format, int quality) {
        Bitmap.CompressFormat compressFormat = parseImageFormat(format);
        if (compressFormat == null)
            throw new IllegalArgumentException("unknown format " + format);
        Bitmap bitmap = wrapper.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, outputStream);
        return outputStream.toByteArray();
    }

    public ImageWrapper fromBytes(byte[] bytes) {
        return ImageWrapper.ofBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    private Bitmap.CompressFormat parseImageFormat(String format) {
        switch (format) {
            case "png":
                return Bitmap.CompressFormat.PNG;
            case "jpeg":
            case "jpg":
                return Bitmap.CompressFormat.JPEG;
            case "webp":
                return Bitmap.CompressFormat.WEBP;
        }
        return null;
    }

    public ImageWrapper load(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return ImageWrapper.ofBitmap(bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    public void releaseScreenCapturer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mScreenCapturer != null) {
            mScreenCapturer.release();
        }
    }

    public Point findImage(ImageWrapper image, ImageWrapper template) {
        return findImage(image, template, 0.9f, null);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold) {
        return findImage(image, template, threshold, null);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold, Rect rect) {
        return findImage(image, template, 0.7f, threshold, rect, TemplateMatching.MAX_LEVEL_AUTO);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float weakThreshold, float threshold, Rect rect, int maxLevel) {
      initOpenCvIfNeeded();
        if (image == null)
            throw new NullPointerException("image = null");
        if (template == null)
            throw new NullPointerException("template = null");
        Mat src = image.getMat();
        if (rect != null) {
            src = new Mat(src, rect);
        }
        Point point = TemplateMatching.fastTemplateMatching(src, template.getMat(), TemplateMatching.MATCHING_METHOD_DEFAULT,
                weakThreshold, threshold, maxLevel);
        if (point != null) {
            if (rect != null) {
                point.x += rect.x;
                point.y += rect.y;
            }
            Log.d(TAG, "findImage: "+mScreenMetrics.mDesignWidth+","+mScreenMetrics.mDesignHeight );
            point.x = mScreenMetrics.scaleX((int) point.x);
            point.y = mScreenMetrics.scaleX((int) point.y);
        }
        if (src != image.getMat()) {
            OpenCVHelper.release(src);
        }
        return point;
    }

    public List<TemplateMatching.Match> matchTemplate(ImageWrapper image, ImageWrapper template, float weakThreshold, float threshold, Rect rect, int maxLevel, int limit) {
        initOpenCvIfNeeded();
        if (image == null)
            throw new NullPointerException("image = null");
        if (template == null)
            throw new NullPointerException("template = null");
        Mat src = image.getMat();
        if (rect != null) {
            src = new Mat(src, rect);
        }
        List<TemplateMatching.Match> result = TemplateMatching.fastTemplateMatching(src, template.getMat(), Imgproc.TM_CCOEFF_NORMED,
                weakThreshold, threshold, maxLevel, limit);
        for (TemplateMatching.Match match : result) {
            Point point = match.point;
            if (rect != null) {
                point.x += rect.x;
                point.y += rect.y;
            }
            point.x = mScreenMetrics.scaleX((int) point.x);
            point.y = mScreenMetrics.scaleX((int) point.y);
        }
        if (src != image.getMat()) {
            OpenCVHelper.release(src);
        }
        return result;
    }

    public Mat newMat() {
        return new Mat();
    }

    public Mat newMat(Mat mat, Rect roi) {
        return new Mat(mat, roi);
    }

    public void initOpenCvIfNeeded() {
        if (mOpenCvInitialized || OpenCVHelper.isInitialized()) {
            return;
        }
        Activity currentActivity = mScriptRuntime.app.getCurrentActivity();
        Context context = currentActivity == null ? mContext : currentActivity;
        //modify
//        mScriptRuntime.console.info("opencv initializing");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            OpenCVHelper.initIfNeeded(context, () -> {
                mOpenCvInitialized = true;
//                mScriptRuntime.console.info("opencv initialized");
            });
        } else {
            VolatileDispose<Boolean> result = new VolatileDispose<>();
            OpenCVHelper.initIfNeeded(context, () -> {
                mOpenCvInitialized = true;
                result.setAndNotify(true);
//                mScriptRuntime.console.info("opencv initialized");
            });
            result.blockedGet();
        }

        Log.d(TAG, "initOpenCvIfNeeded: finish");
    }
}
