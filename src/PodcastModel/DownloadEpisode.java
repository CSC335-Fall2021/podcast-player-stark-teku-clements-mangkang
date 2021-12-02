package PodcastModel;
import java.io.BufferedInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * This class's main purpose is to download the specific podcast episode
 * @author Nathan Teku
 *
 */
public class DownloadEpisode {

	// private attributes needed for downloading the episode
	private byte readingMemory[];
	private int collectedBytes;
	final private int arrayLength = 1024;
	private FileOutputStream newFile;
	
	
	/**
	 * This constructor downloads the episode 
	 * @param url The String URL
	 * @param title the Title of the specific podcast
	 * @throws IOException
	 */
	public DownloadEpisode(String url,String title) throws IOException
	{
		
		
	    // URL object
		URL obj = new URL(url);
		// set the collected bytes to 0
		collectedBytes = 0;
		// create the Buffered Input Stream object with the URL being passed 
		BufferedInputStream readingSource = new BufferedInputStream(obj.openStream());
		// initialize the byte array
		readingMemory = new byte[arrayLength];
		// create the output text file
		newFile = new FileOutputStream(title + ".txt");
		
		// reading the text file
	    while ((collectedBytes = readingSource.read(readingMemory, 0, 1024)) != -1) {
	        newFile.write(readingMemory, 0, collectedBytes);
	    }
		
		
		
	}
}
