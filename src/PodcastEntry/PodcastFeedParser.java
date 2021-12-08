package PodcastEntry;

import PodcastEntry.PodcastFeed;
import PodcastEntry.PodcastEpisode;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

/**
 * This class is responsible for taking a RSS feed and then parsing it into a
 * PodcastFeed class with its associated PodcastEpisodes
 * 
 * @author Michael Stark, Nathan Teku, Kyle Clements, Tinnawit Mangkang
 */
public class PodcastFeedParser {
	private URL feedURL;

	/**
	 * Instantiates a fresh feed parser for the feed at the given URL
	 * 
	 * @param url The URL of the feed that we are going to parse
	 * @throws RuntimeException
	 */
	public PodcastFeedParser(String url) throws RuntimeException {
		try {
			feedURL = new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Opens an input stream to the RSS file at our url
	 * 
	 * @return An input stream to the RSS file at our url
	 * @throws IOException
	 */
	private InputStream fetch() throws IOException {
		try {
			return feedURL.openStream();
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Parses the feed and converts it into a PodcastFeed with its associated
	 * PodcastEpisodes
	 * 
	 * @return A PodcastFeed or null if we failed to create one
	 * @throws RuntimeException
	 */
	public PodcastFeed parse() throws RuntimeException {
		PodcastFeed feed = null;

		try {
			// Need to temporarily store values while parsing
			boolean parsingHeader = true; // First bit we parse should always be the header/feed description
			boolean parsingImageTag = false;
			DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
			String title = "";
			String description = "";
			String copyright = "";
			String language = "";
			String imageurl = "";
			String link = "";
			String guid = "";
			String mediaURL = "";
			String duration = "";
			LocalDate pubDate = LocalDate.MIN;

			// Setup parser stream
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			XMLEventReader reader = factory.createXMLEventReader(fetch());

			// Loop over every element in the XML file
			while (reader.hasNext()) {
				XMLEvent e = reader.nextEvent();

				if (e.isStartElement()) {
					/*
					 * Start tag handling
					 */
					String tag = e.asStartElement().getName().getLocalPart();
					tag = tag.toLowerCase(); // For easier handling

					// Ugly switch to handle individual types of tag we care about
					switch (tag) {
					case "item":
						// Assume we're done with the header when we encounter item tags
						if (parsingHeader) {
							parsingHeader = false;
							feed = new PodcastFeed(feedURL.toString(), title, description, copyright, language,
									imageurl, link);

							// Clean vars for next run
							title = "";
							description = "";
							copyright = "";
							language = "";
							imageurl = "";
							link = "";
							guid = "";
							mediaURL = "";
							duration = "";
							pubDate = LocalDate.MIN;
						}
					case "title":
						if (!parsingImageTag) {
							title = getTagContents(reader);
						}
						break;
					case "description":
						if (!parsingImageTag) {
							description = getTagContents(reader);
						}
						break;
					case "copyright":
						copyright = getTagContents(reader);
						break;
					case "language":
						language = getTagContents(reader);
						break;
					case "image":
						parsingImageTag = true;
						break;
					case "link":
						if (!parsingImageTag) {
							link = getTagContents(reader);
						}
						break;
					case "url":
						if (parsingImageTag) {
							imageurl = getTagContents(reader);
						}
						break;
					case "guid":
						guid = getTagContents(reader);
						break;
					case "enclosure":
						// Media URL is a tag attribute on enclosure tag
						mediaURL = getAttributeContents(e, "url");
						break;
					case "duration":
						duration = getTagContents(reader);
						break;
					case "pubdate":
						pubDate = LocalDate.parse(getTagContents(reader), formatter);
						break;
					default:
						break;
					}

				} else if (e.isEndElement()) {
					/*
					 * End tag handling
					 */

					// Image tag
					if (e.asEndElement().getName().getLocalPart() == ("image")) {
						parsingImageTag = false;
					}

					// Item tag
					if (e.asEndElement().getName().getLocalPart() == ("item")) {
						PodcastEpisode n = new PodcastEpisode();
						n.setTitle(title);
						n.setDescription(description);
						n.setGUID(guid);
						n.setMediaURL(mediaURL);
						n.setDuration(duration);
						n.setPublishDate(pubDate);

						feed.addEpisode(n);

						// Clean vars for next run
						title = "";
						description = "";
						copyright = "";
						language = "";
						imageurl = "";
						link = "";
						guid = "";
						mediaURL = "";
						duration = "";
						pubDate = LocalDate.MIN;
					}
				}
			}

		} catch (XMLStreamException | IOException e) {
			throw new RuntimeException(e);
		}

		return feed;
	}

	/**
	 * Gets the contents within a tag
	 * 
	 * @param e      The XMLEvent at the start of the tag
	 * @param reader The XMLEventReader parsing the file
	 * @return The contents of the tag
	 * @throws XMLStreamException
	 */
	private String getTagContents(XMLEventReader reader) throws XMLStreamException {
		// Move inside the tag
		XMLEvent e = reader.nextEvent();

		// Sanity check
		if (e.isCharacters()) {
			return e.asCharacters().getData();
		}

		// Tag didn't contain text :(
		return "";
	}

	/**
	 * Attempts to return the value of an attribute on a tag
	 * 
	 * @param e      An XMLEvent which is the start of the tag to search
	 * @param target The attribute as a string to find
	 * @return The value of the attribute or "" if attribute not found
	 */
	private String getAttributeContents(XMLEvent e, String target) {
		Iterator<Attribute> attributes = e.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equals(target)) {
				return attribute.getValue();
			}
		}

		return "";
	}

}
