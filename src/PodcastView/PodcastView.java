package PodcastView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import PodcastController.PodcastController;
import PodcastEntry.PodcastEpisode;
import PodcastEntry.PodcastFeed;
import PodcastModel.PodcastModel;
import PodcastModel.DownloadEpisode;
import PodcastModel.PlayUpdate;
import PodcastModel.PlaylistUpdate;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("deprecation")
public class PodcastView extends Application implements Observer {

	private BorderPane obj;
	private MediaPlayer option;
	private PodcastController controller;
	private TableView<PodcastEpisode> podcastList;
	private Label headerLabel;
	private ChoiceBox<PodcastFeed> feedSelector;
	private Slider timeSlider;
	private Button removeFeedBtn;
	private Button feedInfoBtn;
	private Label curTimeLabel;
	private Label totalTimeLabel;
	private Label playbackRateLabel;
	private boolean isTempPaused;
	private Slider volumeBar;
	// True if the track is changed and false if current track is being manipulated
	private boolean isTrackNew;
	private Button playPauseButton;
	private Slider playbackRateBar;
	private ImageView image;
	private static final int minWindowWidth = 900;
	private static final int minWindowHeight = 400;

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

		// Initially set track to new
		isTrackNew = true;

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
		stage.setMinWidth(minWindowWidth);
		stage.setMinHeight(minWindowHeight);
		stage.setResizable(false);
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
		obj.setMinSize(minWindowWidth, minWindowHeight);
		obj.setPadding(new Insets(15));
		obj.setStyle("-fx-background-color:#00FF7F; -fx-opacity:1;");

