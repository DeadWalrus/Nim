package core.client.gameelements;

import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;


public class NimBoard extends Pane {
    private ArrayList<Boolean> sticksInPlay;
    private final ArrayList<NimStick> sticks;
    private final GridPane stickPane;
    private final int PANE_WIDTH = 400;
    private final int PANE_HEIGHT = 400;

    private final int NUM_STICKS = 9;

    public NimBoard(){
        this.setStyle("-fx-background-color: lightgreen");
        this.sticksInPlay = new ArrayList<>();
        this.sticks = new ArrayList<>();
        this.stickPane = new GridPane();
        initSticksInPlay();
        createBoard();
        getChildren().add(this.stickPane);
    }

    public void updateBoard(ArrayList<Boolean> sticks){
        this.sticksInPlay = sticks;
        drawBoard();
    }
    private void initSticksInPlay(){
        for(int i = 0; i < this.NUM_STICKS; i++){
            this.sticksInPlay.add(true);
        }
    }
    private void createBoard(){
        for(int i = 0; i < this.NUM_STICKS; i++){
            this.sticks.add(new NimStick(15, 90, i));
        }
//        this.stickPane.setHgap(10);
//        this.stickPane.setVgap(10);
        for(int i = 0; i < this.NUM_STICKS; i++){
            this.stickPane.add(this.sticks.get(i), i, i);
        }

    }
    private void drawBoard(){
        this.stickPane.getChildren().clear();
        for(int i = 0; i < this.NUM_STICKS; i++){
            if(this.sticksInPlay.get(i)){
                this.stickPane.add(this.sticks.get(i), i, i);
            }
        }
    }
    private void initBoardLayout(){
        // Set location of matches and other objects
    }
    public ArrayList<Boolean> getBoardData(){
        return this.sticksInPlay;
    }

    private class NimStick extends Rectangle{
        private int indexInPlay;
        public NimStick(int width, int height, int indexInPlay){
            this.indexInPlay = indexInPlay;
            this.setWidth(width);
            this.setHeight(height);
            this.setFill(Color.BLUE);
            this.setOnMouseEntered(e -> showBorder());
            this.setOnMouseExited(e -> collapseBorder());
            this.setOnMouseClicked(e -> removeFromPlay());
        }
        private void removeFromPlay(){
            stickPane.getChildren().remove(this);
            sticksInPlay.set(indexInPlay, false);
        }
        private void showBorder(){
            this.setStyle("fx-border-width: 1; -fx-border-color: black");
        }
        private void collapseBorder(){
            this.setStyle("-fx-border-width: 0");
        }
    }
}