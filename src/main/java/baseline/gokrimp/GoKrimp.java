package baseline.gokrimp;

import java.util.*;

public class GoKrimp {
	private String[] inputString;
	private ArrayList<SingleEvent> singleItems;
	private HashMap<String, SingleEvent> singleItemsInMap;
	private int minSupport = 2;
	private int itemGap;
	private int seqGap;
	private boolean[] availability;
	private HashMap<String, Integer> frequentSequences;
//	private int MDLScore;

	public GoKrimp(String[] inputString, int itemGap, int seqGap) {
		this.setInputString(inputString);
		this.itemGap = itemGap;
		this.seqGap = seqGap;
		this.availability = new boolean[inputString.length];
		for (int i = 0; i < this.availability.length; i++)
			this.availability[i] = true;
		this.frequentSequences = new HashMap<String, Integer>();
//		this.MDLScore = inputString.length;
	}

	/**
	 * Take statistics of support number
	 */
	private void getSupportMap() {
		this.singleItemsInMap = new HashMap<String, SingleEvent>();
		for(int i = 0; i< this.inputString.length; i++){
			String temp = this.inputString[i];
			if (!singleItemsInMap.containsKey(temp))
				singleItemsInMap.put(temp, new SingleEvent(temp, 1, i));
			else
				singleItemsInMap.get(temp).addOneToSupport();
		}
		this.singleItems = new ArrayList<>(singleItemsInMap.values());
		Collections.sort(this.singleItems);
	}

	public int initCountCandidatePatterns(ArrayList<String> patternCandidate,
										  ArrayList<ArrayList<Integer>> matchingIndexes){
		int last = -1;
		for(int i = 0; i< inputString.length; i++){
			if(availability[i] && inputString[i].equals(patternCandidate.get(0))){
				last = i;
			}else if(availability[i] && inputString[i].equals(patternCandidate.get(1))){
				if(last != -1 && i - last <= itemGap+1){
					ArrayList<Integer> newMatch = new ArrayList<>();
					newMatch.add(last);
					newMatch.add(i);
					matchingIndexes.add(newMatch);
					last = -1;
				}
			}
		}
		int support = matchingIndexes.size();
//		int MDLBenefit = support * (2-1) - 2;
		int MDLBenefit = support * (2-1) - 1;
		return MDLBenefit;
	}

	public int addOneMoreEventToCandidatePattern(ArrayList<String> patternCandidate,
												 ArrayList<ArrayList<Integer>> matchingIndexes,
												 String newEvent, int prevMDL){
		ArrayList<ArrayList<Integer>> newMatchingIndexes = new ArrayList<ArrayList<Integer>>();
		int prevLength = patternCandidate.size();
		for(int i = 0; i< matchingIndexes.size()-1; i++){
			int toPos = matchingIndexes.get(i+1).get(0);
			int startPos = matchingIndexes.get(i).get(prevLength-1) + 1;
			int startIndex = matchingIndexes.get(i).get(0);
			int lastIndex = matchingIndexes.get(i).get(prevLength-1);
			for(int j = startPos; j< toPos; j++){
				if(availability[j] && inputString[j].equals(newEvent) && j-startIndex <= seqGap + 1 &&
						j-lastIndex <= itemGap+1){
					ArrayList<Integer> newOneMatch = new ArrayList<Integer>(matchingIndexes.get(i));
					newOneMatch.add(j);
					newMatchingIndexes.add(newOneMatch);
				}
			}
		}
		int matchingIndexSize = matchingIndexes.size();
		int startPos = matchingIndexes.get(matchingIndexSize-1).get(prevLength-1) + 1;
		int startIndex = matchingIndexes.get(matchingIndexSize-1).get(0);
		int lastIndex = matchingIndexes.get(matchingIndexSize-1).get(prevLength-1);
		for(int j = startPos; j< inputString.length; j++){
			if(availability[j] && inputString[j].equals(newEvent) && j-startIndex <= seqGap + 1 &&
					j-lastIndex <= itemGap+1){
				ArrayList<Integer> newOneMatch = new ArrayList<Integer>(matchingIndexes.get(matchingIndexSize-1));
				newOneMatch.add(j);
				newMatchingIndexes.add(newOneMatch);
			}
		}
		int support = newMatchingIndexes.size();
//		int MDLBenefit = support * (prevLength)-(prevLength+1);
		int MDLBenefit = support * (prevLength)- 1;
		if(MDLBenefit > prevMDL){
			patternCandidate.add(newEvent);
			matchingIndexes.clear();
			matchingIndexes.addAll(newMatchingIndexes);
		}
		return MDLBenefit;
	}

