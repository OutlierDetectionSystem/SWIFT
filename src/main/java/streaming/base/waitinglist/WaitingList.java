package streaming.base.waitinglist;

import java.util.LinkedList;

public class WaitingList {
	private String waitingEle;
	private LinkedList<WaitingPattern> waitingPatternList = new LinkedList<WaitingPattern>();

	public WaitingList(String waitingEle, LinkedList<WaitingPattern> waitingPatternList){
		this.waitingEle = waitingEle;
		this.waitingPatternList = waitingPatternList;
	}

	public WaitingList(String waitingEle) {
		this.waitingEle = waitingEle;
	}

	public void removeFPFromWL(int fpId) {
		LinkedList<WaitingPattern> removePattern = new LinkedList<WaitingPattern>();
		for (WaitingPattern wp : this.waitingPatternList) {
			if (wp.getIdentifier() == fpId)
				removePattern.add(wp);
		}
		this.waitingPatternList.removeAll(removePattern);
	}

	public String getWaitingEle() {
		return waitingEle;
	}

	public void setWaitingEle(String waitingEle) {
		this.waitingEle = waitingEle;
	}

	public LinkedList<WaitingPattern> getWaitingPatternList() {
		return waitingPatternList;
	}

	public void setWaitingPatternList(LinkedList<WaitingPattern> waitingPatternList) {
		this.waitingPatternList = waitingPatternList;
	}

	public void addNewPatternToList(WaitingPattern wp) {
		this.waitingPatternList.add(wp);
	}

	public String printWaitingList() {
		String finalRes = "Waiting: " + waitingEle + "\n";
		for (WaitingPattern wp : waitingPatternList) {
			finalRes += "{" + wp.printWaitingPattern() + "} \t";
		}
		return finalRes;
	}

}
