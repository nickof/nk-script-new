package org.autojs.autojs.nkScript.interImp;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.core.accessibility.SimpleActionAutomator;
import com.stardust.autojs.core.accessibility.UiSelector;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;
import com.stardust.automator.UiObjectCollection;

import org.opencv.core.Point;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UiSelectorImp {

    private static final String TAG ="nkScript-"+UiSelectorImp.class.getSimpleName() ;
    public ScriptRuntime scriptRuntime;
    public AccessibilityBridge accessibilityBridge;
    public SimpleActionAutomator simpleActionAutomator;
    public ImagesImp imagesImp;

    public UiSelectorImp( ImagesImp imagesImp )   {

        scriptRuntime=EnvScriptRuntime.getScriptRuntime();
        accessibilityBridge=scriptRuntime.accessibilityBridge;
        simpleActionAutomator=scriptRuntime.automator;
        this.imagesImp=imagesImp;

    }


    public UiObject clickXy(Map<String,String> nodeCondition ) throws Exception {
        UiObject uiObject=fnode(nodeCondition);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if ( uiObject!=null ){

                Rect rect= uiObject.bounds();
                Log.d(TAG, "clickXy: rect.bounds="+uiObject.bounds()+",text="+uiObject.text()  );
                //Log.d(TAG, "clickXy: rect.bounds="+uiObject.boundsInParent() +",text="+uiObject.text()  );
                rect.set( rect.left+1,rect.top+1,rect.right-1,rect.bottom-1 );
                Log.d(TAG, "clickXy: rect.right="+rect.right );
                int x=r_( rect.left,rect.right );
                int y=r_(rect.top,rect.bottom);
                Log.d(TAG, "clickXy: "+x+","+y+","+getUbjectDes(uiObject) );

               if(simpleActionAutomator.click( x,y ) )
                   Log.d(TAG, "clickXy: true");
               return uiObject;

            }
        }else
        {
            throw new Exception("clickXy-api24-needed");
        }
        return null;
    }

    public UiObject clickXy(Map<String,String>[] nodeConditionArray ) throws Exception {

        UiObject uiObject=fnode( nodeConditionArray );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if ( uiObject!=null ){

                Rect rect= uiObject.bounds();
                rect.set( rect.left+1,rect.top+1,rect.right-1,rect.bottom-1 );
                int x=r_( rect.left,rect.right );
                int y=r_(rect.top,rect.bottom);
                //Log.d(TAG, "clickXy: rect="+rect.toString()+"--x,y="+x+","+y  );
                Log.d(TAG, "clickXy: "+x+","+y+","+getUbjectDes(uiObject) );
                simpleActionAutomator.click( x,y );
                return uiObject;

            }
        }else
        {
            throw new Exception("clickXy-api24-needed");
        }
        return null;
    }

