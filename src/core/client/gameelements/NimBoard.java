package core.client.gameelements;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;


public class NimBoard extends Pane {
    private ArrayList<Boolean> sticksInPlay;
    private final ArrayList<NimStick> sticks;
    private final ArrayList<Point2D> positions;
    private final Pane stickPane;
    private static final int PANE_WIDTH = 400;
    private static final int PANE_HEIGHT = 400;
    private static final double STICK_X_OFFSET = PANE_WIDTH/2.0;
    private static final double STICK_Y_OFFSET = PANE_HEIGHT/5.0;
    private static final int NUM_STICKS = 9;

    public NimBoard(){
        this.setStyle("-fx-background-color: lightgreen");
        this.sticksInPlay = new ArrayList<>();
        this.sticks = new ArrayList<>();
        this.positions = new ArrayList<>();
        this.stickPane = new Pane();
        this.stickPane.setPrefSize(PANE_WIDTH, PANE_HEIGHT);
        initSticksInPlay();
        initPositions();
        createBoard();
        getChildren().add(this.stickPane);
    }

    public void updateBoard(ArrayList<Boolean> sticks){
        this.sticksInPlay = sticks;
        drawBoard();
    }
    private void initSticksInPlay(){
        for(int i = 0; i < NUM_STICKS; i++){
            this.sticksInPlay.add(true);
        }
    }
    private void initPositions(){
        int levelCount = (int)Math.sqrt(NUM_STICKS);
        for(int i = 0; i < levelCount; i++){
            int columnCount = (int)Math.pow(i, 2) + 1;
            System.out.println("Column count: " + columnCount);
            for(int j = 0; j <= columnCount; j++){
                System.out.println("(j - i) = " + (j - i));
                this.positions.add(new Point2D(STICK_X_OFFSET + (20 * (j - i)), STICK_Y_OFFSET + (100 * i)));
            }
        }
    }
    private void createBoard(){
        for(int i = 0; i < NUM_STICKS; i++){
            this.sticks.add(new NimStick(15, 90, i));
        }

        for(int i = 0; i < NUM_STICKS; i++){
            NimStick currentStick = this.sticks.get(i);
            currentStick.setLayoutX(this.positions.get(i).getX());
            currentStick.setLayoutY(this.positions.get(i).getY());
            System.out.println(currentStick.getLayoutX());
            System.out.println(currentStick.getLayoutY());
            this.stickPane.getChildren().add(currentStick);
        }
    }
    private void drawBoard(){
        this.stickPane.getChildren().clear();
        for(int i = 0; i < NUM_STICKS; i++){
            if(this.sticksInPlay.get(i)){
                this.stickPane.getChildren().add(this.sticks.get(i));
            }
        }
    }
    public List<Boolean> getBoardData(){
        return this.sticksInPlay;
    }

    private class NimStick extends Rectangle{
        private final int index;
        public NimStick(int width, int height, int indexInPlay){
            this.index = indexInPlay;
            this.setWidth(width);
            this.setHeight(height);
            this.setFill(Color.BLUE);
            this.setOnMouseEntered(e -> showBorder());
            this.setOnMouseExited(e -> collapseBorder());
            this.setOnMouseClicked(e -> removeFromPlay());
        }
        private void removeFromPlay(){
            stickPane.getChildren().remove(this);
            sticksInPlay.set(index, false);
        }
        private void showBorder(){
            this.setStyle("fx-border-width: 1; -fx-border-color: black");
        }
        private void collapseBorder(){
            this.setStyle("-fx-border-width: 0");
        }
    }
}