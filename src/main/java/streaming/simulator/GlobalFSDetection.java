package streaming.simulator;
//
//import streaming.patterngen.SingleFSDetection;
//import streaming.util.FileUtile;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map.Entry;
//
//public class GlobalFSDetection {
//	private int itemGap;
//	private int seqGap;
//	private int smallSupport;
//	private int largeSupport;
//	private ArrayList<String> inputStringArray;
////	private HashMap<Integer, HashSet<String>> totalLFS;
//	private HashMap<Integer, HashMap<String, Integer>> totalLFS;
//	private HashMap<String, Integer> countForEachFS;
//	private HashMap<Integer, String> deviceIdMap;
//	private boolean containDeviceId;
//	private HashMap<String, String> metaDataMapping;
//
//	public GlobalFSDetection(ArrayList<String> inputStringArray, HashMap<Integer, String> deviceIdMap,
//							 HashMap<String, String> metaDataMapping,int itemGap,
//							 int seqGap, int smallSupport, int largeSupport,
//							 boolean containDeviceId) {
//		this.inputStringArray = inputStringArray;
//		this.deviceIdMap = deviceIdMap;
//		this.metaDataMapping = metaDataMapping;
//		this.itemGap = itemGap;
//		this.seqGap = seqGap;
//		this.smallSupport = smallSupport;
//		this.largeSupport = largeSupport;
//		this.containDeviceId = containDeviceId;
//	}
//
//
//	public void detectGlobalFrequentSequence() {
//		this.totalLFS = new HashMap<Integer, HashMap<String, Integer>>();
////		this.totalLFS = new HashMap<Integer, HashSet<String>>();
//		this.countForEachFS = new HashMap<String, Integer>();
//		for (int i = 0; i < inputStringArray.size(); i++) {
//			String inputStr = inputStringArray.get(i);
////			 int windowSize = 100;
//			int windowSize = Integer.MAX_VALUE;
//			SingleFSDetection localFS = new SingleFSDetection(inputStr, windowSize, this.itemGap, this.seqGap);
//			localFS.FrequentSequenceMining();
//			HashMap<String,Integer> curLFS = localFS.getFrequentPatternsWithCounts();
//
//			if (curLFS.size() > 0) {
//				totalLFS.put(i, curLFS);
//				for (String tempStr : curLFS.keySet()) {
//					if (this.countForEachFS.containsKey(tempStr)) {
//						this.countForEachFS.put(tempStr, this.countForEachFS.get(tempStr) + 1);
//					} else {
//						this.countForEachFS.put(tempStr, 1);
//					}
//				}
//			}
//			if (i % 1 == 0) {
//				System.out.println(i + "," + curLFS.size());
//			}
//		}
//	}
//
//	public void outputFSCountToFileTSLevel(String targetDirectory) {
//		System.out.println(this.countForEachFS.size());
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(targetDirectory,"LocalOutlierReport.tsv")));
//			for(Entry<Integer, HashMap<String,Integer>> curSeq: this.totalLFS.entrySet()){
//				HashMap<String,Integer> outliers = new HashMap<String,Integer>();
//				for(String curFS: curSeq.getValue().keySet()){
//					if(countForEachFS.get(curFS) <= this.smallSupport && curSeq.getValue().get(curFS) > 3)
//						outliers.put(curFS, curSeq.getValue().get(curFS));
//				}
//				if(outliers.size() > 0){
//					bw.write("Device id: " + this.deviceIdMap.get(curSeq.getKey()));
//					bw.newLine();
//					for(String tempO: outliers.keySet()){
//						bw.write(tempO + ", Frequency: " + outliers.get(tempO));
//						bw.newLine();
//						String strInMeta = "";
//						String [] subs = tempO.split(",");
//						for(String substring: subs){
//							strInMeta += this.metaDataMapping.get(substring.trim()) + "\t";
//						}
//						bw.write(strInMeta);
//						bw.newLine();
//						bw.newLine();
//
//					}
//				}
//			}
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void outputFSCountToFile() {
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter("TotalFSCount.tsv"));
//			BufferedWriter bw2 = new BufferedWriter(new FileWriter("InFreqFSWithCount.tsv"));
//			BufferedWriter bw3 = new BufferedWriter(new FileWriter("FreqFSWithCount.tsv"));
//			for (Entry<String, Integer> entry : countForEachFS.entrySet()) {
//				bw.write(entry.getKey() + "\t" + entry.getValue());
//				bw.newLine();
//				if(entry.getValue() >= this.largeSupport){
//					bw3.write(entry.getKey() + "\t" + entry.getValue());
//					bw3.newLine();
//				}
//				if(entry.getValue() <= this.smallSupport){
//					bw2.write(entry.getKey() + "\t" + entry.getValue());
//					bw2.newLine();
//				}
//			}
//			bw.close();
//			bw2.close();
//			bw3.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
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
//	public ArrayList<String> getInputStringArray() {
//		return inputStringArray;
//	}
//
//	public void setInputStringArray(ArrayList<String> inputStringArray) {
//		this.inputStringArray = inputStringArray;
//	}
//
//	public static void main(String[] args) {
//		 String inputPath = "ConnectedLogs.tsv";
////		String inputPath = "extractedClean_less10000_withId.tsv";
////		String metaPath = "extractedMeta.tsv";
////		 String inputPath = "logfile.tsv";
////		String inputPath = "data/inputData/vlc-data.txt";
//		ArrayList<String> inputStringArray = new ArrayList<String>();
//		HashMap<Integer, String> deviceIdMap = new HashMap<Integer, String>();
//		FileUtile.readInDatasetWithDeviceIds(inputPath, inputStringArray, deviceIdMap);
//
//        String metaDataFile = "data/inputData/vlc-dict.txt";
//        HashMap<String, String> metaDataMapping = FileUtile.readInMetaDataToMemory(FileUtile.readInDataset(metaDataFile));
//
//		long startTime = System.currentTimeMillis();
//		int itemgap = 10000;
//		int seqGap = 100000;
//		int smallSupCount = 1;
//		int largeSupCount = 500;
////		GlobalFSDetection globalFS = new GlobalFSDetection(inputStringArray, deviceIdMap, null,
////				itemgap, seqGap, smallSupCount, largeSupCount, true);
//		GlobalFSDetection globalFS = new GlobalFSDetection(inputStringArray, deviceIdMap, metaDataMapping,
//				itemgap, seqGap, smallSupCount, largeSupCount, true);
//		globalFS.detectGlobalFrequentSequence();
////		globalFS.outputFSCountToFile();
//
//		String outputFolder = "data/results/vlc-" + seqGap + "-"+ itemgap;
//		new File(outputFolder).delete();
//		new File(outputFolder).mkdir();
//		globalFS.outputFSCountToFileTSLevel(outputFolder);
//		System.out.println("Compute Global Frequent Sequence takes " + (System.currentTimeMillis() - startTime) / 1000
//				+ " seconds!");
//	}
//}
