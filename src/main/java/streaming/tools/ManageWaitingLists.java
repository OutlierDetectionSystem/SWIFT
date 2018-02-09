package streaming.tools;

import streaming.base.CountSummary;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.FrequentPattern;
import streaming.base.atomics.SingleElement;
import streaming.base.waitinglist.WaitingList;
import streaming.base.waitinglist.WaitingPattern;
import streaming.patterngen.MergeElements;

import java.util.*;

public class ManageWaitingLists {
	protected LinkedList<BaseElement> mainSequence;
	protected HashMap<String, WaitingList> waitingElements;
	protected HashSet<Integer> MoreThanOne;
	protected HashMap<String, HashSet<String>> freqPatterns;
	protected HashMap<Integer, CountSummary> identifierToCount;
	protected int itemGap;
	protected int seqGap;
	protected MergeElements mergeFunction;

	public ManageWaitingLists(LinkedList<BaseElement> mainSequence, HashMap<String, WaitingList> waitingElements,
                              HashMap<Integer, CountSummary> identifierToCount, HashSet<Integer> MoreThanOne,
                              HashMap<String, HashSet<String>> freqPatterns, int itemGap, int seqGap, MergeElements merge) {
		this.mainSequence = mainSequence;
		this.identifierToCount = identifierToCount;
		this.MoreThanOne = MoreThanOne;
		this.freqPatterns = freqPatterns;
		this.waitingElements = waitingElements;
		this.itemGap = itemGap;
		this.seqGap = seqGap;
		this.mergeFunction = merge;
	}

	public void removeFPFromWaitingElements(int fpId){
		for(WaitingList wl: this.waitingElements.values()){
			wl.removeFPFromWL(fpId);
		}
	}

