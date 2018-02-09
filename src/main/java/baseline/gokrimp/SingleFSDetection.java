package baseline.gokrimp;

import base.SingleFS;
import decay.DecayFunction;

import java.util.*;


public class SingleFSDetection extends SingleFS {
	private String inputString;
	private HashMap<String, Integer> frequentSequences;
	private String [] previousArray;
	private int MDLScore;

	public SingleFSDetection(int windowSize, int itemGap, int seqGap, DecayFunction decayFunction) {
		super(windowSize, itemGap, seqGap, decayFunction);
	}

	public void FrequentSequenceMiningForString(String [] inputArray){
		GoKrimp tempObj = new GoKrimp(inputArray, this.itemGap, this.seqGap);
		tempObj.GoKrimpAlgorithm();
		this.frequentSequences = tempObj.getFrequentSequences();
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
}
