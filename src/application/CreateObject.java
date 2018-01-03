package application;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
	private List<String> dynamicObjectFieldTypeList;//contient la liste des types de l'objet a instancier ex:int-string-int-int
	private List<String> dynamicObjectFieldNameList;//contient le nom de chaque champs qu l'utilisateur a choisi
	private List<TextField>  dynamicObjectFieldValueList;//contient les valeurs entrees par le user pour chaque champ
	private HashMap<Integer,Object> temporaryPool;
	private HashMap<String,Integer> temporaryObjectNameList;
	
	//objectNameList contiendera la liste de tout les objets qui seront creer
	public  CreateObject(Stage owner,HashMap<String,Integer> objectNameList,HashMap<Integer,Object> pool){
		primaryStage = new Stage();
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.initOwner(owner);// Specifies the owner Window (parent) for new window
		
		//les 3 listes seront remplies par la fonction dynamicClassCall()
		dynamicObjectFieldTypeList=new ArrayList<String>();
		dynamicObjectFieldNameList=new ArrayList<String>();
		dynamicObjectFieldValueList=new ArrayList<TextField>();
		
		temporaryObjectNameList = new HashMap<String,Integer>();
		  //si la liste des noms n'est pas vide on ajoute tout les noms
		  if(!(objectNameList.isEmpty()))
		    temporaryObjectNameList.putAll(objectNameList);
		 
		temporaryPool = new HashMap<Integer,Object>();
		  //si le pool n'est pas vide on ajoute tout sont contenus au pool temporaire
		  if(!(pool.isEmpty()))
		    temporaryPool.putAll(pool);
		  
		comboBoxObjectCLass = new ComboBox<String>();
		availableClassList=this.getAvailableCLass();
		
		messageErreur = new StringBuilder();
		errorTexte = new Text("Create New Object");
		newObjectName = new TextField();
		  //show error message if the class name exist else show: Create New Java Class
		  newObjectName.textProperty().addListener((observable,oldvalue,newvalue)->{
			  if(objectNameList.containsKey(newvalue))
				  errorTexte.setText("Object name \""+newObjectName.getText()+"\" already exist");
			  else if(newObjectName.getText().contains(" "))
				  errorTexte.setText("Invalid object name, must not start or end with a blank");
			  else errorTexte.setText("Create New Object");
			  
		  });
		 
		root = new BorderPane();
		root.setTop(getTopPane());
        root.setCenter(getCenterPane());
        
         finish = new Button("finish");
	     finish.setOnAction(new EventHandler<ActionEvent>(){
		      public void handle(ActionEvent e){
		    	  if(verifierFormulaire()){
		    	    try {
						dynamicClassCall();
		    		    //ajouter le nouveau nom a la liste de nom
		    		    objectNameList.clear();
		    		    objectNameList.putAll(temporaryObjectNameList);
		    		    //ajouter le nouvel objet au pool
		    		    pool.clear();
		    		    pool.putAll(temporaryPool);
		    	        primaryStage.close(); // return to main window
		    		 } 
		    	     catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
				     }
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
      for (File file : files) 
          if (file.isFile()) 
        	  list.add(file.getName().replace(".class",""));
      
      return list;
  }
  
  //si l'objet est valide par la fonction verifier formulaire on ajoute son nom a la liste des nom
  public void addObjectName(String name,int signature){
	  
	  temporaryObjectNameList.put(name,signature);
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
    int signature=-2;
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
    		  
    		  //verifier si l'objet existe dans le pool en fonctin de sa signature
    		  signature=signature(comboBoxObjectCLass.getSelectionModel().getSelectedItem(),dynamicObjectFieldValueList);
    		  if(!temporaryPool.containsKey(signature)){
    			 objet=constructeur[0].newInstance(listeDesValeur);
    			 //ajouter l'objet creer au poole et a la liste
    			 temporaryObjectNameList.put(newObjectName.getText(),signature);
    			 temporaryPool.put(signature, objet);
       		  }
    		  else
    		   //ajouter le nom du nouveau objet a la liste des noms et lui donner comme valeur la signature de l'objet existant
    		   temporaryObjectNameList.put(newObjectName.getText(),signature);
    		} 
    		else{
    		  //si le constructeur n'a pas de parametre
    			//verifier si l'objet existe dans le pool en fonctin de sa signature
      		  signature=signature(comboBoxObjectCLass.getSelectionModel().getSelectedItem(),dynamicObjectFieldValueList);
      		  if(!temporaryPool.containsKey(signature)){
      			objet=constructeur[0].newInstance();
      			 //ajouter l'objet creer au poole et a la liste
      			 temporaryObjectNameList.put(newObjectName.getText(),signature);
      			 temporaryPool.put(signature, objet);
         		  }
      		  else
      		   //ajouter le nom du nouveau objet a la liste des noms et lui donner comme valeur la signature de l'objet existant
      		   temporaryObjectNameList.put(newObjectName.getText(),signature);
    		   
    		}
    	}
    	catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e){
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
  }
  
  //cette fonction est appelee par dynamicClassCall pour savoir s'il faut creer un nouveau objet ou retourner un qui est existant
  //prend en parametres: nom de la classe + valeur de chaque attribut
  public int signature(String classeName,List<TextField> dynamicObjectFieldValueList){
	  
	  int resultat=3,i=0,k=0;
	  int taille=classeName.length();
	  for(i=0;i<taille;i++)
		  resultat=(int) (resultat+classeName.charAt(i)*Math.pow(7, taille-i));
	  if(!dynamicObjectFieldValueList.isEmpty())
        for(i=0;i<dynamicObjectFieldValueList.size();i++){
        	taille=dynamicObjectFieldValueList.get(i).getText().length();
    	  for(k=0;k<taille;k++)
    	    resultat=(int) (resultat+dynamicObjectFieldValueList.get(i).getText().charAt(k)*4);
        }
	  return resultat;
  }
  
  public boolean verifierFormulaire(){
	  System.out.println(">>> avant delete: "+messageErreur+" Isempty: "+dynamicObjectFieldNameList.isEmpty());
	messageErreur.setLength(0);//supprimer les anciennes valeurs
	System.out.println(">>> apres delete: "+messageErreur);
	//verifier si on a entrer un nom
	if(newObjectName.getText().trim().isEmpty())
	  messageErreur.append("\n- Enter an Object Name");
    
	  
	//verifier si le nom entrer existe
	if(temporaryObjectNameList.containsKey(newObjectName.getText()))
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
