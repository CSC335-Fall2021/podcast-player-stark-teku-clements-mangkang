package PodcastView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import PodcastController.PodcastController;
import PodcastEntry.PodcastEpisode;
import PodcastEntry.PodcastFeed;
import PodcastModel.PodcastModel;
import PodcastModel.PlayUpdate;
import PodcastModel.PlaylistUpdate;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
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
	private TableView<PodcastEpisode> podcastList;

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

		// Create the list of Podcast Episodes
		podcastList = new TableView<PodcastEpisode>();
		TableColumn<PodcastEpisode, String> titleCol = new TableColumn<PodcastEpisode, String>("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory<PodcastEpisode, String>("title"));
		titleCol.setMinWidth(650);
		TableColumn<PodcastEpisode, String> publishDateCol = new TableColumn<PodcastEpisode, String>("Date Published");
		publishDateCol.setCellValueFactory(new PropertyValueFactory<PodcastEpisode, String>("publishDate"));
		publishDateCol.setMinWidth(100);
		TableColumn<PodcastEpisode, String> durationCol = new TableColumn<PodcastEpisode, String>("Duration");
		durationCol.setCellValueFactory(new PropertyValueFactory<PodcastEpisode, String>("duration"));
		durationCol.setMinWidth(100);
		podcastList.getColumns().add(titleCol);
		podcastList.getColumns().add(publishDateCol);
		podcastList.getColumns().add(durationCol);

		// Event handler for when podcast episode is double clicked
		podcastList.setOnMouseClicked((event) -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
			}
		});

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
			controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
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

		if (arg.getClass() == PlayUpdate.class) {
			PlayUpdate playEpisode = (PlayUpdate) arg;

			// Stop current playback
			if (option != null) {
				option.stop();
			}

			// Load and play our new file
			if (playEpisode.getEpisode() != null) {
				Media currentMedia = new Media(playEpisode.getEpisode().getMediaURL());
				option = new MediaPlayer(currentMedia);
				option.setAutoPlay(false);
				option.play();
			}
		}

	}

	// TODO: This is harcoded to nuke and reload the list fresh. It also assumes a
	// single playlist and will need to be modified if we want to support multiple
	// feeds
	private void updatePlaylist(PodcastFeed feed) {
		ArrayList<PodcastEpisode> episodes = feed.getEpisodes();
		podcastList.getItems().clear();

		podcastList.getItems().addAll(episodes);
	}

}
