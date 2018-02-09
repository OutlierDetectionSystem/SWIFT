package streaming.base.waitinglist;

import streaming.base.atomics.BaseElement;

import java.util.ArrayList;

public class WaitingPattern {
	private int identifier;
	private ArrayList<String> matchPattern;
	private ArrayList<Integer> indexesInWP;
	private int currentPos;
	private BaseElement startElement;

	public WaitingPattern(int identifier, ArrayList<String> matchPattern, int currentPos, BaseElement curElement) {
		this.identifier = identifier;
		this.matchPattern = matchPattern;
		this.currentPos = currentPos;
		this.indexesInWP = new ArrayList<Integer>();
		this.indexesInWP.add(curElement.getEndIndex());
		startElement = curElement;
	}

	public boolean totalMatch() {
		if (currentPos == matchPattern.size() - 1)
			return true;
		else
			return false;
	}

	public boolean worthSplit(int countOfWP) {
		if (currentPos <= 1)
			return false;
		int cost = (matchPattern.size() - (currentPos + 1)) * countOfWP;
		int benefit = currentPos + 1;
		int gapPunishment = indexesInWP.get(indexesInWP.size()-1)-indexesInWP.get(0)-currentPos;
		if (benefit - cost - gapPunishment > 0) {
			return true;
		} else
			return false;
	}

	public String getNextWaitingElement() {
		return matchPattern.get(currentPos + 1);
	}

	public boolean updateWaitingPatternWithNew(BaseElement newElement, int itemGap, int seqGap) {
		if ((newElement.getEndIndex() - indexesInWP.get(0) > seqGap + 1)
				|| (newElement.getEndIndex() - indexesInWP.get(indexesInWP.size()-1) > itemGap + 1)
				|| (newElement.getEndIndex() - indexesInWP.get(0) > matchPattern.size() * 2 - currentPos - 1)) { // punish gap
			// cannot accept this new pattern
			return false;
		}
		this.currentPos++;
		this.indexesInWP.add(newElement.getEndIndex());
		return true;
	}
	public int computeWaitingListBenefit(BaseElement newElement, int itemGap, int seqGap){
		if ((newElement.getEndIndex() - indexesInWP.get(0) > seqGap + 1)
				|| (newElement.getEndIndex() - indexesInWP.get(indexesInWP.size()-1) > itemGap + 1)
				|| (newElement.getEndIndex() - indexesInWP.get(0) > matchPattern.size() * 2 - currentPos - 1)) { // punish gap
			// cannot accept this new pattern
			return -1;
		}
		int tempCurrentPos = currentPos+1;
		if (tempCurrentPos == matchPattern.size() - 1)
			return matchPattern.size()-1;
		return -1;
	}

	public boolean updateWaitingPatternWithMerge(BaseElement newElement, int itemGap, int seqGap){
		if(newElement.getStartIndex() > this.indexesInWP.get(0))
			return false;
		if ((newElement.getEndIndex() - indexesInWP.get(0) > seqGap + 1)
				|| (newElement.getEndIndex() - indexesInWP.get(indexesInWP.size()-1) > itemGap + 1)) {
			// cannot accept this new pattern
			return false;
		}
		this.currentPos++;
		indexesInWP.add(newElement.getEndIndex());
		return true;
	}

	public ArrayList<String> getMatchPattern() {
		return matchPattern;
	}


	public void setMatchPattern(ArrayList<String> matchPattern) {
		this.matchPattern = matchPattern;
	}

	public int getCurrentPos() {
		return currentPos;
	}

	public void setCurrentPos(int currentPos) {
		this.currentPos = currentPos;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public ArrayList<Integer> getIndexesInWP() {
		return indexesInWP;
	}

	public void setIndexesInWP(ArrayList<Integer> indexesInWP) {
		this.indexesInWP = indexesInWP;
	}

	public BaseElement getStartElement() {
		return startElement;
	}

	public void setStartElement(BaseElement startElement) {
		this.startElement = startElement;
	}

	public String printWaitingPattern(){
		String finalRes = identifier + "," + currentPos + "," + indexesInWP.get(0) +"," + indexesInWP.get(indexesInWP.size()-1) + ", [";
		for(Integer in: indexesInWP){
			finalRes += in + ",";
		}
		finalRes += "]";
		return finalRes;
	}

}
