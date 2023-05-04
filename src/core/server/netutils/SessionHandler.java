package core.server.netutils;

import java.io.*;
import java.net.Socket;

public class SessionHandler implements Runnable{
    private final Socket player1;
    private final Socket player2;
    private ObjectOutputStream toPlayer1;
    private ObjectInputStream fromPlayer1;
    private ObjectOutputStream toPlayer2;
    private ObjectInputStream fromPlayer2;
    private boolean gameRunning;

    public SessionHandler(Socket player1, Socket player2){
        this.player1 = player1;
        this.player2 = player2;
    }

    public void run(){
        // Read integer signals from client to determine type of data being received
        try{
            initDataStreams();
            this.toPlayer1.writeBoolean(true);
            this.toPlayer1.flush();
            this.toPlayer2.writeBoolean(true);
            this.toPlayer2.flush();
        } catch(IOException ex){
            System.out.println("ERR: Could not initialize data streams...");
        }
        this.gameRunning = true;
        ConnectionMonitor cm = new ConnectionMonitor(player1, player2);
        new Thread(cm).start();
        while(this.gameRunning){
            if(player1.isClosed() || player2.isClosed()){
                System.out.println("player disconnected");
                return;
            }
            try{
                // Determine connection of clients

                if(fromPlayer1.available() != 0 && fromPlayer2.available() != 0){

                    System.out.println("Client data available");
                    System.out.println(this.fromPlayer1.readBoolean());
                    System.out.println(this.fromPlayer2.readBoolean());
                }
            }catch(IOException ex){
                System.out.println("Could not get data from client");
                ex.printStackTrace();
                this.gameRunning = false;
            }
        }
    }

    private void initDataStreams() throws IOException{
        this.toPlayer1 = new ObjectOutputStream(this.player1.getOutputStream());
        this.fromPlayer1 = new ObjectInputStream(this.player1.getInputStream());
        this.toPlayer2 = new ObjectOutputStream(this.player2.getOutputStream());
        this.fromPlayer2 = new ObjectInputStream(this.player2.getInputStream());
    }

    private class ConnectionMonitor implements Runnable{
        Socket player1;
        Socket player2;
        public ConnectionMonitor(Socket player1, Socket player2){
            this.player1 = player1;
            this.player2 = player2;
        }
        public void run(){
            // Send probe every 500 ms to determine if alive
            long startTime = System.currentTimeMillis();
            while(!player1.isClosed() && !player2.isClosed()){

                if((System.currentTimeMillis() - startTime) % 500 == 0){
                    System.out.println("Status probes sent");
                    try{
                        sendProbes();
                    } catch (IOException ex){
                        System.out.println("Could not determine connection state. Closing connections");
                    }
                }
            }
        }

        private void sendProbes() throws IOException{
            toPlayer1.writeInt(100);
            toPlayer2.writeInt(100);
            System.out.println("Probes sent");
            while(fromPlayer1.available() == 0 || fromPlayer2.available() == 0){
                ;;
            }

        }
    }
}


