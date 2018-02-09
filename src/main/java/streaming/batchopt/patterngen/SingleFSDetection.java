package streaming.batchopt.patterngen;

import base.SingleFS;
import streaming.base.CountSummary;
import streaming.base.TimeCostStatistic;
import streaming.base.atomics.BaseElement;
import streaming.base.waitinglist.WaitingList;
import streaming.batchopt.tools.ManageWaitingLists;
import decay.DecayFunction;
import decay.ExpDecay;
import history.HistoryPatternList;
import streaming.patterngen.GenerateSingleElement;
import streaming.tools.ManageIdentifierCount;
import streaming.tools.ManageIdentifiers;
import streaming.util.CombineSingleEvents;

import java.util.*;

public class SingleFSDetection extends SingleFS {
	private int curPositionOfInput = 0;

	private LinkedList<BaseElement> mainSequence = new LinkedList<BaseElement>();
	private HashMap<Integer, CountSummary> identifierToCount = new HashMap<Integer, CountSummary>();
	private HashSet<Integer> MoreThanOne = new HashSet<Integer>();
	private HashMap<String, HashSet<String>> freqPatterns = new HashMap<String, HashSet<String>>();
	private HashMap<String, WaitingList> waitingElements = new HashMap<String, WaitingList>();
	private MergeElementsForBatch mergeFunction;
	private GenerateSingleElement generateSingleElements;
	private ManageWaitingLists waitingListManager;
	private ExpireElementsForBatch expireFunction;
	private HashMap<String, Integer> currentFreqPatterns;

	public SingleFSDetection(int windowSize, int itemGap, int seqGap, DecayFunction decayFunction) {
		super(windowSize, itemGap, seqGap, decayFunction);
		this.mergeFunction = new MergeElementsForBatch(mainSequence, freqPatterns, identifierToCount, MoreThanOne, this.itemGap,
				this.seqGap);
		this.generateSingleElements = new GenerateSingleElement(mainSequence, identifierToCount, MoreThanOne);
		this.waitingListManager = new ManageWaitingLists(mainSequence, waitingElements, identifierToCount, MoreThanOne,
				freqPatterns, this.itemGap, this.seqGap, this.mergeFunction);
		this.expireFunction = new ExpireElementsForBatch(mainSequence, freqPatterns, identifierToCount, MoreThanOne,
				this.itemGap, this.seqGap, this.waitingElements, this.mergeFunction, this.waitingListManager);
		this.historyPatterns = new HistoryPatternList(decayFunction);
	}

	/**
	 * Do not deal with it one by one, try to read all and merge from left to right
	 * @param inputArray
	 */
	public void initialization(String [] inputArray, int batchSize){
//		for(String currentElement: inputArray){
//			this.generateSingleElements.generateNewAndCombine(currentElement, curPositionOfInput);
//			curPositionOfInput++;
//		}
//		ArrayList<BaseElement> mergeCandidates = new ArrayList<>(mainSequence);
//		mergeFunction.mergeElements(mergeCandidates);
		int i = 0;
		List<String> inputArrayList = Arrays.asList(inputArray);
		System.out.println(inputArrayList.size());
		while(i < inputArray.length){
			System.out.println(i);
			if(i + batchSize < inputArray.length) {
				String[] newInput = inputArrayList.subList(i, Math.min(i + batchSize, inputArray.length)).toArray(new String[batchSize]);
				readBatchMoreElementNoExpire(newInput, batchSize);
//				System.out.println("Batch Size: " + batchSize + ", newInputsize: " + newInput.length);
				i = i + batchSize;
			}else{
				int newBatchSize = inputArray.length -i;
				String[] newInput = inputArrayList.subList(i, inputArray.length).toArray(new String[newBatchSize]);
				readBatchMoreElementNoExpire(newInput, newBatchSize);
//				System.out.println("Batch Size: " + newBatchSize + ", newInputsize: " + newInput.length);
				i = i + newBatchSize;
			}
//			printCurrentStatus();
//			break;
		}
//		printCurrentStatus();
	}

