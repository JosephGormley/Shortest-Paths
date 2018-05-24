// GUI IMPORTS. 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

// BACKGROUND IMPORTS. 
import java.util.Stack;
import java.util.HashMap;
import java.util.List; 
import java.util.ArrayList;

public class AStar extends Application {

    // AESTHETICS.
    private final Color empty = Color.WHITE;
    private final Color sp = Color.GREEN;
    private final Color ep = Color.RED;
    private final Color op = Color.BLACK;
    private final Color open = Color.DARKGREY;
    private final Color closed = Color.LIGHTGREY;
    private final Duration seekPause = Duration.millis(25);
    private final Duration pathPause = Duration.millis(100);    

    // REPRESENTS STATUS OF BLOCK.
    private enum Status { EMPTY, WALL, OPEN, CLOSED }

    // REPRESENTS BLOCK BEING PLACED.
    private enum Selected { START, WALL, END, EMPTY, RUN }

   /*******************
    * GLOBAL FIELD(S) *
    *******************/
    // ROOM / MENU GAP,
    private static final int V_GAP = 0; 

    // COLOR BAR GAP.
    private static final int H_GAP = 25;
  
    // SIZE OF WINDOW. 
    private static final int H = 600;
    private static final int W = 850;

    // SIZE OF MENU & ROOM. 
    private static final int ROOM_H = 600;
    private static final int ROOM_W = 600;
    private static final int MENU_H = ROOM_H; 
    private static final int MENU_W = W - ROOM_W;     

    // NUMBER OF COLS AND ROWS.
    private static final int BLOCK_SIZE = 15;
    private static final int ROW_SIZE = ROOM_H / BLOCK_SIZE;
    private static final int COL_SIZE = ROOM_W / BLOCK_SIZE; 

    // REPRESENTS PROGRAM'S STATE. 
    private static Block[][] grid = new Block[ROW_SIZE][COL_SIZE]; 
    private List<Block> openList = new ArrayList<Block>();
    private List<Block> closedList = new ArrayList<Block>(); 
    private Selected buttonSelected = Selected.START;
    private int comparisonCount = 0;

    // MAPPING OF BLOCK STATE TO COLOR. 
    private HashMap<Selected, Color> map = new HashMap<Selected, Color>();

    // NECESSARY TO RUN.
    private Block startPoint;
    private Block endPoint;
    
   /***************
    * BLOCK CLASS *
    ***************/
    /* REPRESENTS TILES OF 2D-ROOM */
    private class Block extends StackPane {
  
        private int x, y;
        private int gCost, hCost, fCost;
        private Status status; 
       
        private Rectangle r;
        private Block parent;
  
        public Block(int x, int y){
            this.x = x;
            this.y = y;          
            gCost = 0;
            hCost = 0;
            status = Status.EMPTY;
            parent = null;  

            // X AND Y ARE SWAPPED DUE TO NATURE OF NEXT TWO LINES.             
            setTranslateX(y * BLOCK_SIZE);
            setTranslateY(x * BLOCK_SIZE);
            r = new Rectangle(BLOCK_SIZE - 1, BLOCK_SIZE - 1, empty); 
            r.setOnDragDetected(e -> r.startFullDrag());
            getChildren().add(r);                     
  
            setOnMousePressed(e -> placeBlock());
            setOnMouseDragEntered(e -> placeBlock());                                    
        }
  
        private void placeBlock(){
 
            r.setFill(map.get(buttonSelected));    
            switch(buttonSelected){
                case START:  
                    // ONLY ONE SP AT A TIME.
                    if(startPoint != null){ 
                        grid[startPoint.x][startPoint.y].status = Status.EMPTY;
                        grid[startPoint.x][startPoint.y].r.setFill(empty);
                    }
                    grid[x][y].status = Status.CLOSED;
                    startPoint = grid[x][y];
                    break;
                case WALL:
                    grid[x][y].status = Status.WALL;
                    break;
                case END: 
                    if(endPoint != null){
                        grid[endPoint.x][endPoint.y].status = Status.EMPTY;
                        grid[endPoint.x][endPoint.y].r.setFill(empty);
                    }
                    endPoint = grid[x][y];
                    break;
                case EMPTY:
                    grid[x][y].status = Status.EMPTY;
                    break;
            }

        }

