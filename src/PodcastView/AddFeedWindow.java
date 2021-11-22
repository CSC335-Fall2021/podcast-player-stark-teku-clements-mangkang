package PodcastView;

import PodcastController.PodcastController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A Modal dialog box for the user to add an RSS feed to the app
 * 
 * @author Michael Stark
 */
public class AddFeedWindow extends Stage {

	/**
	 * Constructor
	 * 
	 * @param controller The controller for the main view
	 */
	public AddFeedWindow(PodcastController controller) {
		Stage newFeedDialog = new Stage();
		GridPane pane = new GridPane();

		pane.setHgap(10);
		pane.setVgap(20);
		pane.setPadding(new Insets(20));

		Label feedLabel = new Label("Podcast RSS Feed URL: ");
		TextField feedURLField = new TextField();
		feedURLField.setMinWidth(310);
		Button addBtn = new Button("Add");
		addBtn.setDefaultButton(true);
		Button cancelBtn = new Button("Cancel");

		HBox urlBox = new HBox(10, feedLabel, feedURLField);
		urlBox.setAlignment(Pos.CENTER);
		HBox buttonBox = new HBox(10, cancelBtn, addBtn);
		buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

		pane.add(urlBox, 0, 0);
		pane.add(buttonBox, 0, 1);

		addBtn.setOnAction((click) -> {
			controller.addPodcastFeed(feedURLField.getText());
			newFeedDialog.close();
		});

		cancelBtn.setOnAction((click) -> {
			newFeedDialog.close();
		});

		Scene newScene = new Scene(pane, 500, 110);
		newFeedDialog.setResizable(false);
		newFeedDialog.setScene(newScene);
		newFeedDialog.initModality(Modality.APPLICATION_MODAL);
		newFeedDialog.setTitle("Add Podcast RSS Feed");
		newFeedDialog.showAndWait();
	}

}
