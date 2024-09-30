package core.server.netutils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Monitors for connecting clients and spawns a ServerSessionHandler when two clients connect
 */
public class ConnectionService implements Runnable{
    private final ServerSocket serverSocket; // ServerSocket to retrieve connection info from
    private boolean running; // Is ConnectionService running?

    /**
     * Create a new ConnectionService.
     * @param serverSocket ServerSocket to get connection info from
     */
    public ConnectionService(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /**
     * Set running to true and wait for connections
     */
    public void run(){
        this.running = true;

        try{
            awaitConnection();
        } catch(IOException ex){
            this.running = false;
            ex.printStackTrace();
        }
    }

    /**
     * Wait for connections. When two clients connect, spawn a ServerSessionHandler in new thread to handle connection
     * @throws IOException if clients can't connect, or if spawning the ServerSessionHandler failed
     */
    private void awaitConnection() throws IOException {
        // Spawn SessionHandler when two clients connect
        ExecutorService es = Executors.newCachedThreadPool();
        while(this.running){
            Socket player1Socket = serverSocket.accept();
            System.out.println("Client connected with address " + player1Socket.getInetAddress());
            Socket player2Socket = serverSocket.accept();
            System.out.println("Client connected with address " + player2Socket.getInetAddress());
            if(player1Socket.isConnected() && player2Socket.isConnected()){
                System.out.println("Spawning session handler");
                es.execute(new ServerSessionHandler(player1Socket, player2Socket));
            }
        }
    }
}
