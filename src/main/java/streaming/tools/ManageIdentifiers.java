package streaming.tools;

import java.util.HashMap;

public class ManageIdentifiers {
	private static int indexIdentifier = 0;
	private static HashMap<String, Integer> stringToIdentifier = new HashMap<String, Integer>();
	
	public static int getIdentifierForString(String currentElement) {
		int currentIdentifier = -1;
		if (stringToIdentifier.containsKey(currentElement))
			currentIdentifier = stringToIdentifier.get(currentElement);
		else {
			currentIdentifier = indexIdentifier;
			stringToIdentifier.put(currentElement, currentIdentifier);
			indexIdentifier++;
		}
		return currentIdentifier;
	}

}
