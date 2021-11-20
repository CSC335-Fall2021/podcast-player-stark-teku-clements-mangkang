package PodcastEntry;

import java.io.Serializable;

/**
 * This class represents and individual episode of a podcast and contains all of
 * the associated details belonging to it
 * 
 * @author Michael Stark
 */
public class PodcastEpisode implements Serializable {
	private static final long serialVersionUID = 1L;
	private String episodeGUID;
	private String episodeTitle;
	private String episodeDescription;
	private String episodeLink;
	private String episodeMediaURL;

	/**
	 * Constructor
	 */
	public PodcastEpisode() {
		// This space for rent
	}
	
	/**
	 * Gets this episode's GUID
	 * 
	 * @return The GUID as a string
	 */
	public String getGUID() {
		return episodeGUID;
	}
	
	/**
	 * Sets the GUID
	 * 
	 * @param s The string to set the GUID to
	 */
	public void setGUID(String s) {
		episodeGUID = s;
	}
	
	/**
	 * Gets this episode's title
	 * 
	 * @return The title as a string
	 */
	public String getTitle() {
		return episodeTitle;
	}
	
	/**
	 * Sets the title
	 * 
	 * @param s The string to set the title to
	 */
	public void setTitle(String s) {
		episodeTitle = s;
	}
	
	/**
	 * Gets this episode's description
	 * 
	 * @return The description as a string
	 */
	public String getDescription() {
		return episodeDescription;
	}
	
	/**
	 * Sets the description
	 * 
	 * @param s The string to set the description to
	 */
	public void setDescription(String s) {
		episodeDescription = s;
	}
	
	/**
	 * Gets this episode's link
	 * 
	 * @return The URL as a string
	 */
	public String getLink() {
		return episodeLink;
	}
	
	/**
	 * Sets the link for this episode
	 * 
	 * @param s The string to set the link URL to
	 */
	public void setLink(String s) {
		episodeLink = s;
	}
	
	/**
	 * Gets the URL to this episode's media
	 * 
	 * @return The URL as a string
	 */
	public String getMediaURL() {
		return episodeMediaURL;
	}
	
	/**
	 * Sets the Media URL for this episode
	 * 
	 * @param s The string to set the Media URL to
	 */
	public void setMediaURL(String s) {
		episodeMediaURL = s;
	}

}
