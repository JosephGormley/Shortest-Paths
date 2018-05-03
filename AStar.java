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

// BACKGROUND IMPORTS. 
import java.util.HashMap;
import java.util.List; 
import java.util.ArrayList;

public class AStar extends Application {

    // REPRESENTS STATUS OF BLOCK.
    private enum Status { START, WALL, END, OPEN, ROUTE, CLOSED }

    // REPRESENTS BLOCK BEING PLACED.
    private enum Selected { START, WALL, END, OPEN, RUN }

   /*******************
    * GLOBAL FIELD(S) *
    *******************/
    // ROOM / MENU GAP,
    private static final int V_GAP = 0; 

    // COLOR BAR GAP.
    private static final int H_GAP = 5;
  
    // SIZE OF WINDOW. 
    private static final int H = 600;
    private static final int W = 850;

    // SIZE OF MENU & ROOM. 
    private static final int ROOM_H = 600;
    private static final int ROOM_W = 600;
    private static final int MENU_H = ROOM_H; 
    private static final int MENU_W = W - ROOM_W;     

    // NUMBER OF COLS AND ROWS.
    private static final int BLOCK_SIZE = 25;
    private static final int ROW_SIZE = ROOM_H / BLOCK_SIZE;
    private static final int COL_SIZE = ROOM_W / BLOCK_SIZE; 

    // REPRESENTS PROGRAM'S STATE. 
    private static Block[][] grid = new Block[ROW_SIZE][COL_SIZE]; 
    Selected buttonSelected = Selected.START;

    // MAPPING OF BLOCK STATE TO COLOR. 
    HashMap<Selected, Color> map = new HashMap<Selected, Color>();

    // NECESSARY TO RUN.
    Block startPoint;
    Block endPoint;
    Block currentPoint;

   /***************
    * BLOCK CLASS *
    ***************/
    /* REPRESENTS TILES OF 2D-ROOM */
    private class Block extends StackPane {

        private int x, y;
        private int gCost, hCost, fCost;
        private Status status; 
       
        private Rectangle r;

        public Block(int x, int y){
            this.x = x;
            this.y = y;          
            gCost = 0;
            hCost = 0;
            status = Status.OPEN;

            // X AND Y ARE SWAPPED DUE TO NATURE OF NEXT TWO LINES.             
            setTranslateX(y * BLOCK_SIZE);
            setTranslateY(x * BLOCK_SIZE);
            r = new Rectangle(BLOCK_SIZE - 1, BLOCK_SIZE - 1, Color.WHITE); 
            r.setOnDragDetected(e -> r.startFullDrag());
            getChildren().add(r);                     
 
            setOnMousePressed(e -> placeBlock());
            setOnMouseDragEntered(e -> placeBlock());                                    
        }
  
        private void placeBlock(){
 
            r.setFill(map.get(buttonSelected));    
            System.out.println(x + " " + y);
            switch(buttonSelected){
                case START:
                    grid[x][y].status = Status.START; 
                    startPoint = grid[x][y];
                    break;
                case WALL:
                    grid[x][y].status = Status.WALL;
                    break;
                case END:
                    grid[x][y].status = Status.END;
                    endPoint = grid[x][y];
                    break;
                case OPEN:
                    grid[x][y].status = Status.OPEN;
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
        map.put(Selected.START, Color.GREEN);
        map.put(Selected.WALL, Color.BLACK);
        map.put(Selected.END, Color.RED);
        map.put(Selected.OPEN, Color.WHITE);       

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
                 
        // MENU - RUN. 
        Button run = new Button();
         run.setText("RUN");
         run.getStyleClass().add("button-run");
         run.setOnAction(e -> runAlgorithm(addStart, addWall, addEnd));
        
        // MENU - REMOVE (TRASHCAN IMAGE).
        Button remove = new Button();
         remove.getStyleClass().add("button-remove");
         remove.setAlignment(Pos.BOTTOM_LEFT);         
         remove.setOnAction(e -> buttonSelected = Selected.OPEN);

        // MENU TEXT. 
        Label comparisons = new Label("COMPARISONS:");
         comparisons.setStyle("-fx-font-size: 20;");
         comparisons.setPrefWidth(MENU_W - 10);
         comparisons.setAlignment(Pos.CENTER_LEFT);
        Label count = new Label();
         count.setText("0");
         count.setTextFill(Color.RED);
         count.setStyle("-fx-background-color: White; -fx-font-size: 60px;");
         count.setPrefHeight(MENU_H/3 - 1);
         count.setPrefWidth(MENU_W);
         count.setAlignment(Pos.CENTER);
        Label author = new Label();
         author.setText("designed & written by: Joseph Gormley");
         author.setStyle("-fx-font-size: 10px;");
         spCount.setMargin(author, new Insets(0, 2, 0, 0));         

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
    private void runAlgorithm(Button addStart, Button addWall, Button addEnd){

        // DECLARE / INIT NECESSARY DATA STRUCTURES.
        List<Block> openList = new ArrayList<Block>(); 
        List<Block> closedList = new ArrayList<Block>();   
 
        // DISABLE BUTTONS TO PREVENT RUINING 'RUN' STATE. 
        addStart.setDisable(true);
        addWall.setDisable(true);
        addEnd.setDisable(true);

        currentPoint = startPoint; 
        currentPoint.status = Status.WALL; // TO PREVENT RETRACING. 
        while(currentPoint != endPoint){

            // CALCULATE COSTS OF NEIGHBORS.
            List<Block> neighbors = findNeighbors(currentPoint);
            for(Block b : neighbors){
  
                int xDiff = 0;
                int yDiff = 0;

                // CALCULATE G & H COSTS.
                // I.O.W. EUCLIDEAN DISTANCES. 
                xDiff = b.x - startPoint.x;
                yDiff = b.y - startPoint.y;
                b.gCost = (int)Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
                xDiff = b.x - endPoint.x; 
                yDiff = b.y - endPoint.y;
                b.hCost = (int)Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));

                // CALCULATE F COST.      
                b.fCost = b.gCost + b.hCost;
                System.out.print("Neighbor: " + b.x + "-" + b.y);
                System.out.print(" | gCost of " + b.gCost);
                System.out.print(" | hCost of " + b.hCost);
                System.out.print(" | fCost of " + b.fCost);
                System.out.println();
                openList.add(b);
            
            }

            Block b = openList.get(0);
            // SEARCH OPEN LIST FOR LOWEST F COST. 
            for(int i = 1; i < openList.size(); i++){
                if(b.fCost > openList.get(i).fCost){
                    b = openList.get(i);
                }
            }
           
            b.r.setFill(Color.BLUE);
            currentPoint = b;
            System.out.println("New current point: " + currentPoint.x + " " + currentPoint.y);
            openList.remove(b);
            currentPoint.status = Status.WALL; // TO PREVENT RETRACING.
//            closedList.add(b);     
     
        }   
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
 
            if(newX >= 0 && newX < ROW_SIZE && newY >= 0 && newY < COL_SIZE && grid[newX][newY].status != Status.WALL){
                Block neighbor = grid[newX][newY];
                neighbors.add(neighbor);
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
       System.out.println();
    }
    
}

