package core.client.netutils;

import core.NimNetworkSignals;
import core.SignalParser;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSessionHandler implements Runnable, NimNetworkSignals{
    //private Socket server;
    private final ObjectInputStream fromServer;
    private final ObjectOutputStream toServer;
    private ClientDataTransferService cdts;
    private TextArea taOutputArea;
    private boolean sessionActive;

    public ClientSessionHandler(Socket server, TextArea taOutputArea) throws IOException{
        this.taOutputArea = taOutputArea;
        System.out.println("Initializing streams");
        this.toServer = new ObjectOutputStream(server.getOutputStream()); // Initialize outputstream first???
        System.out.println("toServer initialized");
        this.fromServer = new ObjectInputStream(server.getInputStream());
        System.out.println("fromServer initialized");

        this.sessionActive = true;
    }

    public void run(){
        System.out.println("Creating new transfer service");
        this.cdts = new ClientDataTransferService();
        // While the session is running, listen for data from server and act upon it
        int signal = this.cdts.getSignal(fromServer);
        this.taOutputArea.appendText(signal + "");
        if(signal == CONNECTION_PROBE){
            this.cdts.sendSignal(CONNECTION_ESTABLISHED, toServer);
            System.out.println("Signal " + CONNECTION_ESTABLISHED + " sent");
            // Start the game loop
        }
    }

    private class ClientDataTransferService implements SignalParser {
        public ClientDataTransferService(){
            System.out.println("Created new ClientDataTransferService");
        }

        public void sendSignal(int signal, ObjectOutputStream toServer){
            System.out.println("Sending signal");
            try{
                toServer.writeInt(signal);
                toServer.flush();
            } catch(IOException ex){
                ex.printStackTrace();
                System.out.println("Could not send signal to server");
            }
        }

        public int getSignal(ObjectInputStream fromServer){
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
