package PodcastView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import PodcastController.PodcastController;
import PodcastEntry.PodcastEpisode;
import PodcastEntry.PodcastFeed;
import PodcastModel.PodcastModel;
import PodcastModel.PlaylistUpdate;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class PodcastView extends Application implements Observer {

	private BorderPane obj;
	private MediaPlayer option;
	private PodcastController controller;
	private ListView<PodcastEpisode> podcastList;

	@Override
	public void start(Stage arg0) throws Exception {
		PodcastModel model = new PodcastModel();
		controller = new PodcastController(model);
		model.addObserver(this);

		createView();

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
		obj.setStyle("-fx-background-color:#00FF7F; -fx-opacity:1;");

		// Create a list of available podcasts
		podcastList = new ListView<PodcastEpisode>();

		// TODO: We use a hardcoded feed for now, need to make it user definable
		controller.addPodcastFeed("https://podcastfeeds.nbcnews.com/HL4TzgYC");

		Slider timeSlider = new Slider();
		timeSlider.setMinWidth(200);

		VBox player = new VBox(timeSlider, podcastList);

		Button playButton = new Button("Play");
		Button pauseButton = new Button("Pause");
		Button nextTrack = new Button("Next Track");
		Button previousTrack = new Button("Previous Track");

		Label label = new Label("                             Welcome to our Podcast Player");

		label.setFont(new Font("Helvetica", 30));

		label.setAlignment(Pos.TOP_CENTER);

		obj.setTop(label);

		obj.setCenter(player);

		HBox buttonBar = new HBox(20, previousTrack, playButton, pauseButton, nextTrack);
		buttonBar.setAlignment(Pos.CENTER);
		obj.setBottom(buttonBar);

		previousTrack.setOnMouseClicked((click) -> {
			// TO BE IMPLEMENTED Kyle or Tinnawit

		});

		playButton.setOnMouseClicked((click) -> {
			// Stop current playback
			if (option != null) {
				option.stop();
			}

			// Load and play our new file
			Media currentMedia = new Media(podcastList.getSelectionModel().getSelectedItem().getMediaURL());
			option = new MediaPlayer(currentMedia);
			option.setAutoPlay(false);
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
		// New set of episodes for a feed
		if (arg.getClass() == PlaylistUpdate.class) {
			PlaylistUpdate playlistChange = (PlaylistUpdate) arg;

			updatePlaylist(playlistChange.getPodcastFeed());
		}

	}

	// TODO: This is harcoded to nuke and reload the list fresh. It also assumes a
	// single playlist and will need to be modified if we want to support multiple
	// feeds
	private void updatePlaylist(PodcastFeed feed) {
		ArrayList<PodcastEpisode> episodes = feed.getEpisodes();
		podcastList.getItems().clear();

		for (PodcastEpisode e : episodes) {
			podcastList.getItems().add(e);
		}
	}

}
