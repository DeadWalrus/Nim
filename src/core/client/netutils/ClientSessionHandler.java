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
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
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
        this.cdts = new ClientDataTransferService();
        // While the session is running, listen for data from server and act upon it
        while(this.sessionActive){
            System.out.println("Getting signal");
            int signal = cdts.getSignal();
            System.out.println(signal);
            switch(signal){
                case TEST_SIGNAL -> Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Appending text");
                        taOutputArea.appendText(signal + "");
                    }
                });
            }
        }
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