/*    public UiObject  waitTrueGroup(){

    }*/

    public UiObject clickXy( UiObject uiObject ) throws Exception {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if ( uiObject!=null ){

                Rect rect= uiObject.bounds();
                rect.set( rect.left+1,rect.top+1,rect.right-1,rect.bottom-1 );
                int x=r_( rect.left,rect.right );
                int y=r_(rect.top,rect.bottom);

                //Log.d(TAG, "clickXy: rect="+rect.toString()+"--x,y="+x+","+y  );
                Log.d(TAG, "clickXy: "+x+","+y+","+getUbjectDes(uiObject) );

                if(simpleActionAutomator.click( x,y ) )
                    Log.d(TAG, "clickXy: true");

            }
        }else
        {
            throw new Exception("clickXy-api24-needed");
        }
        return uiObject;

    }

    public UiObject clkNodeWaitCorlor(Map<String,String> nodeCondition,long timeout,long diff ) throws Exception {
        //imagesImp.requestWaitPermission();
        String col=nodeCondition.get("c"),rect;
        if ( col==null ){
            return null;
        }

        UiObject uiObject = fnode( nodeCondition );
        if ( uiObject!=null ){

            Log.d(TAG, "clkNodeWaitCorlor: uiObject-bounds"+uiObject.bounds()  );
            //Rect(0, 0 - 1080, 2160)
            Rect rect1=uiObject.bounds();
            rect=rect1.left+","+rect1.top+","+rect1.right+","+ rect1.bottom;
           // rect=uiObject.
            Point p= imagesImp.findMultiColors( col,rect,0 );

            if (p!=null){

                Log.d(TAG, "clkNodeWaitCorlor: not-clicked,but color-true");
                return uiObject;

            }else{

                long stTime=System.currentTimeMillis();
                clickXy( uiObject );

                while ( true ){
                    p= imagesImp.findMultiColors( col,rect,0  );
                    if ( p!=null ){
                        Log.d(TAG, "clkNodeWaitCorlor: clicked-color-wait-true");
                        return uiObject;
                    }

                    Thread.sleep(diff);
                    if ( System.currentTimeMillis()-stTime>timeout ){
                        Log.d(TAG, "clkNodeWaitCorlor: clicked-colorFailed");
                        return null;
                    }

                }
            }

        }

        return null;

    }

    public String getUbjectDes(UiObject uiObject){
        return uiObject.getText()+","+uiObject.bounds();
    }

    public int r_(int min, int max) {
        int randomNumber;
        randomNumber = (int) (((max - min + 1) * Math.random() + min));
        return randomNumber;
    }

