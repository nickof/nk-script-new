package org.autojs.autojs.nkScript.interImp.webSocket;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.MainActivity;
import org.autojs.autojs.R;
import org.autojs.autojs.nkScript.Run;
import org.autojs.autojs.nkScript.Service.ScriptService;
import org.autojs.autojs.nkScript.ThreadpoolScriptManager;
import org.autojs.autojs.nkScript.interImp.InterMy;
import org.autojs.autojs.nkScript.model.ShareDataScript;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketImp extends OkHttpClient   {

        public boolean flagConnection=false;
        private static OkHttpClient mClient;
        public final int maxTime=10;
        public int reconTime=0;

        String ip;
        String ettext;
        WebSocket websocket;
        TextView textViewConnection;
        TextView  textViewLogReceive;
        MainActivity application;
        EditText editTextIp;
        Button buttonConnect;
        EditText editTextText;
        Button buttonSend;
        public final String urlSuHao="ws://192.168.1.221/ws";

    private String TAG="nk-WebSocketImp";
    public Request request;
    private Request requestSuhaho;

    public WebSocketImp(MainActivity application) {

       editTextIp=  (EditText)application.findViewById(R.id.edit_ip );
       ip=editTextIp.getText().toString();
       editTextText= application.findViewById( R.id.et_send );
       ettext=editTextText.getText().toString();

       this.application=application;
       textViewConnection=(TextView) application.findViewById(R.id.tv_connectionLog);
       textViewLogReceive=(TextView) application.findViewById(R.id.tv_receiveLog );
       buttonConnect=(Button) application.findViewById(R.id.bt_connection);
       buttonSend=(Button) application.findViewById(R.id.bt_send);

        getOkHttpClient();
        testRunSocket();

    }

    public WebSocketImp() {

        editTextIp=  (EditText)application.findViewById(R.id.edit_ip );
        ip=editTextIp.getText().toString();
        editTextText= application.findViewById( R.id.et_send );
        ettext=editTextText.getText().toString();

        this.application=application;
        textViewConnection=(TextView) application.findViewById( R.id.tv_connectionLog );
        textViewLogReceive=(TextView) application.findViewById( R.id.tv_receiveLog );
        buttonConnect=(Button) application.findViewById( R.id.bt_connection );
        buttonSend=(Button) application.findViewById( R.id.bt_send );

        getOkHttpClient();
        testRunSocket();

    }

    public  OkHttpClient getOkHttpClient(){

        if ( mClient==null ){
            mClient = new OkHttpClient.Builder()
                    .readTimeout(10, TimeUnit.SECONDS )//设置读取超时时间
                    .writeTimeout(10, TimeUnit.SECONDS )//设置写的超时时间
                    .connectTimeout(10, TimeUnit.SECONDS )//设置连接超时时间
                    .pingInterval(30, TimeUnit.SECONDS )
                    .build();
        }
        return mClient;
    }

        public void testRunSocket( ){
            //连接地址
            //构建一个连接请求对象
            buttonConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        socketConect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //throw new InterruptedException();
                    }
                }
            });

            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    editTextText =application.findViewById(R.id.et_send);
                    boolean isSendSuccess = websocket.send( editTextText.getText().toString() );
                    Log.d(TAG, "onClick: editTextText="+editTextText);
                        application.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSendSuccess){
                                textViewConnection.setText( "发送成功" );
                                }else {
                                    textViewConnection.setText("发送失败");
                                };
                            }
                        });
                }

            });
        }

        public Request getRequest(Request request, String url ){
            if (request==null){
                request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();
            }
            return request;
        }

    public void socketConect2() throws InterruptedException {

        getOkHttpClient();
        String url = urlSuHao ;
        request = getRequest(request,url);
        //Request request = new Request.Builder().url( url ).build();
        Log.d(TAG, "socketConect: url="+url );
        Log.d(TAG, "socketConect: request="+ request);
        Log.d(TAG, "socketConect: mClient="+mClient.toString() );

        //开始连接
        websocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                flagConnection=true;
                reconTime=0;
                application.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewConnection.setText("连接成功");
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

                Log.d(TAG, "onMessage: receive"+text );
                if (text.indexOf("2")>-1 ){
                    //stopScriptService();
                    ThreadpoolScriptManager.shutDownAll();
                }

           /*     application.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: text="+text );
                        if ( text.indexOf("1")>-1 ){
                            startScriptService();
                            Log.d(TAG, "run: script-run-condition-on");

                        }//else if (text.equals("2"))
                        textViewLogReceive.setText( text );
                    }
                });*/
                //收到消息...（一般是这里处理json）

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.d(TAG, "onMessage: bytes");
                //收到消息...（一般很少这种消息）
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed: "+reason );
                application.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewConnection.setText( "连接关闭-"+reason  );
                    }
                });

            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure( webSocket, throwable, response );

                //webSocket.close( 3000,"connect-fail-so-closed" );
                if (response==null){
                    Log.d(TAG, "onFailure: response-null" );
                }else
                    Log.d(TAG, "onFailure: response="+response.toString() );

                application.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        textViewConnection.setText( "连接失败"+throwable.toString() );
                    }
                });

                mClient.dispatcher().executorService().shutdown();
