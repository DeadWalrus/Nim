package core.client.netutils;

import core.NimNetworkSignals;
import core.SignalParser;
import core.client.gameelements.NimBoard;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientSessionHandler implements Runnable, NimNetworkSignals{
    private final ObjectInputStream fromServer;
    private final ObjectOutputStream toServer;
    private ClientDataTransferService cdts;
    private final TextArea taOutputArea;
    private boolean sessionActive;
    private boolean isTurn;
    private NimBoard board;

    public ClientSessionHandler(Socket server, TextArea taOutputArea) throws IOException{
        this.taOutputArea = taOutputArea;
        this.taOutputArea.appendText("Initializing streams\n");
        this.toServer = new ObjectOutputStream(server.getOutputStream()); // Initialize outputstream first???
        this.fromServer = new ObjectInputStream(server.getInputStream());
        this.taOutputArea.appendText("Streams initialized\n");
        this.sessionActive = true;
    }
    public void setNimBoard(NimBoard board){
        this.board = board;
    }

    public void run(){
        this.taOutputArea.appendText("Creating new transfer service\n");
        this.cdts = new ClientDataTransferService();
        // While the session is running, listen for data from server and act upon it
        int signal = this.cdts.getSignal(fromServer);
        this.taOutputArea.appendText(signal + "");
        if(signal == CONNECTION_PROBE){
            this.cdts.sendSignal(CONNECTION_ESTABLISHED, toServer);
            this.taOutputArea.appendText("Signal " + CONNECTION_ESTABLISHED + " sent\n");
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
                case CLOSE_CONNECTION -> closeSession(false);
                default -> closeSession(true);
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
            this.taOutputArea.appendText("Could not retrieve board data\n");
        }

        Platform.runLater(new BoardUpdater(boardData));
    }

    public void sendBoardData(List<Boolean> boardData){
        this.isTurn = false;
        try{
            this.cdts.sendSignal(BOARD_DATA, toServer);
            toServer.writeObject(boardData);
        } catch(IOException ex){
            ex.printStackTrace();
            this.taOutputArea.appendText("Could not send board data\n");
        }
    }
    public void closeSession(boolean sendCloseSignal){
        if(sendCloseSignal){
            this.cdts.sendSignal(CLOSE_CONNECTION, toServer);
        }
        this.sessionActive = false;
        try{
            this.fromServer.close();
            this.toServer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            this.taOutputArea.appendText("Could not close data streams\n");
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
            taOutputArea.appendText("Created new ClientDataTransferService\n");
        }

        public void sendSignal(int signal, ObjectOutputStream toServer){
            taOutputArea.appendText("Sending signal\n");
            try{
                toServer.writeInt(signal);
                toServer.flush();
            } catch(IOException ex){
                ex.printStackTrace();
                taOutputArea.appendText("Could not send signal to server\n");
            }
        }

        public int getSignal(ObjectInputStream fromServer){
            taOutputArea.appendText("Getting signal\n");
            int signal = 0;
            try{
                signal = fromServer.readInt();
            } catch(IOException ex){
                ex.printStackTrace();
                taOutputArea.appendText("Could not get signal from server\n");
            }
            taOutputArea.appendText("Got signal " + signal + " from server\n");
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
