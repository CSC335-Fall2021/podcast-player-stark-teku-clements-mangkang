package PodcastModel;

import PodcastEntry.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Observable;

/**
 * The Model for the Podcast Player MVC
 * 
 * @author Michael Stark
 */
@SuppressWarnings("deprecation")
public class PodcastModel extends Observable {
	private ArrayList<PodcastFeed> followedFeeds;
	private PodcastFeed favoriteEpisodes;
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
	 * Attempts to remove a feed from the list of followed podcast feeds
	 * 
	 * @param f The PodcastFeed to remove
	 */
	public void removeFeed(PodcastFeed f) {
		followedFeeds.remove(f);
		setChanged();
		notifyObservers(new PlaylistUpdate(f, true));
	}

	/**
	 * Adds a PodcastEpisode to the favorites playlist
	 * 
	 * @param e
	 */
	public void addFavorite(PodcastEpisode e) {
		if (e != null) {
			if (favoriteEpisodes == null) {
				favoriteEpisodes = new PodcastFeed("favorite", "Favorites", "My favorite podcast episodes", "", "",
						"https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/300px-No_image_available.svg.png",
						"");
				followedFeeds.add(0, favoriteEpisodes);
			}

			favoriteEpisodes.addEpisode(e);
			setChanged();
			notifyObservers(new PlaylistUpdate(favoriteEpisodes));
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
		nowPlaying.setListenedTo(true);

		setChanged();
		notifyObservers(new PlayUpdate(nowPlaying));
	}

	/**
	 * Loads our feeds and episodes from disk
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void loadFeeds() throws IOException {
		try {
			FileInputStream fileStream = new FileInputStream("PodcastDB");
			ObjectInputStream objStream = new ObjectInputStream(fileStream);

			followedFeeds = (ArrayList<PodcastFeed>) objStream.readObject();

			for (PodcastFeed feed : followedFeeds) {
				setChanged();
				notifyObservers(new PlaylistUpdate(feed));
			}

			objStream.close();
			fileStream.close();
		} catch (IOException | ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Saves our feeds and episodes to disk
	 * 
	 * @throws IOException
	 */
	public void saveFeeds() throws IOException {
		try {
			FileOutputStream fileStream = new FileOutputStream("PodcastDB");
			ObjectOutputStream objStream = new ObjectOutputStream(fileStream);

			objStream.writeObject(followedFeeds);

			objStream.close();
			fileStream.close();

		} catch (IOException e) {
			throw e;
		}
	}

}
