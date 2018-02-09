package baseline.seqkrimp;

import base.SingleFS;
import decay.DecayFunction;
import decay.ExpDecay;

import java.util.*;


public class SingleFSDetection extends SingleFS {
	private String inputString;
	private HashMap<String, Integer> frequentSequences;
	private String [] previousArray;
	private int MDLScore;

	public SingleFSDetection(String inputString, int windowSize, int itemGap, int seqGap, DecayFunction decayFunction) {
		super(windowSize, itemGap, seqGap, decayFunction);
		this.inputString = inputString;
	}

	public SingleFSDetection(int windowSize, int itemGap, int seqGap, DecayFunction decayFunction) {
		super(windowSize, itemGap, seqGap, decayFunction);
	}

	public void FrequentSequenceMining() {
		// this.printBasicInformation();
		String[] elements = inputString.split(",");
		for (int i = 0; i <= Math.max(0, elements.length - windowSize); i++) {
			String[] tempInputString = Arrays.copyOfRange(elements, i, Math.min(elements.length,i + windowSize));
			// first generate fs candidates
//			System.out.println(tempInputString.length);
			FSDetectionInWindow tempObj = new FSDetectionInWindow(tempInputString, this.itemGap, this.seqGap);
			tempObj.findClosedFreqSeqInOneString();
			this.frequentSequences = tempObj.greedyDetectFS();
		}
	}

	public void FrequentSequenceMiningForString(String [] inputArray){
		FSDetectionInWindow tempObj = new FSDetectionInWindow(inputArray, this.itemGap, this.seqGap);
		tempObj.findClosedFreqSeqInOneString();
		this.frequentSequences = tempObj.greedyDetectFS();
		this.MDLScore = tempObj.getMDLScore();
	}

	public void initialization(String [] inputArray){
		this.FrequentSequenceMiningForString(inputArray);
		this.previousArray = inputArray;
	}

	public void readBatchMoreElement(String [] inputArray, int batchSize){
		if(inputArray.length == 0)
			return;
		ArrayList<String> newInputs = new ArrayList<String>(Arrays.asList(inputArray));
		ArrayList<String> previousInputs = new ArrayList<String>(Arrays.asList(previousArray));
		newInputs.addAll(0, previousInputs.subList(0, windowSize-batchSize));
		String [] newInputsInArray = newInputs.toArray(new String[newInputs.size()]);
		this.FrequentSequenceMiningForString(newInputsInArray);
		this.previousArray = newInputsInArray;
	}

	public int getMDLScore(){
		return MDLScore;
	}

	public void addCurrentFreqPatternToHistroy(){
		this.historyPatterns.addNewBatch(this.frequentSequences);
	}

	public HashMap<String, Integer> getFrequentPatternsWithCounts(){
		return frequentSequences;
	}
	
	public Set<String> getFrequentPatterns(){
		return this.frequentSequences.keySet();
	}

	public void printFrequentSequence(){
		for(Map.Entry<String, Integer> entry: frequentSequences.entrySet()){
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	public static void main(String[] args) {
//		 String str = "A,B,C,A,B,C,A,B,D,A,B,C";
		String str = "A,B,C,D,A,B,C,D,A,B,C";
//		 String str = "A,B,C,A,B,C,A,B,C";
		// String str = "A,A,B,B,A,A,B,B";
//		String str = "A,A,B,A,C,B,B,D,C,C";
//		 String str ="A,C,B,B,C,B,A,B";
		SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 1, 10, new ExpDecay(0.1));
		fsdetect.FrequentSequenceMining();
		fsdetect.printFrequentSequence();
	}
}
