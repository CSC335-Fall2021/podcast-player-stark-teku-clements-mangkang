package PodcastEntry;

import PodcastEntry.PodcastEpisode;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a Podcast RSS Feed. It contains details about the
 * podcast itself as well as a list of associated episodes
 * 
 * @author Michael Stark
 */
public class PodcastFeed implements Serializable {
	private static final long serialVersionUID = 1L;
	private String feedURL;
	private String feedTitle;
	private String feedDescription;
	private String feedCopyright;
	private String feedLanguage;
	private String feedImageURL;
	private String feedLink;
	private ArrayList<PodcastEpisode> feedEpisodes;

	/**
	 * Constructs a new Podcast Feed object with default values
	 */
	public PodcastFeed(String url, String title, String desc, String copyright, String lang, String image,
			String link) {
		feedURL = url;
		feedTitle = title;
		feedDescription = desc;
		feedCopyright = copyright;
		feedLanguage = lang;
		feedImageURL = image;
		feedLink = link;

		feedEpisodes = new ArrayList<PodcastEpisode>();
	}

	/**
	 * Gets this Podcast Feed's RSS URL
	 * 
	 * @return The URL as a string
	 */
	public String getURL() {
		return feedURL;
	}

	/**
	 * Gets this Podcast Feed's Title
	 * 
	 * @return The title as a string
	 */
	public String getTitle() {
		return feedTitle;
	}

	/**
	 * Gets this Podcast Feed's Description
	 * 
	 * @return The description as a string
	 */
	public String getDescription() {
		return feedDescription;
	}

	/**
	 * Gets this Podcast Feed's Copyright info
	 * 
	 * @return The copyright info as a string
	 */
	public String getCopyright() {
		return feedCopyright;
	}

	/**
	 * Gets this Podcast Feed's Language
	 * 
	 * @return The language as a string
	 */
	public String getLanguage() {
		return feedLanguage;
	}

	/**
	 * Gets this Podcast Feed's Image URL
	 * 
	 * @return The URL as a string
	 */
	public String getImageURL() {
		return feedImageURL;
	}

	/**
	 * Gets this Podcast Feed's link
	 * 
	 * @return The URL as a string
	 */
	public String getLink() {
		return feedLink;
	}

	/**
	 * Gets all episodes related to this Podcast feed
	 * 
	 * @return An Arraylist of PodcastEpisodes
	 */
	public ArrayList<PodcastEpisode> getEpisodes() {
		return feedEpisodes;
	}

	/**
	 * Adds an episode to this podcast feed
	 * 
	 * @param e The PodcastEpisode to add
	 */
	public void addEpisode(PodcastEpisode e) {
		feedEpisodes.add(e);
	}
	
	/**
	 * Returns a string representing this PodcastFeed.
	 * Currently this is the same as getTitle()
	 * 
	 * @return A string representing the Podcast Feed
	 */
	@Override
	public String toString() {
		return feedTitle;
	}
}