//                    try {
//                        mClient.dispatcher().executorService().awaitTermination(2*10, TimeUnit.MILLISECONDS );
//                    } catch (InterruptedException e) {
//                        Log.d(TAG, "onFailure: awaitTermination");
//                        e.printStackTrace();
//                    }

                mClient=null;
                if (reconTime<maxTime){

                    reconTime++;
                    Log.d(TAG, "onFailure: 重连"+reconTime+",wait-5" );
                    try {
                        Thread.sleep(5000);
                        socketConect();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                }else{
                    application.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            textViewConnection.setText( "连续超过"+maxTime+"次失败");
                        }
                    });
                    Log.d(TAG, "onFailure: 多次重连失败");
                    webSocket.close(1000,"连接失败超过"+reconTime );
                }

            }

        });

   /*         for (int i=0;i<10;i++){
                if (flagConnection){
                    Log.d(TAG, "socketConect: suc.");
                    break;
                }
                Thread.sleep(500);
            }
*/
    }


    public void socketConect() throws InterruptedException {

            getOkHttpClient();
            String url = "ws://"+editTextIp.getText().toString() ;
            request = getRequest(request,url);
            //Request request = new Request.Builder().url( url ).build();
            Log.d(TAG, "socketConect: url="+url );
            Log.d(TAG, "socketConect: request="+ request);
            Log.d(TAG, "socketConect: mClient="+mClient.toString() );

            //开始连接
            websocket = mClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    flagConnection=true;
                    reconTime=0;
                    application.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewConnection.setText("连接成功");
                        }
                    });
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);

                    Log.d(TAG, "onMessage: receive"+text );
                    ShareDataScript.serverJson=text;
                    
                    if (text.indexOf("2")>-1 ){
                        //stopScriptService();
                        ThreadpoolScriptManager.shutDownAll();
                    }

                    application.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: text="+text );
                            if ( text.indexOf("1")>-1 ){
                                startScriptService();
                                Log.d(TAG, "run: script-run-condition-on");

                            }//else if (text.equals("2"))
                            textViewLogReceive.setText( text );
                        }
                    });
                    //收到消息...（一般是这里处理json）

                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                    Log.d(TAG, "onMessage: bytes");
                    //收到消息...（一般很少这种消息）
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    Log.d(TAG, "onClosed: "+reason );
                    application.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewConnection.setText( "连接关闭-"+reason  );
                        }
                    });

                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                    super.onFailure( webSocket, throwable, response );

                    //webSocket.close( 3000,"connect-fail-so-closed" );
                    if (response==null){
                        Log.d(TAG, "onFailure: response-null" );
                    }else
                        Log.d(TAG, "onFailure: response="+response.toString() );

                    application.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            textViewConnection.setText( "连接失败"+throwable.toString() );
                        }
                    });

                    mClient.dispatcher().executorService().shutdown();
//                    try {
//                        mClient.dispatcher().executorService().awaitTermination(2*10, TimeUnit.MILLISECONDS );
//                    } catch (InterruptedException e) {
//                        Log.d(TAG, "onFailure: awaitTermination");
//                        e.printStackTrace();
//                    }

                    mClient=null;
                    if (reconTime<maxTime){
                        
                        reconTime++;
                        Log.d(TAG, "onFailure: 重连"+reconTime+",wait-5" );
                        try {
                            Thread.sleep(5000);
                            socketConect();
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                    }else{
                            application.runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    textViewConnection.setText( "连续超过"+maxTime+"次失败");
                                }
                            });
                            Log.d(TAG, "onFailure: 多次重连失败");
                            webSocket.close(1000,"连接失败超过"+reconTime );
                            stopScriptService();
                    }

                }

            });


        }


        public void stopScriptService(){
            synchronized (ShareDataScript.ScriptServiceLock){
                Application application= (Application) GlobalAppContext.get();
                if (application==null){
                    Log.d(TAG, "onMessage: application is null");
                }
                application.stopService( new Intent( application, ScriptService.class ));
            }
        }

        public void startScriptService(){

            synchronized (ShareDataScript.ScriptServiceLock){
                Application application= (Application) GlobalAppContext.get();
                if (application==null)
                    Log.d(TAG, "onMessage: application is null");
                //intent=new Intent( application, ScriptService.class );
                Intent intent=new Intent( application, ScriptService.class );

                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                    application.startForegroundService( intent );
                } else {
                    application.startService( intent );
                }

            }
        }


}
