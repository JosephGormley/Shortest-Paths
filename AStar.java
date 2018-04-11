import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AStar extends Application implements EventHandler<ActionEvent>{

   /*******************
    * GLOBAL FIELD(S) *
    *******************/
    // SIZE OF 'ROOM'. 
    final private int ROWS = 3;
    final private int COLUMNS = 6;
    
    private enum Status { START, END, ROUTE, CLOSED, OPEN }
        
   /***************
    * MAIN METHOD *
    ***************/
    public static void main(String[] args){
        init();
        launch(args);
    }

   /*******************
    * CLASS METHOD(S) *
    *******************/
    /* INITIALIZE / DECLARE 2D ARRAY TO REPRESENT A 'ROOM'. */
    public void init(){


    }
    @Override
    /* CALLED BY LAUNCH() TO SET AND OPEN PRIMARY STAGE */
    public void start(Stage primaryStage) throws Exception {

       // SET A STAGE & SCENE. 
       //  ___________
       // |___________|
       // |           |
       // |           |
       // |           | 
       // |___________| 
       primaryStage.setTitle("Path Finder");
       GridPane layout = new GridPane();
       Scene scene = new Scene(layout, 300, 250);       
       
       // ADD 'SQUARES' TO GIVE WINDOW A 'ROOM'. 
       // INITIALIZE 2D ARRAY TO REPRESENT A 'ROOM'. 
       //  ____________
       // |____________|
       // |[][][][][][]|
       // |[][][][][][]|
       // |[][][][][][]|
       // |____________| 
       for(int row = 0; row < ROWS; row++){
           for(int col = 0; col < COLUMNS; col++){
               layout.add(new Button(), row, col, 1, 1);
           }
       }

       // DISPLAY WINDOW TO USER. 
       primaryStage.setScene(scene);
       primaryStage.show();

    }
 
   /********************
    * HELPER METHOD(S) *   
    ********************/
    // NOTE THESE METHODS WILL BE USED FOR DEBUGGING */
     
    @Override
    /* HANDLER FOR THIS CLASS (STAGE). */ 
    public void handle(ActionEvent event){
       //if(event.getSource() == button){
           System.out.println("Button is pressed");
       //}
    }
    
}

