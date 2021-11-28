package PodcastModel;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
public class DownloadEpisode {

	private byte readingMemory[];
	private int collectedBytes;
	final private int arrayLength = 1024;
	private FileOutputStream newFile;
	public DownloadEpisode(String url,String title) throws IOException
	{
		
		
	     
		URL obj = new URL(url);
		collectedBytes = 0;
		BufferedInputStream readingSource = new BufferedInputStream(obj.openStream());
		readingMemory = new byte[arrayLength];
		newFile = new FileOutputStream(title + ".txt");
		
	 
	    while ((collectedBytes = readingSource.read(readingMemory, 0, 1024)) != -1) {
	        newFile.write(readingMemory, 0, collectedBytes);
	    }
		
		
		
	}
}
