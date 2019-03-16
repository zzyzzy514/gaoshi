package com.example.zzy.bbc;


import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private final  int HANDLER_MSG_TELL_RECV=0x124;
    private EditText client_host_ip,client_port,client_content;
    private Button client_submit;
    Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("python服务器显示的数据是："+msg.obj);
            builder.create().show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvent();
    }
    private void initEvent(){
        client_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String host=client_host_ip.getText().toString();
                String port=client_port.getText().toString();
                String content=client_content.getText().toString();

                Toast.makeText(MainActivity.this,host+","+port+","+content,Toast.LENGTH_LONG).show();

                startNetThread(host,Integer.parseInt(port),content);
            }
        });
    }
    private void initViews(){
        client_submit=(Button)findViewById(R.id.client_submit);
        client_host_ip=(EditText)findViewById(R.id.client_host_ip);
        client_content=(EditText)findViewById(R.id.client_content);
        client_port=(EditText)findViewById(R.id.client_port);

    }

    //ip地址，端口号，数据
    private void startNetThread(final String host,final int port,final String data){
        new Thread(){
            @Override
            public void run(){

                try {
                    Socket socket = new Socket(host,port);
                    OutputStream outputStream =socket.getOutputStream();
                    outputStream.write(data.getBytes());

                    outputStream.flush();
                    System.out.println("打印客户端的内容："+socket);

                    InputStream is=socket.getInputStream();
                    byte[] bytes=new byte[1024];
                    int n=is.read(bytes);
                    System.out.println(new String(bytes, 0, n));

                    Message msg=handler.obtainMessage(HANDLER_MSG_TELL_RECV,new String(bytes, 0, n));
                    msg.sendToTarget();
                    is.close();
                    socket.close();

                } catch (Exception e) {
                }
            }
        }.start();
    }

}

