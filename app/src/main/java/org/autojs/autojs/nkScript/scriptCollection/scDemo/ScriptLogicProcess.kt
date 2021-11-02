package org.autojs.autojs.nkScript.scriptCollection.scDemo

import org.autojs.autojs.nkScript.Run
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class ScriptLogicProcess:Run() {

    fun waitTureExGroupProcess( mutableList: MutableList<Any> ):Boolean{
        for (index in 0..mutableList.size ){
            var ret=node.waitTrueEx( mutableList[index],mutableList[index+1],3000 )
            if (ret!=null){
                Thread.sleep( node.r_long( 800,1500 ))
            }
        }
        return true
    }



}

