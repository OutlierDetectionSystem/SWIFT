package streaming.batchopt.tools;

import streaming.base.CountSummary;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.FrequentPattern;
import streaming.base.waitinglist.WaitingList;
import streaming.base.waitinglist.WaitingPattern;
import streaming.tools.ManageIdentifierCount;
import streaming.tools.ManageIdentifiers;

import java.util.*;

public class ManageWaitingTool {
	public static void dealWithTotalMatchWaiting(WaitingPattern wp, LinkedList<BaseElement> totalList) {
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<BaseElement> innerElements = new ArrayList<BaseElement>();
		String itemsInStr = "";

		ArrayList<Integer> indexesInWP = wp.getIndexesInWP();
		// find the first position of element
		int startPos = totalList.indexOf(wp.getStartElement());
		LinkedList<BaseElement> removeItems = new LinkedList<BaseElement>();
		int index = 0;
		for (int i = startPos; i < totalList.size(); i++) {
			if(index == indexesInWP.size())
				break;
			BaseElement cur = totalList.get(i);
			removeItems.add(cur);
			if(cur.getEndIndex() < (int)indexesInWP.get(index)) {
				innerElements.add(cur);
			}else{
				for(int ii = cur.getStartIndex(); ii<= cur.getEndIndex(); ii++){
					if(ii == indexesInWP.get(index)) {
						indexes.add(ii);
						items.add(cur.getItemsInString());
						itemsInStr += cur.getItemsInString() + ",";
						index++;
					}
				}
			}
		}
		if (itemsInStr.length() > 0) {
			itemsInStr = itemsInStr.substring(0, itemsInStr.length() - 1);
		}
		FrequentPattern newFP = new FrequentPattern(wp.getIdentifier(), items, indexes, innerElements, itemsInStr);
		totalList.add(startPos,newFP);
		totalList.removeAll(removeItems);
	}

	public static void CheckMatchWithFP(BaseElement currentElement, HashMap<String, WaitingList> waitingElements,
                                        int itemGap, int seqGap, HashMap<String, HashSet<String>> freqPatterns, LinkedList<BaseElement> totalList,
                                        HashMap<Integer, CountSummary> identifierToCount) {
		String currentItem = currentElement.getItemsInString();
		// first check all sequences that are waiting for this element
		if (ManageWaitingTool.checkValidationOfWL(currentItem, waitingElements)) {
			LinkedList<WaitingPattern> waitingPatternList = waitingElements.get(currentItem).getWaitingPatternList();
			// need this element again, add to this bag
			LinkedList<WaitingPattern> tempBag = new LinkedList<WaitingPattern>();

			for (WaitingPattern wp : waitingPatternList) {
				if(ManageIdentifierCount.getCountSummaryForIdentifier(wp.getIdentifier(), identifierToCount).getCount() < 2)
					continue;
				// first update waiting pattern to accept this new element
				if (wp.updateWaitingPatternWithNew(currentElement, itemGap, seqGap)) {
					if (wp.totalMatch()) {
						// do merge
						ManageWaitingTool.dealWithTotalMatchWaiting(wp, totalList);
						waitingElements.clear();
						return;
					} else {
						// save to the next list, not this one
						String nextElement = wp.getNextWaitingElement();
						if (nextElement.equals(currentItem)) {
							tempBag.add(wp);
						} else {
							ManageWaitingTool.addToWaitingElementList(wp, nextElement, waitingElements);
						}
					}
				}
			}
			waitingPatternList.clear();
			waitingPatternList.addAll(tempBag);
		} // end not empty waiting list
			// if cannot merge, then enlarge to the waiting list by adding this
			// new
			// element
		HashSet<String> freqPatternCandidates = freqPatterns.get(currentItem);
		if (freqPatternCandidates != null && freqPatternCandidates.size() > 0) {
			for (String fp : freqPatternCandidates) {
				// add a new WaitlingElement to waitingList
				ArrayList<String> subs = new ArrayList<String>(Arrays.asList((fp.split(","))));
				WaitingPattern wp = new WaitingPattern(ManageIdentifiers.getIdentifierForString(fp), subs, 0,
						currentElement);
//				wp.addElementsToWP(currentElement);
				if(subs.size() == 1){
					System.out.println(fp);
				}
				ManageWaitingTool.addToWaitingElementList(wp, subs.get(1), waitingElements);
			}
		}
	}

	public static boolean checkValidationOfWL(String currentItem, HashMap<String, WaitingList> waitingElements) {
		if (waitingElements.size() == 0)
			return false;
		if (!waitingElements.containsKey(currentItem))
			return false;
		if (waitingElements.get(currentItem).getWaitingPatternList() == null)
			return false;
		if (waitingElements.get(currentItem).getWaitingPatternList().size() == 0)
			return false;
		return true;
	}

	public static void addToWaitingElementList(WaitingPattern wp, String nextElement,
                                               HashMap<String, WaitingList> waitingElements) {
		if (waitingElements.containsKey(nextElement)) {
			waitingElements.get(nextElement).addNewPatternToList(wp);
		} else {
			waitingElements.put(nextElement, new WaitingList(nextElement));
			waitingElements.get(nextElement).addNewPatternToList(wp);
		}
	}
}
