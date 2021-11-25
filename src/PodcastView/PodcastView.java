package PodcastView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import PodcastController.PodcastController;
import PodcastEntry.PodcastEpisode;
import PodcastEntry.PodcastFeed;
import PodcastModel.PodcastModel;
import PodcastModel.PlayUpdate;
import PodcastModel.PlaylistUpdate;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class PodcastView extends Application implements Observer {

	private BorderPane obj;
	private MediaPlayer option;
	private Media selected;
	private PodcastController controller;
	private TableView<PodcastEpisode> podcastList;
	private Label headerLabel;
	private ChoiceBox<PodcastFeed> feedSelector;
	private Slider volumeBar;

	@Override
	public void start(Stage arg0) throws Exception {
		PodcastModel model = new PodcastModel();
		controller = new PodcastController(model);
		model.addObserver(this);

		createView();

		// Combine our Menu and our Main view
		VBox root = new VBox(createMenu());
		root.getChildren().add(obj);

		// Load saved feeds
		controller.loadFeeds();

		// Show the UI
		Scene display = new Scene(root);
		Stage stage = new Stage();
		// Save our feed list on quit
		stage.setOnCloseRequest((event) -> {
			try {
				controller.saveFeeds();
			} catch (IOException e) {
				showErrorMessage("Error saving podcast information: " + e);
			}
		});
		stage.setScene(display);
		stage.setTitle("Podcast Player");
		stage.show();

	}

	/**
	 * Creates our MenuBar
	 * 
	 * @return
	 */
	private MenuBar createMenu() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem addFeedMenu = new MenuItem("Add Podcast RSS Feed...");
		fileMenu.getItems().add(addFeedMenu);
		menuBar.getMenus().add(fileMenu);

		// Event handler for menu items
		addFeedMenu.setOnAction((click) -> {
			new AddFeedWindow(controller);
		});

		return menuBar;
	}

	private void createView() {

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
		TableColumn<PodcastEpisode, String> listenedCol = new TableColumn<PodcastEpisode, String>("Listened");
		listenedCol.setCellValueFactory(cellData -> {
			cellData.getTableColumn().setStyle("-fx-alignment: CENTER;");
			if (cellData.getValue().getListenedTo()) {
				return new SimpleStringProperty("X");
			} else {
				return new SimpleStringProperty("");
			}
		});
		listenedCol.setMinWidth(10);
		TableColumn<PodcastEpisode, String> publishDateCol = new TableColumn<PodcastEpisode, String>("Date Published");
		publishDateCol.setCellValueFactory(new PropertyValueFactory<PodcastEpisode, String>("publishDate"));
		publishDateCol.setMinWidth(100);
		TableColumn<PodcastEpisode, String> durationCol = new TableColumn<PodcastEpisode, String>("Duration");
		durationCol.setCellValueFactory(new PropertyValueFactory<PodcastEpisode, String>("duration"));
		durationCol.setMinWidth(90);
		podcastList.getColumns().add(titleCol);
		podcastList.getColumns().add(listenedCol);
		podcastList.getColumns().add(publishDateCol);
		podcastList.getColumns().add(durationCol);

		// Event handler for when podcast episode is double clicked
		podcastList.setOnMouseClicked((event) -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
			}
		});

		Slider timeSlider = new Slider();
		timeSlider.setMinWidth(200);
		timeSlider.setPadding(new Insets(10, 0, 10, 0));

		// Podcast Feed Selector
		Label feedSelectorLabel = new Label("Podcast: ");
		MenuBar menuBar = new MenuBar();
		feedSelector = new ChoiceBox<PodcastFeed>();
		HBox feedSelectorBox = new HBox(10, feedSelectorLabel, feedSelector);
		feedSelectorBox.setAlignment(Pos.CENTER);

		// Event handler
		feedSelector.setOnAction((click) -> {
			changePlaylist(feedSelector.getSelectionModel().getSelectedItem());
		});

		VBox player = new VBox(10, timeSlider, feedSelectorBox, podcastList);
		player.setPadding(new Insets(10, 10, 10, 10));

		Button playButton = new Button("Play");
		Button pauseButton = new Button("Pause");
		Button nextTrack = new Button("Next Track");
		Button previousTrack = new Button("Previous Track");
		Button download = new Button ("Download");
		
		
		headerLabel = new Label("Welcome to our Podcast Player");
		headerLabel.setFont(Font.font("Helvetica",FontWeight.EXTRA_BOLD, 30));
		BorderPane.setAlignment(headerLabel, Pos.CENTER);

		obj.setTop(headerLabel);

		obj.setCenter(player);
        
		
		 
		
		volumeBar = new Slider();
		HBox buttonBar = new HBox(20, previousTrack, playButton, pauseButton, nextTrack, download,volumeBar);
		buttonBar.setAlignment(Pos.CENTER);
		obj.setBottom(buttonBar);

		previousTrack.setOnMouseClicked((click) -> {
			int numberOfEpisodes = podcastList.getItems().size();
			int nextInd = (podcastList.getSelectionModel().getSelectedIndex() - 1) % numberOfEpisodes;
			podcastList.getSelectionModel().select(nextInd);
			controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
		});

		playButton.setOnMouseClicked((click) -> {
			 
			controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
		});

		pauseButton.setOnMouseClicked((click) -> {
		 
			if (option.getStatus() == Status.PLAYING) {
				option.pause();
			} else {
				option.play();
			}
		});

		nextTrack.setOnMouseClicked((click) -> {
			int numberOfEpisodes = podcastList.getItems().size();
			int nextInd = (podcastList.getSelectionModel().getSelectedIndex() + 1) % numberOfEpisodes;
			podcastList.getSelectionModel().select(nextInd);
			controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
		});
		
		download.setOnMouseClicked( (click) -> {
	      
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
				headerLabel.setText("Loading: " + playEpisode.getEpisode().getTitle());
				option = new MediaPlayer(currentMedia);
				volumeBar.setValue(option.getVolume() * 100);
				volumeBar.valueProperty().addListener(new InvalidationListener()  {

					@Override
					public void invalidated(javafx.beans.Observable arg0) {
						// TODO Auto-generated method stub
						option.setVolume(volumeBar.getValue()/100);
					}
					
				});
				option.setOnPlaying(() -> {
					headerLabel.setText(playEpisode.getEpisode().getTitle());
					podcastList.refresh();
				});

				option.setOnError(() -> {
					showErrorMessage("An unexpected error was encountered when playing the selected podcast.");
				});
				option.setAutoPlay(false);
				option.play();
			}
		}
		

	}

	/**
	 * Adds a PodcastFeed to the choice box TODO: Make this smarter so it doesn't
	 * add duplicates
	 * 
	 * @param feed The PodcastFeed to add to the list
	 */
	private void updatePlaylist(PodcastFeed feed) {
		feedSelector.getItems().addAll(feed);
		if (feedSelector.getSelectionModel().getSelectedItem() == null) {
			feedSelector.getSelectionModel().select(feed);
			changePlaylist(feed);
		}
	}

	/**
	 * Clears the podcastList and loads it with all of the episodes in feed
	 * 
	 * @param feed The PodcastFeed to display
	 */
	private void changePlaylist(PodcastFeed feed) {
		podcastList.getItems().clear();
		podcastList.getItems().addAll(feed.getEpisodes());
	}

	/**
	 * Shows a generic error when for when bad things happen
	 * 
	 * @param msg The message to display to the user
	 */
	private void showErrorMessage(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error!");
		alert.setContentText(msg);
		alert.showAndWait();
	}

}
