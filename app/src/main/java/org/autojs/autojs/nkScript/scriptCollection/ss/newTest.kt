package org.autojs.autojs.nkScript.scriptCollection.ss

import android.util.Log
import com.stardust.app.GlobalAppContext
import org.autojs.autojs.nkScript.Run
import org.autojs.autojs.nkScript.functionInterface.InterScript
import org.autojs.autojs.nkScript.interImp.InterMy
import kotlin.math.log

class newTest: Run(),InterScript {

    override fun script() {
        var nodeSS=newNode()

        //appUtils.stopApp ("jp.naver.line.android")
        //imagesImp.requestWaitPermission();
        requestPermission()
       // imagesImp.requestWaitPermission()
        appUtils.deleteAlbum();
        imagesImp.captureSaveImageToGallery()
        Log.d(TAG, "script: sms="+appUtils.getSms("512288")   )

        while ( true ){
                appUtils.toast("script-run.");
                Thread.sleep(3000)
        }

    }

}