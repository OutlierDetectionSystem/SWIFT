package streaming.batchopt.patterngen;

import com.sun.xml.internal.rngom.parse.host.Base;
import streaming.base.CountSummary;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.FrequentPattern;
import streaming.base.atomics.SingleElement;
import streaming.base.waitinglist.WaitingList;

import streaming.batchopt.tools.ManageWaitingLists;
import streaming.patterngen.ExpireElements;
import streaming.tools.ManageFrequentPatternLists;
import streaming.tools.ManageIdentifierCount;

import java.util.*;

public class ExpireElementsForBatch extends ExpireElements{
	private HashMap<Integer, HashSet<FrequentPattern>> splitHoldingCandidates;
	private ArrayList<BaseElement> mergeElementCandidatesInPartSplit = new ArrayList<BaseElement>();
	public ExpireElementsForBatch(LinkedList<BaseElement> mainSequence, HashMap<String, HashSet<String>> freqPatterns,
								  HashMap<Integer, CountSummary> identifierToCount, HashSet<Integer> MoreThanOne, int itemGap, int seqGap,
								  HashMap<String, WaitingList> waitingElements, MergeElementsForBatch mergeFunction,
								  ManageWaitingLists waitingListManager) {
		super(mainSequence, freqPatterns, identifierToCount, MoreThanOne, itemGap, seqGap, waitingElements, mergeFunction, waitingListManager);
		this.splitHoldingCandidates = new HashMap<Integer, HashSet<FrequentPattern>>();
	}

	public void addToSplitHoldingCandidates(FrequentPattern curFP){
		if(splitHoldingCandidates.containsKey(curFP.getIdentifier())){
			splitHoldingCandidates.get(curFP.getIdentifier()).add(curFP);
		}else{
			HashSet<FrequentPattern> newFPSet = new HashSet<>();
			newFPSet.add(curFP);
			splitHoldingCandidates.put(curFP.getIdentifier(), newFPSet);
		}
	}
	/**
	 * Expire batch size number of elements in the main function (only the last one in expire need to remerge)
	 * Maintain a split candidate set
	 * @param batchSize
	 */
	public void expireElements(int batchSize){
		int expiredCount = 0;
		try {
			while (expiredCount < batchSize) {
				BaseElement removeEle = this.mainSequence.getFirst();
				if (removeEle.getNumEventsInElement() <= batchSize - expiredCount) {
					this.totalRemove(removeEle);
					expiredCount += removeEle.getNumEventsInElement();
				} else {
					int removeEventSize = batchSize - expiredCount;
					this.partRemove(removeEle, removeEventSize);
					expiredCount += removeEventSize;
				}
			}
		}catch(java.util.NoSuchElementException e){
			e.printStackTrace();
			System.out.println(batchSize + "," + mainSequence.size() + "," + expiredCount);
			System.exit(0);
		}
	}

	public void totalRemove(BaseElement removeEle){
		int removeId = removeEle.getIdentifier();
		this.mainSequence.removeFirst();
		if (removeEle.getClass().toString().endsWith("SingleElement")) {
			this.identifierToCount.get(removeId).removeElementFromList(removeEle);
			if (this.identifierToCount.get(removeId).getCount() < 2)
				this.MoreThanOne.remove(removeId);
		}else{ // if it is a frequent pattern
			// if the fp does not exist after remove
			if (this.identifierToCount.get(removeId).getCount() <= 2 ) {
				addToSplitHoldingCandidates((FrequentPattern) removeEle);
//				this.splitHoldingCandidates.add((FrequentPattern) removeEle);
			} // end if
			else
				this.identifierToCount.get(removeId).removeElementFromList(removeEle);
		}
	}

	public void partRemove(BaseElement removeEle, int removeEventSize){
		int removeId = removeEle.getIdentifier();
		if (removeEle.getClass().toString().endsWith("SingleElement")) {
			// if it is a single element
			if (((SingleElement) removeEle).isRepeatItem()) {
				((SingleElement) removeEle).addNumberOfStartIndex(removeEventSize);
			} else {
				System.out.println("Error occur: should be removing a repeat item " + removeEle.getItemsInString() + "," + removeEventSize);
			}
		} else {

			this.mainSequence.removeFirst();
			// if the fp does not exist after remove
			if (this.identifierToCount.get(removeId).getCount() <= 2 ) {
				addToSplitHoldingCandidates((FrequentPattern) removeEle);
//				this.splitHoldingCandidates.add((FrequentPattern) removeEle);
			} // end if
			else
				this.identifierToCount.get(removeId).removeElementFromList(removeEle);

			LinkedList<BaseElement> newElements = ((FrequentPattern) removeEle).generateBaseElements(this.itemGap,
					this.seqGap, this.freqPatterns, removeEventSize, identifierToCount);
			this.mainSequence.addAll(0, newElements);

			for (BaseElement newEle : newElements) {
				// update countSummary, more than one
				if (ManageIdentifierCount.getCountSummaryForIdentifier(newEle.getIdentifier(), this.identifierToCount)
						.addElementToList(newEle)) {
					this.MoreThanOne.add(newEle.getIdentifier());
				}
				mergeElementCandidatesInPartSplit.add(newEle);
			}
		}
	}
	/**
	 * For each pattern occurrence, first delete, if the count < 2, then split, otherwise do not have to split
	 */
	public void dealWithHoldingSplits(){
		ArrayList<BaseElement> elementCandidates = new ArrayList<BaseElement>();
		for(Map.Entry<Integer, HashSet<FrequentPattern>> curFPList: this.splitHoldingCandidates.entrySet()){
			int removeId = curFPList.getKey();
			for(FrequentPattern removeEle: curFPList.getValue())
				this.identifierToCount.get(removeId).removeElementFromList(removeEle);
			if(this.identifierToCount.get(removeId).getCount() == 0){
				MoreThanOne.remove(removeId);
				FrequentPattern fp = curFPList.getValue().iterator().next();
				ManageFrequentPatternLists.removeItemFromFPList(fp.getItems().get(0), fp.getItemsInString(),
						freqPatterns);
				this.waitingListManager.removeFPFromWaitingElements(removeId);
			}
			// if the fp does not exist after remove
			if (this.identifierToCount.get(removeId).getCount() == 1 ) {
				splitRemainingFrequentPattern(removeId, elementCandidates);
			} // end if
		}
		elementCandidates.addAll(0, mergeElementCandidatesInPartSplit);
		this.mergeFunction.mergeElements(elementCandidates);
		splitHoldingCandidates.clear();
		mergeElementCandidatesInPartSplit.clear();
	}
}
