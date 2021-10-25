package org.autojs.autojs.nkScript.interImp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.image.ColorFinder;
import com.stardust.autojs.core.image.ImageWrapper;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Images;
import com.stardust.util.ScreenMetrics;

import org.autojs.autojs.MainActivity;
import org.autojs.autojs.nkScript.Run;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import kotlin.jvm.Synchronized;
import kotlin.jvm.Throws;

public class ImagesImp {

    private static final String TAG ="ImagesImp" ;
    public  ScriptRuntime scriptRuntime;
    public Images images;
    public ColorFinder colorFinder;
    public ImageWrapper imageWrapperMain;
    public final int imageStat_release=0;
    public final int imageStat_keep=1;
    public int imageStat=0;
    public Run run;
    public final ScreenMetrics mScreenMetrics;

    public ImagesImp()  {


        scriptRuntime= EnvScriptRuntime.getScriptRuntime();
        images = scriptRuntime.getImages();
        mScreenMetrics = scriptRuntime.getScreenMetrics();
        Log.d(TAG, "ImagesImp: screenMetrics mDesign="+ mScreenMetrics.mDesignWidth
        +","+ mScreenMetrics.mDesignHeight);
        //screenMetrics.setScreenMetrics(1080,1920);
        colorFinder =new ColorFinder(mScreenMetrics);
        Log.d(TAG, "ImagesImp: cons-finish");
        //this.run=run;

    }