	public void readBatchMoreElementNoExpire(String [] inputArray,int batchSize){
		long start =  System.currentTimeMillis();
		// first match existing patterns
		this.matchNewBatchWithWaitingLists(inputArray);
		// check if new generated elements can merge?
		mergeEventsInNewBatch(batchSize);
		CombineSingleEvents.combineSingleElementsWithSameIdentifier(mainSequence, identifierToCount, MoreThanOne);
	}

	public void expireBatchSizeEvents(int batchSize){
		long start = System.currentTimeMillis();
		expireFunction.expireElements(batchSize);
		TimeCostStatistic.expireTime += System.currentTimeMillis()-start;
	}

	public void matchNewBatchWithWaitingLists(String [] inputArray){
		for(String currentElement: inputArray){
			if (this.generateSingleElements.generateNewAndCombine(currentElement, curPositionOfInput)) {
				this.waitingListManager.checkWaitingPatternLists();
			}else
				this.waitingListManager.updateWaitingPatternListsForRepeatLetter();
			this.curPositionOfInput++;
		}
	}

	public void mergeEventsInNewBatch(int batchSize){
		// first find where to start (determine new events)
		int startIndex = curPositionOfInput-batchSize;
//		System.out.println(startIndex);
		ArrayList<BaseElement> mergeCandidates = new ArrayList<>();
		for(int i = mainSequence.size() - 1; i >=0 ; i--){
			BaseElement curEle = mainSequence.get(i);
			if(curEle.getEndIndex() >= startIndex)
				mergeCandidates.add(0, curEle);
			else
				break;
		}
		mergeFunction.mergeElements(mergeCandidates);
	}

	public void readBatchMoreElement(String [] inputArray,int batchSize){
		// first expire batchSize events, then read in these elements
		this.expireBatchSizeEvents(batchSize);
		long start =  System.currentTimeMillis();
		// first match existing patterns
		this.matchNewBatchWithWaitingLists(inputArray);
//		printCurrentStatus();
		// deal with holding elements in expire
		expireFunction.dealWithHoldingSplits();
		// check if new generated elements can merge?
		mergeEventsInNewBatch(batchSize);
		TimeCostStatistic.mergeTime += System.currentTimeMillis()-start;
		CombineSingleEvents.combineSingleElementsWithSameIdentifier(mainSequence, identifierToCount, MoreThanOne);
//		printCurrentStatus();
	}

	public HashMap<String, Integer> getFrequentPatternsWithCounts(){
		HashMap<String,Integer> freqSeqForTS = new HashMap<String, Integer>();
		for (Map.Entry<String, HashSet<String>> entry : freqPatterns.entrySet()) {
			for(String str: entry.getValue()){
				freqSeqForTS.put(str, ManageIdentifierCount.getCountSummaryForIdentifier
						(ManageIdentifiers.getIdentifierForString(str),this.identifierToCount).getCount());
			}
		}
		return freqSeqForTS;
	}

	public void combineSingleEvents(){
		CombineSingleEvents.combineSingleElementsWithSameIdentifier(mainSequence, identifierToCount, MoreThanOne);
	}

