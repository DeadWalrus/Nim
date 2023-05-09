package core.server.netutils;

import core.NimNetworkSignals;

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
        sdts.sendSignal(TEST_SIGNAL, toPlayer1);
        sdts.sendSignal(TEST_SIGNAL, toPlayer2);
    }

    private class ServerDataTransferService{

        public ServerDataTransferService(){
            System.out.println("Created new ServerDataTransferService");
        }
        protected void sendSignal(int signal, ObjectOutputStream player){
            try{
                player.writeInt(signal);
                player.flush();
            } catch (IOException ex){
                System.out.println("Could not send signal " + signal);
                ex.printStackTrace();
            }
            System.out.println("Signal " + signal + " sent");
        }
    }
}

