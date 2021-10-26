package org.autojs.autojs.nkScript.scriptCollection.ss

import com.stardust.app.GlobalAppContext
import org.autojs.autojs.nkScript.Run
import org.autojs.autojs.nkScript.functionInterface.InterScript
import org.autojs.autojs.nkScript.interImp.InterMy

class newTest: Run(),InterScript {

    override fun script() {
        var nodeSS=newNode()
        appUtils.stopApp ("jp.naver.line.android")
//        while (true){
//            node.nodeSetText( nodeSS.test2,"www.google.com")
//            Thread.sleep(3000)
//        }
    }

}