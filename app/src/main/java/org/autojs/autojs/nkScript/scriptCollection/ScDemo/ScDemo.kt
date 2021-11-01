package org.autojs.autojs.nkScript.scriptCollection.ScDemo

import android.util.Log
import org.autojs.autojs.nkScript.Run
import org.autojs.autojs.nkScript.functionInterface.InterScript

class ScDemo:Run(),InterScript {
    override var TAG="nkScript-ScDemo"
    var nodeData=NodeData("a");


    override fun script() {

//        var ret=node.fnode( nodeData.a )
//        Log.d(TAG, "script: "+ret.text() )

        requestPermission()
        appUtils.deleteAlbum()
        imagesImp.captureSaveImageToGallery()

        var sms=appUtils.getSms( "" )
        Log.d(TAG, "script: sms="+sms )
        appUtils.stopApp( "com.quxianzhuan.wap" )

        while (true){

            Log.d(TAG, "script: run.")
            toast("脚本运行中")
            Thread.sleep(1000)

        }


    }

}