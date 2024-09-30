package core.client.gameelements;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Display container for the sticks
 */
public class NimBoard extends Pane {
    private ArrayList<Boolean> sticksInPlay; // Stores the sticks that are still in play
    private final ArrayList<NimStick> sticks; // Stores the sticks themselves
    private final ArrayList<Point2D> positions; // Stores the (x,y) positions of the sticks
    private final ArrayList<NimStick> currentMove; // Stores the sticks removed from the game
    private final Pane stickPane; // Pane used to display the sticks
    private static final int PANE_WIDTH = 400; // Width of the stick pane
    private static final int PANE_HEIGHT = 400; // Height of the stick pane
    private static final double STICK_X_OFFSET = PANE_WIDTH/2.0; // Base x position of sticks
    private static final double STICK_Y_OFFSET = PANE_HEIGHT/5.0; // Base y position of sticks
    private static final int NUM_STICKS = 9; // Number of sticks to start the game with

    /**
     * Creates a NimBoard to hold the game elements.
     */
    public NimBoard(){
        this.setStyle("-fx-background-color: lightgreen");
        this.sticksInPlay = new ArrayList<>();
        this.sticks = new ArrayList<>();
        this.positions = new ArrayList<>();
        this.currentMove = new ArrayList<>();
        this.stickPane = new Pane();
        this.stickPane.setPrefSize(PANE_WIDTH, PANE_HEIGHT);
        initSticksInPlay();
        initPositions();
        createBoard();
        getChildren().add(this.stickPane);
    }

    /**
     * Update the game board with new stick locations
     * @param sticksInPlay Boolean ArrayList containing the state of each stick
     */
    public void updateBoard(ArrayList<Boolean> sticksInPlay){
        if(playerWon()){
            new Alert(Alert.AlertType.INFORMATION, "You Won!!!").show();
        }
        this.sticksInPlay = sticksInPlay;
        drawBoard();
    }

    private boolean playerWon(){
        for(boolean b : this.sticksInPlay){
            if(b){
                return false;
            }
        }
        return true;
    }
    /**
     * Initialize the sticksInPlay list
     */
    private void initSticksInPlay(){
        for(int i = 0; i < NUM_STICKS; i++){
            this.sticksInPlay.add(true);
        }
    }

    /**
     * Initialize the stick positions list
     */
    private void initPositions(){
        int levelCount = (int)Math.sqrt(NUM_STICKS);
        int columnAdd = 1;
        for(int i = 0; i < levelCount; i++){
            int columnCount = i + columnAdd;
            for(int j = 0; j < columnCount; j++){
                this.positions.add(new Point2D(STICK_X_OFFSET + (20 * (j - i)), STICK_Y_OFFSET + (100 * i)));
            }
            columnAdd++;
        }
    }

    /**
     * Add the sticks to the board
     */
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

    /**
     * Draw the board with the new stick valid states
     */
    private void drawBoard(){
        this.currentMove.clear();
        this.stickPane.getChildren().clear();
        for(int i = 0; i < NUM_STICKS; i++){
            boolean stickIsValid = this.sticksInPlay.get(i);
            if(stickIsValid){
                this.stickPane.getChildren().add(this.sticks.get(i));
            }
        }
    }

    /**
     * Retrieve the sticksInPlay list
     * @return the list containing the stick states
     */
    public List<Boolean> getBoardData(){
        return this.sticksInPlay;
    }

    /**
     * Keeps track of the information about each stick
     */
    private class NimStick extends Rectangle{
        private final int index; // Index in list

        /**
         * Creates a new NimStick with specific width, height, and index in list
         * @param width Width of NimStick
         * @param height Height of NimStick
         * @param indexInPlay Index in list
         */
        public NimStick(int width, int height, int indexInPlay){
            this.index = indexInPlay;
            this.setWidth(width);
            this.setHeight(height);
            this.setFill(Color.BLUE);
            this.setOnMouseClicked(e -> removeFromPlay());
        }

        /**
         * Removes the stick from the pane and sets the respective false value in sticksInPlay
         */
        private void removeFromPlay(){
            if(!currentMove.isEmpty() && this.getLayoutY() != currentMove.get(0).getLayoutY()){
                new Alert(Alert.AlertType.ERROR, "You can only remove sticks in one row per turn").show();
                return;
            }
            currentMove.add(this);
            stickPane.getChildren().remove(this);
            sticksInPlay.set(index, false);
        }

    }
}