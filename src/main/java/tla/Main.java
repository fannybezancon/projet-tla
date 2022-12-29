package tla;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

    	ArrayList<String> files_niveaux = new ArrayList<>();
    	File[] files = new File("src/main/resources/niveaux").listFiles();
    	for (File file : files) {
    	    if (file.isFile()) {
    	        files_niveaux.add(new String(Main.class.getResourceAsStream("/niveaux/" + file.getName()).readAllBytes()));
    	    }
    	}
  
    	//test analyse lexicale
    	System.out.println("debut du test d'analyse lexicale");
		try {
			for(String i: files_niveaux) {
				List<Token> tokens = new AnalyseLexicale().analyse(i);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println();
		
		//test analyse syntaxique + récupérer le noeud racine donné par l'analyse syntaxique de chaque niveau
		ArrayList<Noeud> noeuds_racine = new ArrayList<>();
		System.out.println("test analyse syntaxique");
		try {
			for(String i: files_niveaux) {
				List<Token> tokens = new AnalyseLexicale().analyse(i);
				Noeud racine = new AnalyseSyntaxique().analyse(tokens);
				noeuds_racine.add(racine);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println();		
		
		
        // fenêtre principale et panneau de menu
        GridPane menuPane = new GridPane();
        ArrayList<Button> btnsNiveaux = new ArrayList<>();
        for (int i = 0; i < noeuds_racine.size(); i++) {
        	Button btnNiveau = new Button("niveau "+(i+1));
        	btnsNiveaux.add(btnNiveau);
        	menuPane.add(btnNiveau, 0, i);
        }
        ImageView imageView = new ImageView(LibrairieImages.imgJoueurGrand);
        menuPane.add(imageView, 1, 0, 1, 5);

        Scene scene = new Scene(menuPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        // panneau racine du jeu

        BorderPane borderPane = new BorderPane();

        Plateau plateau = new Plateau(borderPane);
        
        /*for (int i = 0; i < btnsNiveaux.size(); i++) {
    		int finalI = i;      	
        	btnsNiveaux.get(i).setOnAction(event -> {
        
        	// affiche le panneau racine du jeu (à la place du panneau de menu)
        	scene.setRoot(borderPane);

        	// affecte un object correspondant au niveau choisi
        	plateau.setNiveau(new Interpreteur.interpreter(noeuds_racine.get(finalI)));
        	
        	// démarre le jeu
        	plateau.start();

        	// ajuste la taille de la fenêtre
        	primaryStage.sizeToScene();
        	});
        }*/

        // gestion du clavier

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.Q) {
                // touche q : quitte le jeu et affiche le menu principal
                plateau.stop();
                scene.setRoot(menuPane);
                primaryStage.sizeToScene();
            }
            if (event.getCode() == KeyCode.R) {
                // touche r : redémarre le niveau en cours
                plateau.start();
            }

            if (event.getCode() == KeyCode.LEFT) {
                plateau.deplGauche();
            }
            if (event.getCode() == KeyCode.RIGHT) {
                plateau.deplDroite();
            }
            if (event.getCode() == KeyCode.UP) {
                plateau.deplHaut();
            }
            if (event.getCode() == KeyCode.DOWN) {
                plateau.deplBas();
            }
        });
    }
}
