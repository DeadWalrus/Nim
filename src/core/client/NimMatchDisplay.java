package core.client;

import core.client.gameelements.NimBoard;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class NimMatchDisplay extends Stage {
    private NimBoard gameBoard;

    public NimMatchDisplay(NimBoard gameBoard){
        this.gameBoard = gameBoard;
        BorderPane gamePane = getGamePane();
        gamePane.setPrefSize(300, 500);
        Scene nimScene = new Scene(gamePane);
        this.setScene(nimScene);
    }

    private BorderPane getGamePane(){
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(this.gameBoard);
        gamePane.setRight(this.getGameControlPane());
        return gamePane;
    }

    private VBox getGameControlPane(){
        Button completeMove = new Button("Submit Moves");
        //completeMove.setOnAction(e -> this.sendBoardData());
        VBox gameControlPane = new VBox();
        gameControlPane.getChildren().add(completeMove);
        return gameControlPane;
    }

//    private void sendBoardData(){
//        try{
//            this.dts.sendData(90);
//        } catch(IOException e){
//            System.out.println("Could not send board data");
//            e.printStackTrace();
//        }
//    }
}