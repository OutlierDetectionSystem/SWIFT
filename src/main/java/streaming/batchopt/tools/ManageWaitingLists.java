package streaming.batchopt.tools;

import streaming.base.CountSummary;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.SingleElement;
import streaming.base.atomics.FrequentPattern;
import streaming.base.waitinglist.WaitingList;
import streaming.base.waitinglist.WaitingPattern;
import streaming.tools.*;

import streaming.batchopt.patterngen.MergeElementsForBatch;

import java.util.*;

public class ManageWaitingLists extends streaming.tools.ManageWaitingLists {
	public ManageWaitingLists(LinkedList<BaseElement> mainSequence, HashMap<String, WaitingList> waitingElements,
                              HashMap<Integer, CountSummary> identifierToCount, HashSet<Integer> MoreThanOne,
                              HashMap<String, HashSet<String>> freqPatterns, int itemGap, int seqGap, MergeElementsForBatch merge) {
		super(mainSequence, waitingElements, identifierToCount, MoreThanOne, freqPatterns, itemGap, seqGap, merge);
	}

	public void checkWaitingPatternLists() {
		BaseElement currentElement = this.mainSequence.getLast();
		String currentItem = currentElement.getItemsInString();
		// first check all sequences that are waiting for this element
		if (streaming.tools.ManageWaitingTool.checkValidationOfWL(currentItem, waitingElements)) {
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
						this.dealWithTotalMatchWaiting(wp, false);
						this.waitingElements.clear();
						return;
					} // not deal with split here
					else {
						// save to the next list, not this one
						String nextElement = wp.getNextWaitingElement();
						if (nextElement.equals(currentItem)) {
							tempBag.add(wp);
						} else {
							streaming.tools.ManageWaitingTool.addToWaitingElementList(wp, nextElement, waitingElements);
						}
					}
				}
			}
			waitingPatternList.clear();
			waitingPatternList.addAll(tempBag);
		} // end not empty waiting list
		// generate new waiting patterns
		this.generateNewWaitingPatternToList(currentItem, currentElement);
	}

	public void dealWithTotalMatchWaiting(WaitingPattern wp, boolean withMerge) {
		// found a total match with frequent sequence
		// create new frequent pattern and add
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<BaseElement> innerElements = new ArrayList<BaseElement>();
		String itemsInStr = "";

		ArrayList<Integer> indexesInWP = wp.getIndexesInWP();
		// find the first position of element
		int startPos = findStartPosition(wp, indexesInWP.get(0));
		if(startPos == -1){
			return;
		}

		LinkedList<BaseElement> removeItems = new LinkedList<BaseElement>();

		for (int i = startPos; i < this.mainSequence.size(); i++) {
			if(this.mainSequence.get(i).getClass().toString().endsWith("FrequentPattern"))
				return;
		}

		int index = 0;
		// find true start point
		for(int i = startPos; i< this.mainSequence.size(); i++){
			BaseElement cur = this.mainSequence.get(i);
			if(cur.getEndIndex() < indexesInWP.get(index))
				startPos++;
			else
				break;
		}

		// the first element might be a repeated one and should not be removed from the main sequence
		BaseElement firstEle = this.mainSequence.get(startPos);
		int endIndex = -1;
		if(firstEle.getClass().toString().endsWith("SingleElement") && ((SingleElement) firstEle).isRepeatItem() &&
				(firstEle.getStartIndex() < (int) indexesInWP.get(index))){
			endIndex = indexesInWP.get(index)-1;
			for(int ii = (int)indexesInWP.get(index); ii<= firstEle.getEndIndex(); ii++){
				if(ii == (int)indexesInWP.get(index)) {
					indexes.add(ii);
					items.add(firstEle.getItemsInString());
					itemsInStr += firstEle.getItemsInString() + ",";
					index++;
				}else
					return;
			}
			startPos++;
		}
		for (int i = startPos; i < this.mainSequence.size(); i++) {
			BaseElement cur = this.mainSequence.get(i);
			if(cur.getEndIndex() < (int)indexesInWP.get(index)) {
				innerElements.add(cur);
				removeItems.add(cur);
			}else if(cur.getClass().toString().endsWith("SingleElement")){
				SingleElement currentElement = (SingleElement) cur;
				// first find a start point
				if(currentElement.getStartIndex() != (int) indexesInWP.get(index))
					return;
				else{
					for(int ii = currentElement.getStartIndex(); ii<= currentElement.getEndIndex(); ii++){
						if(ii == (int)indexesInWP.get(index)) {
							indexes.add(ii);
							items.add(currentElement.getItemsInString());
							itemsInStr += currentElement.getItemsInString() + ",";
							index++;
						}else
							return;
					}
					removeItems.add(cur);
				}
			}
		} // end traversing main

		if(endIndex!=-1) {
			((SingleElement)firstEle).setEndIndex(endIndex);
		}
		if (itemsInStr.length() > 0) {
			itemsInStr = itemsInStr.substring(0, itemsInStr.length() - 1);
		}

		this.mainSequence.removeAll(removeItems);
		for(BaseElement cur: removeItems){
			identifierToCount.get(cur.getIdentifier()).removeElementFromList(cur);
			if (identifierToCount.get(cur.getIdentifier()).getCount() < 2) {
				MoreThanOne.remove(cur.getIdentifier());
			}
		}

		innerElements = mergeFunction.splitInnerIntoSingles(innerElements);
		FrequentPattern newFP = new FrequentPattern(wp.getIdentifier(), items, indexes, innerElements, itemsInStr);
		this.mainSequence.add(newFP);
		if (this.identifierToCount.get(wp.getIdentifier()).addElementToList(newFP)) {
			MoreThanOne.add(wp.getIdentifier());
		}
		if(withMerge)
			// check if this one can merge with others
			mergeFunction.mergeElements(newFP);
	}

	/**
	 * Deal with patterns such as AABB AABB AABB
	 */
	public void updateWaitingPatternListsForRepeatLetter() {
		SingleElement currentElement =  (SingleElement) this.mainSequence.getLast();
		String currentItem = currentElement.getItemsInString();
		// first check all sequences that are waiting for this element
		if (streaming.tools.ManageWaitingTool.checkValidationOfWL(currentItem, waitingElements)) {
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
						this.dealWithTotalMatchWaiting(wp, false);
						this.waitingElements.clear();
						return;
					}  else {
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
		// generate new waiting patterns
		this.generateNewWaitingPatternToList(currentItem, currentElement);
	}
}