    public void requestWaitPermission() throws Exception {

            long stTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //images.initOpenCvIfNeeded()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    if (images.getmScreenCapturer()==null ) {
                        Log.d(TAG, "requestWaitPermission: request permission");
                        images.requestScreenCapture(3);
                    }
                    while (images.getmScreenCapturer() == null) {
                        Log.d(TAG, "wait-permisson");
                        try {
                            Thread.sleep(1000);
                            if (System.currentTimeMillis()-stTime>1000*30){
                                throw new Exception("wait-permission-outtime");
                            }
                        } catch (InterruptedException e) {
                            Log.d(TAG, "interrupted");
                            throw new Exception("wait-permisson-interrupted");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
                images.initOpenCvIfNeeded();
        }


    public void waitPermissionOnlyWait() throws Exception {

        long stTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //images.initOpenCvIfNeeded()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //images.requestScreenCapture(3);
                while (images.getmScreenCapturer() == null) {
                    Log.d(TAG, "wait-permisson-only-wait");
                    try {
                        Thread.sleep(1000);
                        if (System.currentTimeMillis()-stTime>1000*30){
                            throw new Exception("wait-permission-outtime");
                        }
                    } catch (InterruptedException e) {
                        Log.d(TAG, "interrupted");
                        throw new Exception("wait-permisson-interrupted");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        images.initOpenCvIfNeeded();

    }

    public void waitPermission2() throws InterruptedException {

        Log.d(TAG, "waitPermission2: run");
        if (images.getmScreenCapturer()==null){
            while (this.images.getmScreenCapturer()==null ){
                GlobalAppContext.toast("wait-permisson2");
                Log.d(TAG,"wait-permisson2");
                Thread.sleep(1000);
            }
        }
    }

    //"#7a7a7a",[[35,78,"#6200ee"],[315,116,"#ffffff"],[813,-183,"#6200ee"],[537,-251,"#3700b3"]]
    //"ff7716","56|20|fefefe,72|34|fefefe,94|46|ff7716,97|86|ff7716,16|11|fefefe"
    //"7a7a7a,35|78|6200ee,315|116|ffffff,813|-183|6200ee,537|-251|3700b3"
    //"60|305|7a7a7a,95|383|6200ee,375|421|ffffff,873|122|6200ee,597|54|3700b3"

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean captureScreen( String path ) {
//        if (!hasRequiredPermissions())
//            askForRequiredPermissions();
        return images.captureScreen(path);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public File captureSaveImageToGallery() {
        return images.captureSaveImageToGallery();
    }


    public String colorTransformMultiColors( String colorGroup ){

        //"60|305|7a7a7a,95|383|6200ee,375|421|ffffff,873|122|6200ee,597|54|3700b3"
        String []colorArr=colorGroup.split(",");
        StringBuilder stringBuilder=new StringBuilder();
        int stx,sty;
        String distanceX,distanceY;
        String []colorArrSub;

        colorArrSub=colorArr[0].split( Pattern.quote("|") );
        stringBuilder.append(colorArrSub[2]);

        if (colorArr.length>1){
            stx=Integer.parseInt(colorArrSub[0]);
            sty=Integer.parseInt(colorArrSub[1]);
            for (int i=1;i<colorArr.length;i++){
                stringBuilder.append(",");
                //Log.d(TAG, "colorTransformMultiColors: "+colorArr[i] );
                colorArrSub=colorArr[i].split( Pattern.quote("|") );
                distanceX= (Integer.parseInt(colorArrSub[0])-stx)+"";
                distanceY=(Integer.parseInt(colorArrSub[1])-sty)+"";
                colorArr[i]=colorArr[i].replace(colorArrSub[0],distanceX);
                colorArr[i]=colorArr[i].replace(colorArrSub[1],distanceY);
                stringBuilder.append(colorArr[i]);
            }
        }
        Log.d(TAG, "colorTransformMultiColors: "+stringBuilder.toString() );
        return stringBuilder.toString();

    }

    //[28,83,940,905]
    //mapOf<String,String>( "c" to "48|21|d7ccf0,84|196|6200ee,
    // 147|418|ffffff","s" to "0.9","r" to "[68,266,1057,794]")

    public Point findMultiColors(  Map<String,String> map) throws InterruptedException {
        String rect;
        int threshold=0;

        if ( map.containsKey("r") ){
            rect=map.get("r");
        }else
            rect="0,0,"+ScreenMetrics.getDeviceScreenWidth()+","+ScreenMetrics.getDeviceScreenHeight() ;

        if (map.containsKey("s"))
            threshold= Integer.parseInt(map.get("s") );

       // String colorGroup=colorTransformMultiColors( map.get("c") );
        String colorGroup= map.get("c");
        Point p=null;
         synchronized ( this ){
            p= findMultiColors( colorGroup,rect,threshold );
        }
        return  p;
    }

    //
    public Point findMultiColors(String colorGroup,String rect,int threshold ) throws InterruptedException {

        //waitPermission2();
        //Log.d(TAG,"findMultiColors-run" );
        getImageWrapperMain();
        colorGroup=colorTransformMultiColors( colorGroup );
        //Log.d(TAG, "findMultiColors: imageWrapperMain="+imageWrapperMain );
        String[] colorArr=colorGroup.split(",");
        String[] colorArrSub;
        Log.d(TAG, "findMultiColors: colorGroup="+colorGroup);

        int firstColor=Integer.parseInt( colorArr[0],16 );
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
                        +","+ points[jCurIdx+1]+","+points[jCurIdx+2]
                        +",thread="+Thread.currentThread().getName()  );
                jCurIdx+=3;
            }
        }

        Log.d(TAG, "findMultiColors: rect="+rect.toString() );
        //  Log.d(TAG, "findMultiColors: "+ points.toString() );
        String[] rectArr=rect.split(",");
        rectArr[0]= rectArr[0].replace("[","");
        rectArr[3]=rectArr[3].replace("]","");
        Log.d(TAG, "findMultiColors: rect="+rectArr.toString() );

        int x,y,x2,y2;
        x=Integer.parseInt( rectArr[0] );
        y=Integer.parseInt( rectArr[1] );
        x2=Integer.parseInt( rectArr[2] );
        y2=Integer.parseInt( rectArr[3] );

        x2=x2-x;
        y2=y2-y;

        Rect rect1=new Rect( x,y,x2,y2 );
        Log.d(TAG, "findMultiColors: rect="+rect1  );

//        Rect rect1=new Rect( Integer.parseInt(rectArr[0]),Integer.parseInt(rectArr[1]),
//                Integer.parseInt( rectArr[2]),Integer.parseInt(rectArr[3]) );
        //Log.d(TAG,"rect="+rect1.toString() );
        //images.initOpenCvIfNeeded();

        for (int i=0;i<points.length;i++){
            Log.d(TAG, "findMultiColors: point,i,v="+i+","+points[i] );
        }

        Log.d(TAG, "findMultiColors:"
        +"\nimageWrapperMain"+imageWrapperMain.toString()
        +"\nfirstColor="+firstColor
        +"\nthreshold="+threshold
        +"\nrect="+rect1);
        Point point= colorFinder.findMultiColors( imageWrapperMain,firstColor,threshold, rect1,points );
        return point;

    }

    public ImageWrapper getImageWrapperMain() {
        if (imageStat==imageStat_release){
            imageWrapperMain=images.captureScreen();
        }
        return imageWrapperMain;
    }

    public void ks() throws InterruptedException {
        waitPermission2();
        if (imageWrapperMain!=null)
            imageWrapperMain.recycle();
        imageWrapperMain=images.captureScreen();
        imageStat=imageStat_keep;
    }

    public void relase() throws InterruptedException {
        waitPermission2();
        imageStat=imageStat_release;
    }

}
