package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

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
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewClass{

	private BorderPane root;
	private Text errorTexte;
	private TextField className;
	private CheckBox check;
	private ComboBox<String> comboBox;
	private ComboBox<Integer> nbrAttributs;
	private ObservableList<String> list;
	private ObservableList<Integer> listeDeNombre;
	private ObservableList<String> listeTypeAttribut; 
	private List<ComboBox<String>> comboBoxAttribut; 
	private List<TextField> nomAttributs;
	private List<String> attributsSuperClasse;
	private List<CheckBox> usedInComparable;
	private Stage primaryStage;
	private StringBuilder messageErreur;
	private boolean isComparable=false;//des qu'un checkbox d'attribut est coche le mettre a true
	
	public  NewClass(Stage owner){
		primaryStage = new Stage();
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.initOwner(owner);// Specifies the owner Window (parent) for new window
		
		listeDeNombre = FXCollections.observableArrayList();
		  listeDeNombre.addAll(0,1,2,3,4,5,6,7,8,9);
		listeTypeAttribut = FXCollections.observableArrayList();  
		  listeTypeAttribut.addAll("-- Type --","String","int","float"); 
		list = this.listeDeClasses();//contient la liste de toutes les classes java presentes dans le repertoir courant
		comboBoxAttribut = new ArrayList<ComboBox<String>>();  
		nomAttributs = new ArrayList<TextField>();
		attributsSuperClasse = new ArrayList<String>(); //contiendra les champs de la super class s'il y a heritage
		messageErreur = new StringBuilder();
		errorTexte = new Text("Create New Java Class");
		className = new TextField();
		  //show error message if the class name exist else show: Create New Java Class
		  className.textProperty().addListener((observable,oldvalue,newvalue)->{
			  if(list.contains(newvalue))
				  errorTexte.setText("Class name \""+className.getText()+"\" already exist");
			  else if(className.getText().contains(" "))
				  errorTexte.setText("Invalid Class name, must not start or end with a blank");
			  else errorTexte.setText("Create New Java Class");
			  
		  });
		check = new CheckBox();
		comboBox = new ComboBox<String>();
		  comboBox.setDisable(true); //disable untill checkbox is cheked 
		
		nbrAttributs = new ComboBox<Integer>();
		  nbrAttributs.setPrefWidth(10);
		usedInComparable = new ArrayList<CheckBox>(); 
		root = new BorderPane();
		root.setTop(getTopPane());
        root.setCenter(getCenterPane());
        root.setBottom(getBottomPane());
        
		Scene scene = new Scene(root,420,600);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("New Java Class");
		primaryStage.show();
	}
	
  private VBox getTopPane(){
	VBox vbox = new VBox();
	  vbox.setPadding(new Insets(10));
	  vbox.setSpacing(5);
    Text texte = new Text(15,15,"Java Class");
      texte.setFont(Font.font (null,FontWeight.BOLD,18));
      VBox.setMargin(errorTexte,new Insets(0, 5, 10, 15));
      vbox.getChildren().addAll(texte,errorTexte,new Separator());
	return vbox;
  }
	
  private VBox getCenterPane() {
	  VBox vbox = new VBox();
	  VBox attrBox = new VBox();
    GridPane root = new GridPane();
	  root.setHgap(15);
	  root.setVgap(15);
	  root.setPadding(new Insets(10));
	Label nom = new Label("Nom: ");
	
	root.add(nom,0,0);
	root.add(className,1,0,2,1);
	HBox hbox = new HBox();
	 Label herite = new Label(" extends");
	 hbox.getChildren().addAll(check,herite);
	 //add listener to checkbox
	  check.setOnAction(new EventHandler<ActionEvent>(){
	    	public void handle(ActionEvent e){
	    		if(check.isSelected()){
	    		 System.out.println(check.isSelected());
	    		 comboBox.setDisable(false);
	    		}
	    		else comboBox.setDisable(true);
	    	}
	   }); 
	root.add(hbox,0,1); 
	  //ajouter la liste des classes presentes au combobox
	  comboBox.setItems(list);
      comboBox.getSelectionModel().select(0);
    root.add(comboBox,1,1,2,1);
    
	Label attributs = new Label("Nombre d'attributs: ");
	root.add(attributs, 0,2,2,1);
	  nbrAttributs.setItems(listeDeNombre);
	  nbrAttributs.getSelectionModel().select(0);
	root.add(nbrAttributs,2,2);
	vbox.getChildren().addAll(root,new Separator());
	
   nbrAttributs.setOnAction(new EventHandler<ActionEvent>(){
    	public void handle(ActionEvent e){
    		if(nbrAttributs.getValue()!=0){
    			attrBox.getChildren().clear();
    			attrBox.getChildren().add(getAttributField(nbrAttributs.getValue()));
    			System.out.println("size: "+comboBoxAttribut.size());
    		}
    		else attrBox.getChildren().clear();
    		
    	}
   });
   vbox.getChildren().add(attrBox);
	 System.out.println("valeur: "+nbrAttributs.getValue());
	return vbox;
  }
  
  private VBox getBottomPane(){
	VBox vbox = new VBox();
	  vbox.setPadding(new Insets(10));
	        
	Button finish = new Button("finish");
	     //finish.setDisable(true);//disabled tant que le formulaire n'est pas valide
	     finish.setOnAction(new EventHandler<ActionEvent>(){
		      public void handle(ActionEvent e){
		    	  if(verifierFormulaire()){
		    		 createNewClass(className.getText());
		    	     primaryStage.close(); // return to main window
		    	  }
		    	  else{
		    		  Alert alert = new Alert(AlertType.ERROR);
		    		   alert.setTitle("Error dialog");
		    		   if(className.getText().trim().isEmpty())
		    		     alert.setHeaderText("Enter the class name");
		    		   else  alert.setHeaderText("Cannot create \""+className.getText()+"\" class");
		    		   String s = new String(messageErreur);
		    		   alert.setContentText(s);
		    		   alert.show();     
		    	  }
		      }
		 }); 
	Button cancel = new Button("cancel");
	   cancel.setOnAction(new EventHandler<ActionEvent>(){
	      public void handle(ActionEvent e){
	    	  comboBoxAttribut.clear();  
	    	  nomAttributs.clear();
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
  
  //retourne une liste contenant le nom des classes presentes
  public  ObservableList<String> listeDeClasses() {

      ObservableList<String> list = FXCollections.observableArrayList();
      list.add("-- Choisir une classe --");

      File[] files = new File("./src/fichierCreer").listFiles();
      for (File file : files) {
          if (file.isFile()) {
        	  list.add(file.getName().replace(".java",""));
          }
      }
      return list;
  }
  
  //retourne un champ pour remplir les attributs avec nbr nombre d'attributs a creer
  public VBox getAttributField(int  nbr) {
	  VBox root = new VBox();
	    root.setPadding(new Insets(10));
	    root.setSpacing(10);
	  comboBoxAttribut.clear();  
	  nomAttributs.clear();
	  usedInComparable.clear();
	  int i=0; 
	  for(i=0;i<nbr;i++ ){
		  comboBoxAttribut.add(new ComboBox<String>());
		  nomAttributs.add(new TextField());
		  usedInComparable.add(new CheckBox());
	  }
	  for(i=0;i<nbr;i++){
		  comboBoxAttribut.get(i).setItems(listeTypeAttribut);
		  comboBoxAttribut.get(i).getSelectionModel().select(0);
	  }
	  for(i=0;i<nbr;i++)
		  root.getChildren().add(getField(comboBoxAttribut.get(i),
				                          nomAttributs.get(i),
				                          i,
				                          usedInComparable.get(i)
				                         ));
	  System.out.println("---- checkbox size: "+usedInComparable.size());
	  System.out.println("---- comboAttribut size: "+comboBoxAttribut.size());
	return root;
  }
  
  public HBox getField(ComboBox<String> combo,TextField texte,int numero,CheckBox check){
	  HBox root = new HBox();
	  root.setSpacing(10);
	  numero++;
	  root.getChildren().addAll(new Label(" #"+numero+": "),
			                    texte,new Separator(Orientation.VERTICAL),
			                    new Label(" Type: "),
			                    combo,
			                    new Separator(Orientation.VERTICAL),
			                    check);
	  return root;
  }
  
  public void createNewClass(String nomDeLaClasse){
	  
	  StringBuilder sb = new StringBuilder();
	  //ajouter le packagename
	  sb.append("package fichierCreer;\n\n");
	 
	  //import java.io.Serializable;
	  sb.append("import java.io.Serializable;\n\n");
	  //ajouter le nom de la classe
	  sb.append("public class "+nomDeLaClasse);
	  
	  //ajouter la super classe s'il y a heritage
	  if(check.isSelected())
		  sb.append(" extends "+comboBox.getSelectionModel().getSelectedItem())
		      .append(" implements Serializable")
		        .append("{\n");
	  else sb.append(" implements Serializable").append("{\n");
	  /*
	  if(nbrAttributs.getValue()!=0)
		for(int j=0;j<usedInComparable.size();j++){
			if(usedInComparable.get(j).isSelected()){
	          sb.append(" implements Comparable<"+nomDeLaClasse+">{\n");
	          isComparable=true;
	          break;
			}
		}
	  else sb.append("{\n"); */
	  
	  //ajouter les attributs de la super classe ss'il y a heritage
	  //......
      if(check.isSelected()){
    	  try {
			ajouterAttributSuperClasse(sb);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
	  
	  //ajouter les attributs de la classe
	  if(nbrAttributs.getSelectionModel().getSelectedIndex()!=0)
	     ajouterAttributs(sb);
	  
	  //ajouter le constructeur 
	  ajouterConstructeur(sb,nomDeLaClasse);
	       
	  //initialiser les attributs
	  initialiserAttribut(sb);
	  
	  //ajouter les methodes
	  ajouterToString(sb);
	  ajouterEquals(sb,nomDeLaClasse);
	  ajouterHashCode(sb);
	
	  if(isComparable==true)  ajouterCompareTo(sb);
	  
	  //fin de la classe
	  sb.append("\n}");
	  
	  //creer le fichier
	  File root=null;
	  String source = new String(sb);
	  try {
		//Save source in .java file.  
		 root = new File("./src/fichierCreer/"); //repertoire ou l'on stockera le nouveau fichier java
		 File sourceFile= new File(root,nomDeLaClasse+".java");
		 sourceFile.getParentFile().mkdirs();
		 Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));
		/*
		System.out.println("java home: "+System.getProperty("java.home"));
		System.setProperty("java.home","C:\\Program Files\\Java\\jdk1.8.0_151");
		System.out.println("java home: "+System.getProperty("java.home"));
		System.out.println("current getPAth: "+ sourceFile.getPath().getClass());
		*/
		JavaCompiler compiler1 = ToolProvider.getSystemJavaCompiler();
	    StandardJavaFileManager fileManager = compiler1.getStandardFileManager(null, null, null);
	      fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File("./bin/")));//chemin des fichiers .class
	    // Compile the file
	      boolean success = compiler1.getTask(null, fileManager, null, null, null,
	          fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)))
	          .call();
	      if(success)
	        System.out.println("--- COMPILATION SUCCESS --- ");
	      else System.out.println("--- COMPILATION FAILED --- ");
	      fileManager.close();
	  }catch (IOException e) {
	     // TODO Auto-generated catch block
	     e.printStackTrace();
	  }
  }
  
  //ajouter attributs de la super classe
  public void ajouterAttributSuperClasse(StringBuilder sb) throws ClassNotFoundException{
	  System.out.println("current classe extends: "+comboBox.getSelectionModel().getSelectedItem());
	  //on recupere la super classe
	  Class superClasse= Class.forName("fichierCreer."+comboBox.getSelectionModel().getSelectedItem());
	  Field[] champs = superClasse.getDeclaredFields();
	  System.out.println("\n\n>>>> "+champs.toString());
		System.out.println("Attributs de la classe: "+comboBox.getSelectionModel().getSelectedItem());
		/*
		if(champs.length!=0)
		 for(Field ch : champs){
			 if(ch.getType().getName()=="java.lang.String"){
				 sb.append("\tprivate final String ")
				   .append(ch.getName())
				   .append(";\n");
				 attributsSuperClasse.add("String "+ch.getName());
			 }
			 else{
				 sb.append("\tprivate final ")
			        .append(ch.getType().getName())
			        .append(" ")
			        .append(ch.getName())
			        .append(";");
			     attributsSuperClasse.add(ch.getType().getName()+" "+ch.getName());      
			 }
		 }
		 */
		if(champs.length!=0)
		  for(Field ch : champs){
		    if(ch.getType().getName()=="java.lang.String")
			    attributsSuperClasse.add("String "+ch.getName());
			else attributsSuperClasse.add(ch.getType().getName()+" "+ch.getName());      
		  }
		 sb.append("\n");
  }
  
  //ajouter les attributs de la classe 
  public void ajouterAttributs(StringBuilder sb){
     for(int i=0;i<nbrAttributs.getSelectionModel().getSelectedIndex();i++){
    	 //ajouter: private final type nom ;
		 sb.append("\tprivate final ")
		    	  .append(comboBoxAttribut.get(i).getSelectionModel().getSelectedItem())
		    	  .append(" "+nomAttributs.get(i).getText())
		    	  .append(";\n");
	 }
  }
  
  //ajouter le constructeur
  public void ajouterConstructeur(StringBuilder sb,String nomDeLaClasse){
	  sb.append("\n\tpublic "+nomDeLaClasse+"(");
	//ajouter les attributs de la super classe
	if(check.isSelected() && !attributsSuperClasse.isEmpty())
	   for(int i=0;i<attributsSuperClasse.size();i++)
	     sb.append(attributsSuperClasse.get(i))
	       .append(",");
	
	//ajouter tous les attributs
	for(int i=0;i<nbrAttributs.getSelectionModel().getSelectedIndex();i++)
	   sb.append(comboBoxAttribut.get(i).getSelectionModel().getSelectedItem())
 	     .append(" "+nomAttributs.get(i).getText())
 	     .append(",");

	//supprimer la derniere virgule
    if(nbrAttributs.getSelectionModel().getSelectedIndex()!=0||!attributsSuperClasse.isEmpty())
	  sb.setLength(sb.length()-1);
	sb.append("){\n"); 
  }
  
  public void initialiserAttribut(StringBuilder sb){
	  //appeler super() s'il y a heritage
	  if(check.isSelected() && !attributsSuperClasse.isEmpty()){
		StringBuilder tmp = new StringBuilder();
		int k=0;
		sb.append("\t\t").append("super(");
	    for(int i=0;i<attributsSuperClasse.size();i++){
	      k=attributsSuperClasse.get(i).indexOf(" ");
	      tmp.append(attributsSuperClasse.get(i).substring(k+1,attributsSuperClasse.get(i).length()));
		    sb.append(tmp)
		      .append(",");
		  tmp.setLength(0);
	    }
		//supprimer la derniere virgule
	    sb.setLength(sb.length()-1);
	    sb.append(");\n"); 
	  }
	  
	  for(int i=0;i<nbrAttributs.getSelectionModel().getSelectedIndex();i++){
			 sb.append("\t\t this.")
			   .append(nomAttributs.get(i).getText())
			   .append(" = ")
			   .append(nomAttributs.get(i).getText())
			   .append(";\n");
	  }
	  sb.append("\t}");
  }
  
  public void ajouterToString(StringBuilder sb){
	  sb.append("\n\n\t").append("public String toString(){\n").append("\t\treturn ");
	  if(check.isSelected()){
		sb.append("super.toString()");
	    if(nbrAttributs.getValue()!=0){
	    	sb.append("+\"[");
		  for(int i=0;i<nbrAttributs.getValue();i++){
			 sb.append(nomAttributs.get(i).getText())
			   .append("=\"+")
			   .append(nomAttributs.get(i).getText())
			   .append("+\",");
		  }
		  sb.setLength(sb.length()-1);
		  sb.append("]\";\n\t}");
	    }
	    else sb.append(";\n\t}");
	  }
	  else{
		sb.append("getClass().getName()");
		if(nbrAttributs.getValue()!=0){
		  sb.append("+\"[");
		  for(int i=0;i<nbrAttributs.getValue();i++){
		    sb.append(nomAttributs.get(i).getText())
			  .append("=\"+")
			  .append(nomAttributs.get(i).getText())
			  .append("+\",");
		  }
		  sb.setLength(sb.length()-1);
		  sb.append("]\";\n\t}");
		 }
		else sb.append(";\n\t}");
	  }
  }
  
  public void ajouterHashCode(StringBuilder sb){
	sb.append("\n\n\t")
      .append("public int hashCode(){\n")
      .append("\t\t")
      .append("int resultat=0;")
      .append("\n\t\t");
	
	if(check.isSelected())
		sb.append("resultat=super.hashCode();");
	else  sb.append("resultat=31;");
	  
	if(nbrAttributs.getValue()!=0){
	  for(int i=0;i<nbrAttributs.getValue();i++)
	   if(comboBoxAttribut.get(i).getValue()=="String"){
		  sb.append("\n\t\t")
		    .append("resultat=97*resultat+(")
		    .append("this.")
		    .append(nomAttributs.get(i).getText())
		    .append(" != null ?")
	        .append("this.")
	        .append(nomAttributs.get(i).getText())
	        .append(".hashCode():0);");
	   } 
	}
	sb.append("\n\t\treturn resultat;").append("\n\t}");
  }
  
  public void ajouterEquals(StringBuilder sb,String nomDeLaClasse){
	  sb.append("\n\n\t")
	     .append("public boolean equals(Object otherObject){\n")
	      .append("\t\t");
	  if(check.isSelected()){
		sb.append("if(!super.equals(otherObject)) return false;")
		  .append("\n\t\t")
		  .append("if(otherObject==null) return false;")
		  .append("\n\t\t")
		  .append("if(!(otherObject instanceof "+nomDeLaClasse+")) return false;");
	    if(nbrAttributs.getValue()!=0){
	      sb.append("\n\t\t")
		    .append(nomDeLaClasse)
		    .append(" other=(")
		    .append(nomDeLaClasse)
		    .append(") ")
		    .append("otherObject;")
		    .append("\n\t\t")
		    .append("return ");
		  for(int i=0;i<nbrAttributs.getValue();i++){
			sb.append(nomAttributs.get(i).getText())
			  .append("==")
			  .append("other.")
			  .append(nomAttributs.get(i).getText())
			  .append(" && ");
		  }
		  sb.setLength(sb.length()-4);
		  sb.append(";\n\t}");
	    }
	    else sb.append("\n\t\treturn true;").append("\n\t}");
	  }
	  else{
		  sb.append("if(this==otherObject) return true;")
		  .append("\n\t\t")
		  .append("if(otherObject==null) return false;")
		  .append("\n\t\t")
		  .append("if(!(otherObject instanceof "+nomDeLaClasse+")) return false;");
		if(nbrAttributs.getValue()!=0){
		  sb.append("\n\t\t")
		    .append(nomDeLaClasse)
		    .append(" other=(")
		    .append(nomDeLaClasse)
		    .append(") ")
		    .append("otherObject;")
		    .append("\n\t\t")
		    .append("return ");
		  for(int i=0;i<nbrAttributs.getValue();i++){
			  sb.append(nomAttributs.get(i).getText())
			  .append("==")
			  .append("other.")
			  .append(nomAttributs.get(i).getText())
			  .append(" && ");
		  }
		  sb.setLength(sb.length()-4);
		  sb.append(";\n\t}");
		}
		 else sb.append("\n\t\treturn true;").append("\n\t}");
	  }
  }
  
  public boolean verifierFormulaire(){
	  
	  messageErreur.setLength(0);//supprimer les anciennes valeurs
	  //verifier si on a entrer un nom
	  if(className.getText().trim().isEmpty()){
		  messageErreur.append("\n- Enter a class Name");
	  }
	  
	  //verifier si le nom entrer existe
	  if(list.contains(className.getText())){
		  messageErreur.append("\n- Class name \""+className.getText()+"\" already exist");
	  }
	  
	  //si le nom contient des espaces
	  if(className.getText().contains(" ")){
		  messageErreur.append("\n- Class name is not valid. A Java class name must not start or end with a blank");
	  }
	  //verifier si on extends est cocher et qu'on a pas selectionner une classe mere
	  if(check.isSelected() && comboBox.getSelectionModel().getSelectedIndex()==0){
		  messageErreur.append("\n- Choose a super class");
	  }
	  
	  //verifier si on a choisit des attributs
	  if( nbrAttributs.getSelectionModel().getSelectedIndex()!=0){
		  //verifier le nom et le type de chaque variable
		  for(int i=0;i<nomAttributs.size();i++)
			     if(nomAttributs.get(i).getText().trim().isEmpty())
			    	 messageErreur.append("\n- Enter a name for variable #"+(i+1));
		  for(int i=0;i<comboBoxAttribut.size();i++)
			     if(comboBoxAttribut.get(i).getSelectionModel().getSelectedIndex()==0)
			    	 messageErreur.append("\n- Choose type for variable #"+(i+1)); 
	  }
	  
	  if(messageErreur.length()==0)
	   return true;
	  else return false;
  }
  
  public void ajouterCompareTo(StringBuilder sb){
		 /*sb.append("\n\n\t").append("@Override")
	      .append("\n\tpublic int compareTo(")
	      .append(nomDeLaClasse)
	      .append(" other")
	      .append("){");
		
		for(int i=0;i<attributPourCompareTo.size();i++){
		  sb.append("\n\t\t")
		    .append("if(this.")
		    .append(attributPourCompareTo.get(i))
		    .append("<other.")
	        .append(attributPourCompareTo.get(i))
	        .append(") return -1;")
		    .append("\n\t\t")
		    .append("else if(this.")
		    .append(attributPourCompareTo.get(i))
		    .append(">other.")
	        .append(attributPourCompareTo.get(i))
	        .append(") return 1;")
	        .append("\n\t\t")
	        .append("else{");
		   if((i+1)==attributPourCompareTo.size()){
			 sb.append("\n\t\t ")
			   .append("return true;");
		   }
		 }
		*/
  }
}
