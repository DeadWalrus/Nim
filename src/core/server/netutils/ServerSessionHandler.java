package core.server.netutils;

import core.NimNetworkSignals;
import core.SignalParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Session handler for the server. Handles the communication between clients.
 */
public class ServerSessionHandler implements Runnable, NimNetworkSignals{
    private final Socket player1; // Player 1 socket
    private ObjectInputStream fromPlayer1; // Player 1 input
    private ObjectOutputStream toPlayer1; // Player 1 output
    private final Socket player2; // Player 2 socket
    private ObjectInputStream fromPlayer2; // Player 2 input
    private ObjectOutputStream toPlayer2; // Player 2 output

    private ServerDataTransferService sdts; // Data transfer service for sending and receiving signals
    private boolean gameIsRunning; // Is the game running?

    /**
     * Create new session handler with player 1 and 2 sockets.
     * @param player1 Socket for player 1
     * @param player2 Socket for player 2
     * @throws IOException when streams can't be initialized
     */
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

    /**
     * Verify both players are connected and start the session loop
     */
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
            try{
                sessionLoop();
            } catch(IOException ex){
                ex.printStackTrace();
                System.out.println("Problem occurred in session loop");
            }

        }
    }

    /**
     * Handles the relay of game data and connection events such as a disconnect from one of the players.
     * @throws IOException when game data cannot be read from player
     */
    @SuppressWarnings("unchecked")
    private void sessionLoop() throws IOException{
        ObjectInputStream currentPlayerInput = this.fromPlayer1;
        ObjectOutputStream currentPlayerOutput = this.toPlayer1;
        ObjectInputStream waitingPlayerInput = this.fromPlayer2;
        ObjectOutputStream waitingPlayerOutput = this.toPlayer2;
        while(this.gameIsRunning){
            this.sdts.sendSignal(TURN_INDICATOR, currentPlayerOutput);
            // Get signal from player
            int signal = this.sdts.getSignal(currentPlayerInput);
            // If signal isn't session term signal
            if(signal == CLOSE_CONNECTION){
                this.endSession(waitingPlayerOutput);
                return;
            }
            // Get move from player
            
            try{
                
                Object serverStickData = currentPlayerInput.readObject();
                // this is fine.
                
                ArrayList<Boolean> stickStatus = (ArrayList<Boolean>) serverStickData;
                if(currentPlayerLost(stickStatus)){
                    this.sdts.sendSignal(LOSE_SIGNAL, currentPlayerOutput);
                    this.sdts.sendSignal(WON_SIGNAL, waitingPlayerOutput);
                }else{
                    this.sdts.sendSignal(BOARD_DATA, waitingPlayerOutput);
                    waitingPlayerOutput.writeObject(stickStatus);
                    waitingPlayerOutput.flush();
                }
            } catch(ClassNotFoundException ex){
                ex.printStackTrace();
                System.out.println("Could not relay data to player");
            }
            // Swap players
            ObjectInputStream tempIn = currentPlayerInput;
            currentPlayerInput = waitingPlayerInput;
            waitingPlayerInput = tempIn;
            ObjectOutputStream tempOut = currentPlayerOutput;
            currentPlayerOutput = waitingPlayerOutput;
            waitingPlayerOutput = tempOut;
        }
    }

    /**
     * Checks to see if the current player has lost
     * @param stickStatus Status of the sticks. If there are no sticks left, player lost
     * @return True if player lost, false if no one lost yet
     */
    private boolean currentPlayerLost(List<Boolean> stickStatus){
        for(boolean b : stickStatus){
            if(b){
                return false;
            }
        }
        return true;
    }
    /**
     * Ends the session in case of failure and alerts the players
     * @param playerOut players to notify of session termination
     */
    private void endSession(ObjectOutputStream... playerOut){
        for(ObjectOutputStream p : playerOut){
            try{
                p.writeInt(CLOSE_CONNECTION);
            } catch(IOException ex){
                System.out.println("Could not send term signal to player");
            }
        }
        try{
            this.gameIsRunning = false;
            this.toPlayer1.close();
            this.fromPlayer1.close();
            this.player1.close();
            this.toPlayer2.close();
            this.fromPlayer2.close();
            this.player2.close();
        } catch(IOException ex){
            System.out.println("Could not end session");
        }
    }

    /**
     * Handles the sending and receiving of signals from players.
     */
    private class ServerDataTransferService implements SignalParser {

        /**
         * Log that the transfer service has been created
         */
        public ServerDataTransferService(){
            System.out.println("Created new ServerDataTransferService");
        }

        @Override
        public void sendSignal(int signal, ObjectOutputStream toPlayer){
            try{
                toPlayer.writeInt(signal);
                toPlayer.flush();
            } catch (IOException ex){
                System.out.println("Could not send signal " + signal);
                ex.printStackTrace();
            }
            System.out.println("Signal " + signal + " sent");
        }

        @Override
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

