package PodcastModel;

import PodcastEntry.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * The Model for the Podcast Player MVC
 */
@SuppressWarnings("deprecation")
public class PodcastModel extends Observable {
	private ArrayList<PodcastFeed> followedFeeds;
	private PodcastEpisode nowPlaying;

	/**
	 * Constructor
	 */
	public PodcastModel() {
		followedFeeds = new ArrayList<PodcastFeed>();
		nowPlaying = null;
	}

	/**
	 * Attempts to add a new feed to our list of followed podcast feeds
	 * 
	 * @param url
	 * @return
	 */
	public boolean addFeed(String url) {
		PodcastFeedParser p = new PodcastFeedParser(url);
		PodcastFeed f = p.parse();

		if (f == null) {
			return false;
		} else {
			followedFeeds.add(f);

			setChanged();
			notifyObservers(new PlaylistUpdate(f));
			return true;
		}
	}

	/**
	 * Gets the list of followed feeds and associated info
	 * 
	 * @return An ArrayList<PodcastFeed> of feeds
	 */
	public ArrayList<PodcastFeed> getFeeds() {
		return followedFeeds;
	}

	/**
	 * Sets a podcast episode as playing
	 * 
	 * @param p The PodcastEpisode to play
	 */
	public void startPlayback(PodcastEpisode p) {
		nowPlaying = p;

		setChanged();
		notifyObservers(new PlayUpdate(p));
	}

	/**
	 * Stops/Pauses playback of the current podcast
	 */
	public void stopPlayback() {

	}

	public void nextTrack() {

	}

	public void prevTrack() {

	}

}
