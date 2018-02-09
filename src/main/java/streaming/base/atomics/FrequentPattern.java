package streaming.base.atomics;

import streaming.base.CountSummary;
import streaming.base.waitinglist.WaitingList;
import streaming.batchopt.tools.ManageWaitingTool;
import streaming.tools.GeneralTools;
import streaming.tools.ManageIdentifiers;
import streaming.util.CombineSingleEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class FrequentPattern implements BaseElement {
	private int identifier;
	private String itemsInStr;
	private ArrayList<String> items;
	private ArrayList<Integer> indexesForItems;
	private ArrayList<BaseElement> innerElements;

	public FrequentPattern(int identifier, ArrayList<String> items, ArrayList<Integer> indexes,
                           ArrayList<BaseElement> innerElements, String itemsInStr) {
		this.identifier = identifier;
		this.items = items;
		this.indexesForItems = indexes;
		this.innerElements = innerElements;
		this.itemsInStr = itemsInStr;
	}

	@Override
	public boolean equals(BaseElement e){
		if(e.getClass().toString().endsWith("SingleElement"))
			return false;
		FrequentPattern ele = (FrequentPattern) e;
		if(this.identifier != ele.identifier)
			return false;
		if(!this.itemsInStr.equals(ele.itemsInStr))
			return false;
		if(this.indexesForItems.get(0) != ele.indexesForItems.get(0))
			return false;
		return true;
	}

	@Override
	public int getNumEventsInElement() {
		return indexesForItems.get(indexesForItems.size()-1)-indexesForItems.get(0) + 1;
	}

	public LinkedList<BaseElement> mergeWithInnerElements(ArrayList<BaseElement> newItems) {
		return GeneralTools.mergeTwoList(innerElements, newItems);
	}

	public LinkedList<BaseElement> generateBaseElements(int itemGap, int seqGap,
                                                        HashMap<String, HashSet<String>> freqPatterns, boolean withFirst, HashMap<Integer, CountSummary> identifierToCount) {
		ArrayList<BaseElement> newItems = new ArrayList<BaseElement>();
		int start = 0;
		if(!withFirst)
			start = 1;
		for (int i = start; i < this.items.size(); i++) {
			SingleElement singleEle = new SingleElement(ManageIdentifiers.getIdentifierForString(items.get(i)),
					items.get(i), indexesForItems.get(i));
			newItems.add(singleEle);
		}
		LinkedList<BaseElement> mergeRes = this.mergeWithInnerElements(newItems);
		mergeRes = ReplaceFrequenceSequences(mergeRes, itemGap, seqGap, freqPatterns, identifierToCount);
//		mergeRes = CombineSingleEvents.combineSingleElementsWithSameIdentifier(mergeRes);
		return mergeRes;
	}

	public LinkedList<BaseElement> generateBaseElements(int itemGap, int seqGap,
														HashMap<String, HashSet<String>> freqPatterns, int removeEventSize,
														HashMap<Integer, CountSummary> identifierToCount) {
		ArrayList<BaseElement> newItems = new ArrayList<BaseElement>();
		for (int i = 0; i < this.items.size(); i++) {
			SingleElement singleEle = new SingleElement(ManageIdentifiers.getIdentifierForString(items.get(i)),
					items.get(i), indexesForItems.get(i));
			newItems.add(singleEle);
		}
		LinkedList<BaseElement> mergeRes = this.mergeWithInnerElements(newItems);
		LinkedList<BaseElement> removeElementsList = new LinkedList<>();
		int i = 0;
		for(BaseElement baseElement: mergeRes){
			i += baseElement.getNumEventsInElement();
			removeElementsList.add(baseElement);
			if(i >= removeEventSize)
				break;
		}
		mergeRes.removeAll(removeElementsList);
		mergeRes = ReplaceFrequenceSequences(mergeRes, itemGap, seqGap, freqPatterns, identifierToCount);
//		mergeRes = CombineSingleEvents.combineSingleElementsWithSameIdentifier(mergeRes);
		return mergeRes;
	}

	// TODO after split into several pieces, check again if these pieces can
	// match current frequent sequences
	public LinkedList<BaseElement> ReplaceFrequenceSequences(LinkedList<BaseElement> mergeRes, int itemGap, int seqGap,
                                                             HashMap<String, HashSet<String>> freqPatterns, HashMap<Integer, CountSummary> identifierToCount) {
		HashMap<String, WaitingList> waitingElements = new HashMap<String, WaitingList>();
		LinkedList<BaseElement> returnResult = new LinkedList<BaseElement>();
		returnResult.addAll(mergeRes);
		for (BaseElement currentElement : mergeRes) {
			ManageWaitingTool.CheckMatchWithFP(currentElement, waitingElements, itemGap, seqGap, freqPatterns,
					returnResult, identifierToCount);
		}
		return returnResult;
	}

	@Override
	public int getStartIndex() {
		return indexesForItems.get(0);
	}

	public int getEndIndex() {
		return indexesForItems.get(indexesForItems.size() - 1);
	}

	@Override
	public int getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(int identifier) {
		this.identifier = identifier;

	}

	@Override
	public int getFrontEleItemIndex() {
		return indexesForItems.get(indexesForItems.size() - 1);
	}

	@Override
	public int getBackEleItemIndex() {
		return indexesForItems.get(0);
	}

	@Override
	public int getFrontEleSeqIndex() {
		return indexesForItems.get(0);
	}

	@Override
	public int getBackEleSeqIndex() {
		return indexesForItems.get(indexesForItems.size() - 1);
	}

	public ArrayList<String> getItems() {
		return items;
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
	}

	@Override
	public String getItemsInString() {
		return this.itemsInStr;
	}

	public ArrayList<Integer> getIndexesForItems() {
		return indexesForItems;
	}

	public void setIndexesForItems(ArrayList<Integer> indexesForItems) {
		this.indexesForItems = indexesForItems;
	}

	public ArrayList<BaseElement> getInnerElements() {
		return innerElements;
	}

	public void setInnerElements(ArrayList<BaseElement> innerElements) {
		this.innerElements = innerElements;
	}

	@Override
	public String getStartItem() {
		return this.items.get(0);
	}

	@Override
	public String printElementInfo() {
		String finalRes = identifier + "\t" + "(" + itemsInStr + ")" + "\t" + "(";
		for (String str : this.items) {
			finalRes += str + " ";
		}
		finalRes += ")" + "\t" + "[";
		for (Integer i : this.indexesForItems) {
			finalRes += i + " ";
		}
		finalRes += "]" + "\t" + "{";
		for (BaseElement e : innerElements) {
			finalRes += e.getIdentifier() + " ";
		}
		finalRes += "}";
		return finalRes;
	}
}
