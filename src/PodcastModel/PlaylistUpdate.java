package PodcastModel;

import PodcastEntry.PodcastFeed;

/**
 * A simple class for signalling to the view that a PodcastFeed has been fetched
 * and parsed
 * 
 * @author Michael Stark
 */
public class PlaylistUpdate {
	private PodcastFeed feed;

	/**
	 * Constructor
	 * 
	 * @param f The PodcastFeed which is new/updated
	 */
	public PlaylistUpdate(PodcastFeed f) {
		feed = f;
	}

	/**
	 * Gets the PodcastFeed
	 * 
	 * @return The new/updated PodcastFeed
	 */
	public PodcastFeed getPodcastFeed() {
		return feed;
	}

}
