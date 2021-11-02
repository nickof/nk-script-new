package org.autojs.autojs.nkScript.scriptCollection.scDemo

import android.util.Log
import org.autojs.autojs.nkScript.Run
import org.autojs.autojs.nkScript.functionInterface.InterScript

class ScDemo:Run(),InterScript {

    override var TAG="ScDemo"
    var n=scriptData();

    override fun script() {

        requestPermission()
        //appUtils.openUrl("http://139.196.75.235/login-view")
        //node.clickXy( n )
        while ( true ){
            var nn=node.fnode( n.tt )
            if (nn!=null){
                Log.d(TAG, "script-find: ="+node.getUbjectDes( nn ) )
            }else
                Log.d(TAG, "script: fnode-null")
            toast( "script-run" )
            Thread.sleep(2000)
        }

    }

}