        public String toString(){            
            String s = grid[x][y].status + " ";
            return s.charAt(0) + " ";
        }
    }
    
   /******************
    * INITIALIZATION *
    ******************/
    /* GUI / 2D-ROOM INIT */
    private HBox initialize() {
  
        HBox hb = new HBox();
        VBox menu = new VBox(V_GAP);
        Pane room = new Pane();
        StackPane spCount = new StackPane();
  
        // MENU VISUALIZATIONS. 
        menuInit(menu, spCount); 

        // ROOM VISUALIZATIONS. 
        roomInit(room);
       
        hb.getChildren().addAll(room, menu);  

        // MAP BLOCK STATES TO COLORS.
        map.put(Selected.START, sp);
        map.put(Selected.WALL, op);
        map.put(Selected.END, ep);
        map.put(Selected.EMPTY, empty);       

        return hb;
    }

   /*************
    * MENU INIT *
    *************/
    private void menuInit(VBox menu, StackPane spCount){

        // MENU LAYOUT. 
        menu.setStyle("-fx-background-color: White;");
        menu.setPrefHeight(MENU_H);
        menu.setPrefWidth(MENU_W);
        menu.setAlignment(Pos.BOTTOM_CENTER);
        spCount.setStyle("-fx-background-color: LightGrey;");
        spCount.setPrefHeight(MENU_H / 3);
        spCount.setPrefWidth(MENU_W);

        // MENU TITLE.
        Label title = new Label();
         title.setText("A*");
         title.setStyle("-fx-font-size: 100px");

        // MENU SELECTION BAR. 
        HBox colorBar = new HBox(H_GAP);
         colorBar.setPrefHeight(BLOCK_SIZE);
         colorBar.setPrefWidth(MENU_W);
         colorBar.setAlignment(Pos.CENTER);

        Button addStart = new Button();
         addStart.setText("SP");
         addStart.getStyleClass().add("button-addStart");
         addStart.setOnAction(e -> buttonSelected = Selected.START);

        Button addWall = new Button();
         addWall.setText("W");
         addWall.getStyleClass().add("button-addWall");   
         addWall.setOnAction(e -> buttonSelected = Selected.WALL);

        Button addEnd = new Button();
         addEnd.setText("EP");
         addEnd.getStyleClass().add("button-addEnd");
         addEnd.setOnAction(e -> buttonSelected = Selected.END);
        
        // MENU - REMOVE (TRASHCAN IMAGE).
        Button remove = new Button();
         remove.getStyleClass().add("button-remove");
         remove.setAlignment(Pos.BOTTOM_LEFT);
         remove.setOnAction(e -> buttonSelected = Selected.EMPTY);
                 
        // MENU TEXT. 
        Label comparisons = new Label("COMPARISONS:");
         comparisons.setStyle("-fx-font-size: 20;");
         comparisons.setPrefWidth(MENU_W - 10);
         comparisons.setAlignment(Pos.CENTER_LEFT);
        Label count = new Label();
         count.setText(comparisonCount + "");
         count.setTextFill(Color.RED);
         count.setStyle("-fx-background-color: White; -fx-font-size: 60px;");
         count.setPrefHeight(MENU_H/3 - 1);
         count.setPrefWidth(MENU_W);
         count.setAlignment(Pos.CENTER);
        Label author = new Label();
         author.setText("designed & written by: Joseph Gormley");
         author.setStyle("-fx-font-size: 10px;");
         spCount.setMargin(author, new Insets(0, 2, 0, 0));         

        // MENU - RUN.
        Button run = new Button();
         run.setText("RUN");
         run.getStyleClass().add("button-run");
         run.setOnAction(e -> runAlgorithm(run, addStart, addWall, addEnd, remove, count));

        // PIECE TOGETHER NODES.
        colorBar.getChildren().addAll(addStart, addWall, addEnd);
        spCount.getChildren().addAll(count, author);
        spCount.setAlignment(author, Pos.BOTTOM_RIGHT);
        menu.getChildren().addAll(title, colorBar, run, remove, comparisons, spCount);
        menu.setMargin(run, new Insets(30, 0, 0, 0));
        menu.setMargin(remove, new Insets(0, 135, 30, 0));
        menu.setMargin(title, new Insets(0, 0, 10, 40));
    }

