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
	private boolean removing = false;

	/**
	 * Constructor
	 * 
	 * @param f The PodcastFeed which is new/updated
	 */
	public PlaylistUpdate(PodcastFeed f) {
		feed = f;
		removing = false;
	}
	
	/**
	 * Constructor
	 * 
	 * @param f The PodcastFeed which is new/updated
	 * @param r If we're removing the PodcastFeed
	 */
	public PlaylistUpdate(PodcastFeed f, boolean r) {
		feed = f;
		removing = true;
	}

	/**
	 * Gets the PodcastFeed
	 * 
	 * @return The new/updated PodcastFeed
	 */
	public PodcastFeed getPodcastFeed() {
		return feed;
	}
	
	/**
	 * Gets if we're removing this feed or not
	 * 
	 * @return True if removing, false if not
	 */
	public boolean removalRequest() {
		return removing;
	}

}
