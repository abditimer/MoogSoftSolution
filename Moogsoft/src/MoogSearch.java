import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class finds 'X' relevant words/phrases related to the movie "The
 * Interview".
 * 
 * @author Abdi Timer
 *
 */
public class MoogSearch {
	private String[] wordsToIgnore;
	private int noOfRelativeWords;
	private Map<String, Integer> sortedWordsOnSite;

	public MoogSearch(String startingWebsite, int noOfRelativeWords, String[] wordsToIgnore) {
		this.wordsToIgnore = wordsToIgnore;
		this.noOfRelativeWords = noOfRelativeWords;

		try {
			// Start with the interviews imdb website - connect to it using
			// jSoups connect()
			Document document = Jsoup.connect(startingWebsite).get();
			// This returns the text on the site
			String fullSiteText = document.body().text();
			// regex to split around other elements, to return words we need
			String[] words = fullSiteText.split("[^A-ZÃ…Ã„Ã–a-zÃ¥Ã¤Ã¶]+");
			// hasmap to store our words
			Map<String, Integer> wordsOnSite = new TreeMap<>();
			// iterate over the singlewords in our array of words
			for (String singleWord : words) {
				if ("".equals(singleWord)) {
					continue;
				}
				if (wordsOnSite.containsKey(singleWord)) {
					// it contains the word already
					Integer currentCount = wordsOnSite.get(singleWord) + 1;
					wordsOnSite.put(singleWord, currentCount);
				} else {
					// does not countain the word
					wordsOnSite.put(singleWord, Integer.valueOf(1));
				}
			}
			// This returns a sorted map based on the values (key-value pair)
			sortedWordsOnSite = sortByValue(wordsOnSite);
			//print relative words
			printRelativeWords();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Allows you to print the relative words.
	 */
	public void printRelativeWords() {
		// iterate over the words and remove listed words
		for (String toRemove : wordsToIgnore) {
			sortedWordsOnSite.remove(toRemove);
		}
		// A list of all the words collected.
		List<String> keys = new ArrayList<>(sortedWordsOnSite.keySet());
		// finds the cut-off point for our words
		int limit = (keys.size() - 1) - noOfRelativeWords;
		System.out.println("Printing relevant words:");
		int count = 1;
		for (int i = keys.size() - 1; i > limit; i--) {
			System.out.println(count + ": " + keys.get(i));
			count++;
		}
	}

	public String[] getWordsToIgnore() {
		return wordsToIgnore;
	}

	public void setWordsToIgnore(String[] wordsToIgnore) {
		this.wordsToIgnore = wordsToIgnore;
	}

	public int getNoOfRelativeWords() {
		return noOfRelativeWords;
	}

	public void setNoOfRelativeWords(int noOfRelativeWords) {
		this.noOfRelativeWords = noOfRelativeWords;
	}

	/**
	 * Lets us find any other linked sites.
	 * 
	 * @param document
	 *            The Document which we want to search for other site links.
	 * @return Returns a list of all the other sites linked from our main site.
	 */
	public ArrayList<String> findLinksOnSite(Document document) {
		Elements otherSites = document.select("a[href]");
		for (Element e : otherSites) {
			System.out.println("link: " + e.attr("abs:href"));
		}
		ArrayList<String> a = new ArrayList<>();

		return a;
	}

	/**
	 * Method allows for a Map to be sorted based on the key values in the Map.
	 * 
	 * @param map
	 *            The map which needs to be sorted based on the values
	 * @return Returns Map that is of the same generic type, with it sorted in
	 *         terms of the values
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		// Words to remove
		String[] toRemove = { "to", "the", "a", "and", "of", "in", "The", "is", "with", "they", "on", "their", "an",
				"his", "by", "it", "s" };
		int numberOfRelevantWords = 5;
		MoogSearch test = new MoogSearch("http://www.imdb.com/title/tt2788710/", numberOfRelevantWords, toRemove);
	}
}
