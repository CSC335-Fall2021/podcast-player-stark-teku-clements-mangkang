package PodcastController;

import PodcastModel.PodcastModel;
import PodcastEntry.PodcastFeed;
import PodcastEntry.PodcastEpisode;
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
	 * Pauses playback of the podcast
	 */
	public void pauseEpisode() {
		myModel.stopPlayback();
	}

	/**
	 * Stops playing the currently playing podcast episode (if applicable) and
	 * starts playing the next episode
	 */
	public void nextEpisode() {
		myModel.nextTrack();
	}

	/**
	 * Stops playing the currently playing podcast episode (if applicable) and
	 * starts playing the previous episode
	 */
	public void prevEpisode() {
		myModel.prevTrack();
	}

}
