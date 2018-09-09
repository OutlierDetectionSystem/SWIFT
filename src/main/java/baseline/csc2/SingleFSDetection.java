package baseline.csc2;

import base.SingleFS;
import baseline.gokrimp.GoKrimp;
import decay.DecayFunction;

import java.util.*;


public class SingleFSDetection extends SingleFS {
	private String inputString;
	private ArrayList<Episode> frequentSequences;
	private String [] previousArray;
	private int MDLScore;

	public SingleFSDetection(int windowSize, int itemGap, int seqGap, DecayFunction decayFunction) {
		super(windowSize, itemGap, seqGap, decayFunction);
	}

	public void FrequentSequenceMiningForString(String [] inputArray){
		CSC tempObj = new CSC(inputArray, this.itemGap, this.seqGap);
		tempObj.CSCMain();
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

	public double getEventCoverPercentage() {
		// compute total event type number
		HashSet<String> totalEventType = new HashSet<String>(Arrays.asList(this.previousArray));
		HashSet<String> coveredEventType = new HashSet<>();
		for (Episode entry : frequentSequences) {
			coveredEventType.addAll(entry.getContents());
		}
		return coveredEventType.size() * 1.0 / totalEventType.size();
	}

	public int getMDLScore(){
		return MDLScore;
	}
}
