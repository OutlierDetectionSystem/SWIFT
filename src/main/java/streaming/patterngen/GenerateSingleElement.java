package streaming.patterngen;

import streaming.base.CountSummary;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.SingleElement;
import streaming.tools.ManageIdentifierCount;
import streaming.tools.ManageIdentifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class GenerateSingleElement {
	private LinkedList<BaseElement> mainSequence;
	private HashMap<Integer, CountSummary> identifierToCount;
	private HashSet<Integer> MoreThanOne;

	public GenerateSingleElement(LinkedList<BaseElement> mainSequence, HashMap<Integer, CountSummary> identifierToCount,
                                 HashSet<Integer> MoreThanOne) {
		this.mainSequence = mainSequence;
		this.identifierToCount = identifierToCount;
		this.MoreThanOne = MoreThanOne;
	}

	public boolean generateNewAndCombine(String currentElement, int currentIndex) {
		SingleElement newEle = this.wrapUpSingleElement(currentElement, currentIndex);
		return combineRepeatElement(newEle);
	}

	private SingleElement wrapUpSingleElement(String currentElement, int currentIndex) {
		int currentIdentifier = ManageIdentifiers.getIdentifierForString(currentElement);
		return new SingleElement(currentIdentifier, currentElement, currentIndex);
	}

	/**
	 * combine repeat elements if exists, if there is no repeat element, add
	 * current element to the main list to deal otherwise just update the repeat
	 * element, do not have to do any merge
	 * 
	 * @param curEle
	 * @return true if needs further merge/split; false if can read another
	 *         element
	 */
	private boolean combineRepeatElement(SingleElement curEle) {
		// empty list (no frequent sequence / no existing element)
		if (mainSequence.size() < 1) {
			// add to main list
			this.mainSequence.add(curEle);
			// add count to summary
			if (ManageIdentifierCount.getCountSummaryForIdentifier(curEle.getIdentifier(), this.identifierToCount)
					.addElementToList(curEle))
				this.MoreThanOne.add(curEle.getIdentifier());
			return false;
		}
		if (mainSequence.getLast().getIdentifier() == curEle.getIdentifier()) {
			// add to existing element, as a repeat element, do not need further
			// computation
			SingleElement previousElement = (SingleElement) mainSequence.getLast();
			if (previousElement.isRepeatItem()) {
				previousElement.setIndexOfItemEnd(curEle.getStartIndex());
			} else {
				previousElement.setRepeatItem(true);
				previousElement.setIndexOfItemEnd(curEle.getStartIndex());
			}
			return false;
		} else {
			// add to main list
			this.mainSequence.add(curEle);
			// add count to summary
			if (ManageIdentifierCount.getCountSummaryForIdentifier(curEle.getIdentifier(), this.identifierToCount)
					.addElementToList(curEle))
				this.MoreThanOne.add(curEle.getIdentifier());
			return true;
		}
	}
}