//    public boolean waitFalseEx( Object nodeCondition ) throws Exception {
//        if (nodeCondition.getClass().isArray())
//            return waitFalse((Map<String, String>[]) nodeCondition,3000,50 );
//        else
//            return waitFalse((Map<String, String>) nodeCondition,3000,50);
//    }

    public boolean waitFalseEx( Object nodeCodition ) throws Exception {
        if ( nodeCodition.getClass().isArray() )
            return waitFalseEx((Map<String, String>[]) nodeCodition,3000,50 );
        else
            return waitFalseEx((Map<String, String>) nodeCodition,3000,50 );
    }


    public boolean waitFalseEx( Map<String,String> nodeCodition,long timeout,long diff ) throws Exception {
        UiObject uiObject =clickXy(nodeCodition);
        if (uiObject!=null){
            return waitFalse( nodeCodition,timeout,diff );
        }else
            return true;
    }

    public boolean waitFalseEx( Map<String,String>[] nodeCodition,long timeout,long diff ) throws Exception {
        UiObject uiObject =clickXy(nodeCodition);
        if (uiObject!=null){
            return waitFalse( nodeCodition,timeout,diff );
        }else
            return true;
    }

    public boolean waitFalse( Object nodeCondition ) throws Exception {
        if (nodeCondition.getClass().isArray())
            return waitFalse((Map<String, String>[]) nodeCondition,3000,50 );
        else
            return waitFalse((Map<String, String>) nodeCondition,3000,50);
    }

    public boolean waitFalse(Map<String,String> nodeCodition, long timeout,long diff) throws Exception {

        long stTime=System.currentTimeMillis();
        UiObject uiObject;
        while (true){

            uiObject=fnode( nodeCodition );
            if ( uiObject==null ){
                Log.d(TAG, "waitFalse: map-true");
                return true;
            }

            try {
                Thread.sleep( diff );
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new Exception("InterruptedException");
            }

            if (System.currentTimeMillis()-stTime>timeout){
                return false;
            }
        }

    }

    public boolean waitFalse(Map<String,String>[] nodeCodition, long timeout,long diff) throws Exception {
        long stTime=System.currentTimeMillis();
        UiObject uiObject;

        while (true){

            uiObject=fnode( nodeCodition );
            if ( uiObject==null ){
                Log.d(TAG, "waitFalse: arr-true");
                return true;
            }

            try {
                Thread.sleep( diff );
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new Exception("InterruptedException");
            }

            if (System.currentTimeMillis()-stTime>timeout){
                return false;
            }
        }
    }

    public UiSelector newUiSelector(){
        return new UiSelector( accessibilityBridge );
    }

    public UiSelector set(Map<String,String> nodeCondition  ){

        UiSelector uiSelector=new UiSelector( accessibilityBridge );
        String v;
        for ( String key :
           nodeCondition.keySet() ) {
            v=  nodeCondition.get( key );

            switch(key) {
                case "text":
                    uiSelector.textMatches(v);
                    break; //可选
                case "textC":
                    uiSelector.textContains(v);
                    break; //可选
                case "id":
                    uiSelector.idMatches(v);
                    break; //可选
                case "idC":
                    uiSelector.idContains(v);
                    break; //可选
                case "desc":
                    uiSelector.descMatches(v);
                case "descC":
                    uiSelector.descContains(v);
                    break; //可选
                case "class":
                    uiSelector.classNameMatches(v);
                    break;
            }
        }

        UiGlobalSelector uiGlobalSelector=( UiGlobalSelector)uiSelector;
        Log.d(TAG, "set: match="+uiGlobalSelector );
        return uiSelector;

    }

    public UiObject waitTrue( Map<String,String> nodeConditon,long timeout ){

        UiSelector uiSelector=set( nodeConditon );
        UiObject uiObject;

        if( nodeConditon.containsKey( "idx" ) ) {
            uiObject = uiSelector.findOne ( timeout,Integer.parseInt( nodeConditon.get("idx") ) );
        }else{

            uiObject = uiSelector.findOne ( timeout,0);
        }


        return uiObject;

    }

    public UiObject waitTrue( Map<String,String> [] nodeConditonArr ,long timeout ) throws Exception {
        UiObject uiObject;
        long stTime=System.currentTimeMillis();

        while (true){
            Log.d(TAG, "waitTrue:-arr ");
            uiObject=fnode( nodeConditonArr );
            if (uiObject!=null){
                return uiObject;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "waitNodeTrue: Exception-"+e.getMessage()  );
                throw new Exception( "InterruptedException" );
            }

            if ( System.currentTimeMillis()-stTime>timeout )
                return null;

        }

    }



    public UiObject waitTrueEx(  Map<String,String>  nodeConditon ,  Map<String,String> nodeConditon2, long timeout  ) throws Exception {

        UiSelector uiSelector=set( nodeConditon );
        UiObject uiObject;

        if( nodeConditon.containsKey( "idx" ) ) {
            uiObject = uiSelector.findOne ( timeout,Integer.parseInt( nodeConditon.get("idx") ) );
        }else
            uiObject = uiSelector.findOne ( timeout ) ;

        if ( uiObject!=null ){
            clickXy( uiObject );
            return waitTrue( nodeConditon2,timeout );
        }

        return null;
    }

    public UiObject waitTrueEx( Object nodeCondition,Object nodeCondition2,long timeout  ) throws Exception {

        UiObject uiObject;
        UiObject ret;
        String str;

        Log.d(TAG, "waitTrueEx: Object"  );

        Map<String,String>[] nodeCondition3=null;
        if ( nodeCondition.getClass().isArray()  ) {
             ret= clickXy( (Map<String, String>[]) nodeCondition );
        }else{
            ret=clickXy( ( Map<String,String> ) nodeCondition );
        }

        if( ret!=null ){
            if ( nodeCondition2.getClass().isArray() ){
                ret=waitTrue ( (Map<String, String>[]) nodeCondition2,timeout );
                if (ret!=null)
                 Log.d(TAG, "waitTrueEx: Object="+getUbjectDes( ret ) );
                return ret;
            }else
                ret= waitTrue((Map<String, String>) nodeCondition2,timeout );
                if (ret!=null)
                    Log.d(TAG, "waitTrueEx: Object="+getUbjectDes( ret ) );
                return ret;
        }
        return null;

    }

    public UiObject waitTrueEx(  UiObject uiObject ,  Map<String,String> nodeConditon2, long timeout  ) throws Exception {

        if ( uiObject!=null ){
            clickXy( uiObject );
            return waitTrue( nodeConditon2,timeout );
        }
        return null;
    }



    public  UiObjectCollection  fnodeAll (Map<String,String> nodeCondition){

        UiSelector uiSelector=set( nodeCondition );
        UiObject uiObject;
        UiObjectCollection uiObjectCollection=uiSelector.find() ;

        for (int i=0;i<uiObjectCollection.size();i++){
            Log.d(TAG, "fnodeAll: i="+i+","+uiObjectCollection.get(i).bounds()  );
        }
        return uiObjectCollection ;

    }

    public boolean boolBound( UiObject uiObject ){
        Rect rect= uiObject.bounds();
        int x,y,x2,y2;

        x=rect.left;
        y=rect.top;
        x2=rect.right;
        y2=rect.bottom;

        if ( x<0||y<0||x2<0||y2<0 )
            return true;
        return false;
    }

