package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RunObject {

	private Stage primaryStage;
	private BorderPane root;
	private Text errorTexte;
	private StringBuilder messageErreur;
	private ObservableList<String> listOfObject;//contain the name of available object
	private ComboBox<String> comboBoxOfObject1;//choisir l'objet1
	private ComboBox<String> comboBoxOfObject2;//choisir l'objet2
	private ComboBox<String> comboBoxOfMethod1;//choisir methode 1
	private ComboBox<String> comboBoxOfMethod2;//choisir methode 2
	private Button resultat1;
	private Button resultat2;
	private TextArea screen;
	private StringBuilder consoleMessage;
	public RunObject(Stage owner){
	  primaryStage = new Stage();
	  primaryStage.initModality(Modality.APPLICATION_MODAL);
	  primaryStage.initOwner(owner);// Specifies the owner Window (parent) for new window
		
	  consoleMessage = new StringBuilder();
	  errorTexte = new Text("Run Available Object");
	  screen = new TextArea(">...");
	  screen.setEditable(false);
	  
	  listOfObject = getListOfObject();
	  
	  comboBoxOfObject1 = new ComboBox<String>();
	    comboBoxOfObject1.setStyle("-fx-min-width:100");
	  comboBoxOfObject2 = new ComboBox<String>();
	    comboBoxOfObject2.setStyle("-fx-min-width:100");
	  comboBoxOfMethod1 = new ComboBox<String>();
	     comboBoxOfMethod1.getItems().addAll("-- methode --","toString","hashCode");
	  comboBoxOfMethod2 = new ComboBox<String>();
	     comboBoxOfMethod2.getItems().addAll("-- methode --","equals");
	     
	  resultat1 = new Button("run");
	    resultat1.setOnAction(new EventHandler<ActionEvent>(){
	        public void handle(ActionEvent e){
	          if(verifier1()){
	        	consoleMessage.setLength(0);
	        	consoleMessage.append(screen.getText());
	        	consoleMessage.append("\n> appuyer sur le bouton resultat1");
	    	    screen.setText(new String(consoleMessage));
	          }
	          else{
	            Alert;
	          }
	        }
	     }); 
	  resultat2 = new Button("run");
	   resultat2.setOnAction(new EventHandler<ActionEvent>(){
	        public void handle(ActionEvent e){
	          if(verifier2()){
	        	consoleMessage.setLength(0);
	        	consoleMessage.append(screen.getText());
	        	consoleMessage.append("\n> appuyer sur le bouton resultat2");
	    	    screen.setText(new String(consoleMessage));
	          }
	          else{
	        	Alert;
	          }
	        }
	     }); 
	     
	  root = new BorderPane();
	  root.setTop(getTopPane());
      root.setCenter(getCenterPane());
      root.setBottom(getBottomPane());
        
	  Scene scene = new Scene(root,480,500);
	  primaryStage.setTitle("Run Object");// Set the stage title
	  primaryStage.setScene(scene); // Place the scene in the stage
	  primaryStage.setResizable(false);
	  primaryStage.show();// Display the stage
	}
	
	private VBox getTopPane(){
	  VBox vbox = new VBox();
		vbox.setPadding(new Insets(10));
	  Text texte = new Text(15,15,"Run Object");
	    texte.setFont(Font.font (null,FontWeight.BOLD,18));
	    VBox.setMargin(errorTexte,new Insets(0, 5, 10, 15));
	  vbox.getChildren().addAll(texte,errorTexte,new Separator());
	  return vbox;
	}
	
	private VBox getCenterPane() {
	  VBox vbox = new VBox();
	    vbox.setPadding(new Insets(10));
	    vbox.setSpacing(5);
	    VBox.setMargin(screen,new Insets(-13,3,3,3));
	  HBox hbox1 = new HBox();
	    hbox1.setPadding(new Insets(10));
	    hbox1.setSpacing(5);
	    hbox1.getChildren().addAll(new Label("Object 1: "),
	    		                     comboBoxOfObject1,
	    		                       new Separator(Orientation.VERTICAL),
	    		                         new Label("method: "),
	    		                           comboBoxOfMethod1,
	    		                             new Separator(Orientation.VERTICAL),
	    		                               resultat1);
	  HBox hbox2 = new HBox();
	    hbox2.setPadding(new Insets(10));
	    hbox2.setSpacing(5);
	    hbox2.getChildren().addAll(new Label("Object 2: "),
	    		                     comboBoxOfObject2,
	    		                       new Separator(Orientation.VERTICAL),
	    		                         new Label("method: "),
	    		                          comboBoxOfMethod2,
	    		                             new Separator(Orientation.VERTICAL),
	    		                               resultat2);
	 vbox.getChildren().addAll(screen,new Separator(),hbox1,new Separator(),hbox2);
     return vbox;
	}
	
	private VBox getBottomPane(){
	  VBox vbox = new VBox();
		 vbox.setPadding(new Insets(10)); 
	  Button close = new Button("Close");
         //finish.setDisable(true);//disabled tant que le formulaire n'est pas valide
         close.setOnAction(new EventHandler<ActionEvent>(){
	        public void handle(ActionEvent e){
	    	   primaryStage.close(); // return to main window
	        }
	     }); 
      HBox hbox = new HBox(20);
         hbox.setPadding(new Insets(5));
         hbox.setAlignment(Pos.TOP_RIGHT);
         hbox.getChildren().add(close);        
      vbox.getChildren().addAll(new Separator(), hbox);  
      return vbox;
    }
	
	public ObservableList<String> getListOfObject(){
      ObservableList<String> liste = FXCollections.observableArrayList();
		
	  return liste;
	}
	/*
	public ObservableList<String> getListOfObject1Method(){
	  ObservableList<String> liste = FXCollections.observableArrayList();
		
	  return liste;
	}
	public ObservableList<String> getListOfObject2Method(){
	  ObservableList<String> liste = FXCollections.observableArrayList();
		
	  return liste;
	}
	*/
}
