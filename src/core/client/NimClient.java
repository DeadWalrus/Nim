package core.client;
import core.client.netutils.MatchService;
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

public class NimClient  extends Application{
    private TextArea taConnectionInfo;
    private boolean gameInProgress;
    private Socket server;
    MatchService match;
    /**
     * Initialize GUI for the client
     * @param primaryStage Main window
     */
    public void start(Stage primaryStage){
        BorderPane primaryPane = new BorderPane();
        primaryPane.setPrefSize(400, 400);
        primaryPane.setBottom(this.getBottomPane());
        this.taConnectionInfo = new TextArea();
        primaryPane.setCenter(this.taConnectionInfo);
        Scene primaryScene = new Scene(primaryPane);
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Welcome to Nim");
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
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
        btConnect.setOnAction(e -> connectToServer(tfServerAddress.getText()));
        bottom.getChildren().addAll(tfServerAddress, btConnect);
        return bottom;
    }

    /**
     * Connect to the server
     */
    private void connectToServer(String serverAddress){
        if(this.match != null && this.match.isRunning()){
            this.taConnectionInfo.appendText("Game already in progress\n");
            return;
        }

        try{
            this.server = new Socket(serverAddress, 8888);
            this.taConnectionInfo.appendText("Connected to server with address " + serverAddress + "\n");
            this.match = new MatchService(server);
            match.setOutputLog(this.taConnectionInfo);
            // Wait for server to find match
            // If no match is found, present user with error box.
            new Thread(match).start();

            this.gameInProgress = true;

        }catch (IOException ex){
            new Alert(Alert.AlertType.ERROR, "An error occurred during initialization of match/connection.").show();
        }
    }

    private void disconnectFromServer() throws IOException{
        if(this.server != null){
            this.match.endMatch();
            this.server.close();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
