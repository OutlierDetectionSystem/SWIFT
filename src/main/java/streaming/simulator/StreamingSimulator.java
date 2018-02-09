package streaming.simulator;//package streaming.outlierdetection;
//
//import streaming.patterngen.SingleFSDetection;
//import streaming.util.FileUtile;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import org.apache.commons.io.FileUtils;
//import java.util.Map;
//
///**
// * Detect violations only at the observation points,
// * otherwise just do summarize the max frequency of each freq seq
// */
//public class StreamingSimulator {
//	private int itemGap;
//	private int seqGap;
//	private ArrayList<SingleFSDetection> singleSequenceSimulator;
//	private HashSet<Integer> hasMoreElements;
//	private GlobalFreqSeq globalFS;
//	private ViolationDetection violationDetection;
//	private HashMap<Integer, HashMap<String, Integer>> maxFrequencyOffreqSeqs;
//	private int globalIndex = 10;
//	private int observationInterval = 500;
//
//	public StreamingSimulator(int itemGap, int seqGap, int minLocalSupport, int minGlobalSupport, int minViolationSupport) {
//		this.itemGap = itemGap;
//		this.seqGap = seqGap;
//		this.globalFS = new GlobalFreqSeq(minLocalSupport, minGlobalSupport, minViolationSupport);
//		this.violationDetection = new ViolationDetection();
//		this.maxFrequencyOffreqSeqs = new HashMap<Integer, HashMap<String, Integer>>();
//	}
//
//	public void initilization(int windowSize, ArrayList<String> inputStringArray){
//		this.singleSequenceSimulator =  new ArrayList<SingleFSDetection>();
//		this.hasMoreElements = new HashSet<Integer>();
//		for(int i = 0; i< inputStringArray.size(); i++){
//			String inputStr = inputStringArray.get(i);
//			SingleFSDetection singleFSDetection = new SingleFSDetection(inputStr, windowSize, this.itemGap, this.seqGap);
//			singleFSDetection.initialization();
//			this.singleSequenceSimulator.add(singleFSDetection);
//
//			if(singleFSDetection.hasMoreElements())
//				this.hasMoreElements.add(i);
//			else {
//				singleFSDetection.addToMaxFrequency();
//				outlierDetection(singleFSDetection.getMaxFrequencyOffreqSeqs(), i);
//				singleFSDetection.clearMaxFrequency();
//			}
//		}
////		System.out.println(globalFS.getCurrentFreqPatterns().size());
//		globalIndex = windowSize;
//	}
//
//	public void startStreamingSimulator(String targetDirectory, HashMap<Integer, String> deviceIdMap,
//										HashMap<String, String> metaDataMapping){
//		while(this.hasMoreElements.size() > 0){
////			System.out.println(hasMoreElements.size());
//			HashSet<Integer> tempHasMoreElements = new HashSet<Integer>();
//			globalIndex++;
//			for(Integer index: hasMoreElements){
////				System.out.println(index);
//				singleSequenceSimulator.get(index).readOneMoreElement();
//				singleSequenceSimulator.get(index).addToMaxFrequency();
//				if(singleSequenceSimulator.get(index).hasMoreElements()) {
//					tempHasMoreElements.add(index);
//					if(globalIndex % observationInterval == 0) {
//						outlierDetection(singleSequenceSimulator.get(index).getMaxFrequencyOffreqSeqs(), index);
//						singleSequenceSimulator.get(index).clearMaxFrequency();
//					}
//				}
//				else {
//					outlierDetection(singleSequenceSimulator.get(index).getMaxFrequencyOffreqSeqs(), index);
//					singleSequenceSimulator.get(index).clearMaxFrequency();
//				}
//			}
//			this.hasMoreElements = tempHasMoreElements;
//		}
//	}
//
//	public void outlierDetection(HashMap<String, Integer> freqSeqWithCount, int deviceId){
//		HashSet<String> violationCandidates = globalFS.addToTypicalPatterns(freqSeqWithCount, deviceId);
//		this.violationDetection.detectViolations(globalFS.getFreqSeqInList(), violationCandidates, deviceId);
//	}
//
//	public void outputFinalResultsToFile(String targetDirectory, HashMap<Integer, String> deviceIdMap,
//										 HashMap<String, String> metaDataMapping, int globalIndex){
//		globalFS.outputLocalOutliers(targetDirectory, deviceIdMap, metaDataMapping, globalIndex);
//		violationDetection.outputFilteredViolationResultsToFile(globalFS.getTypicalPatterns(), targetDirectory, deviceIdMap, metaDataMapping,globalIndex);
//		violationDetection.outputViolationResultsToFile(globalFS.getTypicalPatterns(), targetDirectory, deviceIdMap, metaDataMapping,globalIndex);
//		globalFS.outputTypicalPatterns(targetDirectory, deviceIdMap, metaDataMapping, globalIndex);
//	}
//
//	public void outputFinalStatus(){
//		int index=0;
//		for(SingleFSDetection singleFSDetection: singleSequenceSimulator){
//			System.out.println(index++);
//			singleFSDetection.printCurrentStatus();
//		}
//	}
//	public int getItemGap() {
//		return itemGap;
//	}
//
//	public void setItemGap(int itemGap) {
//		this.itemGap = itemGap;
//	}
//
//	public int getSeqGap() {
//		return seqGap;
//	}
//
//	public void setSeqGap(int seqGap) {
//		this.seqGap = seqGap;
//	}
//
//	public static void main(String[] args) {
//		 String inputPath = "ConnectedLogs.tsv";
////		String inputPath = "extractedClean_less10000_withId.tsv";
////		String metaPath = "extractedMeta.tsv";
////		 String inputPath = "logfile.tsv";
////		String inputPath = "data/inputData/vlc-data.txt";
//		ArrayList<String> inputStringArray = new ArrayList<String>();
//
//		HashMap<Integer, String> deviceIdMap = new HashMap<Integer, String>();
//		FileUtile.readInDatasetWithDeviceIds(inputPath, inputStringArray, deviceIdMap);
//        String metaDataFile = "data/inputData/vlc-dict.txt";
//        HashMap<String, String> metaDataMapping = FileUtile.readInMetaDataToMemory(FileUtile.readInDataset(metaDataFile));
////		String temp = inputStringArray.get(187);
////		inputStringArray.clear();
////		inputStringArray.add(temp);
//		long startTime = System.currentTimeMillis();
//		int itemgap = 2;
//		int seqGap = 10000;
//		int minLocalSupport = 5;
//		int minGlobalSupport = 10;
//		int minViolationSupport = 2;
//		String outputFolder = "results/vlc-" + seqGap + "-"+ itemgap;
//		try {
//			FileUtils.deleteDirectory(new File(outputFolder));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		new File(outputFolder).mkdir();
//
//		StreamingSimulator globalFS = new StreamingSimulator(itemgap, seqGap, minLocalSupport, minGlobalSupport, minViolationSupport);
//		globalFS.initilization(100, inputStringArray);
//		globalFS.startStreamingSimulator(outputFolder, deviceIdMap, metaDataMapping);
////		globalFS.outputFinalStatus();
//		globalFS.outputFinalResultsToFile(outputFolder, deviceIdMap, metaDataMapping, 0);
//
//		System.out.println("Compute Global Frequent Sequence takes " + (System.currentTimeMillis() - startTime) / 1000
//				+ " seconds!");
//	}
//}