	public void addCurrentFreqPatternToHistroy(){
		this.currentFreqPatterns = this.getFrequentPatternsWithCounts();
		this.historyPatterns.addNewBatch(this.currentFreqPatterns);
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

	public String getFreqPatternsInString(){
		String str = "";
		for (Map.Entry<String, Integer> entry : currentFreqPatterns.entrySet()) {
			int lengthFP = entry.getKey().split(",").length;
			int benefit = (lengthFP-1) * entry.getValue() -  1;
			str += "(" + entry.getKey() + "," + entry.getValue()+ ")" + "\t";
		}
		return str;
	}

	public int getMDLScore(){
		int count = windowSize;
		for (Map.Entry<String, Integer> entry : currentFreqPatterns.entrySet()) {
			int lengthFP = entry.getKey().split(",").length;
			int benefit = (lengthFP-1) * entry.getValue() -  1;
			count -= benefit;
		}
//		printCurrentStatus();
//		for (Map.Entry<String, HashSet<String>> entry : freqPatterns.entrySet()) {
//			for(String str: entry.getValue()){
//				int lengthFP = str.split(",").length;
//				int support = ManageIdentifierCount.getCountSummaryForIdentifier
//						(ManageIdentifiers.getIdentifierForString(str),this.identifierToCount).getCount();
//				int benefit = (lengthFP-1) * support -  1;
//				count -= benefit;
//			}
//		}

		return count;
	}

	// for better test
	public void printCurrentStatus() {
		int count = 0;
		count+= mainSequence.size();
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

	public HashMap<Integer, CountSummary> getIdentifierToCount() {
		return identifierToCount;
	}

	public void setIdentifierToCount(HashMap<Integer, CountSummary> identifierToCount) {
		this.identifierToCount = identifierToCount;
	}

	public ExpireElementsForBatch getExpireFunction() {
		return expireFunction;
	}

	public void setExpireFunction(ExpireElementsForBatch expireFunction) {
		this.expireFunction = expireFunction;
	}

	public HashMap<String, HashSet<String>> getFreqPatterns() {
		return freqPatterns;
	}

	public void setFreqPatterns(HashMap<String, HashSet<String>> freqPatterns) {
		this.freqPatterns = freqPatterns;
	}

	public static void main(String[] args) {
//					String str = "A,B,A,B,A,B,A,B,A,B,A,B";
//		String str = " 2, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 51, 51, 51, 4, 51, 4, 51, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 50, 51, 4, 51, 4, 4, 4, 4, 4, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 1, 4, 51, 4, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 47, 42, 39, 52, 40, 47, 18, 19, 17, 16, 17, 15, 13, 15, 13, 1, 12, 9, 7, 12, 10, 11, 13, 15, 17, 19, 19, 21, 19, 21, 13, 21, 20, 30, 20, 22, 34, 33, 34, 30, 29, 33, 34, 32, 31, 32, 31, 32, 31, 32, 27, 45, 25, 52, 39, 52, 39, 52, 37, 16, 14, 14, 16, 47, 16, 47, 40, 24, 20, 24, 23, 27, 26, 27, 32, 30, 32, 31, 32, 31, 30, 29, 20, 24, 20, 18, 40, 47, 41, 39, 47, 41, 47, 8, 6, 4, 51, 4, 51, 4, 51, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 50, 50, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 51, 51, 51, 51, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 51, 4, 51, 4, 51, 4, 51, 4, 51, 4, 47, 6, 47, 39, 52, 41, 47, 18, 20, 24, 30, 29, 30, 1, 30, 32, 31, 26, 28, 49, 28, 49, 42, 47, 49, 51, 47, 49" ;
		String str = "1241,4,571,18,21,825,630,1,33,13,31,11,20,6,1162,308,29,13,805,87,841,721,894,1401,721,752,752,890,1391,650,1783,390,617,749,1350,1110,535,1365,1037,1013,1013,721,390,921,1603,330,749,1350,1110,921,535,204,921,1013,921,1013,1441,1214,921,1013,1013,1013,443,467,1013,707,1365,1013,1013,871,871,1013,1013,1013,1013,1580,1013,467,1013,921,1013,921,1013,1013,1013,1013,1013,1013,1349,1204,1456,746,0,1034,1102,909,1176,891,1202,1176,891,1593,692,721,721,1002,22,682,921,921,921,467,398,547,195,1558,542,303,457,1271,195,1558,542,303,457,82,82,482,199,682,682,682,682,1288,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,467,548,548,398,548,548,548,548,548,548,548,195,1558,542,548,548,548,457,548,1741,548,548,548,548,548,548,328,64,316,474,1479,548,548,64,548,548,548,548,548,548,548,548,548,64,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,64,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,548,562,682,251,34,921,34,921,34,921,34,921,682,682,682,682,682,64,573,857,474,857,474,316,817,474,1572,1728,401,1513,474,580,1434,809,1341,1341,1561,1646,987,360,1288,1572,1572,393,676,1365,1757,1553,187,842,792,554,1155,1044,803,1325,916,682,316,817,474,1553,316,817,474,1553,316,817,474,1553,316,817,474,1553,4,571,18,21,825,630,1,33,475,13,31,11,20,6,1162,308,29,13,1460,841,1454,5,1629,797,752,475,752,890,466,1650,1783,390,617,749,1350,535,1110,1365,940,1057,1204,1456,746,0,1034,1102,1651,1176,891,1202,1176,891,1593,692,1288,390,1603,330,34,921,34,921,34,921,34,921,34,921,34,921,1441,34,921,34,921,1336,1741,34,921,443,682,682,682,871,871,195,1558,542,457,1271,34,921,34,921,34,921,34,921,1002,22,195,1558,542,316,474,1572,1483,7,682,682,682,573,857,474,408,1365,1480,857,474,408,1365,1480,82,682,682,857,474,857,474,316,817,474,1572,682,316,709,316,709,316,817,474,1572,316,709,316,817,474,1572,1728,401,1513,474,580,1434,809,1341,1341,1561,1646,987,360,1288,1572,1572,393,676,1365,1044,803,1325,1757,1553,480,916,682,187,842,554,1155,851,682,316,817,474,682,682,682,682,1553,682,316,817,474,682,682,682,1553,682,682,316,817,474,682,682,1553,682,682,682,316,817,474,682,1553,682,682,682,682,1553,682,316,817,474,682,682,682,762,1673,1572,1572,393,328,1755,1323,484,187,682,479,236,398,916,226,1015,652,296,1332,682,390,286,285,317,964,682,229,1759,682,682,539,1539,482,199,1288,682,682,682,686,682,857,474,4,571,18,21,825,630,1,33,13,31,11,20,6,867,308,29,13,408,1365,1480,682,316,817,474,1572,408,1365,1480,316,817,474,1572,408,1365,1480,682,682,682,871,316,817,474,1572,1483,7,682,682,752,1365,857,474,4,571,18,21,825,630,1,33,13,31,11,20,6,867,308,29,13,1623,573,316,817,474,1572,390,682,1728,401,1513,474,580,1434,1341,1646,987,360,187,1288,1572,1572,393,1553,1426,1757,682,842,792,554,1287,1155,1044,803,916,682,316,817,474,682,682,682,682,1553,682,316,817,474,682,682,682,1553,682,682,316,817,474,682,682,1553,682,682,682,316,817,474,682,1553,682,682,682,762,1673,1572,1572,393,328,1755,1323,484,682,187,479,236,398,916,226,1015,652,296,1332,682,390,286,285,317,964,682,229,1759,682,682,682,682,686,682,752,4,571,18,21,825,630,1,33,13,31,11,20,6,867,308,29,13,857,474,1623,316,817,474,1572,316,1561,709,316,1561,709,682,539,1539,482,199,1288,682,1728,401,1513,474,580,1434,1341,1646,987,360,1288,1572,1572,393,1553,1426,1757,187,842,792,554,1287,1155,851,851,316,817,474,1044,1174,1070,803,916,682,682,1174,1146,682,682,285,317,1691,285,732,317,682,1553,682,316,817,474,682,682,682,1553,682,682,682,316,817,474,682,682,1553,682,682,682,682,1553,682,682,682,316,817,474,682,1553,682,682,735,682,682,316,817,474,1553,682,682,682,682,1553,682,316,817,474,682,682,682,1553,682,682,316,817,474,682,682,682,1553,682,682,316,817,474,682,682,1553,682,871,573,682,682,316,817,474,682,1553,682,682,682,682,762,1673,1572,1572,393,328,1755,1323,484,187,479,236,398,916,226,1015,652,296,1332,682,390,286,285,317,964,682,229,1759,682,682,682,682,682,686,871,316,817,474,1572,1483,7,682,539,1539,482,1288";
		SingleFSDetection fsdetect = new SingleFSDetection(1000, 0, 10, new ExpDecay(0.1));
		fsdetect.initialization(str.split(","), 100);
		String batch = "682,573,857,474,1623,857,474,316,817,474,1572,316,1561,709,682,1728,401,1513,474,580,1434,1341,1646,987,360,1288,1572,1572,393,1553,1426,1757,187,842,554,1155,851,1044,803,851,916,682,851,851,720,682,316,817,474,682,682,682,1553,682,682,316,817,474,682,682,1553,682,682,682,682,316,817,474,1553,682,682,682,682,1553,316,817,474,682,682,682,682,682,1553,682,682,682,682,762,1673,1572,1572,393,328,1755,1323,484,187,479,236,398";
		fsdetect.readBatchMoreElement(batch.split(","),100);
		fsdetect.printCurrentStatus();
	}
}
