package PodcastView;

import java.io.FileNotFoundException;


import java.io.IOException;
import java.net.MalformedURLException;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("deprecation")
public class PodcastView extends Application implements Observer {

	private BorderPane obj;
	private MediaPlayer option;
	private Media selected;
	private PodcastController controller;
	private TableView<PodcastEpisode> podcastList;
	private Label headerLabel;
	private ChoiceBox<PodcastFeed> feedSelector;
	private Slider timeSlider;
	private Duration duration;
	private Label curTimeLabel;
	private Label totalTimeLabel;
	private boolean isTempPaused;
	private Slider volumeBar;
	// True if the track is changed and false if current track is being manipulated
	private boolean isTrackNew; 

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
		TableColumn<PodcastEpisode, String> downloadedCol = new TableColumn<PodcastEpisode, String>("Downloaded");
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
		
		Button playPauseButton = new Button("Play");

		// Event handler for when podcast episode is double clicked
		podcastList.setOnMouseClicked((event) -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
				playPauseButton.setText("Pause");
			}
		});

		createSlider();
		
		Pane timeLabelsSpace = new Pane();
		HBox.setHgrow(timeLabelsSpace, Priority.ALWAYS);
		
		HBox timeLabelHBox = new HBox(curTimeLabel, timeLabelsSpace, totalTimeLabel);

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

		VBox player = new VBox(10, timeSlider, timeLabelHBox, feedSelectorBox, podcastList);
		player.setPadding(new Insets(10, 10, 10, 10));

		Button nextTrack = new Button("Next Track");
		Button previousTrack = new Button("Previous Track");
		Button download = new Button ("Download");
		
		
		headerLabel = new Label("Welcome to our Podcast Player");
		headerLabel.setFont(Font.font("Helvetica",FontWeight.EXTRA_BOLD, 30));
		BorderPane.setAlignment(headerLabel, Pos.CENTER);

		obj.setTop(headerLabel);

		obj.setCenter(player);
        
		volumeBar = new Slider();
		HBox buttonBar = new HBox(20, previousTrack, playPauseButton, nextTrack, download,volumeBar);
		buttonBar.setAlignment(Pos.CENTER);
		obj.setBottom(buttonBar);

		previousTrack.setOnMouseClicked((click) -> {
			int numberOfEpisodes = podcastList.getItems().size();
			int nextInd = (podcastList.getSelectionModel().getSelectedIndex() - 1) % numberOfEpisodes;
			podcastList.getSelectionModel().select(nextInd);
			controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
			playPauseButton.setText("Pause");
		});

		playPauseButton.setOnMouseClicked((click) -> {
			 
			if (isTrackNew == true) {
				try {
					controller.playEpisode(podcastList.getSelectionModel().getSelectedItem());
					playPauseButton.setText("Pause");
				}
				catch (NullPointerException e){
					showErrorMessage("Select a podcast before you play!");
				}
				
			}
			else {
				if (option.getStatus() == Status.PLAYING) {
					option.pause();
					playPauseButton.setText("Play");
					
				}
				else {
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
		
		download.setOnMouseClicked( (click) -> {
			   
		      try {
		    	  String url = podcastList.getSelectionModel().getSelectedItem().getMediaURL();
				  String name = podcastList.getSelectionModel().getSelectedItem().getTitle();
				  DownloadEpisode obj = new DownloadEpisode(url,name);
				
				
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error!");
				alert.setContentText("Download is complete!");
				alert.showAndWait(); 
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	private void createSlider() {
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
				timeSlider.setDisable(true);

				option.play();
				
				option.setAutoPlay(false);
				
				timeSlider.setValue(0);
				totalTimeLabel.setText("0:00");
				
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
				
				initPlayerSliderListeners();
			}
		}
		

	}
	
	private void initPlayerSliderListeners() {
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
	    	timeSlider.setMax(newDuration.toSeconds());
			duration = newDuration;
	    	totalTimeLabel.setText((getTimeStr(duration)));
		});
		
		option.setOnEndOfMedia(() -> {
			option.stop();
		});
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
