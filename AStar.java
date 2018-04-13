import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AStar extends Application {

   /*******************
    * GLOBAL FIELD(S) *
    *******************/
    // SIZE OF 'ROOM'. 
    final private int ROWS = 20;
    final private int COLUMNS = 20;

    // REPRESENTS STATUS OF SQUARE.    
    private enum Status { START, END, ROUTE, CLOSED, OPEN }
        
    private Status[][] room;

   /***************
    * MAIN METHOD *
    ***************/
    public static void main(String[] args){
        initialize();
        launch(args);
    }

   /*******************
    * CLASS METHOD(S) *
    *******************/
    /* INITIALIZE / DECLARE 2D ARRAY TO REPRESENT A 'ROOM'. */
    public static void initialize(){


    }

    @Override
    /* CALLED BY LAUNCH() TO SET AND OPEN PRIMARY STAGE */
    public void start(Stage primaryStage) throws Exception {

       HBox layout = new HBox(100);
       Scene scene = new Scene(layout, 600, 800);

       GridPane room = new GridPane();
       VBox buttons = new VBox(50);  

       Button addStart = new Button();
       Button addEnd = new Button();       

       // SET SCENE & STAGE. 
       //  ____________
       // |____________|
       // |       |    |
       // |       |    |
       // |       |    | 
       // |_______|____|
       //   room | buttons  
       layout.getChildren().addAll(room, buttons);
       
       // ADD 'SQUARES' TO GIVE LEFT WINDOW A 'ROOM'.
       // ADD BUTTONS TO GIVE RIGHT WINDOW A 'MENU'.  
       //  ____________
       // |____________|
       // |[][][] |    |
       // |[][][] |    |
       // |[][][] |    |
       // |_______|____| 
       for(int row = 0; row < ROWS; row++){
           for(int col = 0; col < COLUMNS; col++){
               Button button = new Button();
               button.setPrefSize(50, 50);
               room.add(button, row, col, 1, 1);
           }
       }

       buttons.getChildren().addAll(addStart, addEnd);

       // DISPLAY WINDOW TO USER. 
       primaryStage.setScene(scene);
       // primaryStage.setResizable(false);
       primaryStage.show();
       
    }



 
   /********************
    * HELPER METHOD(S) *   
    ********************/
    // NOTE THESE METHODS WILL BE USED FOR DEBUGGING 
     
    /* HANDLER FOR THIS CLASS (STAGE). */ 
    public void handle(ActionEvent event){
      // if(event.getSource() == button){
           System.out.println("Button is pressed");
       //}
    }
    
}

