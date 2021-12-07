package PodcastView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import PodcastController.PodcastController;
import PodcastModel.DownloadEpisode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class AddMP3Window extends Stage {

	private MediaPlayer option;
	private Media currentMedia;
	private MediaView video;
	private String URL;
	private GridPane grid;
	private GridPane pane;
	private Stage stage;
	public AddMP3Window(PodcastController controller) {
		 
		Stage newStage = new Stage();
		grid = new GridPane();
		
	    grid.setHgap(10);
		grid.setVgap(20);
		grid.setPadding(new Insets(20));
		
		
		TextField mp3Text = new TextField();
		Label label = new Label("Enter MP3 URL: ");
		Button cancelButton = new Button("Cancel");
		Button playButton = new Button("Play");
		
		mp3Text.setMinWidth(310);
		
		HBox mp3Box = new HBox(10, label, mp3Text);
		mp3Box.setAlignment(Pos.CENTER);
		HBox buttonBox = new HBox(10, cancelButton, playButton);
		buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
		
		
		
		grid.add(mp3Box, 0, 0);
		grid.add(buttonBox, 0, 1);
		 
	    
		
		playButton.setOnAction((click) -> {
			
			   stage = new Stage();
			   pane = new GridPane();
			   pane.setMinSize(900, 400);
				pane.setMaxHeight(900);
				
				URL = mp3Text.getText();
	      
				 /*
				    try {
						new DownloadEpisode(URL,"newDownload");
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
					try {
						currentMedia = new Media(new File(URL).toURI().toURL().toString());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					option = new MediaPlayer(currentMedia);
					//option.setAutoPlay(true);
					option.setOnReady(() -> stage.sizeToScene());
					 
					 
					 
					 
					//pane.getChildren().addAll(option);
					option.play();
					Scene newScene = new Scene(pane);
				
					 
					stage.setScene(newScene);
					stage.show();
					 
			 
				 
			 
			 
		});
		
		Scene scene = new Scene(grid);
		newStage.setScene(scene);
		
		newStage.show();
	}
}
