package core.client;

import core.client.gameelements.NimBoard;
import core.client.netutils.ClientSessionHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NimMatchDisplay extends Stage {
    private NimBoard gameBoard; // Game board
    private ClientSessionHandler clientSessionHandler; // Session handler
    private Button completeMove; // Button to submit move to server
    private Label lTurnStatus; // Label to show whose turn it is

    /**
     * Create a new NimMatchDisplay with game board and session handler
     * @param gameBoard Game board to draw
     * @param clientSessionHandler Session handler to query
     */
    public NimMatchDisplay(NimBoard gameBoard, ClientSessionHandler clientSessionHandler){
        this.lTurnStatus = new Label("Not your turn");
        this.gameBoard = gameBoard;
        this.clientSessionHandler = clientSessionHandler;
        this.clientSessionHandler.setNimBoard(gameBoard);
        this.clientSessionHandler.setTurnLabel(this.lTurnStatus);
        this.completeMove = new Button("Submit Moves");
        BorderPane gamePane = getGamePane();
        Scene nimScene = new Scene(gamePane);
        this.setScene(nimScene);
    }

    /**
     *
     * {@return the main pane
     */
    private BorderPane getGamePane(){
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(this.gameBoard);
        gamePane.setBottom(this.getGameControlPane());
        return gamePane;
    }

    /**
     * {@return the game control pane
     */
    private VBox getGameControlPane(){
        this.completeMove.setOnAction(e -> this.sendBoardData());
        VBox gameControlPane = new VBox();
        gameControlPane.setStyle("-fx-background-color: lightgreen");
        gameControlPane.getChildren().addAll(this.completeMove, this.lTurnStatus);
        return gameControlPane;
    }

    /**
     * Send the board data to the server after move has been made
     */
    private void sendBoardData(){
        this.lTurnStatus.setText("Not your turn");
        if(!this.clientSessionHandler.isCurrentTurn()){
            new Alert(Alert.AlertType.INFORMATION, "Not your turn!").show();
            return;
        }
        // If board data is empty, client won the game, send win signal
        this.clientSessionHandler.sendBoardData(this.gameBoard.getBoardData());
    }
}