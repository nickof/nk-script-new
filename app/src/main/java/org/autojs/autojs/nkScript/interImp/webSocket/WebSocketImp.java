package org.autojs.autojs.nkScript.interImp.webSocket;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        public final String urlSuHao="ws://192.168.1.221:8080/ws";

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

    private void writeFile(Response response,String fileName) {

        InputStream is = null;
        FileOutputStream fos = null;
        is = response.body().byteStream();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File( path, fileName );

        try {

            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            //??????????????????????????????
            long fileSize = response.body().contentLength();
            long sum = 0;
            int porSize = 0;

            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes);
                sum += len;
                porSize = (int) ( (sum * 1.0f / fileSize ) * 100 );
  /*              Message message = handler.obtainMessage(1);
                message.arg1 = porSize;
                handler.sendMessage(message);*/

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("myTag", "????????????");

    }

    public  OkHttpClient getOkHttpClient(){

        if ( mClient==null ){
            mClient = new OkHttpClient.Builder()
                    .readTimeout(10, TimeUnit.SECONDS )//????????????????????????
                    .writeTimeout(10, TimeUnit.SECONDS )//????????????????????????
                    .connectTimeout(10, TimeUnit.SECONDS )//????????????????????????
                    .pingInterval(30, TimeUnit.SECONDS )
                    .build();
        }
        return mClient;
    }

        public void testRunSocket( ){
            //????????????
            //??????????????????????????????
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
                                textViewConnection.setText( "????????????" );
                                }else {
                                    textViewConnection.setText("????????????");
                                };
                            }
                        });
                }

            });
        }

        public Request getRequest(Request request, String url ){
            if (request==null){
                request = new Request.Builder()
                        .addHeader("test","111")
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

        //????????????
        websocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                flagConnection=true;
                reconTime=0;
                application.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewConnection.setText("????????????"+response.toString() );
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
                //????????????...????????????????????????json???

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.d(TAG, "onMessage: bytes");
                //????????????...??????????????????????????????
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed: "+reason );
                application.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewConnection.setText( "????????????-"+reason  );
                    }
                });

            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure( webSocket, throwable, response );

                //webSocket.close( 3000,"connect-fail-so-closed" );
                if (response==null){
                    Log.d(TAG, "onFailure: response-null" );
                }else{
                    Log.d(TAG, "onFailure: response="+response.toString() );
                   // Log.d(TAG, "onFailure: response body="+response.he );
                }


                application.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        textViewConnection.setText( "????????????"+throwable.toString() );
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
                    Log.d(TAG, "onFailure: ??????"+reconTime+",wait-5" );
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
                            textViewConnection.setText( "????????????"+maxTime+"?????????");
                        }
                    });
                    GlobalAppContext.toast("??????????????????");
                    Log.d(TAG, "onFailure: ??????????????????");
                    webSocket.close(1000,"??????????????????"+reconTime );
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
            url=urlSuHao;
            request = getRequest(request,url);
            //Request request = new Request.Builder().url( url ).build();
            Log.d(TAG, "socketConect: url="+url );
            Log.d(TAG, "socketConect: request="+ request);
            Log.d(TAG, "socketConect: mClient="+mClient.toString() );

            //????????????
            websocket = mClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    flagConnection=true;
                    reconTime=0;
                    application.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewConnection.setText("????????????");
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
                    //????????????...????????????????????????json???

                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                    Log.d(TAG, "onMessage: bytes");
                    //????????????...??????????????????????????????
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    Log.d(TAG, "onClosed: "+reason );
                    application.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewConnection.setText( "????????????-"+reason  );
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
                            textViewConnection.setText( "????????????"+throwable.toString() );
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
                        Log.d(TAG, "onFailure: ??????"+reconTime+",wait-5" );
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
                                    textViewConnection.setText( "????????????"+maxTime+"?????????");
                                }
                            });
                            Log.d(TAG, "onFailure: ??????????????????");
                            webSocket.close(1000,"??????????????????"+reconTime );
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
