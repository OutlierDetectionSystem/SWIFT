package streaming.patterngen;

import base.SingleFS;
import decay.DecayFunction;
import decay.ExpDecay;
import history.HistoryPatternList;
import streaming.base.CountSummary;
import streaming.base.MergeCandidate;
import streaming.base.TimeCostStatistic;
import streaming.base.atomics.BaseElement;
import streaming.base.waitinglist.WaitingList;
import streaming.tools.ManageIdentifierCount;
import streaming.tools.ManageIdentifiers;
import streaming.tools.ManageWaitingLists;
import streaming.util.CombineSingleEvents;

import java.util.*;

public class SingleFSDetection extends SingleFS {
	private int curPositionOfInput = 0;
	private LinkedList<BaseElement> mainSequence = new LinkedList<BaseElement>();
	private int currentSize = 0;

	private HashMap<Integer, CountSummary> identifierToCount = new HashMap<Integer, CountSummary>();
	private HashSet<Integer> MoreThanOne = new HashSet<Integer>();
	private HashMap<String, HashSet<String>> freqPatterns = new HashMap<String, HashSet<String>>();
	private HashMap<String, WaitingList> waitingElements = new HashMap<String, WaitingList>();
	private MergeElements mergeFunction;
	private GenerateSingleElement generateSingleElements;
	private ManageWaitingLists waitingListManager;
	private ExpireElements expireFunction;

	private HashMap<String, Integer> currentFreqPatterns;

//	private HashMap<String, Integer> maxFrequencyOffreqSeqs;
	public SingleFSDetection(String inputString, int windowSize, int itemGap, int seqGap, DecayFunction decayFunction) {
		super(windowSize, itemGap, seqGap, decayFunction);
		this.inputElements = new LinkedList<String>(Arrays.asList(inputString.split(",")));
		this.mergeFunction = new MergeElements(mainSequence, freqPatterns, identifierToCount, MoreThanOne, this.itemGap,
				this.seqGap);
		this.generateSingleElements = new GenerateSingleElement(mainSequence, identifierToCount, MoreThanOne);
		this.waitingListManager = new ManageWaitingLists(mainSequence, waitingElements, identifierToCount, MoreThanOne,
				freqPatterns, this.itemGap, this.seqGap, this.mergeFunction);
		this.expireFunction = new ExpireElements(mainSequence, freqPatterns, identifierToCount, MoreThanOne,
				this.itemGap, this.seqGap, this.waitingElements, this.mergeFunction, this.waitingListManager);
//		this.maxFrequencyOffreqSeqs = new HashMap<String, Integer>();
	}

	public SingleFSDetection(int windowSize, int itemGap, int seqGap, DecayFunction decayFunction) {
		super(windowSize, itemGap, seqGap, decayFunction);
		this.mergeFunction = new MergeElements(mainSequence, freqPatterns, identifierToCount, MoreThanOne, this.itemGap,
				this.seqGap);
		this.generateSingleElements = new GenerateSingleElement(mainSequence, identifierToCount, MoreThanOne);
		this.waitingListManager = new ManageWaitingLists(mainSequence, waitingElements, identifierToCount, MoreThanOne,
				freqPatterns, this.itemGap, this.seqGap, this.mergeFunction);
		this.expireFunction = new ExpireElements(mainSequence, freqPatterns, identifierToCount, MoreThanOne,
				this.itemGap, this.seqGap, this.waitingElements, this.mergeFunction, this.waitingListManager);
		this.historyPatterns = new HistoryPatternList(decayFunction);
	}

	public void initialization(){
		while(this.currentSize!= this.windowSize && this.curPositionOfInput < this.inputElements.size()){
			readInOneElement(this.inputElements.get(this.curPositionOfInput));
			this.curPositionOfInput++;
		}
	}

	public void initialization(String [] inputArray){
		for(String input: inputArray){
			readInOneElement(input);
			this.curPositionOfInput++;
			if(curPositionOfInput % 10000 == 0)
				System.out.println(curPositionOfInput);
		}
	}

	public void readOneMoreElement(){
		readInOneElement(this.inputElements.get(this.curPositionOfInput));
		this.curPositionOfInput++;
	}

	public void readBatchMoreElement(int batchSize){
		int index = 0;
		while(index < batchSize && this.curPositionOfInput < this.inputElements.size()){
			readInOneElement(this.inputElements.get(this.curPositionOfInput));
			this.curPositionOfInput++;
			index++;
		}
		CombineSingleEvents.combineSingleElementsWithSameIdentifier(mainSequence, identifierToCount, MoreThanOne);
	}

	public void readBatchMoreElement(String [] inputArray,int batchSize){
		for(String input: inputArray){
			readInOneElement(input);
			this.curPositionOfInput++;
		}
		CombineSingleEvents.combineSingleElementsWithSameIdentifier(mainSequence, identifierToCount, MoreThanOne);
	}

