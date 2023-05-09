package core.client;

import core.client.gameelements.NimBoard;
import core.client.netutils.ClientSessionHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class NimMatchDisplay extends Stage {
    private NimBoard gameBoard;
    private ClientSessionHandler clientSessionHandler;
    private Button completeMove = new Button("Submit Moves");

    public NimMatchDisplay(NimBoard gameBoard){
        this.gameBoard = gameBoard;
        BorderPane gamePane = getGamePane();
        gamePane.setPrefSize(300, 500);
        Scene nimScene = new Scene(gamePane);
        this.setScene(nimScene);
    }
    public void setSessionHandler(ClientSessionHandler clientSessionHandler){
        this.clientSessionHandler = clientSessionHandler;
        this.clientSessionHandler.setNimBoard(this.gameBoard);
    }

    private BorderPane getGamePane(){
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(this.gameBoard);
        gamePane.setRight(this.getGameControlPane());
        return gamePane;
    }

    private VBox getGameControlPane(){
        this.completeMove.setOnAction(e -> this.sendBoardData());
        VBox gameControlPane = new VBox();
        gameControlPane.getChildren().add(this.completeMove);
        return gameControlPane;
    }

    private void sendBoardData(){
        if(!this.clientSessionHandler.isCurrentTurn()){
            System.out.println("Not your turn");
            return;
        }
        // If board data is empty, client won the game, send win signal
        this.clientSessionHandler.sendBoardData(this.gameBoard.getBoardData());
    }
}