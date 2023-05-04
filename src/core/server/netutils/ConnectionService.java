package core.server.netutils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionService implements Runnable{
    ServerSocket serverSocket;
    int port;
    boolean running;

    public ConnectionService(ServerSocket serverSocket, int port){
        this.serverSocket = serverSocket;
        this.port = port;
    }

    public void run(){

        this.running = true;

        try{
            awaitConnection();
        } catch(IOException ex){
            this.running = false;
            ex.printStackTrace();
        }
    }
    private void awaitConnection() throws IOException {
        // Spawn SessionHandler when two clients connect
        ExecutorService es = Executors.newCachedThreadPool();
        while(this.running){
            Socket player1Socket = serverSocket.accept();
            System.out.println("Client connected with address " + player1Socket.getInetAddress());
            Socket player2Socket = serverSocket.accept();
            System.out.println("Client connected with address " + player2Socket.getInetAddress());
            if(player1Socket.isConnected() && player2Socket.isConnected()){
                SessionHandler session = new SessionHandler(player1Socket, player2Socket);
                es.execute(session);
            }
        }
    }
}
