package PodcastEntry;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class represents and individual episode of a podcast and contains all of
 * the associated details belonging to it
 * 
 * @author  Michael Stark, Nathan Teku, Kyle Clements, Tinnawit Mangkang 
 */
public class PodcastEpisode implements Serializable {
	
	
	/**serial VersionUID*/
	private static final long serialVersionUID = 1L;
	/**episodeGUID*/
	private String episodeGUID;
	/**episodeTitle*/
	private String episodeTitle;
	/**episodeDescription*/
	private String episodeDescription;
	/**episodeLink*/
	private String episodeLink;
	/**episodeMediaURL*/
	private String episodeMediaURL;
	/**episodeDuration*/
	private String episodeDuration;
	/**episodeDate*/
	private LocalDate episodeDate;
	/**listendTo*/
	private boolean listenedTo;
	/**isDownloaded*/
	private boolean isDownloaded;

	/**
	 * Constructor
	 */
	public PodcastEpisode() {
		listenedTo = false;
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

	/**
	 * Gets the playback duration of the podcast
	 * 
	 * @return A string representing the playback duration
	 */
	public String getDuration() {
		return episodeDuration;
	}

	/**
	 * Sets the playback duration of the podcast
	 * 
	 * @param s A string containing the playback duration
	 */
	public void setDuration(String s) {
		episodeDuration = s;
	}

	/**
	 * Sets the date that this episode was published
	 * 
	 * @param d The LocalDate published
	 */
	public void setPublishDate(LocalDate d) {
		episodeDate = d;
	}

	/**
	 * Gets the date that this episode was published
	 * 
	 * @return The LocalDate published
	 */
	public LocalDate getPublishDate() {
		return episodeDate;
	}

	/**
	 * Sets if the episode has been listened to
	 * 
	 * @param t Boolean, true if listened to, false if not
	 */
	public void setListenedTo(boolean t) {
		listenedTo = t;
	}

	/**
	 * Gets if this episode has been listened to or not
	 * 
	 * @return True if listened to, false if not
	 */
	public boolean getListenedTo() {
		return listenedTo;
	}

	/**
	 * Sets if this episode has been downloaded to or not
	 * @param d Boolean, true if downloaded, false if not
	 */
	public void setDownloadedTo(boolean d) {
		isDownloaded = d;
	}
	
	/**
	 * Gets if this episode has been downloaded to or not
	 * 
	 * @return True if downloaded, false if not
	 */
	public boolean gotDownloaded() {
		return isDownloaded;
	}
	/**
	 * Gets a string representation of the episode. Currently this is the title and
	 * duration
	 * 
	 * @return A string representing the episode
	 */
	@Override
	public String toString() {
		return episodeTitle + " - " + episodeDuration + " - " + episodeDate;
	}

}