	public boolean findBestCompressing(){
		// first add two events
		if(singleItems.size() < 2)
			return false;
		ArrayList<String> patternCandidate = new ArrayList<String>();
		patternCandidate.add(singleItems.get(0).getEvent());
		patternCandidate.add(singleItems.get(1).getEvent());
		ArrayList<ArrayList<Integer>> matchingIndexes = new ArrayList<>();
		int MDLBenefit = initCountCandidatePatterns(patternCandidate, matchingIndexes);
		if(MDLBenefit <= 0 || matchingIndexes.size() < 2)
			return false;
		int indexOfSingleItems = 2;
		while(indexOfSingleItems < singleItems.size()){
			String newItem = singleItems.get(indexOfSingleItems).getEvent();
			int newMDLBenefit = addOneMoreEventToCandidatePattern(patternCandidate, matchingIndexes,
					newItem, MDLBenefit);
			if(newMDLBenefit <= MDLBenefit)
				break;
			else {
				MDLBenefit = newMDLBenefit;
				indexOfSingleItems++;
			}
		}
		// the new pattern is saved in patternCandidate, the occurrences are saved in matchingIndexes
		dealWithNewPattern(patternCandidate, matchingIndexes, MDLBenefit);
		return true;
	}

	public void dealWithNewPattern(ArrayList<String> patternCandidate,
								   ArrayList<ArrayList<Integer>> matchingIndexes, int MDLBenefit){
		String newStr = "";
		int support = matchingIndexes.size();
		for(String str: patternCandidate){
			newStr += str + ",";
			this.singleItemsInMap.get(str).removeFromSupport(support);
			if(this.singleItemsInMap.get(str).getSupport() < 2)
				this.singleItemsInMap.remove(str);
		}
		if(newStr.length() > 0)
			newStr = newStr.substring(0, newStr.length()-1);
		this.frequentSequences.put(newStr, support);
//		this.MDLScore -= MDLBenefit;
		for(ArrayList<Integer> listOfIndex: matchingIndexes){
			for(int removeId: listOfIndex){
				availability[removeId] = false;
			}
		}
		this.singleItems = new ArrayList<>(singleItemsInMap.values());
		Collections.sort(this.singleItems);
	}

	public void GoKrimpAlgorithm() {
		getSupportMap();
		while(this.singleItems.size() >= 2){
			if(!findBestCompressing()) {
				singleItemsInMap.remove(this.singleItems.get(0).getEvent());
				singleItems.remove(this.singleItems.get(0));
			}
		}
//		for(Map.Entry<String, Integer> singleFreq: frequentSequences.entrySet()){
//			System.out.println(singleFreq.getKey() + "," + singleFreq.getValue());
//		}

	}
	public String[] getInputString() {
		return inputString;
	}

	public void setInputString(String[] inputString) {
		this.inputString = inputString;
	}

	public int getMinSupport() {
		return minSupport;
	}

	public void setMinSupport(int minSupport) {
		this.minSupport = minSupport;
	}

	public int getMDLScore() {
		int MDLScore = this.inputString.length;
		for (Map.Entry<String, Integer> freqSeq : frequentSequences.entrySet()) {
			MDLScore -= (freqSeq.getKey().split(",").length-1) * freqSeq.getValue() - 1;
		}
		return MDLScore;
	}

//	public void setMDLScore(int MDLScore) {
//		this.MDLScore = MDLScore;
//	}

	public HashMap<String, Integer> getFrequentSequences() {
		return frequentSequences;
	}

	public void setFrequentSequences(HashMap<String, Integer> frequentSequences) {
		this.frequentSequences = frequentSequences;
	}

	public static void main(String[] args) {
		String s = "D,H,A,B,C,D,A,B,C,D,H,A,B,C";
//		 String s = "A,A,B,C,D,A,B,C,C,B,A,C,B,A,A,B,C";
//		String s = "B,C,A,B,C,A,B,C,A,B,C";
		//		String s = "D,H,A,B,C,E,D,A,B,C,E,D,H,A,B,C,E";
		// System.out.println("Previous sequence size: " + s.split(",").length);
		int itemGap = 2000;
		int seqGap = 2000;
		GoKrimp obj = new GoKrimp(s.split(","), itemGap, seqGap);
		obj.GoKrimpAlgorithm();
		System.out.println(obj.getMDLScore());
	}
}
