package core.client.netutils;

import core.NimNetworkSignals;
import core.SignalParser;
import core.client.gameelements.NimBoard;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

public class ClientSessionHandler implements Runnable, NimNetworkSignals{
    //private Socket server;
    private final ObjectInputStream fromServer;
    private final ObjectOutputStream toServer;
    private ClientDataTransferService cdts;
    private TextArea taOutputArea;
    private boolean sessionActive;
    private boolean isTurn;
    private NimBoard board;

    public ClientSessionHandler(Socket server, TextArea taOutputArea) throws IOException{
        this.taOutputArea = taOutputArea;
        System.out.println("Initializing streams");
        this.toServer = new ObjectOutputStream(server.getOutputStream()); // Initialize outputstream first???
        System.out.println("toServer initialized");
        this.fromServer = new ObjectInputStream(server.getInputStream());
        System.out.println("fromServer initialized");

        this.sessionActive = true;
    }
    public void setNimBoard(NimBoard board){
        this.board = board;
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
            this.sessionActive = true;
            startGameLoop();
        }
    }
    private void startGameLoop(){
        // Get turn status from server
        while(this.sessionActive){
            int signal = this.cdts.getSignal(fromServer);
            switch(signal){
                case TURN_INDICATOR -> executeTurn();
                case BOARD_DATA -> updateBoardData();
                case CLOSE_CONNECTION -> closeSession();
            }
        }
    }

    private void executeTurn(){
        this.isTurn = true;
    }

    private void updateBoardData(){
        ArrayList<Boolean> boardData = null;
        try{
            boardData = (ArrayList<Boolean>) fromServer.readObject();
        } catch(IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            System.out.println("Could not retrieve board data");
        }

        Platform.runLater(new BoardUpdater(boardData));
    }

    public void sendBoardData(ArrayList<Boolean> boardData){
        this.isTurn = false;
        try{
            this.cdts.sendSignal(BOARD_DATA, toServer);
            toServer.writeObject(boardData);
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Could not send board data");
        }
    }
    public void closeSession(){
        this.cdts.sendSignal(CLOSE_CONNECTION, toServer);
        this.sessionActive = false;
        try{
            this.fromServer.close();
            this.toServer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Could not close data streams");
        }

    }
    public boolean isSessionActive(){
        return this.sessionActive;
    }
    public boolean isCurrentTurn(){
        return this.isTurn;
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
            System.out.println("Got signal " + signal + " from server");
            return signal;
        }
    }
    private class BoardUpdater implements Runnable{
        private ArrayList<Boolean> boardData;

        public BoardUpdater(ArrayList<Boolean> boardData){
            this.boardData = boardData;
        }
        public void run(){
            board.updateBoard(this.boardData);
        }
    }
}
