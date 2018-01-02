package application;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateObject {

	private BorderPane root;
	private Text errorTexte;
	private Button finish;
	private TextField newObjectName;
	private Stage primaryStage;
	private ComboBox<String> comboBoxObjectCLass;//liste de classes qu'on peut instancier
	private ObservableList<String> availableClassList;//liste de classes qu'on peut instancier
	private StringBuilder messageErreur; //contient la liste des erreur trouver par la methode verifierFormulaire()
	private List<String> temporaryObjectNameList;
	private List<String> dynamicObjectFieldTypeList;//contient la liste des types de l'objet a instancier ex:int-string-int-int
	private List<String> dynamicObjectFieldNameList;//contient le nom de chaque champs qu l'utilisateur a choisi
	private List<TextField>  dynamicObjectFieldValueList;//contient les valeurs entrees par le user pour chaque champ
	
	
	//contient la liste de toutes les classes java presentes dans le repertoir courant
	public  CreateObject(Stage owner,List<String> objectNameList){
		primaryStage = new Stage();
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.initOwner(owner);// Specifies the owner Window (parent) for new window
		
		//les 3 listes seront remplies par la fonction dynamicClassCall()
		dynamicObjectFieldTypeList=new ArrayList<String>();
		dynamicObjectFieldNameList=new ArrayList<String>();
		dynamicObjectFieldValueList=new ArrayList<TextField>();
		
		temporaryObjectNameList = new ArrayList<String>();
		  //si la liste des noms n'est pas vide on ajoute tout les noms
		  if(!(objectNameList.isEmpty()))
		    temporaryObjectNameList.addAll(objectNameList);
		  
		comboBoxObjectCLass = new ComboBox<String>();
		availableClassList=this.getAvailableCLass();
		
		messageErreur = new StringBuilder();
		errorTexte = new Text("Create New Object");
		newObjectName = new TextField();
		  //show error message if the class name exist else show: Create New Java Class
		  newObjectName.textProperty().addListener((observable,oldvalue,newvalue)->{
			  if(objectNameList.contains(newvalue))
				  errorTexte.setText("Object name \""+newObjectName.getText()+"\" already exist");
			  else if(newObjectName.getText().contains(" "))
				  errorTexte.setText("Invalid object name, must not start or end with a blank");
			  else errorTexte.setText("Create New Object");
			  
		  });
		 
		root = new BorderPane();
		root.setTop(getTopPane());
        root.setCenter(getCenterPane());
        
         finish = new Button("finish");
	     //finish.setDisable(true);//disabled tant que le formulaire n'est pas valide
	     finish.setOnAction(new EventHandler<ActionEvent>(){
		      public void handle(ActionEvent e){
		    	  if(verifierFormulaire()){
		    		 try {
						dynamicClassCall();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}//afin de remplir les 3 listes
		    		// createNewObject(newObjectName.getText());//cette fonction prendra les parametres des trois listes remplie par dynamicClassCall()
		    		 
		    		 //ajouter le nouveau nom a la liste de nom
		    		  objectNameList.clear();
		    		  objectNameList.addAll(temporaryObjectNameList);
		    		 //ajouter le nouvel objet au pool
		    	     //primaryStage.close(); // return to main window
		    	  }
		    	  else{
		    		  Alert alert = new Alert(AlertType.ERROR);
		    		   alert.setTitle("Error dialog");
		    		   if(newObjectName.getText().trim().isEmpty())
		    		     alert.setHeaderText("Enter object name");
		    		   else  alert.setHeaderText("Cannot create \""+newObjectName.getText()+"\" ");
		    		   String s = new String(messageErreur);
		    		   alert.setContentText(s);
		    		   alert.show();     
		    	  }
		      }
		 }); 
        root.setBottom(getBottomPane(finish));
        
		Scene scene = new Scene(root,480,600);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Create New Object");
		primaryStage.show();
	}
	
  private VBox getTopPane(){
	  VBox vbox = new VBox();
	  vbox.setPadding(new Insets(10));
	  vbox.setSpacing(5);
    Text texte = new Text(15,15,"Create New Object");
      texte.setFont(Font.font (null,FontWeight.BOLD,18));
      VBox.setMargin(errorTexte,new Insets(0, 5, 10, 15));
      vbox.getChildren().addAll(texte,errorTexte,new Separator());
	return vbox;
  }
	
  private VBox getCenterPane() {
	  VBox vbox = new VBox();
	    vbox.setSpacing(10);
	    vbox.setPadding(new Insets(0,10,10,10));
	  VBox panelAttribut = new VBox();
	  HBox hbox = new HBox();
	    hbox.setSpacing(10);
	  Label nom = new Label("Nom: ");
	  Label type = new Label("Type: ");
		
	  //ajouter la liste des classes presentes au combobox
	  comboBoxObjectCLass.setItems(availableClassList);
	  comboBoxObjectCLass.getSelectionModel().select(0);
	  
	  hbox.getChildren().addAll(nom,newObjectName,type,comboBoxObjectCLass);
	  
	  
	  //ajouter les champs d'instances de la classe choisie: type + input - = input
	  //dynamicClassCall();
	   comboBoxObjectCLass.setOnAction(new EventHandler<ActionEvent>(){
		   public void handle(ActionEvent e){
			   if(comboBoxObjectCLass.getSelectionModel().getSelectedIndex()!=0){
				   dynamicObjectFieldNameList.clear();  
				   dynamicObjectFieldTypeList.clear();
				   dynamicObjectFieldValueList.clear();
				   panelAttribut.getChildren().clear(); //supprimer l'anncienne forme
				   panelAttribut.getChildren().add(getAttributField());
			   }
			   else{ 
				   panelAttribut.getChildren().clear();
				   dynamicObjectFieldNameList.clear();  
				   dynamicObjectFieldTypeList.clear();
				   dynamicObjectFieldValueList.clear();
			   }
		       }
	   });
	   vbox.getChildren().addAll(hbox,new Separator(),panelAttribut);
	return vbox;
  }
  
  private VBox getBottomPane(Button finish){
	VBox vbox = new VBox();
	  vbox.setPadding(new Insets(10));
	        
	
	Button cancel = new Button("cancel");
	   cancel.setOnAction(new EventHandler<ActionEvent>(){
	      public void handle(ActionEvent e){
	    	  primaryStage.close(); // return to main window
	      }
	   }); 
	HBox hbox = new HBox(20);
	  hbox.setPadding(new Insets(5));
	  hbox.setAlignment(Pos.TOP_RIGHT);
	        
	  hbox.getChildren().addAll(cancel,finish);        
	  vbox.getChildren().addAll(new Separator(), hbox); 
	return vbox;
  }
  
  //liste de classes qu'on peut instancier
  public ObservableList<String> getAvailableCLass(){
	  ObservableList<String> list = FXCollections.observableArrayList();
      list.add("-- Choisir une classe --");

      File[] files = new File("./bin/fichierCreer").listFiles();
      for (File file : files) {
          if (file.isFile()) {
        	  list.add(file.getName().replace(".class",""));
        	  System.out.println(">> -"+file.getName().replace(".class","")+"-");
          }
      }
      return list;
  }
  
  //si l'objet est valide par la fonction verifier formulaire on ajoute son nom a la liste des nom
  public void addObjectName(String name){
	  
	  temporaryObjectNameList.add(name);
  }
  
  public VBox getAttributField(){
	VBox root = new VBox();
	//cette fonction doit remplir les listes suivantes et doit faire des appel dynamiques selon la classe choisi
     //...dynamicObjectFieldTypeList
	 //...dynamicObjectFieldNameList
	 //seul dynamicObjectFieldValueList est rempli par l'utilisateur
     //il faut verifier dans la methode verifier formulaire que comboBoxObjectCLass.getSelectionModel().getSelectedItem() != 0
	 Class maClasse;
	 Class superClasse;
	try {
		maClasse = Class.forName("fichierCreer."+comboBoxObjectCLass.getSelectionModel().getSelectedItem());
		superClasse=maClasse.getSuperclass();
	 Field[] champs = maClasse.getDeclaredFields();
	 dynamicObjectFieldNameList.clear();  
	 dynamicObjectFieldTypeList.clear();
	 dynamicObjectFieldValueList.clear();
	 if(superClasse!=null){
		 Field[] champsSuper=superClasse.getDeclaredFields();
		 for(Field f : champsSuper){
				dynamicObjectFieldValueList.add(new TextField());
			  dynamicObjectFieldNameList.add(f.getName());
			  if(f.getType().getName()=="java.lang.String")
				dynamicObjectFieldTypeList.add("String");
			  else dynamicObjectFieldTypeList.add(f.getType().getName());
			}
	 }
		for(Field f : champs){
			dynamicObjectFieldValueList.add(new TextField());
		  dynamicObjectFieldNameList.add(f.getName());
		  if(f.getType().getName()=="java.lang.String")
			dynamicObjectFieldTypeList.add("String");
		  else dynamicObjectFieldTypeList.add(f.getType().getName());
		}
     if(!dynamicObjectFieldNameList.isEmpty())
	   for(int i=0;i<dynamicObjectFieldNameList.size();i++){
		   root.getChildren()
		       .add(getField(dynamicObjectFieldNameList.get(i),
				             dynamicObjectFieldTypeList.get(i),
				             dynamicObjectFieldValueList.get(i)
                  ));
	   }
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return root;
  }
  //Type| variableName | variableNameinput = variableValueInput
  public HBox getField(String fieldName,String fieldType,TextField valueTextField){
	  HBox root = new HBox();
	    root.setPadding(new Insets(10));
	    root.setSpacing(2);
	    HBox.setMargin(valueTextField,new Insets(0, 0, 0, 15));
      Label label1 = new Label(fieldType);
      	label1.setStyle("-fx-min-width:80;-fx-font-size:15");
      	label1.setContentDisplay(ContentDisplay.RIGHT);
      Label label2 = new  Label(fieldName);
        //-fx-border-color: green; -fx-border-width: 2;
      	label2.setStyle("-fx-min-width:100;-fx-font-size:15");
      	label2.setContentDisplay(ContentDisplay.LEFT); 
      Label  label3 = new  Label(" = ");
        label3.setContentDisplay(ContentDisplay.CENTER);
        label3.setStyle("-fx-font-size:15");
     // Place nodes in the pane
        root.getChildren()
              .addAll(label1,
            		  label2,
            		  new Separator(Orientation.VERTICAL),
            		  label3,
            		  new Separator(Orientation.VERTICAL),
        		      valueTextField);
	  return root;
  }
  public void dynamicClassCall() throws ClassNotFoundException{
	 //cette fonction appel le constructeur et initialise les variables en utilisant les listes suivantes
	//...dynamicObjectFieldTypeList ====> rempli par getAttributField()
	//...dynamicObjectFieldNameList ====> rempli par getAttributField()
	//...dynamicObjectFieldValueList ====> valeur entree par l'utilisateur
	 // Class c = Class.forName("org.wikibooks.fr.Livre"); // Accès à la classe Livre
     //Constructor constr = c.getConstructor(String.class, int.class); // Obtenir le constructeur (String, int)
     //Object o = constr.newInstance("Programmation Java", 120); // -> new Livre("Programmation Java", 120);

	Class maClasse= Class.forName("fichierCreer."+comboBoxObjectCLass.getSelectionModel().getSelectedItem());
	Constructor[] constructeur = maClasse.getConstructors();
	for(Constructor cts:constructeur)
		System.out.println(">>> constructeur: "+cts.toString()+" - cts.Name: "+cts.getName());
    Object objet = null;
    
    	try {
    		Object[] listeDesValeur= new Object[dynamicObjectFieldValueList.size()];
    		if(!dynamicObjectFieldValueList.isEmpty()){
    			for(int i=0;i<dynamicObjectFieldValueList.size();i++){
    			  if(dynamicObjectFieldTypeList.get(i)=="String"){
    				listeDesValeur[i]=dynamicObjectFieldValueList.get(i).getText();
    			  }
    			  else{
    				if(dynamicObjectFieldTypeList.get(i)=="int")
    				  listeDesValeur[i]=Integer.valueOf(dynamicObjectFieldValueList.get(i).getText());//convertir string en int
    				else 
    				  listeDesValeur[i]=Float.valueOf(dynamicObjectFieldValueList.get(i).getText());//convertir string en float
    			  } 
    			}
    			System.out.println("%%%%% taille: "+listeDesValeur.length);
    			for(int k=0;k<listeDesValeur.length;k++)
    				System.out.println("%%%%%: "+listeDesValeur[k].getClass()+" - toString: "+listeDesValeur[k].toString());
    			objet=constructeur[0].newInstance(listeDesValeur);
    		}
    		else{//si le constructeur n'a pas de parametre
    			objet=constructeur[0].newInstance();
    		}
    	}
    	catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e){
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	System.out.println(">>> objet: "+objet.toString());
  }
  public boolean verifierFormulaire(){
	  System.out.println(">>> avant delete: "+messageErreur+" Isempty: "+dynamicObjectFieldNameList.isEmpty());
	messageErreur.setLength(0);//supprimer les anciennes valeurs
	System.out.println(">>> apres delete: "+messageErreur);
	//verifier si on a entrer un nom
	if(newObjectName.getText().trim().isEmpty())
	  messageErreur.append("\n- Enter an Object Name");
    
	  
	//verifier si le nom entrer existe
	if(temporaryObjectNameList.contains(newObjectName.getText()))
	  messageErreur.append("\n- Object name \""+newObjectName.getText()+"\" already exist");
	
	  
	//si le nom contient des espaces
	if(newObjectName.getText().contains(" "))
	  messageErreur.append("\n- Object name not valid. The name must not start or end with a blank");
	
	//verifier qu'on a choisit une classe
	if(comboBoxObjectCLass.getSelectionModel().getSelectedIndex()== 0)
	  messageErreur.append("\n- Select a Class type for your Object");
	
	//verifier qu'on a entree une valeur pour chaque champ
	if(!(dynamicObjectFieldNameList.isEmpty()))
	  for(int i=0;i<dynamicObjectFieldNameList.size();i++)
		if(dynamicObjectFieldValueList.get(i).getText().trim().isEmpty())
	      messageErreur.append("\n- Enter a value for variable \""+dynamicObjectFieldNameList.get(i)+"\"");
		
	
	if(messageErreur.length()==0) 
		return true;
    else return false;
  }
}