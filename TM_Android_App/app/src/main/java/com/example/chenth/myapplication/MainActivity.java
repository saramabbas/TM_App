package com.example.chenth.myapplication;

import android.os.Handler;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_send;
    private Button bt_stop;
    private TextView tv_recv;
    private EditText commandInput;

    private String send_buff=null;
    private String recv_buff=null;

    private Handler handler = null;
    private Thread connectionThread;

    public static int numOfNodesClicked;

    Socket socket = null;
    volatile boolean isConnected = false;
    volatile boolean isEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        handler = new Handler();

        //Open a single thread for socket communication
        initConnection();


        send();
    }

    private void initConnection() {
        connectionThread = new Thread(ConnectingRunnable);
        connectionThread.start();
    }

    Runnable ConnectingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                socket = new Socket("192.168.1.156", 7654);

                if (socket != null) {
                    System.out.println("###################");
                    MainActivity.this.recv();
                    MainActivity.this.send();
                    while (!isEnded) {      //Keep receiving
                        MainActivity.this.recv();
                    }
                } else
                    System.out.println("socket is null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected void onDestroy() {

        super.onDestroy();

    }


    private void recv() throws IOException {

        /* Open a single thread to receive messages from the server */
        InputStream inputStream = null;
        inputStream = socket.getInputStream();

        if (inputStream!=null){
            Log.i("receive", "input "+inputStream);
            byte[] buffer = new byte[1024];
            int count = inputStream.read(buffer);//count is the number of bytes transferred
            recv_buff = new String(buffer);//Socket communication transmits byte type, which needs to be converted to String type
            System.out.println(recv_buff);

            if (recv_buff == "This program has stopped"){
                isEnded = true;
            }
            Log.i("receive", "printed "+ recv_buff);

        }
        //Display the received data on the TextView
        if (recv_buff!=null){
            Log.i("receive", "textView");
            handler.post(runnableUi);

        }
    }

    //Cannot refresh the UI in the child thread, it should be that the textView is created by the main thread
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            tv_recv.append("\n"+recv_buff);
        }
    };


    Runnable sendCommandRunnable =  new Runnable() {
            @Override
            public void run() {

                //send_buff = commandInput.getText().toString();
                //send_buff = "time ls -l";

                //Send a message to the server
                System.out.println("------------------------");
                OutputStream outputStream=null;
                try {
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(outputStream!=null){
                    try {
                        outputStream.write(send_buff.getBytes());
                        System.out.println("1111111111111111111111");
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
    };

    private void send() {

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_buff = commandInput.getText().toString();
                new Thread(sendCommandRunnable).start();

            }
        });

        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_buff = "stop";
                new Thread(sendCommandRunnable).start();
            }
        });

    }

    private void initView() {
        numOfNodesClicked = 0;
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        tv_recv = (TextView) findViewById(R.id.tv_recv);
        commandInput = (EditText) findViewById(R.id.commandInput);



        Button node1 = findViewById(R.id.button_node_1);
        Button node2 = findViewById(R.id.button_node_2);
        Button node3 = findViewById(R.id.button_node_3);
        Button node4 = findViewById(R.id.button_node_4);

        node1.setOnClickListener(this);
        node2.setOnClickListener(this);
        node3.setOnClickListener(this);
        node4.setOnClickListener(this);

        tv_recv.setMovementMethod(new ScrollingMovementMethod());
    }


    @Override
    public void onClick(View v) {


        if (
                (v.getId() == R.id.button_node_1)|
                (v.getId() == R.id.button_node_2)|
                (v.getId() == R.id.button_node_3)|
                (v.getId() == R.id.button_node_4)
        ){

            Toast.makeText(this, v.getId() + " clicked", Toast.LENGTH_SHORT).show();
            MainActivity.numOfNodesClicked++;
            Log.i("ButtonClick", v.getId() + " enabled. N = " + MainActivity.numOfNodesClicked);
            v.setEnabled(false);
        }

        if(v.getId() == R.id.bt_stop){
            Log.i("ButtonClick", "Stop button pressed");
        }
    }
 }
