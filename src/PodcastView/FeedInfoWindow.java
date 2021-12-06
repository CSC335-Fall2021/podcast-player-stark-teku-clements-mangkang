package PodcastView;

import java.io.File;

import PodcastEntry.PodcastFeed;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window for displaying info about a podcast feed
 * 
 * @author Michael Stark
 */
public class FeedInfoWindow extends Stage {

	private ImageView imageView;
	private Image img;

	/**
	 * Constructor
	 * 
	 * @param feed The PodcastFeed whose info will be displayed
	 */
	public FeedInfoWindow(PodcastFeed feed) {
		Stage newFeedDialog = new Stage();
		GridPane pane = new GridPane();
		BorderPane win = new BorderPane();

		if (feed.getImageURL().equalsIgnoreCase("")) {
			img = new Image(new File("imgs/placeholder.png").toURI().toString());
		} else {
			img = new Image(feed.getImageURL());
		}
		imageView = new ImageView(img);
		imageView.setFitHeight(250);
		imageView.setFitWidth(250);
		BorderPane.setAlignment(imageView, Pos.CENTER);

		// Title
		Label titleLabel = new Label("Title: ");
		Label feedTitle = new Label(feed.getTitle());
		HBox titleBox = new HBox(10, titleLabel, feedTitle);

		// Feed Link
		Label urlLabel = new Label("URL: ");
		Label feedLink = new Label(feed.getLink());
		HBox linkBox = new HBox(10, urlLabel, feedLink);

		// Description
		Label descLabel = new Label("Description: ");
		TextArea feedDesc = new TextArea(feed.getDescription());
		feedDesc.setWrapText(true);
		VBox descBox = new VBox(10, descLabel, feedDesc);

		// Copyright
		Label copyLabel = new Label("Copyright: ");
		Label feedCopy = new Label(feed.getCopyright());
		HBox copyBox = new HBox(10, copyLabel, feedCopy);

		// Language
		Label langLabel = new Label("Language: ");
		Label feedLang = new Label(feed.getLanguage());
		HBox langBox = new HBox(10, langLabel, feedLang);

		VBox infoBox = new VBox(10, titleBox, linkBox);
		infoBox.setAlignment(Pos.CENTER);

		// Close Button
		Button closeBtn = new Button("Close");
		closeBtn.setDefaultButton(true);
		BorderPane.setAlignment(closeBtn, Pos.BOTTOM_RIGHT);

		closeBtn.setOnAction((click) -> {
			newFeedDialog.close();
		});

		// Assemble our window
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(20));
		pane.add(titleBox, 0, 0);
		pane.add(linkBox, 0, 1);
		pane.add(descBox, 0, 2);
		pane.add(copyBox, 0, 3);
		pane.add(langBox, 0, 4);

		win.setPadding(new Insets(20));
		win.setTop(imageView);
		win.setCenter(pane);
		win.setBottom(closeBtn);

		Scene newScene = new Scene(win, 500, 550);
		newFeedDialog.setResizable(false);
		newFeedDialog.setScene(newScene);
		newFeedDialog.initModality(Modality.APPLICATION_MODAL);
		newFeedDialog.setTitle("Feed Info");
		newFeedDialog.showAndWait();
	}

}
