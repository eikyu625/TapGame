package com.mapleworld.game.tapgame.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {


    public interface ObjectAction{
        Object doAction(Object rev, Server server);
    }



    public static final class DefaultObjectAction implements ObjectAction{
        public Object doAction(Object rev,Server server) {
            System.out.println("处理并返回："+rev);
            return rev;
        }
    }



    public static Server startServer(int port) {
        //int port = 65432;
        Server server = new Server(port);
        server.start();
        return server;
    }



    private int port;
    private volatile boolean running=false;
    private long receiveTimeDelay=3000;
    private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class,ObjectAction>();
    private Thread connWatchDog;



    public Server(int port) {

        this.port = port;

    }


    public boolean isRunning() {
        return running;
    }

    public void start(){

        if(running)return;

        running=true;

        connWatchDog = new Thread(new ConnWatchDog());

        connWatchDog.start();

    }



    @SuppressWarnings("deprecation")

    public void stop(){

        if(running)running=false;

        if(connWatchDog!=null)connWatchDog.stop();

    }



    public void addActionMap(Class<Object> cls,ObjectAction action){

        actionMapping.put(cls, action);

    }



    class ConnWatchDog implements Runnable{
        public void run(){
            try {
                ServerSocket ss = new ServerSocket(port,5);
                while(running){
                    Socket s = ss.accept();
                    new Thread(new SocketAction(s)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Server.this.stop();
            }
        }

    }



    class SocketAction implements Runnable{
        Socket s;
        boolean run=true;
        long lastReceiveTime = System.currentTimeMillis();
        public SocketAction(Socket s) {
            this.s = s;
        }
        public void run() {
            while(running && run){
                if(System.currentTimeMillis()-lastReceiveTime>receiveTimeDelay){
                    overThis();
                }else {
                    try {
                        InputStream in = s.getInputStream();
                        if (in.available() > 0) {
                            ObjectInputStream ois = new ObjectInputStream(in);
                            Object obj = ois.readObject();
                            lastReceiveTime = System.currentTimeMillis();
                            System.out.println("接收：\t" + obj);
                            ObjectAction oa = actionMapping.get(obj.getClass());
                            oa = oa == null ? new DefaultObjectAction() : oa;
                            Object out = oa.doAction(obj, Server.this);
                            if (out != null) {
                                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                                oos.writeObject(out);
                                oos.flush();
                            }
                        } else {
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        overThis();

                    }
                }
            }
        }

        private void overThis() {
            if(run)run=false;
            if(s!=null){
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("关闭："+s.getRemoteSocketAddress());
        }
    }
}