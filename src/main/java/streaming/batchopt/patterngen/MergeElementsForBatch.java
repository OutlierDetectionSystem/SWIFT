package streaming.batchopt.patterngen;

import streaming.base.CountSummary;
import streaming.base.MergeCandidate;
import streaming.base.atomics.BaseElement;
import streaming.patterngen.MergeElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MergeElementsForBatch extends MergeElements {

	public MergeElementsForBatch(LinkedList<BaseElement> mainSequence, HashMap<String, HashSet<String>> freqPatterns,
                         HashMap<Integer, CountSummary> identifierToCount, HashSet<Integer> MoreThanOne, int itemGap, int seqGap) {
		super(mainSequence, freqPatterns, identifierToCount, MoreThanOne, itemGap, seqGap);
	}

	public MergeCandidate selectMergeCandidateFromList(ArrayList<BaseElement> elementCandidates){
		ArrayList<BaseElement> removeCandidates = new ArrayList<>();
		for(BaseElement curEle: elementCandidates){
			ArrayList<BaseElement> currentEleInList = new ArrayList<>();
			currentEleInList.add(curEle);
			MergeCandidate mc = this.selectMergeCandidate(currentEleInList);
			if(mc == null)
				removeCandidates.add(curEle);
			else{
				elementCandidates.removeAll(removeCandidates);
				return mc;
			}
		}
		elementCandidates.removeAll(removeCandidates);
		return null;
	}

	public void mergeElements(ArrayList<BaseElement> elementCandidates) {
		MergeCandidate mc = this.selectMergeCandidateFromList(elementCandidates);
		while (mc != null) {
			ArrayList<BaseElement> previousCandidates = new ArrayList<>(elementCandidates);
			// merge and deal with single elements
			elementCandidates = mergeTwoElements(mc, elementCandidates);
			previousCandidates.removeAll(mc.getBackPair());
			previousCandidates.removeAll(mc.getFrontPair());
			elementCandidates.addAll(previousCandidates);
			mc = null;
			if (this.MoreThanOne.size() >= 1) {
				mc = this.selectMergeCandidateFromList(elementCandidates);
			}
		} // end while
	}

//	public void mergeElements(ArrayList<BaseElement> elementCandidates) {
//		MergeCandidate mc = this.selectMergeCandidate(elementCandidates);
//		while (mc != null) {
//			ArrayList<BaseElement> previousCandidates = new ArrayList<>(elementCandidates);
//			// merge and deal with single elements
//			elementCandidates = mergeTwoElements(mc, elementCandidates);
//			previousCandidates.removeAll(mc.getBackPair());
//			previousCandidates.removeAll(mc.getFrontPair());
//			elementCandidates.addAll(previousCandidates);
//			mc = null;
//			if (this.MoreThanOne.size() >= 1) {
//				mc = this.selectMergeCandidate(elementCandidates);
//			}
//		} // end while
//	}
}
