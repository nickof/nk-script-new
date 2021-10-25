package org.autojs.autojs.nkScript.scriptCollection

import android.os.Build
import com.stardust.automator.UiObject
import org.autojs.autojs.nkScript.Run
import org.autojs.autojs.nkScript.functionInterface.InterScript


class test: Run(),InterScript {

    override var TAG="script-test"
    override fun script() {

        appUtils.uninstall( "jp.naver.line.android"  )
        Thread.sleep( 5000 )

        while (true){
            toast(TAG);
            var uiObject= node.swipeNodeDownToTop( nod.lineListView )
            Thread.sleep(1000);
            node.swipeNodeTopToDown( nod.lineListView )
            Thread.sleep(1000);
//            node.swipeNodeleftToRight( nod.andDesk );
//            Thread.sleep(1000);
//            node.swipeNodeRightToLeft( nod.andDesk );
//            Thread.sleep(1000);
           // appUtils.launchPackage  ( "jp.naver.line.android" )

        }
    }

}