	private void readInOneElement(String currentElement){
		long start = System.currentTimeMillis();
		if (this.currentSize == this.windowSize) {
			expireFunction.expireFirstElement();
			this.currentSize--;
		}
		TimeCostStatistic.expireTime += System.currentTimeMillis()-start;
		start =  System.currentTimeMillis();

		if (this.generateSingleElements.generateNewAndCombine(currentElement, curPositionOfInput)) {
			// check if can merge
			int mergeBenefit = mergeFunction.selectMergeElements(this.mainSequence.getLast());
			int matchBenefit = waitingListManager.selectWaitingPattern();
//			System.out.println(mergeBenefit + "," + matchBenefit);
			if(mergeBenefit >= 0 || matchBenefit >= 0){
				if(mergeBenefit > matchBenefit){
					mergeFunction.mergeElements(this.mainSequence.getLast());
					this.waitingListManager.updateWaitingPatternListForMerge(currentElement);
				}
				else
					this.waitingListManager.checkWaitingPatternLists();
			}else{
				this.waitingListManager.checkWaitingPatternLists();
			}
		}
		else{
			this.waitingListManager.updateWaitingPatternListsForRepeatLetter();
		}
		TimeCostStatistic.mergeTime += System.currentTimeMillis()-start;
		this.currentSize++;
	}

	public void FrequentSequenceMining() {
		for (curPositionOfInput = 0; curPositionOfInput < inputElements.size(); curPositionOfInput++) {
			// add one new element to the list
			String currentElement = inputElements.get(curPositionOfInput);
			readInOneElement(currentElement);
//			this.printCurrentStatus();
		}
//		this.printCurrentStatus();
	}

	public HashSet<String> getFrequentPatterns(){
		HashSet<String> freqSeqForTS = new HashSet<String>();
		for (Map.Entry<String, HashSet<String>> entry : freqPatterns.entrySet()) {
			if (entry.getValue().size() > 0) {
				freqSeqForTS.addAll(entry.getValue());
			}
		}
		return freqSeqForTS;
	}

	public HashMap<String, Integer> getFrequentPatternsWithCounts(){
		HashMap<String,Integer> freqSeqForTS = new HashMap<String, Integer>();
		for (Map.Entry<String, HashSet<String>> entry : freqPatterns.entrySet()) {
			for(String str: entry.getValue()){
				freqSeqForTS.put(str, ManageIdentifierCount.getCountSummaryForIdentifier
						(ManageIdentifiers.getIdentifierForString(str),this.identifierToCount).getCount());
			}
		}
//		this.currentFreqPatterns =  freqSeqForTS;
		return freqSeqForTS;
	}

//	public void addToMaxFrequency(){
//		for (Map.Entry<String, HashSet<String>> entry : freqPatterns.entrySet()) {
//			for(String str: entry.getValue()){
//				if(maxFrequencyOffreqSeqs.containsKey(str))
//					maxFrequencyOffreqSeqs.put(str, Math.max(maxFrequencyOffreqSeqs.get(str),
//							ManageIdentifierCount.getCountSummaryForIdentifier
//							(ManageIdentifiers.getIdentifierForString(str),this.identifierToCount).getCount()));
//				else
//					maxFrequencyOffreqSeqs.put(str,ManageIdentifierCount.getCountSummaryForIdentifier
//							(ManageIdentifiers.getIdentifierForString(str),this.identifierToCount).getCount());
//			}
//		}
//	}

//	public void clearMaxFrequency(){
//		this.maxFrequencyOffreqSeqs.clear();
//	}
//
//	public HashMap<String, Integer> getMaxFrequencyOffreqSeqs() {
//		return maxFrequencyOffreqSeqs;
//	}
//
//	public void setMaxFrequencyOffreqSeqs(HashMap<String, Integer> maxFrequencyOffreqSeqs) {
//		this.maxFrequencyOffreqSeqs = maxFrequencyOffreqSeqs;
//	}

	public void addCurrentFreqPatternToHistroy(){
		this.currentFreqPatterns = this.getFrequentPatternsWithCounts();
		this.historyPatterns.addNewBatch(this.currentFreqPatterns);
	}

	public boolean hasMoreElements(){
		return (this.curPositionOfInput < this.inputElements.size());
	}

	public void printBasicInformation() {
		System.out.println("Basic Information: ");
		System.out.println("Item Gap: " + itemGap + " , Sequence Gap: " + seqGap);
	}

	//	public int getMDLScore(){
//		int count = windowSize;
//		for (Map.Entry<String, Integer> entry : currentFreqPatterns.entrySet()) {
//			int lengthFP = entry.getKey().split(",").length;
//			int benefit = (lengthFP-1) * entry.getValue() -  lengthFP;
//			count -= benefit;
//		}
//		return count;
//	}

