import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import PodcastController.PodcastController;
import PodcastEntry.PodcastEpisode;
import PodcastEntry.PodcastFeed;
import PodcastModel.DownloadEpisode;
import PodcastModel.PlayUpdate;
import PodcastModel.PodcastModel;

/**
 * Unit Tests for PodcastPlayer
 * 
 * @author Michael Stark
 */
class PodcastTests {

	@Test
	void testPodcast() {
		PodcastModel myModel = new PodcastModel();
		PodcastController myController = new PodcastController(myModel);
		
		// Clear all podcast feed in case defaults exist
		while (!myController.getPodcastFeeds().isEmpty()) {
			myController.removePodcastFeed(myController.getPodcastFeeds().get(0));
		}
		
		// Load a static feed, this is a saved copy of Dateline NBC's feed
		assertTrue(myController.addPodcastFeed("https://www.cryotube.net/testfeed.xml"));
		
		// Test PodcastFeed
		assertNotEquals(null, myController.getPodcastFeeds());
		PodcastFeed f = myController.getPodcastFeeds().get(0);
		assertEquals("https://www.cryotube.net/testfeed.xml", f.getURL());
		assertEquals("Dateline NBC", f.getTitle());
		assertEquals("Current and classic episodes, featuring compelling true-crime mysteries, powerful documentaries and in-depth investigations.  ", f.getDescription());
		assertEquals("https://dateline-nbc.simplecast.com", f.getLink());
		assertEquals("https://image.simplecastcdn.com/images/ae183fe2-c634-458a-93dd-5770f0676f77/b010809a-c311-425c-9325-2235c21e6939/3000x3000/7f0421f73d2ce0ca272e392c937e1a301285d44fe7c6d710c2844d80c0c7bb1a3e9838ac03ee80fc64199891cb9d5c6e9d4490f5081fb379c0ab2317f2cadf14.jpeg?aid=rss_feed", f.getImageURL());
		assertEquals("2019 NBC News", f.getCopyright());
		assertEquals("en", f.getLanguage());
		assertEquals("Dateline NBC", f.toString());
		assertNotEquals(null, f.getEpisodes());
		myController.loadFeeds();
		 
		// Test PodcastEpisode
		PodcastEpisode e = f.getEpisodes().get(0);
		assertEquals("The Family Secret", e.getTitle());
		assertEquals("9795fb65-62ff-4acf-b2ad-c314614dcd30", e.getGUID());
		assertEquals(null, e.getLink());
		assertFalse(e.getListenedTo());
		assertEquals("https://dts.podtrac.com/redirect.mp3/chtbl.com/track/6D589D/pdst.fm/e/nbcnews.simplecastaudio.com/ae183fe2-c634-458a-93dd-5770f0676f77/episodes/84540373-8767-47b2-a1b9-e73564094577/audio/128/default.mp3?aid=rss_feed&awCollectionId=ae183fe2-c634-458a-93dd-5770f0676f77&awEpisodeId=84540373-8767-47b2-a1b9-e73564094577&feed=HL4TzgYC", e.getMediaURL());
		assertEquals(LocalDate.parse("2021-12-01"), e.getPublishDate());
		assertEquals("<p>In this Dateline classic, a traumatic secret tears a family apart after 27 years. Keith Morrison reports. Originally aired on NBC on April 2, 2010.</p>\n", e.getDescription());
		assertEquals("00:42:19", e.getDuration());
		assertEquals("The Family Secret - 00:42:19 - 2021-12-01", e.toString());
		e.setLink("www.example.com");
		assertEquals("www.example.com", e.getLink());
		e.setListenedTo(true);
		assertTrue(e.getListenedTo());
		
		// Test controller 
		myController.addFavorite(e);
		myController.playEpisode(e);
		try {
			myController.saveFeeds();
		} catch (IOException err) {
			err.printStackTrace();
		}
		
		myController.loadFeeds();
		myController.removePodcastFeed(f);
		
		
		try {
			new DownloadEpisode(e.getMediaURL(),e.getTitle(),e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		assertTrue(e.gotDownloaded());
		
		PlayUpdate p = new PlayUpdate(e);
		assertEquals(e,p.getEpisode());
	}

}
