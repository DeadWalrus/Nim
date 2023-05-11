package core.client;

import core.client.gameelements.NimBoard;
import core.client.netutils.ClientSessionHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NimMatchDisplay extends Stage {
    private NimBoard gameBoard;
    private ClientSessionHandler clientSessionHandler;
    private Button completeMove;

    public NimMatchDisplay(NimBoard gameBoard, ClientSessionHandler clientSessionHandler){
        this.gameBoard = gameBoard;
        this.clientSessionHandler = clientSessionHandler;
        this.clientSessionHandler.setNimBoard(gameBoard);
        this.completeMove = new Button("Submit Moves");
        BorderPane gamePane = getGamePane();
        Scene nimScene = new Scene(gamePane);
        this.setScene(nimScene);
    }

    private BorderPane getGamePane(){
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(this.gameBoard);
        gamePane.setBottom(this.getGameControlPane());
        return gamePane;
    }

    private VBox getGameControlPane(){
        this.completeMove.setOnAction(e -> this.sendBoardData());
        VBox gameControlPane = new VBox();
        gameControlPane.setStyle("-fx-background-color: lightgreen");
        gameControlPane.getChildren().add(this.completeMove);
        return gameControlPane;
    }

    private void sendBoardData(){
        if(!this.clientSessionHandler.isCurrentTurn()){
            new Alert(Alert.AlertType.INFORMATION, "Not your turn!").show();
            return;
        }
        // If board data is empty, client won the game, send win signal
        this.clientSessionHandler.sendBoardData(this.gameBoard.getBoardData());
    }
}