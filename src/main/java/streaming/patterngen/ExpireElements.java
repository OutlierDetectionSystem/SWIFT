package streaming.patterngen;

import streaming.base.CountSummary;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.FrequentPattern;
import streaming.base.atomics.SingleElement;
import streaming.base.waitinglist.WaitingList;
import streaming.tools.ManageFrequentPatternLists;
import streaming.tools.ManageIdentifierCount;
import streaming.tools.ManageWaitingLists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ExpireElements {
	protected LinkedList<BaseElement> mainSequence;
	protected HashMap<Integer, CountSummary> identifierToCount;
	protected HashSet<Integer> MoreThanOne;
	protected HashMap<String, HashSet<String>> freqPatterns;
	protected HashMap<String, WaitingList> waitingElements;
	protected MergeElements mergeFunction;
	protected int itemGap;
	protected int seqGap;
	protected ManageWaitingLists waitingListManager;

	public ExpireElements(LinkedList<BaseElement> mainSequence, HashMap<String, HashSet<String>> freqPatterns,
			HashMap<Integer, CountSummary> identifierToCount, HashSet<Integer> MoreThanOne, int itemGap, int seqGap,
			HashMap<String, WaitingList> waitingElements, MergeElements mergeFunction,
			ManageWaitingLists waitingListManager) {
		this.mainSequence = mainSequence;
		this.identifierToCount = identifierToCount;
		this.MoreThanOne = MoreThanOne;
		this.freqPatterns = freqPatterns;
		this.itemGap = itemGap;
		this.seqGap = seqGap;
		this.waitingElements = waitingElements;
		this.mergeFunction = mergeFunction;
		this.waitingListManager = waitingListManager;
	}

	public void expireFirstElement() {
		// first remove one from the beginning
		BaseElement removeEle = this.mainSequence.getFirst();
		int removeId = removeEle.getIdentifier();
		if (removeEle.getClass().toString().endsWith("SingleElement")) {
			// if it is a single element
			if (((SingleElement) removeEle).isRepeatItem()) {
				((SingleElement) removeEle).addOneOfStartIndex();
			} else {
				this.mainSequence.removeFirst();
				this.identifierToCount.get(removeId).removeElementFromList(removeEle);
				if(this.identifierToCount.get(removeId).getCount() < 2)
					this.MoreThanOne.remove(removeId);
			}
		} else {
			ArrayList<BaseElement> elementCandidates = new ArrayList<BaseElement>();
			// if it is a frequent sequence, split it and re-merge
			// remove frequent sequence from main sequence
			this.mainSequence.removeFirst();
			this.identifierToCount.get(removeId).removeElementFromList(removeEle);
			// if the fp does not exist after remove
			if (this.identifierToCount.get(removeId).getCount() < 2 ) {
				splitRemainingFrequentPattern(removeId, elementCandidates);
			} // end if
			LinkedList<BaseElement> newElements = ((FrequentPattern) removeEle).generateBaseElements(this.itemGap,
					this.seqGap, this.freqPatterns, false, identifierToCount);
			this.mainSequence.addAll(0, newElements);

			for (BaseElement newEle : newElements) {
				// update countSummary, more than one
				if (ManageIdentifierCount.getCountSummaryForIdentifier(newEle.getIdentifier(), this.identifierToCount)
						.addElementToList(newEle)) {
					this.MoreThanOne.add(newEle.getIdentifier());
				}
				elementCandidates.add(newEle);
			}
			this.mergeFunction.mergeElements(elementCandidates);
		}
	}

	public void splitRemainingFrequentPattern(int removeId, ArrayList<BaseElement> elementCandidates){
		FrequentPattern fp = (FrequentPattern) this.identifierToCount.get(removeId).getRefToElement().get(0);
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
			elementCandidates.add(newEle);
		}
		// remove previous frequent pattern from main list and
		// countSummary
		this.mainSequence.remove(fp);
		this.identifierToCount.get(fp.getIdentifier()).removeElementFromList(fp);
		MoreThanOne.remove(fp.getIdentifier());
		this.waitingListManager.removeFPFromWaitingElements(removeId);
	}
}
