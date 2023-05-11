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
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.Socket;

public class NimClient extends Application{
    private TextArea taConnectionInfo;
    private Socket server;
    NimBoard nimBoard;
    NimMatchDisplay nmd;
    ClientSessionHandler clientSessionHandler;
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
            this.nmd.close();
           try{
               disconnectFromServer();
               System.out.println("Disconnected");
           }catch(IOException ex){
               System.out.println("Could not close connection to server");
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
            this.clientSessionHandler = new ClientSessionHandler(this.server, this.taConnectionInfo); //Getting stuck here
            this.taConnectionInfo.appendText("Created ClientSessionHandler connected to " + this.server.getInetAddress());

        }catch (IOException ex){
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred during initialization of match/connection.").show();
        }
    }

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