	public int findStartPosition(WaitingPattern wp, int firstIndex){
		int startPos = this.mainSequence.indexOf(wp.getStartElement());
		if(startPos == -1){
			// cannot find that object, maybe has been merged then split (original one does not exist)
			for(int i = this.mainSequence.size()-1; i>0; i--){
				if((this.mainSequence.get(i).getStartIndex() <= firstIndex) &&
						(this.mainSequence.get(i).getEndIndex() >= firstIndex)){
					startPos = i;
					break;
				}
			}
		}
		return startPos;
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

		int index = 0;
		// find true start point
		for(int i = startPos; i< this.mainSequence.size(); i++){
			BaseElement cur = this.mainSequence.get(i);
			if(cur.getClass().toString().endsWith("FrequentPattern") && cur.getStartIndex() != indexesInWP.get(index))
				return;
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

		ArrayList<FrequentPattern> influencedFP = new ArrayList<FrequentPattern>();
		for (int i = startPos; i < this.mainSequence.size(); i++) {
			BaseElement cur = this.mainSequence.get(i);
			if(cur.getEndIndex() < (int)indexesInWP.get(index)) {
				innerElements.add(cur);
				removeItems.add(cur);
			}
			else if(cur.getClass().toString().endsWith("FrequentPattern")){
				FrequentPattern currentElement = (FrequentPattern)cur;
				for(int ii: currentElement.getIndexesForItems()){
					if(ii == (int)indexesInWP.get(index))
						index++;
					else
						return;
				}
				// total match with the frequent pattern
				items.addAll(currentElement.getItems());
				indexes.addAll(currentElement.getIndexesForItems());
				itemsInStr += cur.getItemsInString() + ",";
				innerElements.addAll(((FrequentPattern)cur).getInnerElements());
				removeItems.add(cur);
			}else if(cur.getClass().toString().endsWith("SingleElement")){
				SingleElement currentElement = (SingleElement) cur;
				// first find a start point
				if(currentElement.getStartIndex() > (int) indexesInWP.get(index))
					return;
				else{
//					if(currentElement.isRepeatItem())
//						return;
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

		if (itemsInStr.length() > 0) {
			itemsInStr = itemsInStr.substring(0, itemsInStr.length() - 1);
		}
		if(endIndex!=-1) {
			((SingleElement)firstEle).setEndIndex(endIndex);
		}
		this.mainSequence.removeAll(removeItems);
		for(BaseElement cur: removeItems){
			identifierToCount.get(cur.getIdentifier()).removeElementFromList(cur);
			if (cur.getClass().toString().endsWith("FrequentPattern") &&
					this.identifierToCount.get(cur.getIdentifier()).getCount() ==1) {
				FrequentPattern fp = (FrequentPattern) this.identifierToCount.get(cur.getIdentifier()).getRefToElement().get(0);
				if(innerElements.contains(fp) || removeItems.contains(fp)){
					ManageFrequentPatternLists.removeItemFromFPList(fp.getItems().get(0), fp.getItemsInString(),
							freqPatterns);
//					this.identifierToCount.get(fp.getIdentifier()).removeElementFromList(fp);
//					this.removeFPFromWaitingElements(fp.getIdentifier());
				}else
					influencedFP.add (fp);
			}
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
		if(influencedFP.size() > 0){
			for(FrequentPattern fp: influencedFP){
				// add new generate elements to the list
				int addPos = this.mainSequence.indexOf(fp);
				ManageFrequentPatternLists.removeItemFromFPList(fp.getItems().get(0), fp.getItemsInString(),
						freqPatterns);
				LinkedList<BaseElement> newElements = fp.generateBaseElements(this.itemGap, this.seqGap,
						this.freqPatterns, true, identifierToCount);
				this.mainSequence.addAll(addPos, newElements);
				for (BaseElement newEle : newElements) {
					// update countSummary, more than one
					if (ManageIdentifierCount
							.getCountSummaryForIdentifier(newEle.getIdentifier(), this.identifierToCount)
							.addElementToList(newEle)) {
						this.MoreThanOne.add(newEle.getIdentifier());
					}
				}
				// remove previous frequent pattern from main list and
				// countSummary
				this.mainSequence.remove(fp);
				this.identifierToCount.get(fp.getIdentifier()).removeElementFromList(fp);
//				MoreThanOne.remove(fp.getIdentifier());
				this.removeFPFromWaitingElements(fp.getIdentifier());
			}
		}
		if(withMerge)
		// check if this one can merge with others
			mergeFunction.mergeElements(newFP);
	}

	public void dealWithWaitingSplit(WaitingPattern wp) {
		// found a sub match with frequent sequence, require split
		// create new frequent pattern
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<BaseElement> innerElements = new ArrayList<BaseElement>();
		String itemsInStr = "";

		ArrayList<Integer> indexesInWP = wp.getIndexesInWP();
		// find the first position of element
		int startPos = findStartPosition(wp, indexesInWP.get(0));
		if(startPos == -1)
			return;

		LinkedList<BaseElement> removeItems = new LinkedList<BaseElement>();

		for (int i = startPos; i < this.mainSequence.size(); i++) {
			if(this.mainSequence.get(i).getClass().toString().endsWith("FrequentPattern"))
				return;
		}
		// the first element might be a repeated one and should not be removed from the main sequence
		BaseElement firstElement = this.mainSequence.get(startPos);
		int index = 0;

		// find true start point
		for(int i = startPos; i< this.mainSequence.size(); i++){
			BaseElement cur = this.mainSequence.get(i);
			if(cur.getEndIndex() < indexesInWP.get(index))
				startPos++;
			else
				break;
		}
		for (int i = startPos; i < this.mainSequence.size(); i++) {
			BaseElement cur = this.mainSequence.get(i);
			if(cur.getEndIndex() < (int)indexesInWP.get(index)) {
				innerElements.add(cur);
				removeItems.add(cur);
			}
			else if(cur.getClass().toString().endsWith("SingleElement")){
				SingleElement currentElement = (SingleElement) cur;
				// first find a start point
				if(currentElement.getStartIndex() > (int) indexesInWP.get(index))
					return;
				else if (currentElement.getStartIndex() < (int) indexesInWP.get(index)){
					currentElement.setEndIndex((int)indexesInWP.get(index)-1);
					for(int ii = (int)indexesInWP.get(index); ii<= currentElement.getEndIndex(); ii++){
						if(ii == (int)indexesInWP.get(index)) {
							indexes.add(ii);
							items.add(currentElement.getItemsInString());
							itemsInStr += currentElement.getItemsInString() + ",";
							index++;
						}else
							return;
					}
				}else{
					if(currentElement.isRepeatItem())
						return;
					for(int ii = currentElement.getStartIndex(); ii<= currentElement.getEndIndex(); ii++){
						if(ii == (int) indexesInWP.get(index)) {
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
		}
		for(BaseElement cur: removeItems){
			identifierToCount.get(cur.getIdentifier()).removeElementFromList(cur);
			if (identifierToCount.get(cur.getIdentifier()).getCount() < 2) {
				MoreThanOne.remove(cur.getIdentifier());
			}
		}
		if (itemsInStr.length() > 0) {
			itemsInStr = itemsInStr.substring(0, itemsInStr.length() - 1);
		}
		int IdForNewFP = ManageIdentifiers.getIdentifierForString(itemsInStr);
		ArrayList<BaseElement> mergeCandidates = new ArrayList<BaseElement>();
		FrequentPattern newFP = new FrequentPattern(IdForNewFP, items, indexes, innerElements, itemsInStr);
		mergeCandidates.add(newFP);

		// remove single elements from list and add new to the main sequence
		this.mainSequence.removeAll(removeItems);
		this.mainSequence.add(newFP);
		ManageIdentifierCount.getCountSummaryForIdentifier(IdForNewFP, this.identifierToCount).addElementToList(newFP);

		// split previous frequent sequences to new sequence and maybe a single
		// element
		LinkedList<BaseElement> modifyElements = new LinkedList<BaseElement>(ManageIdentifierCount
				.getCountSummaryForIdentifier(wp.getIdentifier(), this.identifierToCount).getRefToElement());
		String oldFPInString = modifyElements.get(0).getItemsInString();
		for (BaseElement ele : modifyElements) {
			// for each element that is going to modify
			this.SplitFrequentPattern((FrequentPattern) ele, newFP.getItems(), mergeCandidates, newFP);
		}
		// delete from identifierToCount
		this.identifierToCount.get(wp.getIdentifier()).clearCountSummary();
		// delete previous frequent sequence from morethanone
		MoreThanOne.remove(wp.getIdentifier());
		// delete previous frequent sequence from fp list
		this.freqPatterns.get(newFP.getItems().get(0)).remove(oldFPInString);

		// add to morethanone and fplist
		MoreThanOne.add(newFP.getIdentifier());
		ManageFrequentPatternLists.addItemsToFPList(freqPatterns, newFP.getItems().get(0), newFP.getItemsInString());
		mergeFunction.mergeElements(mergeCandidates);
	}


	public void SplitFrequentPattern(FrequentPattern oldFP, ArrayList<String> items,
                                     ArrayList<BaseElement> mergeCandidates, FrequentPattern newFP) {
		LinkedList<BaseElement> splitResults = new LinkedList<BaseElement>();
		ArrayList<Integer> oldIndexes = oldFP.getIndexesForItems();
		ArrayList<BaseElement> oldInner = oldFP.getInnerElements();
		ArrayList<String> oldItems = oldFP.getItems();

		ArrayList<Integer> newIndexes = new ArrayList<Integer>(oldIndexes.subList(0, items.size()));
		ArrayList<BaseElement> innerElements = new ArrayList<BaseElement>();
		ArrayList<String> newItems = new ArrayList<String>(newFP.getItems());
		int lastIndex = newIndexes.get(items.size() - 1);
		ArrayList<BaseElement> remainingInner = new ArrayList<BaseElement>();

		for (int i = 0; i < oldInner.size(); i++) {
			if (oldInner.get(i).getStartIndex() < lastIndex) {
				innerElements.add(oldInner.get(i));
			} else {
				remainingInner.add(oldInner.get(i));
			}
		}
		// create new element, and add to list, count and merge candidate
		FrequentPattern resultFP = new FrequentPattern(newFP.getIdentifier(), newItems, newIndexes, innerElements,
				newFP.getItemsInString());
		splitResults.add(resultFP);
		ManageIdentifierCount.getCountSummaryForIdentifier(newFP.getIdentifier(), this.identifierToCount)
				.addElementToList(resultFP);
		mergeCandidates.add(resultFP);

		// deal with remaining elements
		ArrayList<BaseElement> remainingMainItems = new ArrayList<BaseElement>();
		for (int i = items.size(); i < oldItems.size(); i++) {
			int curId = ManageIdentifiers.getIdentifierForString(oldItems.get(i));
			SingleElement newEle = new SingleElement(curId, oldItems.get(i), oldIndexes.get(i));
			remainingMainItems.add(newEle);
		}

		LinkedList<BaseElement> finalRemainingElements = GeneralTools.mergeTwoList(remainingInner, remainingMainItems);
		// merge frequent sequence if possible
		HashMap<String, WaitingList> waitingElements = new HashMap<String, WaitingList>();
		LinkedList<BaseElement> copyElements = new LinkedList<BaseElement>();
		copyElements.addAll(finalRemainingElements);
		for (BaseElement currentElement : copyElements) {
			ManageWaitingTool.CheckMatchWithFP(currentElement, waitingElements, itemGap, seqGap, freqPatterns,
					finalRemainingElements, identifierToCount);
		}

		// add other elements and remaining inner elements in certain order
		splitResults.addAll(finalRemainingElements);
		mergeCandidates.addAll(finalRemainingElements);
		for (BaseElement curEle : finalRemainingElements) {
			// identifierToCount for single elements
			if (ManageIdentifierCount.getCountSummaryForIdentifier(curEle.getIdentifier(), this.identifierToCount)
					.addElementToList(curEle)) {
				// add to morethan for single elements
				MoreThanOne.add(curEle.getIdentifier());
				// add to fp if is frequentPattern
				if (curEle.getClass().toString().endsWith("FrequentPattern")) {
					ManageFrequentPatternLists.addItemsToFPList(freqPatterns,
							((FrequentPattern) curEle).getItems().get(0), curEle.getItemsInString());
				}
			}
		}

		// add new split results to main sequence
		int addPos = this.mainSequence.indexOf(oldFP);
		this.mainSequence.addAll(addPos, splitResults);
		// delete previous frequent sequence from main sequence
		this.mainSequence.remove(oldFP);
	}

	public int selectWaitingPattern(){
		BaseElement currentElement = this.mainSequence.getLast();
		String currentItem = currentElement.getItemsInString();
		// first check all sequences that are waiting for this element
		if (ManageWaitingTool.checkValidationOfWL(currentItem, waitingElements)) {
			LinkedList<WaitingPattern> waitingPatternList = waitingElements.get(currentItem).getWaitingPatternList();
			for (WaitingPattern wp : waitingPatternList) {
				if(ManageIdentifierCount.getCountSummaryForIdentifier(wp.getIdentifier(), identifierToCount).getCount() < 2)
					continue;
				int benefit = wp.computeWaitingListBenefit(currentElement, itemGap, seqGap);
				if(benefit > 0)
					return benefit;
			}
		} // end not empty waiting list
		return -1;
	}
	public void checkWaitingPatternLists() {
		BaseElement currentElement = this.mainSequence.getLast();
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
						this.dealWithTotalMatchWaiting(wp, true);
						this.waitingElements.clear();
						return;
					}
//					else if (wp.worthSplit(identifierToCount.get(wp.getIdentifier()).getCount())) {
//						// do split and remerge
//						this.dealWithWaitingSplit(wp);
//						this.waitingElements.clear();
//						return;
//					}
					else {
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

	/**
	 * Deal with patterns such as AABB AABB AABB
	 */
	public void updateWaitingPatternListsForRepeatLetter() {
		SingleElement currentElement =  (SingleElement) this.mainSequence.getLast();
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
						this.dealWithTotalMatchWaiting(wp, true);
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

	/**
	 * After merge, something that inside the waiting list should be updated
	 * Example: ABC ABC ABD ABC, when we have ABD AB, we will get AB as frequent, then waiting list of ABC crashed.
	 * METHOD: Traverse each pattern in waiting list, update the latest several elements with the new merged one
	 */
	public void updateWaitingPatternListForMerge(String currentElement){
		HashMap<String, WaitingList> newWaitingElementsInList = new HashMap<String, WaitingList>();
		// only keep the waitinglist that is waiting for this current element
		if (ManageWaitingTool.checkValidationOfWL(currentElement, waitingElements)) {
			LinkedList<WaitingPattern> waitingPatternList = waitingElements.get(currentElement).getWaitingPatternList();
			// need this element again, add to this bag
			LinkedList<WaitingPattern> tempBag = new LinkedList<WaitingPattern>();
			for (WaitingPattern wp : waitingPatternList) {
				if(ManageIdentifierCount.getCountSummaryForIdentifier(wp.getIdentifier(), identifierToCount).getCount() < 2)
					continue;
				// first update waiting pattern to accept this new element
				BaseElement currentBaseElement = this.mainSequence.getLast();
				if (wp.updateWaitingPatternWithMerge(currentBaseElement, itemGap, seqGap)) {
					if (!wp.totalMatch()) {
						String nextElement = wp.getNextWaitingElement();
						if (newWaitingElementsInList.containsKey(nextElement)) {
							newWaitingElementsInList.get(nextElement).addNewPatternToList(wp);
						} else {
							newWaitingElementsInList.put(nextElement, new WaitingList(nextElement));
							newWaitingElementsInList.get(nextElement).addNewPatternToList(wp);
						}
					}
				}
			}
		}
		this.waitingElements.clear();
		this.waitingElements.putAll(newWaitingElementsInList);
	}

	public void generateNewWaitingPatternToList(String currentItem, BaseElement currentElement) {
		HashSet<String> freqPatternCandidates = freqPatterns.get(currentItem);
		if (freqPatternCandidates != null && freqPatternCandidates.size() > 0) {
			for (String fp : freqPatternCandidates) {
				// add a new WaitlingElement to waitingList
				ArrayList<String> subs = new ArrayList<String>(Arrays.asList((fp.split(","))));
				WaitingPattern wp = new WaitingPattern(ManageIdentifiers.getIdentifierForString(fp), subs, 0,
						currentElement);
				ManageWaitingTool.addToWaitingElementList(wp, subs.get(1), waitingElements);
			}
		}
	}
}
