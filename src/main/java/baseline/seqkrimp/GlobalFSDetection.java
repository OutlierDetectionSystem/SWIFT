package baseline.seqkrimp;

import decay.ExpDecay;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class GlobalFSDetection {
	private String inputFilePath;
	private int itemGap;
	private int seqGap;
	private int smallSupport;
	private int largeSupport;
	private ArrayList<String> inputStringArray;
	private HashMap<Integer, Set<String>> totalLFS;
	private HashMap<String, Integer> countForEachFS;
	private HashMap<Integer, String> deviceIdMap;
	private boolean containDeviceId;
	private int windowSize;
	private HashMap<String, String> metaDataMapping;

	public GlobalFSDetection(String inputFilePath, int itemGap, int seqGap, int windowSize, int smallSupport, int largeSupport, boolean containDeviceId, String metaFilePath) {
		this.inputFilePath = inputFilePath;
		this.itemGap = itemGap;
		this.seqGap = seqGap;
		this.smallSupport = smallSupport;
		this.largeSupport = largeSupport;
		this.containDeviceId = containDeviceId;
		this.windowSize = windowSize;
//		this.readDataFile();
		this.readInMetaDataToMemory(metaFilePath);
		this.readInputStringArray();
	}

	/**
	 * read from data file
	 */
	private void readDataFile() {
		this.inputStringArray = new ArrayList<String>();
		File file = new File(inputFilePath);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;

			while ((str = in.readLine()) != null) {
				inputStringArray.add(str);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
	} // end readDataFile

	
	public void readInMetaDataToMemory(String metaDataPath) {
		this.metaDataMapping = new HashMap<String, String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(metaDataPath));
			String str;
			while ((str = br.readLine()) != null) {
				String[] subStr = str.split("\t");
				if (subStr.length < 2)
					continue;
				metaDataMapping.put(subStr[0], subStr[1]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readInputStringArray() {
		this.inputStringArray = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(this.inputFilePath));
			String str;
			int indexForString = 0;
			if (this.containDeviceId) {
				this.deviceIdMap = new HashMap<Integer, String>();
			}
			while ((str = br.readLine()) != null) {
				if (containDeviceId) {
					String[] subs = str.split("\t");
					this.inputStringArray.add(subs[1]);
					this.deviceIdMap.put(indexForString, subs[0]);
					indexForString++;
				} else
					this.inputStringArray.add(str);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Size of Input: " + this.inputStringArray.size() + ", size of device:" + this.deviceIdMap.size());
	}
	
	public void detectGlobalFrequentSequence() {
		this.totalLFS = new HashMap<Integer, Set<String>>();
		this.countForEachFS = new HashMap<String, Integer>();
		for (int i = 0; i < inputStringArray.size(); i++) {
			String inputStr = inputStringArray.get(i);
//			int windowSize = Integer.MAX_VALUE;
			SingleFSDetection localFS = new SingleFSDetection(inputStr, this.windowSize, this.itemGap, this.seqGap,
					new ExpDecay(0.1));
			localFS.FrequentSequenceMining();
			Set<String> curLFS = localFS.getFrequentPatterns();
//			System.out.println("i = " + i + ", Size: " + curLFS.size());
			if (curLFS.size() > 0) {
				totalLFS.put(i, curLFS);
				for (String tempStr : curLFS) {
					if (this.countForEachFS.containsKey(tempStr)) {
						this.countForEachFS.put(tempStr, this.countForEachFS.get(tempStr) + 1);
					} else {
						this.countForEachFS.put(tempStr, 1);
					}
				}
			} // end if
			if (i % 100 == 0 ) {
				System.out.println(i + " Finished!");
			}
		}
	}

	public void outputFSCountToFileTSLevel() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("LocalOutlierReport.tsv"));
			for(Entry<Integer, Set<String>> curSeq: this.totalLFS.entrySet()){
				HashSet<String> outliers = new HashSet<String>();
				for(String curFS: curSeq.getValue()){
					if(countForEachFS.get(curFS) <= this.smallSupport)
						outliers.add(curFS);
				}
				if(outliers.size() > 0){
					bw.write("Device id: " + this.deviceIdMap.get(curSeq.getKey()));
					bw.newLine();
					for(String tempO: outliers){
						bw.write(tempO);
						bw.newLine();
						String strInMeta = "";
						String [] subs = tempO.split(",");
						for(String substring: subs){
							strInMeta += this.metaDataMapping.get(substring) + "\t";
						}
						bw.write(strInMeta);
						bw.newLine();
						bw.newLine();
						bw.newLine();
						
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void outputFSCountToFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("TotalFSCount.tsv"));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter("InFreqFSWithCount.tsv"));
			BufferedWriter bw3 = new BufferedWriter(new FileWriter("FreqFSWithCount.tsv"));
			for (Entry<String, Integer> entry : countForEachFS.entrySet()) {
				bw.write(entry.getKey() + "\t" + entry.getValue());
				bw.newLine();
				if(entry.getValue() >= this.largeSupport){
					bw3.write(entry.getKey() + "\t" + entry.getValue());
					bw3.newLine();
				}
				if(entry.getValue() <= this.smallSupport){
					bw2.write(entry.getKey() + "\t" + entry.getValue());
					bw2.newLine();
				}
			}
			bw.close();
			bw2.close();
			bw3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
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
		// String inputPath = "ConnectedLogs.tsv";
		String inputPath = "extractedClean_less10000_withId.tsv";
		String metaPath = "extractedMeta.tsv";
		// String inputPath = "logfile.tsv";
		long startTime = System.currentTimeMillis();
		int itemgap = 1;
		int seqGap = 10;
		int smallSupCount = 1;
		int largeSupCount = 500;
		int windowSize = 100;
		GlobalFSDetection globalFS = new GlobalFSDetection(inputPath, itemgap, seqGap, windowSize, smallSupCount, largeSupCount, true, metaPath);
		globalFS.detectGlobalFrequentSequence();
//		globalFS.outputFSCountToFile();
//		globalFS.outputFSCountToFileTSLevel();
		System.out.println("Compute Global Frequent Sequence takes " + (System.currentTimeMillis() - startTime) / 1000
				+ " seconds!");
	}
}
