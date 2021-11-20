
import java.util.ArrayList;

import PodcastEntry.*;
import PodcastModel.PodcastModel;
import PodcastController.PodcastController;

public class PodcastLauncher {

	public PodcastLauncher() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<PodcastFeed> feeds;
		
		PodcastModel m = new PodcastModel();
		PodcastController myController = new PodcastController(m);
		myController.addPodcastFeed("https://podcastfeeds.nbcnews.com/HL4TzgYC");
		feeds = myController.getPodcastFeeds();
		
		for (PodcastFeed f : feeds) {
			System.out.println("**** FEED ****");
			System.out.println("Title: " + f.getTitle());
			System.out.println("Feed URL: " + f.getURL());
			System.out.println("Description: " + f.getDescription());
			System.out.println("Copyright: " + f.getCopyright());
			System.out.println("Language: " + f.getLanguage());
			System.out.println("Image: " + f.getImageURL());
			System.out.println("Link: " + f.getLink());
			System.out.println();
			System.out.println();
			
			ArrayList<PodcastEpisode> episodes = f.getEpisodes();
			for (PodcastEpisode e: episodes) {
				System.out.println("---- Episode ----");
				System.out.println("Title: " + e.getTitle());
				System.out.println("Description: " + e.getDescription());
				System.out.println("Media: " + e.getMediaURL());
				System.out.println();
				System.out.println();
			}
		}

	}

}
