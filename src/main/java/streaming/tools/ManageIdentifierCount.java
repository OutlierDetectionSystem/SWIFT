package streaming.tools;

import streaming.base.CountSummary;

import java.util.HashMap;

public class ManageIdentifierCount {
	
	public static CountSummary getCountSummaryForIdentifier(int identifier,
															HashMap<Integer, CountSummary> identifierToCount) {
		if (identifierToCount.containsKey(identifier))
			return identifierToCount.get(identifier);
		else {
			CountSummary newSummary = new CountSummary(identifier);
			identifierToCount.put(identifier, newSummary);
			return newSummary;
		}
	}
}