//ex: y移动到屏幕2=mapOf<String,String>( "desc" to "移动到屏幕2","pa" to "2","ch" to "0,0,1,1" )
    public UiObject fnode( Map<String,String> nodeConditon ){

        UiSelector uiSelector=set( nodeConditon );
        UiObject uiObject;
        if( nodeConditon.containsKey( "idx" ) ) {
            uiObject = uiSelector.findOnce( Integer.parseInt( nodeConditon.get("idx") ) );
        }else
            uiObject=uiSelector.findOnce();

        if (uiObject==null)
            Log.d(TAG, "fnode: null"+getThreadName() );
        else{

            Boolean flag=boolBound(uiObject);
            if ( flag ){

                UiObjectCollection uiObjectCollection=uiSelector.find();
                int size=uiObjectCollection.size();
                if ( size>1 ) {
                    for (int i=1;i<size;i++ ){
                        uiObject=uiObjectCollection.get(i);
                        if ( !boolBound(uiObject) ){
                            Log.d(TAG, "fnode: idx="+i+","+uiObject.bounds().toString()+getThreadName() );
                            break;
                        }else
                            uiObject=null;
                    }
                }else
                    return null;
            }else
                Log.d(TAG, "fnode: "+uiObject.bounds().toString()+getThreadName() );



            if ( nodeConditon.containsKey("pa") ){
              int  num=Integer.parseInt( nodeConditon.get("pa") );
              uiObject=getParent( uiObject,num );

              if (uiObject==null)
                  return null;
              else
              {
                  if (nodeConditon.containsKey("ch") ) {
                      return getChild( uiObject, nodeConditon.get("ch") );
                  }else
                      return uiObject;
              }
            }else if (nodeConditon.containsKey("ch")){
                return getChild( uiObject, nodeConditon.get("ch") );
            }

        }
        return uiObject;

    }

    public UiObject fnode(Map<String,String> [] array){
        UiObject uiObject =null;
        for ( Map<String,String> ele :
                array  ) {
            uiObject=fnode(ele);
            if( uiObject!=null )
                return uiObject;
        }
        return null;
    }

    public String getThreadName(){
        return "-thr_name="+Thread.currentThread().getName();
    }

    private UiObject getChild( UiObject uiObject,String childLevel ) {

        String[] arr=childLevel.split(",");
        int num;
        for (int i=0;i<arr.length;i++){
             num=Integer.parseInt( arr[i] );
             if ( uiObject.childCount()>num )
                uiObject=uiObject.child(num);
            else
                return null;
        }


        Log.d(TAG, "getChild: "+uiObject.text()+","+uiObject.bounds()  );
        return uiObject;

    }

    public UiObject getParent( UiObject uiObject,int levelNum ){
        for ( int i=0;i<levelNum;i++  ) {
            uiObject=uiObject.parent();
            if ( uiObject==null )
                return null;
        }
        Log.d(TAG, "getParent: "+uiObject.text()+","+uiObject.bounds()  );
        return uiObject;
    }



}
