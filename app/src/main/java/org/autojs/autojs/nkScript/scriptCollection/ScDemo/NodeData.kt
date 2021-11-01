package org.autojs.autojs.nkScript.scriptCollection.ScDemo

class NodeData {

    lateinit var type:String;
    var a={ mapOf<String,String>( "text" to "line" ) }

    constructor( type:String ){
        this.type=type;
    }


}