   /*************
    * ROOM INIT *
    *************/
    private void roomInit(Pane room){
        room.setMinWidth(ROOM_W);
        room.setMinHeight(ROOM_H);
        room.setStyle("-fx-background-color: LightGrey;");
        grid = new Block[ROW_SIZE][COL_SIZE];
        for(int row = 0; row < ROW_SIZE; row++){
           for(int col = 0; col < COL_SIZE; col++){
               Block b = new Block(row, col); 
               room.getChildren().add(b);
               grid[row][col] = b;
           }
       }
    }

    @Override
    /* CALLED BY LAUNCH() TO SET CONTEXT OF STAGE */
    public void start(Stage stage) throws Exception {

        Scene window = new Scene(initialize(), W, H - 1);
        window.getStylesheets().add("ButtonThemes.css");
        stage.setTitle("Dijkstra Shortest Path w/ Heuristic Function - A*");
        stage.setResizable(false);
        stage.setScene(window);

        stage.show();
       
    }
    
   /*********************************
    * A* ALGORITHM (HEURISTIC FUNC) *
    *********************************/
    private void runAlgorithm(Button run, Button addStart, Button addWall, Button addEnd, Button remove, Label count){       
    
        Block currentPoint;
    
        // IF ALREADY RUNNING. OTHERWISE IGNORE. 
        if(run.getText().equals("RESTART")){
    
            run.setText("RUN");
    
            // REMOVE OPEN / CLOSED.  
            for(Block b : openList){
                b.r.setFill(empty);
                grid[b.x][b.y].status = Status.EMPTY;     
            }

            for(Block b : closedList){
                if(b != endPoint){
                    b.r.setFill(empty);  
                    grid[b.x][b.y].status = Status.EMPTY;
                }
            }

            // RETURN SP TO OSP & EP TO OEP.
            startPoint.r.setFill(empty);
            
            // RESTART STATE. 
            addStart.setDisable(false);
            addWall.setDisable(false);
            addEnd.setDisable(false);
            remove.setDisable(false);
            addEnd.requestFocus();
            buttonSelected = Selected.END;
            startPoint = endPoint;
            endPoint = null;
            currentPoint = null;
            comparisonCount = 0; 
            count.setText(comparisonCount + "");
            openList.clear();
            closedList.clear();
            return;
        }   
    
        // DISABLE BUTTONS TO PREVENT RUINING 'RUN' STATE. 
        run.setDisable(true);
        addStart.setDisable(true);
        addWall.setDisable(true);
        addEnd.setDisable(true);
        remove.setDisable(true);        

        // FOR AESTHETIC PURPOSES.
        Timeline visualization = new Timeline();
        KeyFrame fillBlock = null;
        Duration timePoint = Duration.ZERO;      

        // SET CURRENT POINT, ADD CURRENT POINT TO CLOSED LIST. 
        currentPoint = startPoint;
        closedList.add(currentPoint);        
        while(currentPoint != endPoint){
    
            // CALCULATE COSTS OF NEIGHBORS.
            List<Block> neighbors = findNeighbors(currentPoint);
            for(Block b : neighbors){
    
                int xDiff, yDiff, minDiff, maxDiff;
    
                // CALCULATE G COST.                         
                xDiff = Math.abs(b.x - startPoint.x);
                yDiff = Math.abs(b.y - startPoint.y);
                minDiff = Math.min(xDiff, yDiff);
                maxDiff = Math.max(xDiff, yDiff);
                b.gCost = (minDiff * 14) + ((maxDiff - minDiff) * 10);          
      
                // CALCULATE H COST.
                xDiff = Math.abs(b.x - endPoint.x);
                yDiff = Math.abs(b.y - endPoint.y);
                minDiff = Math.min(xDiff, yDiff);
                maxDiff = Math.max(xDiff, yDiff);
                b.hCost = (minDiff * 14) + ((maxDiff - minDiff) * 10);
                
                // CALCULATE F COST.      
                b.fCost = b.gCost + b.hCost;

                // SET STATE OF BLOCK TO OPEN, SET PARENT.
                grid[b.x][b.y].status = Status.OPEN;
                openList.add(b);
                b.parent = currentPoint;

                // ADD COLOR OF BLOCK TO TIMELINE. 
                if(b != startPoint && b != endPoint){
                    fillBlock = new KeyFrame(timePoint, e -> b.r.setFill(open));
                    visualization.getKeyFrames().add(fillBlock);
                    timePoint = timePoint.add(seekPause);
                }
            }
 
            currentPoint = openList.get(0);
            // SEARCH OPEN LIST FOR LOWEST F COST. 
            for(int i = 1; i < openList.size(); i++){
                // IF B HAS LOWER F.
                if(openList.get(i).fCost < currentPoint.fCost){ // IF B HAS LOWER F.
                    currentPoint = openList.get(i);
                    visualization.getKeyFrames().add(new KeyFrame(timePoint, e -> count.setText(comparisonCount++ + "")));
                    
                }
                // IF B HAS SAME F COST, GO WITH LOWER H.
                if(openList.get(i).fCost == currentPoint.fCost){                    
                    currentPoint = openList.get(i).hCost > currentPoint.hCost? currentPoint : openList.get(i);
                    visualization.getKeyFrames().add(new KeyFrame(timePoint, e -> count.setText(comparisonCount++ + "")));
                }           
                    
            }
 
            // ADD COLOR OF BLOCK & COMPARISON COUNT TO TIMELINE.
            if(currentPoint != startPoint && currentPoint != endPoint){ 
                Rectangle r = currentPoint.r; 
                fillBlock = new KeyFrame(timePoint, e -> r.setFill(closed));
                visualization.getKeyFrames().add(fillBlock);
                timePoint = timePoint.add(seekPause);
            }
            
            openList.remove(currentPoint);
            closedList.add(currentPoint);
            grid[currentPoint.x][currentPoint.y].status = Status.CLOSED;
        }   
       
        // TRACE BACK PATH TO START POINT.
        Stack<Block> path = new Stack<Block>();
        for(Block b = endPoint; b != startPoint; b = b.parent){
            path.push(b);    
        }
 
        // ADD PATH TO TIMELINE.         
        while(!path.empty()){
            Block b = path.pop();
            fillBlock = new KeyFrame(timePoint, e -> { b.r.setFill(sp); b.parent.r.setFill(closed); });
            visualization.getKeyFrames().add(fillBlock);
            timePoint = timePoint.add(pathPause);
        }

        // CHANGE RUN TO RESTART, BUTTON REUSE.
        KeyFrame restart = new KeyFrame(timePoint, e -> { run.setText("RESTART"); run.setDisable(false); });
        visualization.getKeyFrames().add(restart);
  
        visualization.play();

    }

