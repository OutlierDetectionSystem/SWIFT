package streaming.patterngen;

import streaming.base.CountSummary;
import streaming.base.MergeCandidate;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.FrequentPattern;
import streaming.base.atomics.SingleElement;
import streaming.tools.ComputeBenefit;
import streaming.tools.ManageFrequentPatternLists;
import streaming.tools.ManageIdentifierCount;
import streaming.tools.ManageIdentifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MergeElements {
	protected LinkedList<BaseElement> mainSequence;
	protected HashMap<Integer, CountSummary> identifierToCount;
	protected HashSet<Integer> MoreThanOne;
	protected HashMap<String, HashSet<String>> freqPatterns;
	protected int itemGap;
	protected int seqGap;

	public MergeElements(LinkedList<BaseElement> mainSequence, HashMap<String, HashSet<String>> freqPatterns,
                         HashMap<Integer, CountSummary> identifierToCount, HashSet<Integer> MoreThanOne, int itemGap, int seqGap) {
		this.mainSequence = mainSequence;
		this.identifierToCount = identifierToCount;
		this.MoreThanOne = MoreThanOne;
		this.freqPatterns = freqPatterns;
		this.itemGap = itemGap;
		this.seqGap = seqGap;
	}

	/**
	 * check if the latest inserted element can be merged with others if can
	 * merge, then merge and check merged list if the merged list can do further
	 * merge, until nothing can be merged up to now
	 * 
	 * @return true if can merge
	 */
	public boolean mergeElements(BaseElement mergeElement) {
		if ((this.identifierToCount.get(mergeElement.getIdentifier()).getCount() > 1)){
//				&& (this.MoreThanOne.size() > 1)) {
			// pick up the merge candidate
			ArrayList<BaseElement> elementCandidates = new ArrayList<BaseElement>();
			elementCandidates.add(mergeElement);
			// select merge candidate (the pair that can largely reduce the
			// number of elements in the set)
			MergeCandidate mc = this.selectMergeCandidate(elementCandidates);
			if (mc == null)
				return false;
			else {
				// if can merge, then clean the subsequence pool, since the new
				// added element cannot be subsequence for FS anymore
				while (mc != null) {
					// merge and deal with single elements
					elementCandidates = mergeTwoElements(mc, elementCandidates);
					mc = null;
					if (this.MoreThanOne.size() > 1) {
						mc = this.selectMergeCandidate(elementCandidates);
					}
				} // end while
				return true;
			}
		} else // cannot merge
			return false;
	}

	public int selectMergeElements(BaseElement mergeElement) {
		if ((this.identifierToCount.get(mergeElement.getIdentifier()).getCount() > 1)){
//				&& (this.MoreThanOne.size() > 1)) {
			// pick up the merge candidate
			ArrayList<BaseElement> elementCandidates = new ArrayList<BaseElement>();
			elementCandidates.add(mergeElement);
			// select merge candidate (the pair that can largely reduce the
			// number of elements in the set)
			MergeCandidate mc = this.selectMergeCandidate(elementCandidates);
			if(mc == null)
				return -1;
			else
				return mc.getBenefit()+mc.getLengthGain();
		} else // cannot merge
			return -1;
	}

	protected MergeCandidate selectMergeCandidate(ArrayList<BaseElement> elementCandidates){
		if(seqGap < MoreThanOne.size() || itemGap < MoreThanOne.size()){
			return selectMergeCandidateWithGap(elementCandidates);
		}else
			return selectMergeCandidateNoGap(elementCandidates);
	}

	protected ArrayList<BaseElement> mergeTwoElements(MergeCandidate mergePair,
                                                    ArrayList<BaseElement> elementCandidates) {
		elementCandidates.clear();
		// get identifier for the new pattern
		String newPattern = mergePair.getFrontPair().get(0).getItemsInString() + ","
				+ mergePair.getBackPair().get(0).getItemsInString();
		int newIdentifier = ManageIdentifiers.getIdentifierForString(newPattern);

		this.MergeMultipleElementsByPairs(mergePair, newIdentifier, elementCandidates);
		return elementCandidates;
	}

	public void mergeElements(ArrayList<BaseElement> elementCandidates) {
		MergeCandidate mc = this.selectMergeCandidate(elementCandidates);
		while (mc != null) {
			// merge and deal with single elements
			elementCandidates = mergeTwoElements(mc, elementCandidates);
			mc = null;
			if (this.MoreThanOne.size() >= 1) {
				mc = this.selectMergeCandidate(elementCandidates);
			}
		} // end while
	}

	/**
	 * Given a list of elements, find one pair that has the largest merge
	 * benefit
	 *
	 * @param elements
	 * @return null if there is no pair to merge
	 */
	public MergeCandidate selectMergeCandidateNoGap(ArrayList<BaseElement> elements) {
		// for each new element, check merge benefit, and return the candidate
		// pair with largest benefit
		HashSet<String> checkedPair = new HashSet<String>();

		MergeCandidate finalMergePair = null;
		for (BaseElement element : elements) {
			if (this.identifierToCount.get(element.getIdentifier()).getCount() <= 1)
				continue;
			int startPos = this.mainSequence.lastIndexOf(element);
			for (Integer mergedId : this.MoreThanOne) {
				if (this.identifierToCount.get(mergedId).getCount() <= 1)
					continue;
				// check if the pair has been checked before
				String checkPair = element.getIdentifier() + "," + mergedId;
				if (checkedPair.contains(checkPair))
					continue;
				else {
					// check if these two element can form a pair and
					// compute benefit
					MergeCandidate mc = ComputeBenefit.GenerateMergePairForTwoIdentifiers(
							this.identifierToCount.get(element.getIdentifier()),
							this.identifierToCount.get(mergedId), this.itemGap, this.seqGap);
					if (mc == null)
						continue;
					else { // mc != null
						if (finalMergePair != null) {
							if (mc.getBenefit() + mc.getLengthGain() > finalMergePair.getBenefit() + finalMergePair.getLengthGain())
								finalMergePair = mc;
							else if ((mc.getBenefit() + mc.getLengthGain() == finalMergePair.getBenefit() +
									finalMergePair.getLengthGain()) && (mc.getBenefit() > finalMergePair.getBenefit()))
								finalMergePair = mc;
						} else {
							finalMergePair = mc;
						}
					}
					checkedPair.add(checkPair);
				} // end search one pair
			}
			for (Integer mergedId : this.MoreThanOne) {
				if (this.identifierToCount.get(mergedId).getCount() <= 1)
					continue;
				// check if the pair has been checked before
				String checkPair = mergedId + "，" + element.getIdentifier();
				// check if the pair has been checked before
				if (checkedPair.contains(checkPair))
					continue;
				else {
					// check if these two element can form a pair and
					// compute benefit
					MergeCandidate mc = ComputeBenefit.GenerateMergePairForTwoIdentifiers(
							this.identifierToCount.get(mergedId),
							this.identifierToCount.get(element.getIdentifier()),this.itemGap, this.seqGap);
					if (mc == null)
						continue;
					else { // mc != null
						if (finalMergePair != null) {
							if (mc.getBenefit() + mc.getLengthGain() > finalMergePair.getBenefit() + finalMergePair.getLengthGain())
								finalMergePair = mc;
							else if ((mc.getBenefit() + mc.getLengthGain() == finalMergePair.getBenefit() +
									finalMergePair.getLengthGain()) && (mc.getBenefit() > finalMergePair.getBenefit()))
								finalMergePair = mc;
						} else {
							finalMergePair = mc;
						}
					}
					checkedPair.add(checkPair);
				} // end search one pair
			}
		}
		return finalMergePair;
	}


	/**
	 * Given a list of elements, find one pair that has the largest merge
	 * benefit
	 *
	 * @param elements
	 * @return null if there is no pair to merge
	 */
	public MergeCandidate selectMergeCandidateWithGap(ArrayList<BaseElement> elements) {
		// for each new element, check merge benefit, and return the candidate
		// pair with largest benefit
		HashSet<String> checkedPair = new HashSet<String>();

		MergeCandidate finalMergePair = null;
		for (BaseElement element : elements) {
			if (this.identifierToCount.get(element.getIdentifier()).getCount() <= 1)
				continue;
			int startPos = this.mainSequence.lastIndexOf(element);
			for (int i = startPos + 1; i < this.mainSequence.size(); i++) {
				BaseElement backCandidate = this.mainSequence.get(i);
				// if exceeds the gap requirements, then break
				if ((backCandidate.getBackEleItemIndex() - element.getFrontEleItemIndex() > 1 + this.itemGap)
						|| (backCandidate.getBackEleSeqIndex() - element.getFrontEleSeqIndex() > 1 + this.seqGap)) {
					break;
				} else {
					if (this.identifierToCount.get(backCandidate.getIdentifier()).getCount() <= 1)
						continue;
					finalMergePair = createMCForGivenIdsWithGap(element.getIdentifier(), backCandidate.getIdentifier(),
							finalMergePair, checkedPair);
				} // end search one pair
			} // end traverse back
			// search front to find element
			for (int i = startPos - 1; i >= 0; i--) {
				BaseElement frontCandidate = this.mainSequence.get(i);
				// if exceeds the gap requirements, then break
				if ((element.getBackEleItemIndex() - frontCandidate.getFrontEleItemIndex() > 1 + this.itemGap)
						|| (element.getBackEleSeqIndex() - frontCandidate.getFrontEleSeqIndex() > 1 + this.seqGap)) {
					break;
				} else {
					if (this.identifierToCount.get(frontCandidate.getIdentifier()).getCount() <= 1)
						continue;
					finalMergePair = createMCForGivenIdsWithGap(frontCandidate.getIdentifier(), element.getIdentifier(),
							finalMergePair, checkedPair);
				} // end search one pair
			} // end traverse front
		}
		return finalMergePair;
	}

	public MergeCandidate createMCForGivenIdsWithGap(int frontId, int backId, MergeCandidate finalMergePair,
                                                     HashSet<String> checkedPair){

		// check if the pair has been checked before
		String checkPair = frontId + "," + backId;
		if (checkedPair.contains(checkPair))
			return finalMergePair;
		else {
			// check if these two element can form a pair and
			// compute benefit
			MergeCandidate mc = ComputeBenefit.GenerateMergePairForTwoIdentifiers(
					this.identifierToCount.get(frontId),
					this.identifierToCount.get(backId), this.itemGap, this.seqGap);
			if (mc == null)
				return finalMergePair;
			else { // mc != null
				if (finalMergePair != null) {
					if(mc.getBenefit() + mc.getLengthGain() > finalMergePair.getBenefit() + finalMergePair.getLengthGain())
						finalMergePair = mc;
					else if((mc.getBenefit() + mc.getLengthGain() == finalMergePair.getBenefit() +
							finalMergePair.getLengthGain()) && (mc.getBenefit() > finalMergePair.getBenefit()))
						finalMergePair = mc;
				} else {
					finalMergePair = mc;
				}
			}
			checkedPair.add(checkPair);
		} // end else
		return finalMergePair;
	}


	public void MergeMultipleElementsByPairs(MergeCandidate mergePair, int newIdentifier,
                                             ArrayList<BaseElement> elementCandidates) {
		BaseElement front = mergePair.getFrontPair().get(0);
		BaseElement back = mergePair.getBackPair().get(0);
		if (front.getClass().toString().endsWith("SingleElement")
				&& back.getClass().toString().endsWith("SingleElement")) {
			this.MergeTwoSingleElements(mergePair, newIdentifier, elementCandidates);
		} else if (front.getClass().toString().endsWith("SingleElement")
				&& back.getClass().toString().endsWith("FrequentPattern")) {
			this.MergeSingleFrequentElements(mergePair, newIdentifier, elementCandidates);
		} else if (front.getClass().toString().endsWith("FrequentPattern")
				&& back.getClass().toString().endsWith("SingleElement")) {
			this.MergeFrequentSingleElements(mergePair, newIdentifier, elementCandidates);
		} else if (front.getClass().toString().endsWith("FrequentPattern")
				&& back.getClass().toString().endsWith("FrequentPattern")) {
			this.MergeTwoFrequentElements(mergePair, newIdentifier, elementCandidates);
		}
	}

	public ArrayList<BaseElement> UpdateInBetweenElements(BaseElement front, BaseElement back,
                                                          ArrayList<BaseElement> elementCandidates,
                                                          ArrayList<BaseElement> frontCandidates,
                                                          ArrayList<BaseElement> backCandidates) {
		ArrayList<BaseElement> innerElements = new ArrayList<BaseElement>();
		int frontPos = this.mainSequence.indexOf(front);
		int backPos = this.mainSequence.indexOf(back);
		if (backPos - frontPos != 0) {
			// add to inner elements
			for (int i = frontPos + 1; i < backPos; i++)
				innerElements.add(this.mainSequence.get(i));
			ArrayList<CountSummary> influencedFreqPattern = new ArrayList<CountSummary>();
			// delete from main sequence
			for (BaseElement e : innerElements) {
				this.mainSequence.remove(e);
				CountSummary tempCS = this.identifierToCount.get(e.getIdentifier());
				tempCS.removeElementFromList(e);
				if (tempCS.getCount() < 2 && this.MoreThanOne.contains(e.getIdentifier())){
					this.MoreThanOne.remove(e.getIdentifier());
					if(tempCS.getRefToElement().get(0).getClass().toString().endsWith("FrequentPattern")) {
						influencedFreqPattern.add(tempCS);
					}
//					this.waitingListManager.removeFPFromWaitingElements(removeId);
				} // end if
			}
			// modify influenced frequent sequence
			for(CountSummary tempCS: influencedFreqPattern){
				if(tempCS.getCount() == 0)
					continue;
				// Split the other frequent pattern if exist
				FrequentPattern fp = (FrequentPattern) tempCS.getRefToElement().get(0);
				if(frontCandidates.contains(fp) || backCandidates.contains(fp))
					continue;
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
//						MoreThanOne.remove(fp.getIdentifier());
			}
		}
		return splitInnerIntoSingles(innerElements);
	}

	public ArrayList<BaseElement> splitInnerIntoSingles(ArrayList<BaseElement> innerElements){
		ArrayList<BaseElement> refactoredInner = new ArrayList<BaseElement>();
		for(BaseElement ele: innerElements){
			if(ele.getClass().toString().endsWith("SingleElement"))
				refactoredInner.add(ele);
			else{
				FrequentPattern curFP = (FrequentPattern) ele;
				ArrayList<BaseElement> newItems = new ArrayList<BaseElement>();
				for (int i = 0; i < curFP.getItems().size(); i++) {
					SingleElement singleEle = new SingleElement(ManageIdentifiers.getIdentifierForString(curFP.getItems().get(i)),
							curFP.getItems().get(i), curFP.getIndexesForItems().get(i));
					newItems.add(singleEle);
				}
				LinkedList<BaseElement> mergeRes = curFP.mergeWithInnerElements(newItems);
				refactoredInner.addAll(mergeRes);
			}
		}
		return refactoredInner;
	}
	public void MergeTwoSingleElements(MergeCandidate mergePair, int newIdentifier,
                                       ArrayList<BaseElement> elementCandidates) {
		for (int i = 0; i < mergePair.getFrontPair().size(); i++) {
			SingleElement front = (SingleElement) mergePair.getFrontPair().get(i);
			SingleElement back = (SingleElement) mergePair.getBackPair().get(i);
			ArrayList<BaseElement> innerElements = UpdateInBetweenElements(front, back, elementCandidates,
					mergePair.getFrontPair(), mergePair.getBackPair());
			if (front.isRepeatItem() && back.isRepeatItem()) {
				this.MergeRepeatRepeatElements(front, back, innerElements, newIdentifier, elementCandidates);
			} else if (front.isRepeatItem() && !back.isRepeatItem()) {
				this.MergeRepeatSingleElements(front, back, innerElements, newIdentifier, elementCandidates);
			} else if (!front.isRepeatItem() && back.isRepeatItem()) {
				this.MergeSingleRepeatElements(front, back, innerElements, newIdentifier, elementCandidates);
			} else {
				this.MergeSingleSingleElements(front, back, innerElements, newIdentifier, elementCandidates);
			}
		}
	}

	public void MergeRepeatRepeatElements(SingleElement front, SingleElement back, ArrayList<BaseElement> innerElements,
                                          int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();
		// build up new pattern
		itemsNew.add(front.getItem());
		itemsNew.add(back.getItem());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.add(front.getEndIndex());
		front.minusOneOfEndIndex();
		indexesNew.add(back.getStartIndex());
		back.addOneOfStartIndex();

		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int backPos = this.mainSequence.indexOf(back);
		this.mainSequence.add(backPos, newFP);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItem(), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void addNewPatternToLists(int newIdentifier, FrequentPattern newFP, String prefix, String itemsInString){
		// add new pattern into count summary, MoreThanOne and FrequentPatterns
		if (ManageIdentifierCount.getCountSummaryForIdentifier(newIdentifier, this.identifierToCount)
				.addElementToList(newFP)) {
			this.MoreThanOne.add(newIdentifier);
			ManageFrequentPatternLists.addItemsToFPList(freqPatterns, prefix, itemsInString);
		}
	}

	public void MergeRepeatSingleElements(SingleElement front, SingleElement back, ArrayList<BaseElement> innerElements,
                                          int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.add(front.getItem());
		itemsNew.add(back.getItem());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.add(front.getEndIndex());
		front.minusOneOfEndIndex();
		indexesNew.add(back.getStartIndex());

		// delete back single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int backPos = this.mainSequence.indexOf(back);
		this.mainSequence.add(backPos, newFP);
		this.mainSequence.remove(back);

		// delete the single item from count summary, more than one
		deleteSingleElementFromLists(back.getIdentifier(), back);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItem(), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void deleteSingleElementFromLists(int identifier, SingleElement element){
		// delete the single item from count summary, more than one
		CountSummary frontCS = ManageIdentifierCount.getCountSummaryForIdentifier(identifier,
				this.identifierToCount);
		frontCS.removeElementFromList(element);
		if (frontCS.getCount() < 2)
			this.MoreThanOne.remove(identifier);
	}

	public void MergeSingleRepeatElements(SingleElement front, SingleElement back, ArrayList<BaseElement> innerElements,
                                          int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.add(front.getItem());
		itemsNew.add(back.getItem());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.add(front.getStartIndex());
		indexesNew.add(back.getStartIndex());
		back.addOneOfStartIndex();

		// delete front single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int frontPos = this.mainSequence.indexOf(front);
		this.mainSequence.add(frontPos, newFP);
		this.mainSequence.remove(front);

		// delete the single item from count summary, more than one
		deleteSingleElementFromLists(front.getIdentifier(), front);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItem(), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void MergeSingleSingleElements(SingleElement front, SingleElement back, ArrayList<BaseElement> innerElements,
                                          int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.add(front.getItem());
		itemsNew.add(back.getItem());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.add(front.getStartIndex());
		indexesNew.add(back.getStartIndex());

		// delete front single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int frontPos = this.mainSequence.indexOf(front);
		this.mainSequence.add(frontPos, newFP);
		this.mainSequence.remove(front);
		this.mainSequence.remove(back);

		// delete the single item from count summary, more than one
		deleteSingleElementFromLists(front.getIdentifier(), front);
		deleteSingleElementFromLists(back.getIdentifier(), back);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItem(), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void updateUnMergedFrequentSequences(CountSummary cs, ArrayList<BaseElement> elementCandidates) {
		// only split if has one left, otherwise do not have to split
		if (cs.getCount() == 1) {
			FrequentPattern fp = (FrequentPattern) cs.getRefToElement().get(0);
			// add new generate elements to the list
			int addPos = this.mainSequence.indexOf(fp);
			ManageFrequentPatternLists.removeItemFromFPList(fp.getItems().get(0), fp.getItemsInString(), freqPatterns);

			LinkedList<BaseElement> newElements = fp.generateBaseElements(this.itemGap, this.seqGap, this.freqPatterns,
					true, identifierToCount);
			this.mainSequence.addAll(addPos, newElements);
			for (BaseElement newEle : newElements) {
				// update countSummary, more than one
				if (ManageIdentifierCount.getCountSummaryForIdentifier(newEle.getIdentifier(), this.identifierToCount)
						.addElementToList(newEle)) {
					this.MoreThanOne.add(newEle.getIdentifier());
				}
				elementCandidates.add(newEle);
			}
			// remove previous frequent pattern from main list and countSummary
			this.mainSequence.remove(fp);
			cs.removeElementFromList(fp);
			MoreThanOne.remove(fp.getIdentifier());

		} // end if
	}

	public void MergeSingleFrequentElements(MergeCandidate mergePair, int newIdentifier,
                                            ArrayList<BaseElement> elementCandidates) {
		// update one by one, finally deal with that frequent sequence
		// (including split and update...)
		for (int i = 0; i < mergePair.getFrontPair().size(); i++) {
			SingleElement front = (SingleElement) mergePair.getFrontPair().get(i);
			FrequentPattern back = (FrequentPattern) mergePair.getBackPair().get(i);
			ArrayList<BaseElement> innerElements = UpdateInBetweenElements(front, back, elementCandidates,
					mergePair.getFrontPair(), mergePair.getBackPair());
			if (front.isRepeatItem()) {
				this.MergeRepeatFrequentElements(front, back, innerElements, newIdentifier, elementCandidates);
			} else {
				this.MergeSingleFrequentElements(front, back, innerElements, newIdentifier, elementCandidates);
			}
		}
		// update the frequent sequence (split if required)
		this.updateUnMergedFrequentSequences(ManageIdentifierCount.getCountSummaryForIdentifier(
				mergePair.getBackPair().get(0).getIdentifier(), this.identifierToCount), elementCandidates);
	}

	public void MergeSingleFrequentElements(SingleElement front, FrequentPattern back,
                                            ArrayList<BaseElement> innerElements, int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.add(front.getItem());
		itemsNew.addAll(back.getItems());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.add(front.getStartIndex());
		indexesNew.addAll(back.getIndexesForItems());
		innerElements.addAll(back.getInnerElements());

		// delete back single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int backPos = this.mainSequence.indexOf(back);
		this.mainSequence.add(backPos, newFP);
		this.mainSequence.remove(back);
		this.mainSequence.remove(front);

		this.deleteSingleElementFromLists(front.getIdentifier(), front);
		this.removeFrequentPatternFromLists(back.getIdentifier(), back);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItem(), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void removeFrequentPatternFromLists(int identifier, FrequentPattern element){
		// delete the frequent sequence item from count summary, more than one
		CountSummary backCS = ManageIdentifierCount.getCountSummaryForIdentifier(identifier,
				this.identifierToCount);
		backCS.removeElementFromList(element);
		if (backCS.getCount() < 2) {
			this.MoreThanOne.remove(identifier);
			ManageFrequentPatternLists.removeItemFromFPList(element.getItems().get(0), element.getItemsInString(),
					freqPatterns);
		}
	}
	public void MergeRepeatFrequentElements(SingleElement front, FrequentPattern back,
                                            ArrayList<BaseElement> innerElements, int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.add(front.getItem());
		itemsNew.addAll(back.getItems());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.add(front.getEndIndex());
		front.minusOneOfEndIndex();
		indexesNew.addAll(back.getIndexesForItems());
		innerElements.addAll(back.getInnerElements());

		// delete back single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int backPos = this.mainSequence.indexOf(back);
		this.mainSequence.add(backPos, newFP);
		this.mainSequence.remove(back);

		// delete the frequent sequence item from count summary, more than one
		this.removeFrequentPatternFromLists(back.getIdentifier(), back);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItem(), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void MergeFrequentSingleElements(MergeCandidate mergePair, int newIdentifier,
                                            ArrayList<BaseElement> elementCandidates) {
		// update one by one, finally deal with that frequent sequence
		// (including split and update...)
		for (int i = 0; i < mergePair.getFrontPair().size(); i++) {
			FrequentPattern front = (FrequentPattern) mergePair.getFrontPair().get(i);
			SingleElement back = (SingleElement) mergePair.getBackPair().get(i);
			ArrayList<BaseElement> innerElements = UpdateInBetweenElements(front, back, elementCandidates, mergePair.getFrontPair()
			, mergePair.getBackPair());
			if (back.isRepeatItem()) {
				this.MergeFrequentRepeatElements(front, back, innerElements, newIdentifier, elementCandidates);
			} else {
				this.MergeFrequentSingleElements(front, back, innerElements, newIdentifier, elementCandidates);
			}
		}
		// update the frequent sequence (split if required)
		this.updateUnMergedFrequentSequences(ManageIdentifierCount.getCountSummaryForIdentifier(
				mergePair.getFrontPair().get(0).getIdentifier(), this.identifierToCount), elementCandidates);
	}

	public void MergeFrequentSingleElements(FrequentPattern front, SingleElement back,
                                            ArrayList<BaseElement> innerElements, int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.addAll(front.getItems());
		itemsNew.add(back.getItem());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.addAll(front.getIndexesForItems());
		indexesNew.add(back.getStartIndex());

		innerElements.addAll(0, front.getInnerElements());

		// delete back single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int backPos = this.mainSequence.indexOf(back);
		this.mainSequence.add(backPos, newFP);
		this.mainSequence.remove(front);
		this.mainSequence.remove(back);

		deleteSingleElementFromLists(back.getIdentifier(), back);
		removeFrequentPatternFromLists(front.getIdentifier(), front);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItems().get(0), itemsInString);

		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void MergeFrequentRepeatElements(FrequentPattern front, SingleElement back,
                                            ArrayList<BaseElement> innerElements, int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.addAll(front.getItems());
		itemsNew.add(back.getItem());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.addAll(front.getIndexesForItems());
		indexesNew.add(back.getStartIndex());
		back.addOneOfStartIndex();

		innerElements.addAll(0, front.getInnerElements());

		// delete back single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int backPos = this.mainSequence.indexOf(back);
		this.mainSequence.add(backPos, newFP);
		this.mainSequence.remove(front);

		// delete the frequent sequence item from count summary, more than one
		removeFrequentPatternFromLists(front.getIdentifier(), front);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItems().get(0), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

	public void MergeTwoFrequentElements(MergeCandidate mergePair, int newIdentifier,
                                         ArrayList<BaseElement> elementCandidates) {
		// update one by one, finally deal with that frequent sequence
		// (including split and update...)
		for (int i = 0; i < mergePair.getFrontPair().size(); i++) {
			FrequentPattern front = (FrequentPattern) mergePair.getFrontPair().get(i);
			FrequentPattern back = (FrequentPattern) mergePair.getBackPair().get(i);
			ArrayList<BaseElement> innerElements = UpdateInBetweenElements(front, back, elementCandidates,
					mergePair.getFrontPair(), mergePair.getBackPair());
			this.MergeFrequentFrequentElements(front, back, innerElements, newIdentifier, elementCandidates);
		}
		// update the frequent sequence (split if required)
		this.updateUnMergedFrequentSequences(ManageIdentifierCount.getCountSummaryForIdentifier(
				mergePair.getFrontPair().get(0).getIdentifier(), this.identifierToCount), elementCandidates);
		this.updateUnMergedFrequentSequences(ManageIdentifierCount.getCountSummaryForIdentifier(
				mergePair.getBackPair().get(0).getIdentifier(), this.identifierToCount), elementCandidates);

	}

	public void MergeFrequentFrequentElements(FrequentPattern front, FrequentPattern back,
                                              ArrayList<BaseElement> innerElements, int newIdentifier, ArrayList<BaseElement> elementCandidates) {
		ArrayList<Integer> indexesNew = new ArrayList<Integer>();
		ArrayList<String> itemsNew = new ArrayList<String>();

		// build up new pattern
		itemsNew.addAll(front.getItems());
		itemsNew.addAll(back.getItems());
		String itemsInString = front.getItemsInString() + "," + back.getItemsInString();

		indexesNew.addAll(front.getIndexesForItems());
		indexesNew.addAll(back.getIndexesForItems());

		innerElements.addAll(0, front.getInnerElements());
		innerElements.addAll(back.getInnerElements());

		// delete back single element from main list and also delete it from
		// countSummary
		// add to correct position of main sequence
		FrequentPattern newFP = new FrequentPattern(newIdentifier, itemsNew, indexesNew, innerElements, itemsInString);
		int backPos = this.mainSequence.indexOf(back);
		this.mainSequence.add(backPos, newFP);
		this.mainSequence.remove(front);
		this.mainSequence.remove(back);

		// delete the single item from count summary, more than one
		removeFrequentPatternFromLists(back.getIdentifier(), back);
		removeFrequentPatternFromLists(front.getIdentifier(), front);

		this.addNewPatternToLists(newIdentifier, newFP, front.getItems().get(0), itemsInString);
		// add the new element as candidates to check, since that is new
		elementCandidates.add(newFP);
	}

}
