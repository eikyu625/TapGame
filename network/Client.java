package com.mapleworld.game.tapgame.network;


import com.mapleworld.game.tapgame.gui.CreateMPRoom;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;



public class Client {


        public void UnknowDoAction(Object obj,Client client) {
            System.out.println("未处理的网络内容：\t"+obj.toString());
        }

        public void HelloDoAction(Object obj,Client client)  throws UnknownHostException, IOException{
            String Message[] = (String []) obj;
            switch(Message[1]){
                case "PW":
                    if(Message[2].equals(CreateMPRoom.getPasswd())){
                        Client.this.sendObject("HL丨");
                    }
                    break;
            }
        }





    public static Client startClient(String serverIp,int port) throws UnknownHostException, IOException {
        /*serverIp = "127.0.0.1";
        port = 65432;*/
        Client client = new Client(serverIp,port);
        client.start();
        return client;
    }



    private String serverIp;
    private int port;
    private Socket socket;
    private boolean running=false;

    private long lastSendTime;





    public Client(String serverIp, int port) {
        this.serverIp=serverIp;
        this.port=port;
    }

    public boolean isRunning(){
        return running;
    }


    public void start() throws UnknownHostException, IOException {
        if(running)return;
        socket = new Socket(serverIp,port);
        System.out.println("本地端口："+socket.getLocalPort());
        lastSendTime=System.currentTimeMillis();
        running=true;
        new Thread(new KeepAliveWatchDog()).start();  //保持长连接的线程，每隔2秒项服务器发一个一个保持连接的心跳消息
        new Thread(new ReceiveWatchDog()).start();    //接受消息的线程，处理消息
        Client.this.sendObject("00");
    }



    public void stop(){

        if(running)running=false;

    }





    public void sendObject(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(obj);
        System.out.println("发送：\t" + obj);
        oos.flush();
    }



    class KeepAliveWatchDog implements Runnable{
        long checkDelay = 10;
        long keepAliveDelay = 2000;
        public void run() {
            while(running){
                if(System.currentTimeMillis()-lastSendTime>keepAliveDelay){
                    try {
                        Client.this.sendObject("90");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Client.this.stop();
                    }
                    lastSendTime = System.currentTimeMillis();
                }else{
                    try {
                        Thread.sleep(checkDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Client.this.stop();
                    }

                }

            }

        }

    }



    class ReceiveWatchDog implements Runnable{
        public void run() {
            while(running){
                try {
                    InputStream in = socket.getInputStream();
                    if(in.available()>0){
                        ObjectInputStream ois = new ObjectInputStream(in);
                        Object obj = ois.readObject();
                        System.out.println("接收：\t"+obj);
                        String [] Message = obj.toString().split("丨");
                        switch (Message[0]){
                            case "HL":
                                //HelloObjectAction h = new HelloObjectAction();
                                //h.doAction(Message,Client.this);
                                break;
                            case "DP":

                                break;

                        }
                        //ObjectAction oa = actionMapping.get(obj.getClass());
                        //oa = oa==null?new DefaultObjectAction():oa;
                        //oa.doAction(obj, Client.this);
                    }else{
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Client.this.stop();
                }
            }
        }
    }
}
