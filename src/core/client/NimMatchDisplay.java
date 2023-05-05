package core.client;

import core.client.gameelements.NimBoard;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class NimMatchDisplay extends Stage {
    private NimBoard gameBoard;

    public NimMatchDisplay(){
        BorderPane gamePane = getGamePane();
        Scene nimScene = new Scene(gamePane);
        this.setScene(nimScene);
    }
    private BorderPane getGamePane(){
        BorderPane gamePane = new BorderPane();
        this.gameBoard = new NimBoard();
        gamePane.setCenter(this.gameBoard);
        return gamePane;
    }
}