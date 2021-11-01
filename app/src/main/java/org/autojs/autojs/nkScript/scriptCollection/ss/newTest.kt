package org.autojs.autojs.nkScript.scriptCollection.ss

import android.util.Log
import com.stardust.app.GlobalAppContext
import org.autojs.autojs.nkScript.Run
import org.autojs.autojs.nkScript.functionInterface.InterScript
import org.autojs.autojs.nkScript.interImp.InterMy
import org.autojs.autojs.nkScript.interImp.Okhttp
import org.autojs.autojs.nkScript.interImp.SetNode
import kotlin.math.log


class newTest: Run(),InterScript {

    var flag=false;
    override fun script() {

        var nodeSS=newNode()
        requestPermission()

       // var a=appUtils.writeConfig("aa",100 );
       // var b=appUtils.readConfig("aa",300);
        var ret=Okhttp.get("https://blog.ruige.fun/type");
        Log.d(TAG, "script: httpResponse="+ret )

        while ( true ){

            appUtils.launchPackage("com.quxianzhuan.wap")
            appUtils.toast("script-run.");

            var uiObject= node.clickXy( nodeSS.ff放大镜 )
            if (uiObject!=null){
                Log.d(TAG, "script: "+uiObject.bounds()  )
            }else{
                Log.d(TAG, "script: 未找到")
            }
                Thread.sleep(3000)

        }

        var nod= SetNode()
        node.clickXy( nod.line  )

    }

    fun step1(): newTest {
        return this
    }

    fun step2(): newTest {
        flag=false;
        return this
    }

}