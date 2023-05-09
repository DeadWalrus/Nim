package core.server.netutils;

import core.NimNetworkSignals;
import core.SignalParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSessionHandler implements Runnable, NimNetworkSignals{
    private final Socket player1;
    private ObjectInputStream fromPlayer1;
    private ObjectOutputStream toPlayer1;

    private final Socket player2;
    private ObjectInputStream fromPlayer2;
    private ObjectOutputStream toPlayer2;

    private ServerDataTransferService sdts;
    private boolean gameIsRunning;


    public ServerSessionHandler(Socket player1, Socket player2) throws IOException{
        this.player1 = player1;
        this.player2 = player2;
        System.out.println("initializing streams");
        this.fromPlayer1 = new ObjectInputStream(this.player1.getInputStream());
        this.toPlayer1 = new ObjectOutputStream(this.player1.getOutputStream());
        this.fromPlayer2 = new ObjectInputStream(this.player2.getInputStream());
        this.toPlayer2 = new ObjectOutputStream(this.player2.getOutputStream());
        System.out.println("streams initialized");
        System.out.println("Session handler spawned");
    }

    public void run(){
        System.out.println("Creating new transfer service");
        this.sdts = new ServerDataTransferService();
        System.out.println("Sending signals");
        // Send connection probe to get status of players.
        sdts.sendSignal(CONNECTION_PROBE, toPlayer1);
        sdts.sendSignal(CONNECTION_PROBE, toPlayer2);
        int player1Status = sdts.getSignal(fromPlayer1);
        int player2Status = sdts.getSignal(fromPlayer2);
        System.out.println("Player 1 status: " + player1Status + "\nPlayer 2 status: " + player2Status);
        // If the players respond with CONNECTION_ESTABLISHED signal, start the game loop
        if(player1Status == CONNECTION_ESTABLISHED && player2Status == CONNECTION_ESTABLISHED){
            this.gameIsRunning = true;
            sessionLoop();
        }
    }

    private void sessionLoop(){
        while(this.gameIsRunning){

        }
    }

    // End the session
    private void endSession(){
    }

    private class ServerDataTransferService implements SignalParser {

        public ServerDataTransferService(){
            System.out.println("Created new ServerDataTransferService");
        }
        public void sendSignal(int signal, ObjectOutputStream player){
            try{
                player.writeInt(signal);
                player.flush();
            } catch (IOException ex){
                System.out.println("Could not send signal " + signal);
                ex.printStackTrace();
            }
            System.out.println("Signal " + signal + " sent");
        }

         public int getSignal(ObjectInputStream fromPlayer){
            int signal = 0;
            try{
                signal = fromPlayer.readInt();
                return signal;
            } catch(IOException ex){
                ex.printStackTrace();
            }
            return signal;
        }
    }
}

