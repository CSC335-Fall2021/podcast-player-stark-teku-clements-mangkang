package PodcastController;

import PodcastModel.PodcastModel;
import PodcastEntry.PodcastFeed;
import PodcastEntry.PodcastEpisode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The Controller for the Podcast Player MVC
 */
public class PodcastController {
	private PodcastModel myModel;

	/**
	 * Constructs a new controller using the supplied model
	 * 
	 * @param m The PodcastModel that this controls
	 */
	public PodcastController(PodcastModel m) {
		myModel = m;
	}

	/**
	 * Adds a feed to our list of followed podcasts
	 * 
	 * @param url The URL for the podcast's RSS feed
	 * @return True if added successfully, false if not
	 */
	public boolean addPodcastFeed(String url) {
		return myModel.addFeed(url);
	}
	
	/**
	 * Removes a feed from the list of followed podcasts
	 * 
	 * @param f The PodcastFeed to remove
	 */
	public void removePodcastFeed(PodcastFeed f) {
		myModel.removeFeed(f);
	}

	/**
	 * Gets the current list of followed podcast feeds
	 * 
	 * @return An ArrayList<PodcastFeed> of followed feeds, can be empty if nothing
	 *         is followed
	 */
	public ArrayList<PodcastFeed> getPodcastFeeds() {
		return myModel.getFeeds();
	}

	/**
	 * Sets a specific podcast episode as playing
	 * 
	 * @param p The PodcastEpisode to play
	 */
	public void playEpisode(PodcastEpisode p) {
		myModel.startPlayback(p);
	}

	/**
	 * Loads saved feeds from disk
	 */
	public void loadFeeds() {
		try {
			myModel.loadFeeds();
		} catch (IOException e) {
			// We're just going to eat this exception and not load the feeds if it happens.
		}

		// User doesn't have any saved feeds. Give them some defaults
		if (myModel.getFeeds().size() == 0) {
			addPodcastFeed("https://podcastfeeds.nbcnews.com/HL4TzgYC");
			addPodcastFeed("https://feeds.megaphone.fm/ADL9840290619");
		}
	}

	/**
	 * Saves our podcast feed and episode info to disk
	 * 
	 * @throws IOException
	 */
	public void saveFeeds() throws IOException {
		myModel.saveFeeds();
	}

}
