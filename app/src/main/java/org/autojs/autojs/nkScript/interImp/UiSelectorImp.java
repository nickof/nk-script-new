package org.autojs.autojs.nkScript.interImp;

import android.util.Log;

import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.core.accessibility.UiSelector;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;

import org.autojs.autojs.autojs.AutoJs;

import java.util.Map;

public class UiSelectorImp {

    private static final String TAG = UiSelectorImp.class.getSimpleName() ;
    public final AutoJs autoJs;
    public final ScriptRuntime scriptRuntime;
    public final AccessibilityBridge accessibilityBridge;

    {
        autoJs= AutoJs.getInstance();
        scriptRuntime=autoJs.getRunTime();
        accessibilityBridge=scriptRuntime.accessibilityBridge;
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


    public UiObject fnode( Map<String,String> nodeConditon ){

        UiSelector uiSelector=set( nodeConditon );
        UiObject uiObject;
        if( nodeConditon.containsKey( "idx" ) ) {
            uiObject = uiSelector.findOnce( Integer.parseInt( nodeConditon.get("idx") ) );
        }else
            uiObject=uiSelector.findOnce();

        if (uiObject==null)
            Log.d(TAG, "fnode: null");
        else{

            Log.d(TAG, "fnode: "+uiObject.bounds().toString() );
            if ( nodeConditon.containsKey("pa") ){
              int  num=Integer.parseInt( nodeConditon.get("pa") );
              uiObject=getParent(uiObject,num);
              if (uiObject==null)
                  return null;
              else
              {
                  if (nodeConditon.containsKey("ch")){
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

    private UiObject getChild( UiObject uiObject,String childLevel ) {
        //1,1,1
        String[] arr=childLevel.split(",");
        int num;
        for (int i=0;i<arr.length;i++){
             num=Integer.parseInt( arr[i] );
             if ( uiObject.childCount()>num )
                uiObject=uiObject.child(num);
            else
                return null;
//             if (uiObject==null)
//                 return null;
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
