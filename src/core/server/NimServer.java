package core.server;

import core.server.netutils.ConnectionService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;


public class NimServer extends Application {
    ServerSocket serverSocket;
    public void start(Stage primaryStage){
        // Initialize gui
        // Initialize network functionality
        try{
            initNetFunctionality();
        } catch(IOException ex){
            System.out.println("ERR: Could not initialize network functionality...");
        }

    }

    private void initNetFunctionality() throws IOException {
        // Create new ServerSocket
        // Create new ConnectionService
        int port = 8888;
        this.serverSocket = new ServerSocket(port);
        ConnectionService cs = new ConnectionService(this.serverSocket, port);
        new Thread(cs).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
