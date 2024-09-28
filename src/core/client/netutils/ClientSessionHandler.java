package core.client.netutils;

import core.NimNetworkSignals;
import core.SignalParser;
import core.client.gameelements.NimBoard;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the network communication for the client
 */
public class ClientSessionHandler implements Runnable, NimNetworkSignals{
    private final ObjectInputStream fromServer; // Data from server
    private final ObjectOutputStream toServer; // Data to server
    private ClientDataTransferService cdts; // Service responsible for sending and receiving signal to and from server
    private final TextArea taOutputArea; // Output log for connection info
    private boolean sessionActive; // Is session active?
    private boolean isTurn; // Is it your turn?
    private NimBoard board; // Board to be used for the game
    private Label lTurnStatus; // Label for displaying turn status.

    /**
     * Creates a new ClientSessionHandler from Socket and sets the output log to taOutputArea
     * @param server The server to connect to
     * @param taOutputArea Output log for connection info
     * @throws IOException If ObjectOutputStreams cannot be initialized
     */
    public ClientSessionHandler(Socket server, TextArea taOutputArea) throws IOException{
        this.taOutputArea = taOutputArea;
        this.taOutputArea.appendText("Initializing streams\n");
        this.toServer = new ObjectOutputStream(server.getOutputStream()); // Initialize outputstream first???
        this.fromServer = new ObjectInputStream(server.getInputStream());
        this.taOutputArea.appendText("Streams initialized\n");
        this.sessionActive = true;
    }

    /**
     * Set the board to be used for the game
     * @param board Board to be used
     */
    public void setNimBoard(NimBoard board){
        this.board = board;
    }

    /**
     * Initializes connection and starts the game
     */
    public void run(){
        this.taOutputArea.appendText("Creating new transfer service\n");
        this.cdts = new ClientDataTransferService();
        int signal = this.cdts.getSignal(fromServer);
        if(signal == CONNECTION_PROBE){
            this.cdts.sendSignal(CONNECTION_ESTABLISHED, toServer);
            this.taOutputArea.appendText("Signal " + CONNECTION_ESTABLISHED + " sent\n");
            this.sessionActive = true;
            this.taOutputArea.appendText("Connection established. Starting game.\n");
            startGameLoop();
        }
    }

    /**
     * Main game loop.
     * Waits for signals from server, then performs appropriate actions
     */
    private void startGameLoop(){
        // Get turn status from server
        while(this.sessionActive){
            int signal = this.cdts.getSignal(fromServer);
            switch(signal){
                case TURN_INDICATOR -> executeTurn();
                case BOARD_DATA -> updateBoardData();
                case CLOSE_CONNECTION -> closeSession(false);
                case WON_SIGNAL -> notifyPlayer(true);
                case LOSE_SIGNAL -> notifyPlayer(false);
                default -> closeSession(true);
            }
        }
    }

    /**
     * Notify player that it is their turn. Update isTurn field.
     */
    private void executeTurn(){
        Platform.runLater(() -> this.lTurnStatus.setText("Your turn"));
        this.isTurn = true;
    }

    /**
     * Update the board with board data from other client
     */
    @SuppressWarnings("unchecked")
    private void updateBoardData(){
        ArrayList<Boolean> boardData = null;
        
        try{
            Object serverBoardData = fromServer.readObject();
            // this is fine.
            boardData = (ArrayList<Boolean>) serverBoardData;
            //boardData = (ArrayList<Boolean>) fromServer.readObject();
        } catch(IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            this.taOutputArea.appendText("Could not retrieve board data\n");
        }

        ArrayList<Boolean> finalBoardData = boardData;
        Platform.runLater(() -> board.updateBoard(finalBoardData));
    }

    /**
     * Notify the player if they won or lost
     * @param winStatus True if won, false if not
     */
    private void notifyPlayer(boolean winStatus){
        if(winStatus){
            Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "You Won!").show());
        }else{
            Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "You Lost!").show());
        }
    }

    /**
     * Send the board data to the other client after move has been made
     * @param boardData Post-move board data
     */
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

    /**
     * Closes the session cleanly (slightly broken atm)
     * @param sendCloseSignal True if CLOSE_CONNECTION signal is to be sent to other client. False if not
     */
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

    /**
     * Set the label responsible for displaying turn status
     * @param lTurnStatus The label to be used
     */
    public void setTurnLabel(Label lTurnStatus){
        this.lTurnStatus = lTurnStatus;
    }

    /**
     * {@return true when session is active, false otherwise
     */
    public boolean isSessionActive(){
        return this.sessionActive;
    }

    /**
     * {@return true if your turn, false otherwise
     */
    public boolean isCurrentTurn(){
        return this.isTurn;
    }

    /**
     * Handles sending and receiving signals to and from server
     */
    private class ClientDataTransferService implements SignalParser {
        public ClientDataTransferService(){
            taOutputArea.appendText("Created new ClientDataTransferService\n");
        }

        @Override
        public void sendSignal(int signal, ObjectOutputStream toServer){
            taOutputArea.appendText("Sending signal\n");
            try{
                toServer.writeInt(signal);
                toServer.flush();
            } catch(IOException ex){
                ex.printStackTrace();
                taOutputArea.appendText("Could not send signal to server\n");
            }
            taOutputArea.appendText("Signal " + signal + " sent\n");
        }

        @Override
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

}
