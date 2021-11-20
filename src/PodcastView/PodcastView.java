package PodcastView;


 
import java.util.Observable;

import java.util.Observer;

import PodcastController.PodcastController;
import PodcastModel.PodcastModel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PodcastView extends Application implements Observer {
    
	private BorderPane obj;
	private MediaPlayer option;
	private String URL;
	
	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
		createView();
		
		
		PodcastModel model = new PodcastModel();
		PodcastController controller = new PodcastController(model);
		model.addObserver(this);
		Scene display = new Scene(obj);
		Stage stage = new Stage();
		stage.setScene(display);
		stage.setTitle("Podcast Player");
		stage.show();
		 
	}
	
	public void createView() {
		
		obj = new BorderPane();
		obj.setMinSize(900, 400);
		obj.setMaxHeight(900);
		obj.setPadding(new Insets(8, 8, 8, 8));
		obj.setStyle("-fx-background-color:#00FF7F; -fx-opacity:1;" );
		 
		
	 
		 
		
		Button playButton = new Button("Play");
		Button pauseButton = new Button("Pause");
		Button nextTrack = new Button("Next Track");
		Button previousTrack = new Button("Previous Track");
		
		
		Label label = new Label("                             Welcome to our Podcast Player");
		 
		
		label.setFont( new Font("Helvetica",30));	
	
		label.setAlignment(Pos.TOP_CENTER);
		

		
	     obj.setTop(label);
		 
		
		
		
		 HBox buttonBar = new HBox( 20, previousTrack,playButton, pauseButton, nextTrack );
		 buttonBar.setAlignment(Pos.CENTER);
		 obj.setBottom(buttonBar);
		
		 previousTrack.setOnMouseClicked((click) -> {
				// TO BE IMPLEMENTED Kyle or Tinnawit
			
			 
			}); 
		 
		playButton.setOnMouseClicked((click) -> {
			// TO BE IMPLEMENTED 
			option.play();
		});
			
		pauseButton.setOnMouseClicked((click) -> {
			// TO BE IMPLEMENTED 
			option.pause();
		}); 
		
		
		nextTrack.setOnMouseClicked((click) -> {
			// TO BE IMPLEMENTED Kyle or Tinnawit
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
