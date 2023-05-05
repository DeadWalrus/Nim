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
    DataExchangeManager dem;

    public SessionHandler(Socket player1, Socket player2){
        this.player1 = player1;
        this.player2 = player2;
    }

    public void run(){
        // Read integer signals from client to determine type of data being received
        try{
            initDataStreams();
            toPlayer1.writeBoolean(true);
            toPlayer2.writeBoolean(true);
            toPlayer1.flush();
            toPlayer2.flush();
        } catch(IOException ex){
            System.out.println("ERR: Could not initialize data streams...");
        }
        this.gameRunning = true;
        this.dem = new DataExchangeManager(player1, player2);
        new Thread(dem).start();
        while(dem.isAlive){
            //
        }
    }

    private void endSession() throws IOException{
        this.gameRunning = false;
        dem.isAlive = false;
        this.toPlayer1.close();
        this.fromPlayer1.close();
        this.player1.close();
        this.toPlayer2.close();
        this.fromPlayer2.close();
        this.player2.close();

    }

    private void initDataStreams() throws IOException{
        this.toPlayer1 = new ObjectOutputStream(this.player1.getOutputStream());
        this.fromPlayer1 = new ObjectInputStream(this.player1.getInputStream());
        this.toPlayer2 = new ObjectOutputStream(this.player2.getOutputStream());
        this.fromPlayer2 = new ObjectInputStream(this.player2.getInputStream());
    }

    private class DataExchangeManager implements Runnable{
        Socket player1;
        Socket player2;
        boolean isAlive;
        public DataExchangeManager(Socket player1, Socket player2){
            this.player1 = player1;
            this.player2 = player2;
            this.isAlive = true;
        }
        public void run(){
            // Wait for data from current active player
            while(this.isAlive){
                // Get data from player 1
                try{
                    // Wait for players to make moves, then transmit those moves
                    relayData(fromPlayer1.readObject(), toPlayer2);
                    relayData(fromPlayer2.readObject(), toPlayer1);
                } catch(IOException | ClassNotFoundException ex){
                    System.out.println("ERR: could not exchange data");
                }
            }
        }

        private void relayData(Object data, ObjectOutputStream toPlayer) throws IOException, ClassNotFoundException{
            System.out.println("In relay data");
            if(data.equals(-1)){
                endSession();
            }
            toPlayer.writeObject(data);
            toPlayer.flush();
            System.out.println("Data relayed: " + data);
        }
    }
}