	public int getMDLScore(){
		int count = windowSize;
		for (Map.Entry<String, Integer> entry : currentFreqPatterns.entrySet()) {
			int lengthFP = entry.getKey().split(",").length;
			int benefit = (lengthFP-1) * entry.getValue() -  1;
			count -= benefit;
		}
		return count;
	}

//	public int getMDLScore(){
//		int count = 0;
//		count+= mainSequence.size();
//		for (Map.Entry<String, HashSet<String>> entry : freqPatterns.entrySet()) {
//			if (entry.getValue().size() > 0) {
//				count+= entry.getValue().size();
////				for (String str : entry.getValue()) {
////					count += str.split(",").length;
////				}
//			}
//		}
//		return count;
//	}
	// for better test
	public void printCurrentStatus() {
		int count = 0;
		count+= mainSequence.size();
		System.out.println("Current Size: " + currentSize);
		System.out.println("Main Sequence: ");
		for (BaseElement be : mainSequence) {
			System.out.println(be.printElementInfo() + "," + be);
		}
		System.out.println("Count Summary Info: ");
		for (Map.Entry<Integer, CountSummary> entry : identifierToCount.entrySet()) {
			if (entry.getValue().getCount() > 0)
				System.out.println(entry.getKey() + "," + entry.getValue().printSummary());
		}
		System.out.println("More Than One: ");
		for (Integer i : MoreThanOne) {
			System.out.print(i + ",");
		}
		System.out.println("Frequent Pattern: ");
		for (Map.Entry<String, HashSet<String>> entry : freqPatterns.entrySet()) {
			if (entry.getValue().size() > 0) {
				System.out.print("Prefix: " + entry.getKey() + "\t");
				for (String str : entry.getValue()) {
					count += str.split(",").length;
					System.out.print(str + "\t" + ManageIdentifierCount.getCountSummaryForIdentifier
							(ManageIdentifiers.getIdentifierForString(str), identifierToCount).getCount());
				}
				System.out.println();
			}
		}
		System.out.println("Waiting Elements: ");
		for (Map.Entry<String, WaitingList> entry : waitingElements.entrySet()) {
			if(entry.getValue().getWaitingPatternList().size() > 0)
			System.out.println("Waiting: " + entry.getKey() + "," + entry.getValue().printWaitingList());
		}
		System.out.println();
		System.out.println("MDL: " + count);
	}

	public LinkedList<BaseElement> getMainSequence() {
		return mainSequence;
	}

	public void setMainSequence(LinkedList<BaseElement> mainSequence) {
		this.mainSequence = mainSequence;
	}

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}

	public HashMap<Integer, CountSummary> getIdentifierToCount() {
		return identifierToCount;
	}

	public void setIdentifierToCount(HashMap<Integer, CountSummary> identifierToCount) {
		this.identifierToCount = identifierToCount;
	}

	public HashMap<String, HashSet<String>> getFreqPatterns() {
		return freqPatterns;
	}

	public void setFreqPatterns(HashMap<String, HashSet<String>> freqPatterns) {
		this.freqPatterns = freqPatterns;
	}

	public static void main(String[] args) {
					String str = "A,B,A,B,A,B,A,B,A,B,A,B";
//		String str = " 2, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 51, 51, 51, 4, 51, 4, 51, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 50, 51, 4, 51, 4, 4, 4, 4, 4, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 1, 4, 51, 4, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 47, 42, 39, 52, 40, 47, 18, 19, 17, 16, 17, 15, 13, 15, 13, 1, 12, 9, 7, 12, 10, 11, 13, 15, 17, 19, 19, 21, 19, 21, 13, 21, 20, 30, 20, 22, 34, 33, 34, 30, 29, 33, 34, 32, 31, 32, 31, 32, 31, 32, 27, 45, 25, 52, 39, 52, 39, 52, 37, 16, 14, 14, 16, 47, 16, 47, 40, 24, 20, 24, 23, 27, 26, 27, 32, 30, 32, 31, 32, 31, 30, 29, 20, 24, 20, 18, 40, 47, 41, 39, 47, 41, 47, 8, 6, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 50, 50, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 51, 51, 51, 51, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 47, 6, 47, 39, 52, 41, 47, 18, 20, 24, 30, 29, 30, 1, 30, 32, 31, 26, 28, 49, 28, 49, 42, 47, 49, 51, 47, 49" ;
//		String str = "A,B,C,D,B,C,A,B,C,D";
		SingleFSDetection fsdetect = new SingleFSDetection(str, 500, 3, 10,
				new ExpDecay(0.1));
		fsdetect.FrequentSequenceMining();
		fsdetect.printCurrentStatus();
	}
}