    private List<Block> findNeighbors(Block b){
        
        List<Block> neighbors = new ArrayList<Block>();
   
        int[] points = { -1, -1, 
                         -1,  0, 
                         -1,  1,
                          0, -1, 
                          0,  1,
                          1, -1,
                          1,  0, 
                          1,  1,   
                       };

        int dx, dy, newX, newY; 

        for(int i = 0; i < points.length; i++){

            dx = points[i];
            dy = points[++i];

            newX = dx + b.x;
            newY = dy + b.y;
 
            if(newX >= 0 && newX < ROW_SIZE && newY >= 0 && newY < COL_SIZE){
                if(grid[newX][newY].status != Status.WALL && grid[newX][newY].status != Status.OPEN && grid[newX][newY].status != Status.CLOSED){
                    Block neighbor = grid[newX][newY];
                    neighbors.add(neighbor);
                }
            }      
        }

        return neighbors;
    }


   /***************
    * MAIN METHOD *
    ***************/
    public static void main(String[] args){
        launch(args);
    }

   /********************
    * HELPER METHOD(S) *   
    ********************/
    // THE FOLLOWING METHODS WILL BE USED FOR DEBUGGING. 
    private static void printGrid(){
       for(int row = 0; row < ROW_SIZE; row++){
           for(int col = 0; col < COL_SIZE; col++){
               System.out.print(grid[row][col] + " ");
           }
           System.out.println();
       }
    }
    
}
