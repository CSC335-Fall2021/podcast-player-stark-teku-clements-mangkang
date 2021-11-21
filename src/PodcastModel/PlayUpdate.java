package PodcastModel;

import PodcastEntry.PodcastEpisode;

/**
 * A simple class for signaling to the view that a new episode should play
 * 
 * @author Michael Stark
 */
public class PlayUpdate {
	private PodcastEpisode activeEpisode;

	/**
	 * Constructor
	 * 
	 * @param p The PodcastEpisode to play
	 */
	public PlayUpdate(PodcastEpisode p) {
		activeEpisode = p;
	}

	/**
	 * Gets the podcast episode
	 * 
	 * @return PodcastEpisode to play
	 */
	public PodcastEpisode getEpisode() {
		return activeEpisode;
	}

}
