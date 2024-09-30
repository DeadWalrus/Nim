package core.client;
import core.client.gameelements.NimBoard;
import core.client.netutils.ClientSessionHandler;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.Socket;

/**
 * Creates a Nim client that logs connection info and connects to the Nim server
 */
public class NimClient extends Application{
    private TextArea taConnectionInfo; // Connection info log
    private Socket server; // Server to connect to
    private NimBoard nimBoard; // Board to be used for the game
    private NimMatchDisplay nmd; // Stage responsible for displaying the actual game
    private ClientSessionHandler clientSessionHandler; // Session handler for sending and receiving game data
    private final String instructionText = """
            Welcome to Nim!
            The goal of this game is to have your opponent pick up the last stick
            You may pick up as many sticks as you want per turn, but they must be from the same row.
            After making your move, click the submit button and the other player will then be able to make their move.""";
    /**
     * Initialize GUI for the client
     * @param primaryStage Main window
     */
    public void start(Stage primaryStage){
        BorderPane primaryPane = new BorderPane();
        primaryPane.setPrefSize(400, 400);
        primaryPane.setBottom(this.getBottomPane());

        this.taConnectionInfo = new TextArea();
        this.nimBoard = new NimBoard();

        primaryPane.setCenter(this.taConnectionInfo);
        Scene primaryScene = new Scene(primaryPane);
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Welcome to Nim");
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            if(this.nmd != null){
                this.nmd.close();
            }
            if(this.clientSessionHandler != null){
                try{
                    disconnectFromServer();
                    System.out.println("Disconnected");
                }catch(IOException ex){
                    System.out.println("Could not close connection to server");
                }
            }
        });
    }

    /**
     * {@return the bottom pane}
     */
    private HBox getBottomPane(){
        HBox bottom = new HBox();
        TextField tfServerAddress = new TextField("localhost");
        Button btConnect = new Button("Connect");
        btConnect.setOnAction(e -> {
            if(this.clientSessionHandler != null && this.clientSessionHandler.isSessionActive()){
                taConnectionInfo.appendText("Session already active");
                return;
            }
            connectToServer(tfServerAddress.getText());
            new Thread(this.clientSessionHandler).start();
            this.nmd = new NimMatchDisplay(this.nimBoard, this.clientSessionHandler);
            this.nmd.show();
            Alert instructions = new Alert(Alert.AlertType.INFORMATION, this.instructionText);
            instructions.setTitle("Welcome to Nim");
            instructions.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            instructions.show();
        });
        bottom.getChildren().addAll(tfServerAddress, btConnect);
        return bottom;
    }

    /**
     * Connect to the server
     */
    private void connectToServer(String serverAddress){
        try{
            this.server = new Socket(serverAddress, 8888);
            this.taConnectionInfo.appendText("Connected to server with address " + serverAddress + "\n");
            this.taConnectionInfo.appendText("Creating new session handler");
            this.clientSessionHandler = new ClientSessionHandler(this.server, this.taConnectionInfo);
            this.taConnectionInfo.appendText("Created ClientSessionHandler connected to " + this.server.getInetAddress() + "\n");

        }catch (IOException ex){
            //ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred during initialization of match/connection.").show();
        }
    }

    /**
     * Disconnect from the server when exiting out of game
     * @throws IOException If connection cannot be closed cleanly
     */
    private void disconnectFromServer() throws IOException{
        if(this.server != null){
            if(this.clientSessionHandler != null && this.clientSessionHandler.isSessionActive()){
                this.clientSessionHandler.closeSession(true);
            }
            this.server.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