		// Create the list of Podcast Episodes
		podcastList = new TableView<PodcastEpisode>();
		TableColumn<PodcastEpisode, String> titleCol = new TableColumn<PodcastEpisode, String>("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory<PodcastEpisode, String>("title"));
		titleCol.setMinWidth(650);
		TableColumn<PodcastEpisode, String> listenedCol = new TableColumn<PodcastEpisode, String>("Listened");
		TableColumn<PodcastEpisode, String> downloadedCol = new TableColumn<PodcastEpisode, String>("Downloaded");
		downloadedCol.setMinWidth(10);
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
		podcastList.getColumns().add(downloadedCol);

		playPauseButton = new Button("Play");

		// Event handler for when podcast episode is double clicked
		podcastList.setOnMouseClicked((event) -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
				playPauseButton.setText("Pause");
			}
		});

		createProgressBar();

		Pane timeLabelsSpace = new Pane();
		HBox.setHgrow(timeLabelsSpace, Priority.ALWAYS);

		HBox timeLabelHBox = new HBox(curTimeLabel, timeLabelsSpace, totalTimeLabel);

		// Podcast Feed Selector
		Label feedSelectorLabel = new Label("Podcast: ");
		feedSelector = new ChoiceBox<PodcastFeed>();
		feedSelector.setMinWidth(200);

		// Podcast Feed Removal Button
		removeFeedBtn = new Button("X");
		removeFeedBtn.setTooltip(new Tooltip("Remove podcast feed"));
		removeFeedBtn.setOnMouseClicked((click) -> {
			controller.removePodcastFeed(feedSelector.getSelectionModel().getSelectedItem());
		});

		// Podcast Feed Info Button
		feedInfoBtn = new Button("?");
		feedInfoBtn.setTooltip(new Tooltip("Podcast Feed Info"));
		feedInfoBtn.setOnMouseClicked((click) -> {
			if (feedSelector.getSelectionModel().getSelectedItem() != null) {
				new FeedInfoWindow(feedSelector.getSelectionModel().getSelectedItem());
			}
		});

		image = new ImageView();
		image.setFitWidth(75);
		image.setFitHeight(75);

		HBox feedSelectorBox = new HBox(10, feedSelectorLabel, feedSelector, feedInfoBtn, removeFeedBtn, image);
		feedSelectorBox.setAlignment(Pos.CENTER);

		// Event handler
		feedSelector.setOnAction((click) -> {
			if (feedSelector.getSelectionModel().getSelectedItem() == null) {
				image.setImage(null);
			} else {
				image.setImage(new Image(feedSelector.getSelectionModel().getSelectedItem().getImageURL()));
			}
			changePlaylist(feedSelector.getSelectionModel().getSelectedItem());
		});

		VBox player = new VBox(10, timeSlider, timeLabelHBox, feedSelectorBox, podcastList);
		player.setPadding(new Insets(10, 10, 10, 10));
		player.setMaxHeight(minWindowHeight*4);
		VBox.setVgrow(player, Priority.ALWAYS);

		Button nextTrack = new Button("Next Track");
		Button previousTrack = new Button("Previous Track");
		Button download = new Button("Download");

		// Favorite Button
		Button favoriteBtn = new Button("<3");
		favoriteBtn.setTooltip(new Tooltip("Favorite Episode"));
		favoriteBtn.setOnMouseClicked((click) -> {
			if (podcastList.getSelectionModel().getSelectedItem() != null) {
				boolean alreadyAdded = false;
				ArrayList<PodcastFeed> feeds = controller.getPodcastFeeds();
				for (PodcastFeed feed : feeds) {
					if (feed.getURL().equals("favorite")) {
						if (feed.getEpisodes().contains(podcastList.getSelectionModel().getSelectedItem())) {
							showErrorMessage("You have already favorited this episode! :)");
							alreadyAdded = true;
							break;
						}
					}
				}
				if (!alreadyAdded) {
					controller.addFavorite(podcastList.getSelectionModel().getSelectedItem());
				}

			} else {
				showErrorMessage("Select a podcast to favorite!");
			}
		});

		// Rewind 30s Button
		Button rewind30Btn = new Button("<< 30s");
		rewind30Btn.setOnMouseClicked((click) -> {
			if (option != null) {
				option.seek(option.getCurrentTime().subtract(Duration.seconds(30)));
			} else {
				showErrorMessage("Start listening before you rewind! :)");
			}
		});

		// Skip 30s Button
		Button skip30Btn = new Button(">> 30s");
		skip30Btn.setOnMouseClicked((click) -> {
			if (option != null) {
				option.seek(option.getCurrentTime().add(Duration.seconds(30)));
			} else {
				showErrorMessage("Start listening before you skip ahead! :)");
			}
		});

		headerLabel = new Label("Welcome to our Podcast Player");
		headerLabel.setFont(Font.font("Helvetica", FontWeight.EXTRA_BOLD, 30));
		BorderPane.setAlignment(headerLabel, Pos.CENTER);

		obj.setTop(headerLabel);

		obj.setCenter(player);

		createPlaybackRateBar();

		volumeBar = new Slider();
		HBox buttonBar = new HBox(20, previousTrack, rewind30Btn, playPauseButton, skip30Btn, nextTrack, favoriteBtn,
				download, volumeBar);
		buttonBar.getChildren().addAll(playbackRateBar, playbackRateLabel);
		buttonBar.setAlignment(Pos.CENTER);
		obj.setBottom(buttonBar);

		previousTrack.setOnMouseClicked((click) -> {
			try {
				int numberOfEpisodes = podcastList.getItems().size();
				int nextInd = (podcastList.getSelectionModel().getSelectedIndex() - 1) % numberOfEpisodes;
				podcastList.getSelectionModel().select(nextInd);
				controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
				playPauseButton.setText("Pause");
			} catch (NullPointerException e) {
				showErrorMessage("Start listening before you seek previous tracks.");
			}

		});

		playPauseButton.setOnMouseClicked((click) -> {

			if (isTrackNew == true) {
				try {
					controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
					playPauseButton.setText("Pause");
				} catch (NullPointerException e) {
					showErrorMessage("Select a podcast before you play!");
				}

			} else {
				if (option.getStatus() == Status.PLAYING) {
					option.pause();
					playPauseButton.setText("Play");

				} else {
					option.play();
					playPauseButton.setText("Pause");
				}
			}

		});

		nextTrack.setOnMouseClicked((click) -> {
			int numberOfEpisodes = podcastList.getItems().size();
			int nextInd = (podcastList.getSelectionModel().getSelectedIndex() + 1) % numberOfEpisodes;
			podcastList.getSelectionModel().select(nextInd);
			controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
			playPauseButton.setText("Pause");
		});

		download.setOnMouseClicked((click) -> {

			try {
				String url = podcastList.getSelectionModel().getSelectedItem().getMediaURL();
				String name = podcastList.getSelectionModel().getSelectedItem().getTitle();
				DownloadEpisode obj = new DownloadEpisode(url, name);

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error!");
				alert.setContentText("Download is complete!");
				alert.showAndWait();

			} catch (NullPointerException e) {
				showErrorMessage("Select a podcast to download!");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Initializes the progress bar and its event listeners. The first listener
	 * detects if the user clicks at a different position at least 0.5 seconds away
	 * from the current position and seeks to the clicked position if that's the
	 * case. The second listener pauses the episode if the progress bar is currently
	 * being dragged and also updates the displayed current timestamp as it is being
	 * dragged. The third listener seeks the episode to the new position and resumes
	 * the episode when the progress bar is released. The third listener doesn't
	 * resume the episode if it wasn't playing while it was being dragged. Lastly,
	 * the method displays two timestamp labels: one for the current timestamp of
	 * the current episode and one for the total duration of it.
	 */
	private void createProgressBar() {
		timeSlider = new Slider();
		timeSlider.setMinWidth(200);
		timeSlider.setPadding(new Insets(10, 0, 0, 0));
		timeSlider.setDisable(true);

		timeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
			double currentSecs = oldValue.doubleValue();
			double clickedSecs = newValue.doubleValue();
			// So that the progress bar's movement doesn't scrub the episode.
			if (Math.abs(currentSecs - clickedSecs) > 0.5) {
				option.seek(Duration.seconds(newValue.doubleValue()));
			}
		});

		timeSlider.setOnMouseDragged((event) -> {
			Status playerStatus = option.getStatus();
			if (playerStatus.equals(Status.PLAYING) || playerStatus.equals(Status.STOPPED)) {
				option.pause();
				isTempPaused = true;
			}
			Duration draggedTime = Duration.seconds(timeSlider.getValue());
			curTimeLabel.setText(getTimeStr(draggedTime));
		});

		timeSlider.setOnMouseReleased((event) -> {
			option.seek(Duration.seconds(timeSlider.getValue()));
			if (isTempPaused) {
				option.play();
				isTempPaused = false;
			}
		});

		curTimeLabel = new Label("0:00");
		totalTimeLabel = new Label("0:00");
	}

	/**
	 * Initializes the playback rate bar and the playback rate label. Sets the
	 * minimum playback rate to 0.5, the maximum rate to 2, and the default rate to
	 * 1. The playback can only be changed in increments of 0.1 and so the playback
	 * rate bar positions at the nearest lower decimal with only one decimal place.
	 */
	private void createPlaybackRateBar() {
		playbackRateBar = new Slider();
		playbackRateBar.setMin(0.5);
		playbackRateBar.setMax(2);
		playbackRateBar.setValue(1);
		playbackRateBar.setDisable(true);

		playbackRateLabel = new Label("1.0x");

		playbackRateBar.valueProperty().addListener((obs, oldValue, newValue) -> {
			double newPlaybackRate = Math.floor(newValue.doubleValue() * 10.0) / 10.0;
			playbackRateBar.setValue(newPlaybackRate);
			option.setRate(newPlaybackRate);
			playbackRateLabel.setText(String.format("%.1fx", newPlaybackRate));
		});
	}

	/**
	 * Returns a String of a given Duration object representing a timestamp. The
	 * returned String is formatted as "H:M:S" if the timestamp is at least an hour
	 * long, or "M:S" if not. "H" represents the number of hours of the timestamp,
	 * "M" represents the number of minutes of the timestamp, and "S" represents the
	 * number of seconds of the timestamp. In both the "H:M:S" and the "M:S" format,
	 * if the number of seconds of the timestamp is less than 10, there'll be a "0"
	 * to the left of the "S" in both formats. In the "H:M:S" format, if the number
	 * of minutes of the timestamp is less than 10, there'll be a "0" to the left of
	 * the "M" in that format.
	 * 
	 * @param time a Duration object representing a given timestamp
	 * @return a String that represents a timestamp and has the format "H:M:S" if
	 *         the timestamp is at least an hour long, or "M:S" if not.
	 */
	private String getTimeStr(Duration time) {
		int minutes = (int) time.toMinutes();
		int seconds = (int) (time.toSeconds() % 60);

		String timeStr = String.format("%d:%02d", minutes, seconds);
		if (minutes >= 60) {
			timeStr = String.format("%d:%02d:%02d", minutes / 60, minutes % 60, seconds);
		}
		return timeStr;
	}

	@Override
	public void update(Observable o, Object arg) {
		isTrackNew = false;
		// New set of episodes for a feed
		if (arg.getClass() == PlaylistUpdate.class) {
			PlaylistUpdate playlistChange = (PlaylistUpdate) arg;

			if (playlistChange.removalRequest()) {
				removePlaylist(playlistChange.getPodcastFeed());
			} else {
				addPlaylist(playlistChange.getPodcastFeed());
			}
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
				timeSlider.setDisable(true);
				playbackRateBar.setDisable(true);

				option.play();

				option.setAutoPlay(false);

				timeSlider.setValue(0);
				totalTimeLabel.setText("0:00");
				playbackRateBar.setValue(1);

				volumeBar.setValue(option.getVolume() * 100);
				volumeBar.valueProperty().addListener(new InvalidationListener() {

					@Override
					public void invalidated(javafx.beans.Observable arg0) {
						// TODO Auto-generated method stub
						option.setVolume(volumeBar.getValue() / 100);
					}

				});
				option.setOnPlaying(() -> {
					headerLabel.setText(playEpisode.getEpisode().getTitle());
					podcastList.refresh();
				});

				option.setOnError(() -> {
					showErrorMessage("An unexpected error was encountered when playing the selected podcast.");
				});

				option.setOnEndOfMedia(() -> {
					stopEpisode();
				});

				option.setOnStalled(() -> {
					stopEpisode();
				});

				updateProgBarEpisodeListeners();
			}
		}
	}

	/*
	 * Stops the when the media player is stalled or is at the end of the episode,
	 * resets the progress bar back to the start, and renames the play/pause button
	 * to play.
	 */
	private void stopEpisode() {
		option.stop();
		timeSlider.setValue(0);
		playPauseButton.setText("Play");
	}

	/**
	 * Reinitializes the media player's event listeners every time the current
	 * episode of the media player changes. The first event listener changes the
	 * displayed current timestamp of the episode and the progress bar's position,
	 * as the current episode is playing. Once the current episode is ready to be
	 * played, the second event listener enables the progress bar, sets the maximum
	 * value of the progress bar to the duration of the current episode, and resets
	 * the displayed duration timestamp to the new current episode's duration.
	 */
	private void updateProgBarEpisodeListeners() {
		option.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
			Platform.runLater(() -> {
				if (!timeSlider.isDisabled() && !timeSlider.isValueChanging()) {
					timeSlider.setValue(newTime.toSeconds());
				}
				curTimeLabel.setText((getTimeStr(newTime)));
			});
		});

		option.totalDurationProperty().addListener((obs, oldDuration, newDuration) -> {
			timeSlider.setDisable(false);
			playbackRateBar.setDisable(false);
			timeSlider.setMax(newDuration.toSeconds());
			totalTimeLabel.setText((getTimeStr(newDuration)));
		});
	}

	/**
	 * Adds a PodcastFeed to the choice box
	 * 
	 * @param feed The PodcastFeed to add to the list
	 */
	private void addPlaylist(PodcastFeed feed) {
		if (!feedSelector.getItems().contains(feed)) {
			if (feed.getURL().equalsIgnoreCase("favorite")) {
				feedSelector.getItems().add(0, feed);
			} else {
				feedSelector.getItems().addAll(feed);
			}

			if (feedSelector.getSelectionModel().getSelectedItem() == null) {
				feedSelector.getSelectionModel().select(feed);
				changePlaylist(feed);
			}
		}

		updateFeedButtonEnables();
	}

	/**
	 * Removes a Podcast feed from the choice box
	 * 
	 * @param feed
	 */
	private void removePlaylist(PodcastFeed feed) {
		if (feedSelector.getItems().contains(feed)) {
			feedSelector.getItems().remove(feed);
			if (feedSelector.getSelectionModel().getSelectedItem() == feed && feedSelector.getItems().size() > 0) {
				feedSelector.getSelectionModel().select(0);
			}
		}

		updateFeedButtonEnables();
	}

	/**
	 * Enables or disables the feed buttons to match feed selector state
	 */
	private void updateFeedButtonEnables() {
		removeFeedBtn.setDisable(feedSelector.getItems().isEmpty());
		feedInfoBtn.setDisable(feedSelector.getItems().isEmpty());
	}

	/**
	 * Clears the podcastList and loads it with all of the episodes in feed
	 * 
	 * @param feed The PodcastFeed to display
	 */
	private void changePlaylist(PodcastFeed feed) {
		podcastList.getItems().clear();
		if (feed != null) {
			podcastList.getItems().addAll(feed.getEpisodes());
		}
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