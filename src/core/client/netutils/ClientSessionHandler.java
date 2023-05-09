package core.client.netutils;

import core.NimNetworkSignals;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSessionHandler implements Runnable, NimNetworkSignals{
    private Socket server;
    private final ObjectInputStream fromServer;
    private final ObjectOutputStream toServer;
    private ClientDataTransferService cdts;
    private TextArea taOutputArea;
    private boolean sessionActive;

    public ClientSessionHandler(Socket server, TextArea taOutputArea) throws IOException{
        this.server = server;
        this.taOutputArea = taOutputArea;
        System.out.println("Initializing streams");
        this.fromServer = new ObjectInputStream(this.server.getInputStream());
        System.out.println("fromServer initialized");
        this.toServer = new ObjectOutputStream(this.server.getOutputStream());
        System.out.println("toServer initialized");
        this.sessionActive = true;
    }

    public void run(){
        System.out.println("Creating new transfer service");
        this.cdts = new ClientDataTransferService();
        // While the session is running, listen for data from server and act upon it

    }

    private class ClientDataTransferService{
        public ClientDataTransferService(){
            System.out.println("Created new ClientDataTransferService");
        }
        protected int getSignal(){
            System.out.println("Getting signal");
            int signal = 0;
            try{
                signal = fromServer.readInt();
            } catch(IOException ex){
                ex.printStackTrace();
                System.out.println("Could not get signal from server");
            }
            return signal;
        }
    }
}
