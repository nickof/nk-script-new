package org.autojs.autojs.nkScript.interImp;

import android.os.Build;
import android.util.Log;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.image.ColorFinder;
import com.stardust.autojs.core.image.ImageWrapper;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Images;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.lang.reflect.Array;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public class ImagesImp {

    private static final String TAG ="ImagesImp" ;
    public final ScriptRuntime scriptRuntime;
    public Images images;
    public ColorFinder colorFinder;
    public ImageWrapper imageWrapperMain;
    public final int imageStat_release=0;
    public final int imageStat_keep=1;
    public int imageStat=1;

    public ImagesImp() throws InterruptedException {
        scriptRuntime= EnvScriptRuntime.getScriptRuntime();
        images = scriptRuntime.getImages();
        colorFinder =new ColorFinder(scriptRuntime.getScreenMetrics());
        waitPermission();
    }


    public void waitPermission() throws InterruptedException {

        ExecutorService executorService=InterMy.ThreadStart( ()-> {
            long stTime=System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // images.initOpenCvIfNeeded();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.images.requestScreenCapture(3);
                    while (this.images.getmScreenCapturer()==null ){
                        GlobalAppContext.toast("wait-permisson");
                        Log.d(TAG,"wait-permisson");
                        Thread.sleep(1000);
                        if (System.currentTimeMillis()-stTime>1000*30){
                            throw new Exception("wait-permission-outtime");
                        }
                    }
                }
            }  } ,1);

/*        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }*/
    }

    public void waitPermission2() throws InterruptedException {
        if (this.images.getmScreenCapturer()==null){
            while (this.images.getmScreenCapturer()==null ){
                GlobalAppContext.toast("wait-permisson");
                Log.d(TAG,"wait-permisson");
                Thread.sleep(1000);
            }
        }
    }

    //"ff7716","56|20|fefefe,72|34|fefefe,94|46|ff7716,97|86|ff7716,16|11|fefefe"
    public Point findMultiColors(String colorGroup,String rect,int threshold ) throws InterruptedException {
        waitPermission2();
        //  public Point findMultiColors(ImageWrapper image, int firstColor, int threshold, Rect rect, int[] points)
        //colorFinder.findMultiColors(  )
        ImageWrapper imageWrapperMain=getImageWrapperMain();
        String[] colorArr=colorGroup.split(",");
        String[] colorArrSub;
        int firstColor=Integer.parseInt(colorArr[0],16);
        int[] points = null;
        int jCurIdx=0;

        if (colorArr.length>1){
            points =new int[ (colorArr.length-1)*3 ];
            for (int i=1;i<colorArr.length;i++){
                colorArrSub=colorArr[i].split(Pattern.quote("|"));
                points[jCurIdx]=Integer.parseInt(colorArrSub[0]);
                points[jCurIdx+1]=Integer.parseInt(colorArrSub[1]);
                points[jCurIdx+2]=Integer.parseInt(colorArrSub[2],16);
                Log.d(TAG, "findMultiColors: "+points[jCurIdx]
                        +","+ points[jCurIdx+1]+","+points[jCurIdx+2] );
                jCurIdx+=3;
            }

        }
        //  Log.d(TAG, "findMultiColors: "+ points.toString() );

        String[] rectArr=rect.split(",");
        rectArr[0]= rectArr[0].replace("[","");
        rectArr[3]=rectArr[3].replace("]","");
        Log.d(TAG, "findMultiColors: rect="+rectArr.toString() );


        Rect rect1=new Rect( Integer.parseInt(rectArr[0]),Integer.parseInt(rectArr[1]),
                Integer.parseInt(rectArr[2]),Integer.parseInt(rectArr[3]));
        Log.d(TAG,"rect="+rect1.toString() );
        images.initOpenCvIfNeeded();
        Point point= colorFinder.findMultiColors(imageWrapperMain,firstColor,threshold, rect1,points);

        return point;
    }

    public ImageWrapper getImageWrapperMain() {
        if (imageStat==imageStat_release)
            imageWrapperMain=images.captureScreen();
        return imageWrapperMain;
    }

    public void ks() throws InterruptedException {
        waitPermission2();
        imageWrapperMain=images.captureScreen();
        imageStat=imageStat_keep;
    }

    public void relase() throws InterruptedException {
        waitPermission2();
        imageStat=imageStat_release;
    }

}
