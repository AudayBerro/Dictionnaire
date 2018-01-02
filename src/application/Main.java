package application;
	
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private NewClass newCLassGUI;
	private CreateObject createObjectGui;
	private List<String> objectNameList;//contient la liste des noms des objects crees pour ne pas avoir deux noms identiques
	public void start(Stage primaryStage) {
	  try {
		 objectNameList = new ArrayList<String>();
		//create MenuBar
		MenuBar menu = new MenuBar();
		 //create Menu
		 Menu fileMenu = new Menu("File");
		 Menu dicoMenu = new Menu("Dictionnaire");
		   SeparatorMenuItem sep1 = new SeparatorMenuItem(); 
		   
		  //create MenuItems for fileMenu
		  MenuItem newClassItem = new MenuItem("New Class");
		       newClassItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
		       newClassItem.setOnAction(new EventHandler<ActionEvent>(){
			     public void handle(ActionEvent e){
			       //open a new class GUI
			    	 newCLassGUI = new NewClass(primaryStage);
			     }
			   });
		  MenuItem newObjectItem = new MenuItem("New Object");
		       newObjectItem.setOnAction(new EventHandler<ActionEvent>(){
		    	 public void handle(ActionEvent e){
		    		//open a new object GUI
		    		 createObjectGui = new CreateObject(primaryStage,objectNameList);
		    	 }
		       });
		  MenuItem runObjectItem = new MenuItem("Run Object");
		  	   runObjectItem.setOnAction(new EventHandler<ActionEvent>(){
		    	 public void handle(ActionEvent e){
		    		//open a run object GUI
		    	 }
		       });
		  MenuItem exitItem = new MenuItem("Exit");
		     //accelerator and event when user click
		     exitItem.setOnAction(new EventHandler<ActionEvent>(){
		    	public void handle(ActionEvent e){
		    		System.exit(0);
		    	}
		     }); 
		     exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		  //create MenuItems for dicoMenu
		  
		    fileMenu.getItems().addAll(newClassItem,newObjectItem,runObjectItem,sep1,exitItem);
		  menu.getMenus().addAll(fileMenu,dicoMenu);
			
			BorderPane root = new BorderPane();
			root.setTop(menu);
			Scene scene = new Scene(root,620,610);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Generic Dictionnary Application");
			primaryStage.show();
	  } 
	  catch(Exception e) {
		e.printStackTrace();
	  }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
