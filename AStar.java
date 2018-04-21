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
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.geometry.Pos;

public class AStar extends Application {

   /*******************
    * GLOBAL FIELD(S) *
    *******************/
    // ROOM / MENU AND MENU BUTTON GAPS,
    private static final int V_GAP = 0; 

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

    // REPRESENTS ROOM'S STATE. 
    private static Block[][] grid = new Block[ROW_SIZE][COL_SIZE]; 

    // REPRESENTS STATUS OF BLOCK.
    private enum Status { START, OBSTACLE, END, ROUTE, OPEN, CLOSED } 

    // REPRESENTS BLOCK BEING PLACED. 
    private enum Selected { START, OBSTACLE, END }

   /***************
    * BLOCK CLASS *
    ***************/
    /* REPRESENTS TILES OF 2D-ROOM */
    private class Block extends StackPane {

        private int x, y; 
        private Status status; 
       
        private Rectangle r;

        public Block(int x, int y){
            this.x = x;
            this.y = y;          
            status = Status.OPEN;
            
            setTranslateX(x * BLOCK_SIZE);
            setTranslateY(y * BLOCK_SIZE);
            r = new Rectangle(BLOCK_SIZE - 1, BLOCK_SIZE - 1, Color.WHITE); 

            getChildren().add(r);                     
        }
      
        public String toString(){
            return status + "";
        }
    }
    

   /******************
    * INITIALIZATION *
    ******************/
    /* GUI / 2D-ROOM INIT */
    private Pane initialize() {
 
        Pane pane = new Pane();
        VBox menu = new VBox();
        StackPane sp = new StackPane();
     
        // CREATE WINDOW.         
        pane.setStyle("-fx-background-color: LightGrey;");
        menu = new VBox(V_GAP);
        menu.setStyle("-fx-background-color: White;");
        menu.setPrefHeight(MENU_H);
        menu.setPrefWidth(MENU_W);
        menu.setAlignment(Pos.BOTTOM_CENTER);
        menu.setTranslateX(ROOM_W);
        sp.setStyle("-fx-background-color: LightGrey;");
        sp.setPrefHeight(MENU_H/3 + 1);
        sp.setPrefWidth(MENU_W);
  
        // MENU VISUALIZATIONS. 
        menuInit(menu, sp); 
        // ROOM VISUALIZATIONS. 
        roomInit(pane);
       
        pane.getChildren().addAll(menu);  
        return pane;
    }

   /*************
    * MENU INIT *
    *************/
    public void menuInit(VBox menu, StackPane sp){
        Button addStart = new Button("Start Point");
         addStart.setPrefWidth(150);
        Button addEnd = new Button("End Point");
        Button addObstacle = new Button("Obstacle Point");
        Button startPath = new Button("Start");
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
        sp.getChildren().addAll(count, author);
        sp.setAlignment(author, Pos.BOTTOM_RIGHT);
        menu.getChildren().addAll(addStart, addObstacle, addEnd, startPath, comparisons, sp);
    }

   /*************
    * ROOM INIT *
    *************/
    public void roomInit(Pane pane){
        grid = new Block[ROW_SIZE][COL_SIZE];
        for(int row = 0; row < ROW_SIZE; row++){
           for(int col = 0; col < COL_SIZE; col++){
               Block b = new Block(row, col);
               pane.getChildren().add(b);
               grid[row][col] = b;
           }
       }
    }

    @Override
    /* CALLED BY LAUNCH() TO SET CONTEXT OF STAGE */
    public void start(Stage stage) throws Exception {

       Scene window = new Scene(initialize(), W, H - 1);
       window.getStylesheets().add("ButtonTheme.css");
       stage.setTitle("Dijkstra Shortest Path w/ Heuristic Function - A*");
       stage.setResizable(false);
       stage.setScene(window);

       printRoom();

       stage.show();
       
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
    // NOTE THESE METHODS WILL BE USED FOR DEBUGGING. 
    public static void printRoom(){
       for(int row = 0; row < ROW_SIZE; row++){
           for(int col = 0; col < COL_SIZE; col++){
               System.out.print(grid[row][col] + " ");
           }
           System.out.println();
       }
    }
    
}

