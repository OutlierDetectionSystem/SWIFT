package streaming.batchopt.patterngen;

import decay.DecayFunction;
import decay.ExpDecay;
import streaming.batchopt.patterngen.SingleFSDetection;
import streaming.util.FileUtile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class GlobalFSDetection {
	private int itemGap;
	private int seqGap;
	private int smallSupport;
	private int largeSupport;
	private ArrayList<String> inputStringArray;
	private HashMap<Integer, HashMap<String, Integer>> totalLFS;
	private HashMap<String, Integer> countForEachFS;
	private HashMap<Integer, String> deviceIdMap;
	private boolean containDeviceId;
	private HashMap<String, String> metaDataMapping;

	public GlobalFSDetection(ArrayList<String> inputStringArray, HashMap<Integer, String> deviceIdMap,
							 HashMap<String, String> metaDataMapping,int itemGap,
							 int seqGap, int smallSupport, int largeSupport,
							 boolean containDeviceId) {
		this.inputStringArray = inputStringArray;
		this.deviceIdMap = deviceIdMap;
		this.metaDataMapping = metaDataMapping;
		this.itemGap = itemGap;
		this.seqGap = seqGap;
		this.smallSupport = smallSupport;
		this.largeSupport = largeSupport;
		this.containDeviceId = containDeviceId;
	}


	public void detectGlobalFrequentSequence() {
		this.totalLFS = new HashMap<Integer, HashMap<String, Integer>>();
//		this.totalLFS = new HashMap<Integer, HashSet<String>>();
		this.countForEachFS = new HashMap<String, Integer>();
        int windowSize = 100;
        int batchSize = 10;
		for (int i = 0; i < inputStringArray.size(); i++) {
			String inputStr = inputStringArray.get(i);
//			 int windowSize = 100;
			String [] inputStrSplits = inputStr.split(",");
			if(inputStrSplits.length <= windowSize)
			    continue;
			SingleFSDetection localFS = new SingleFSDetection(windowSize, this.itemGap, this.seqGap,
                    new ExpDecay(0.1));
			localFS.initialization(Arrays.copyOfRange(inputStrSplits, 0, windowSize), batchSize);
			int currentIndex = windowSize;
			while(currentIndex + batchSize <= inputStrSplits.length){
			    localFS.readBatchMoreElement(Arrays.copyOfRange(inputStrSplits, currentIndex, currentIndex + batchSize), batchSize);
//                localFS.printCurrentStatus();
                currentIndex += batchSize;
			}
			if(currentIndex > windowSize)
			    break;
		}
	}

	public int getItemGap() {
		return itemGap;
	}

	public void setItemGap(int itemGap) {
		this.itemGap = itemGap;
	}

	public int getSeqGap() {
		return seqGap;
	}

	public void setSeqGap(int seqGap) {
		this.seqGap = seqGap;
	}

	public ArrayList<String> getInputStringArray() {
		return inputStringArray;
	}

	public void setInputStringArray(ArrayList<String> inputStringArray) {
		this.inputStringArray = inputStringArray;
	}

	public static void main(String[] args) {
		String inputPath = "data/inputData/vlc-data.txt";
		ArrayList<String> inputStringArray = new ArrayList<String>();
		HashMap<Integer, String> deviceIdMap = new HashMap<Integer, String>();
		FileUtile.readInDatasetWithDeviceIdsTwo(inputPath, inputStringArray, deviceIdMap);

        String metaDataFile = "data/inputData/vlc-dict.txt";
        HashMap<String, String> metaDataMapping = FileUtile.readInMetaDataToMemory(FileUtile.readInDataset(metaDataFile));

		long startTime = System.currentTimeMillis();
		int itemgap = 0;
		int seqGap = 100;
		int smallSupCount = 1;
		int largeSupCount = 500;
//		GlobalFSDetection globalFS = new GlobalFSDetection(inputStringArray, deviceIdMap, null,
//				itemgap, seqGap, smallSupCount, largeSupCount, true);
		GlobalFSDetection globalFS = new GlobalFSDetection(inputStringArray, deviceIdMap, metaDataMapping,
				itemgap, seqGap, smallSupCount, largeSupCount, true);
		globalFS.detectGlobalFrequentSequence();
//		globalFS.outputFSCountToFile();

		System.out.println("Compute Global Frequent Sequence takes " + (System.currentTimeMillis() - startTime) / 1000
				+ " seconds!");
	}
}
