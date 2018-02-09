package streaming.tools;

import java.util.HashMap;
import java.util.HashSet;

public class ManageFrequentPatternLists {

	private static HashSet<String> addSetToFrequentList(String startItem,
			HashMap<String, HashSet<String>> freqPatterns) {
		if (freqPatterns.containsKey(startItem))
			return freqPatterns.get(startItem);
		else {
			HashSet<String> newSet = new HashSet<String>();
			freqPatterns.put(startItem, newSet);
			return newSet;
		}
	}

	public static void addItemsToFPList(HashMap<String, HashSet<String>> freqPatterns, String startItem, String newFP) {
		ManageFrequentPatternLists.addSetToFrequentList(startItem, freqPatterns).add(newFP);
	}

	public static void removeItemFromFPList(String keyPrefix, String removeItem,
			HashMap<String, HashSet<String>> freqPatterns) {
		freqPatterns.get(keyPrefix).remove(removeItem);
	}
}
