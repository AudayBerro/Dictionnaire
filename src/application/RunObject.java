package application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
	private boolean poolPlein=false;//sert a savoir s'il y a des objets dans le pool 
	
	public RunObject(Stage owner, HashMap<String,Integer> objectNameList, HashMap<Integer, Object> pool){
	  primaryStage = new Stage();
	  primaryStage.initModality(Modality.APPLICATION_MODAL);
	  primaryStage.initOwner(owner);// Specifies the owner Window (parent) for new window
		
	  messageErreur = new StringBuilder();
	  consoleMessage = new StringBuilder();
	  errorTexte = new Text("Run Available Object");
	  screen = new TextArea(">...");
	  screen.setEditable(false);
	  
	  listOfObject = FXCollections.observableArrayList();
	  comboBoxOfObject1 = new ComboBox<String>();
	    comboBoxOfObject1.setStyle("-fx-min-width:100");
	  comboBoxOfObject2 = new ComboBox<String>();
	    comboBoxOfObject2.setStyle("-fx-min-width:100");
	  //si le pool est plein true sinon false
	  poolPlein=getListOfObject(objectNameList);
	     //remplir comboBoxOfObject1 & comboBoxOfObject2
	  	 comboBoxOfObject1.setItems(listOfObject);
	  	 comboBoxOfObject2.setItems(listOfObject);
	  	 
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
	        	methode1(objectNameList.get( comboBoxOfObject1.getSelectionModel().getSelectedItem()),//send selected object1 signature=key
   	                     pool,
   	                     comboBoxOfMethod1.getSelectionModel().getSelectedItem() 
   	                    );
	        	screen.setText(new String(consoleMessage)); 
	          }
	          else{
	        	  Alert alert = new Alert(AlertType.ERROR);
	    		  alert.setTitle("Error dialog");
	    		  if(comboBoxOfObject1.getSelectionModel().getSelectedIndex()==0)
	    		     alert.setHeaderText("Select an Object from the object1 list");
	    		  else  alert.setHeaderText("Can't  apply metod on \""+comboBoxOfObject1.getSelectionModel().getSelectedItem()+"\" object");
	    		  
	    		  String s = new String(messageErreur);
	    		   alert.setContentText(s);
	    		   alert.show();  
	          }
	        }
	     }); 
	  resultat2 = new Button("run");
	   resultat2.setOnAction(new EventHandler<ActionEvent>(){
	        public void handle(ActionEvent e){
	          if(verifier2()){
	        	consoleMessage.setLength(0);
		        consoleMessage.append(screen.getText());
		        methode2(objectNameList.get( comboBoxOfObject1.getSelectionModel().getSelectedItem()),//send selected object1 signature=key
   	                	 objectNameList.get(comboBoxOfObject2.getSelectionModel().getSelectedItem()),//send selected object2 signature=key
   	                     pool,
   	                     comboBoxOfMethod2.getSelectionModel().getSelectedItem() 
   	                    );
		        screen.setText(new String(consoleMessage)); 
	          }
	          else{
	        	Alert alert = new Alert(AlertType.ERROR);
	    		  alert.setTitle("Error dialog");
	    		if(comboBoxOfObject2.getSelectionModel().getSelectedIndex()==0)
	    		    alert.setHeaderText("Select an Object from the object2 list");
	    		else  alert.setHeaderText("Can't  apply metod on \""+comboBoxOfObject2.getSelectionModel().getSelectedItem()+"\" object");
	    		
	    		String s = new String(messageErreur);
	    		alert.setContentText(s);
	    		alert.show();  
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
         close.setOnAction(new EventHandler<ActionEvent>(){
	        public void handle(ActionEvent e){
	    	   primaryStage.close(); // return to main window
	        }
	     }); 
         
      Button clear = new Button("Clear");   
         clear.setOnAction(new EventHandler<ActionEvent>(){
	        public void handle(ActionEvent e){
	          consoleMessage.setLength(0);
	    	  screen.setText(">...");
	        }
	     }); 
      HBox hbox = new HBox(20);
         hbox.setPadding(new Insets(5));
         hbox.setAlignment(Pos.TOP_RIGHT);
         hbox.getChildren().addAll(clear,close);        
      vbox.getChildren().addAll(new Separator(), hbox);  
      return vbox;
    }
	
	//public ObservableList<String> getListOfObject(HashMap<String, Integer> objectNameList)
	//recupere le nom de chaque objet de la liste des noms rempli par CreateObject
	public boolean getListOfObject(HashMap<String, Integer> objectNameList){
		listOfObject.add("-- Object --");
		if(objectNameList.isEmpty())
			return false;
		else{
		  for(Map.Entry<String,Integer> entry : objectNameList.entrySet())
			listOfObject.add(entry.getKey());
		  return true;
		}
	}
	
	//methode pour objet1: signature=Key(object1)
	public void methode1(int signature,HashMap<Integer, Object> pool,String methode){
		Class classe =(pool.get(signature)).getClass();
		Method[] m = classe.getMethods(); // Obtenir la méthode getNombreDeFeuilles(int)
		for(Method mt:m){
		  if(mt.getName()==methode){
			try {
			  consoleMessage.append("\n> ")
			                  .append(comboBoxOfObject1.getSelectionModel().getSelectedItem())
			                    .append(".")
			                      .append(methode)
			                        .append("(): ")
			                          .append(mt.invoke(pool.get(signature),null));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		}
	  //consoleMessage.append("error on \""+methode+"\"call");
	}
	
	//methode pour objet2: signature1=Key(objet1) & signature2=Key(objet2)
    public void methode2(int signature1,int signature2,HashMap<Integer, Object> pool,String methode){
    	Class classe =(pool.get(signature1)).getClass();
		Method[] m = classe.getMethods(); // Obtenir la méthode getNombreDeFeuilles(int)
		for(Method mt:m)
		  if(mt.getName()==methode){
			try {
			  consoleMessage.append("\n> ")
			                  .append(comboBoxOfObject1.getSelectionModel().getSelectedItem())
			                    .append(".")
			                      .append(methode)
			                        .append("(")
			                          .append(comboBoxOfObject2.getSelectionModel().getSelectedItem())
			                            .append("): ")
			                              .append(mt.invoke(pool.get(signature1),pool.get(signature2)));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		
	}
    
    public boolean verifier1(){
      messageErreur.setLength(0);//supprimer les anciennes valeurs
      if(poolPlein==false){
      	messageErreur.append("The POOl is empty. Create object first!");
      	return false;
      }      
      messageErreur.setLength(0);//supprimer les anciennes valeurs
  	  //verifier si on a entrer un nom
  	  if(comboBoxOfObject1.getSelectionModel().getSelectedIndex()==0)
  		  messageErreur.append("\n- Select Object1");
  	  
  	  //verifier si le nom entrer existe
  	  if(comboBoxOfMethod1.getSelectionModel().getSelectedIndex()==0)
  		  messageErreur.append("\n- Select a method");
  	  
  	  if(messageErreur.length()==0)
  	   return true;
  	  else return false;
    }
    
    public boolean verifier2(){
      messageErreur.setLength(0);//supprimer les anciennes valeurs
      if(poolPlein==false){
    	messageErreur.append("The POOl is empty. Create object first!");
    	return false;
      }
      //verifier si on a entrer un nom
      if(comboBoxOfObject1.getSelectionModel().getSelectedIndex()==0)
    	  messageErreur.append("\n- Select Object1");
    	  
      //verifier si le nom entrer existe
      if(comboBoxOfMethod1.getSelectionModel().getSelectedIndex()==0)
    	  messageErreur.append("\n- Select a method");
    	  
      if(comboBoxOfObject2.getSelectionModel().getSelectedIndex()==0)  
    	  messageErreur.append("\n- Select Object2");
      
      if(comboBoxOfMethod2.getSelectionModel().getSelectedIndex()==0)
    	  messageErreur.append("\n- Select a method");
    	  
      if(messageErreur.length()==0)
    	return true;
      else return false;
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
