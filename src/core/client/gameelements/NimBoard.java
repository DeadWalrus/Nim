package core.client.gameelements;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.SimpleTimeZone;

public class NimBoard extends Pane {
    private ArrayList<Stick> sticks;

    public NimBoard(){
        this.setStyle("-fx-background-color: lightgreen");
        this.sticks = new ArrayList<>();
        this.sticks.add(new Stick(50, 50));
        getChildren().addAll(this.sticks);
    }
    public void updateBoard(ArrayList<Stick> sticks){
        this.sticks = sticks;
        drawBoard();
    }
    private void drawBoard(){
        // Re-draw the board with new stick data
    }
    private void initBoardLayout(){
        // Set location of matches and other objects
    }

//    public ArrayList<Stick> getState(){
//        // Returns the state of the board as array of integers indicating what matches were removed
//        return this.sticks;
//    }

    private class Stick extends Rectangle implements Serializable{

        public Stick(double x, double y){
            this.setLayoutX(x);
            this.setLayoutY(y);
            this.setFill(Color.BLUE);
            this.setWidth(10);
            this.setHeight(30);
        }
    }
}
