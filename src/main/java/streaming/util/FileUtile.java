package streaming.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

public class FileUtile {
	// read in meta file
	public static HashMap<String, String> readInMetaDataToMemory(List<String> originalStrList) {
		System.out.println("Read in Meta data ...");
		HashMap<String, String> metaDataMapping = new HashMap<String, String>();

		for (String str : originalStrList) {
			String[] subStr = str.split("\t");
			if (subStr.length < 3)
				continue;
			metaDataMapping.put(subStr[0], "(" + subStr[1] + ")" + subStr[2]);
		}
		System.out.println("Meta data size: " + metaDataMapping.size());
		return metaDataMapping;
	}

	public static HashMap<Integer, String> readInDeviceIds(String inputPath) {
		HashMap<Integer, String> deviceNameToId = new HashMap<Integer, String>();
		File file = new File(inputPath);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;

			while ((str = in.readLine()) != null) {
				String[] subs = str.split(",");
				deviceNameToId.put(Integer.parseInt(subs[0]), subs[1]);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		return deviceNameToId;
	}

	public static HashMap<String, Integer> readInCategories(String inputPath) {
		HashMap<String, Integer> deviceToCategories = new HashMap<String, Integer>();
		File file = new File(inputPath);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;

			while ((str = in.readLine()) != null) {
				String[] subs = str.split(",");
				deviceToCategories.put(subs[0], Integer.parseInt(subs[1]));
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		return deviceToCategories;
	}

	public static HashMap<String, String> readInLargeMetaDataToMemory(List<String> originalStrList) {
		HashMap<String, String> metaDataMapping = new HashMap<String, String>();

		for (String str : originalStrList) {
			String[] subStr = str.split("\t");
			if (subStr.length < 3)
				continue;
			metaDataMapping.put(subStr[0], "(" + subStr[1] + ")" + subStr[2]);
		}
		return metaDataMapping;
	}

	public static ArrayList<String> readInDataset(String inputPath) {
		ArrayList<String> inputStringArray = new ArrayList<String>();
		File file = new File(inputPath);
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
		return inputStringArray;
	}

	public static void readInDatasetWithDeviceIds(String inputPath, ArrayList<String> inputStringArray, HashMap<Integer, String> deviceIdMap ) {
		System.out.println("Read in dataset with device id....");
		File file = new File(inputPath);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			int count = 0;
			while ((str = in.readLine()) != null) {
				String [] subs = str.split("\t");
				deviceIdMap.put(count, subs[1]);
				inputStringArray.add(subs[2]);
				count++;
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		System.out.println("Number of devices: " + deviceIdMap.size() + ", number of sequences: " + inputStringArray.size());
	}

	public static void readInDatasetWithDeviceIdsTwo(String inputPath, ArrayList<String> inputStringArray, HashMap<Integer, String> deviceIdMap ) {
		System.out.println("Read in dataset with device id....");
		File file = new File(inputPath);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			int count = 0;
			while ((str = in.readLine()) != null) {
				String [] subs = str.split("\t");
				deviceIdMap.put(count, subs[0]);
				inputStringArray.add(subs[1]);
				count++;
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		System.out.println("Number of devices: " + deviceIdMap.size() + ", number of sequences: " + inputStringArray.size());
	}

	public static void readInDeviceIds(String inputPath, HashMap<Integer, String> deviceIdMap ) {
		System.out.println("Read in device id....");
		File file = new File(inputPath);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			while ((str = in.readLine()) != null) {
				String [] subs = str.split("\t");
				deviceIdMap.put(Integer.parseInt(subs[0]), subs[1]);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		System.out.println("Number of devices: " + deviceIdMap.size());
	}


	public static void saveGlobalFrequentSequences(String fileName, HashSet<String> globalFrequentSequence) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			for (String str : globalFrequentSequence) {
				out.write(str);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int[] evaluation(HashSet<Integer> outlierDeviceIds) {
		HashMap<String, Integer> markedData = FileUtile.readInCategories("deviceCategory.csv");
		HashMap<Integer, String> deviceIds = FileUtile.readInDeviceIds("deviceIds.csv");
		System.out.println(markedData.size() + "," + deviceIds.size());
		int[] types = new int[3];
		for (Integer i : outlierDeviceIds) {
			if (markedData.containsKey(deviceIds.get(i))) {
				Integer type = markedData.get(deviceIds.get(i));
				types[type] += 1;
			} else
				continue;
		}
		System.out.println(
				"Not outliers: " + types[0] + ", Wield but not outliers: " + types[1] + ", outlies: " + types[2]);
		return types;
	}

	public static HashSet<Integer> findLocalOutliersAndSaveInFile(File fileName,
			HashMap<Integer, HashMap<String, Integer>> localFrequentSeqsStatic, int thresholdForLocalFSOutliers,
			boolean containDeviceId, HashMap<Integer, String> deviceIdMap, HashMap<String, String> metaDataMapping,
			HashMap<Integer, Integer> outlierFrequencyThreshold) {

		HashMap<String, Integer> gbFreqSeqs = new HashMap<String, Integer>();
		for (Entry<Integer, HashMap<String, Integer>> curElement : localFrequentSeqsStatic.entrySet()) {
			for (String str : curElement.getValue().keySet()) {
				// System.out.println("Temp Result: " + str);
				if (gbFreqSeqs.containsKey(str))
					gbFreqSeqs.put(str, gbFreqSeqs.get(str) + 1);
				else
					gbFreqSeqs.put(str, 1);
			}
		}

		HashSet<String> finalGBFreqSeq = new HashSet<String>();
		for (String str : gbFreqSeqs.keySet()) {
			if (gbFreqSeqs.get(str) >= thresholdForLocalFSOutliers) {
				finalGBFreqSeq.add(str);
			}
		}
		HashSet<Integer> devicesThatContainOutliers = new HashSet<Integer>();
		try {
			// BufferedWriter out = new BufferedWriter(new
			// FileWriter("OutlierRes/" + fileName + ".tsv"));
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			int numOutlierTimeSeries = 0;
			int numOutliersInTotal = 0;
			int maxOutlierInOneSeries = 0;
			for (Entry<Integer, HashMap<String, Integer>> curElement : localFrequentSeqsStatic.entrySet()) {
				HashMap<String, Integer> tempOutliers = new HashMap<String, Integer>();
				for (Entry<String, Integer> curStr : curElement.getValue().entrySet()) {
					if (!finalGBFreqSeq.contains(curStr.getKey())
							&& curStr.getValue() >= outlierFrequencyThreshold.get(curElement.getKey()))
						tempOutliers.put(curStr.getKey(), curStr.getValue());
				}
				if (tempOutliers.size() != 0) {
					numOutlierTimeSeries++;
					devicesThatContainOutliers.add(curElement.getKey());
					numOutliersInTotal += tempOutliers.size();
					maxOutlierInOneSeries = Math.max(maxOutlierInOneSeries, tempOutliers.size());
					for (Entry<String, Integer> curStr : tempOutliers.entrySet()) {
						// System.out.println(curStr);
						out.write("Sequence # " + curElement.getKey());
						if (containDeviceId)
							out.write("Device Id: " + deviceIdMap.get(curElement.getKey()));
						out.newLine();
						out.write("Frequency: " + curStr.getValue() + "\t" + curStr.getKey());
						out.newLine();
						out.newLine();
						String[] subStr = curStr.getKey().split(",");
						for (String tempStr : subStr) {
							out.write(metaDataMapping.get(tempStr) + "\t");
							// System.out.println(metaDataMapping.get(tempStr));
						}
						out.newLine();
						out.newLine();
						out.newLine();
					}
					out.newLine();
				}
			}
			out.close();
			System.out.println(numOutlierTimeSeries + " , " + numOutliersInTotal + " , " + maxOutlierInOneSeries);
			return devicesThatContainOutliers;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static HashSet<Integer> findLocalOutliersAndSaveInFile(File fileName,
			HashMap<Integer, HashMap<String, Integer>> localFrequentSeqsStatic, int thresholdForLocalFSOutliers,
			boolean containDeviceId, HashMap<Integer, String> deviceIdMap, HashMap<String, String> metaDataMapping) {

		HashMap<String, Integer> gbFreqSeqs = new HashMap<String, Integer>();
		for (Entry<Integer, HashMap<String, Integer>> curElement : localFrequentSeqsStatic.entrySet()) {
			for (String str : curElement.getValue().keySet()) {
				// System.out.println("Temp Result: " + str);
				if (gbFreqSeqs.containsKey(str))
					gbFreqSeqs.put(str, gbFreqSeqs.get(str) + 1);
				else
					gbFreqSeqs.put(str, 1);
			}
		}

		HashSet<String> finalGBFreqSeq = new HashSet<String>();
		for (String str : gbFreqSeqs.keySet()) {
			if (gbFreqSeqs.get(str) >= thresholdForLocalFSOutliers) {
				finalGBFreqSeq.add(str);
			}
		}
		HashSet<Integer> devicesThatContainOutliers = new HashSet<Integer>();
		try {
			// BufferedWriter out = new BufferedWriter(new
			// FileWriter("OutlierRes/" + fileName + ".tsv"));
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			int numOutlierTimeSeries = 0;
			int numOutliersInTotal = 0;
			int maxOutlierInOneSeries = 0;
			for (Entry<Integer, HashMap<String, Integer>> curElement : localFrequentSeqsStatic.entrySet()) {
				HashMap<String, Integer> tempOutliers = new HashMap<String, Integer>();
				for (Entry<String, Integer> curStr : curElement.getValue().entrySet()) {
					if (!finalGBFreqSeq.contains(curStr.getKey()))
						tempOutliers.put(curStr.getKey(), curStr.getValue());
				}
				if (tempOutliers.size() != 0) {
					numOutlierTimeSeries++;
					devicesThatContainOutliers.add(curElement.getKey());
					numOutliersInTotal += tempOutliers.size();
					maxOutlierInOneSeries = Math.max(maxOutlierInOneSeries, tempOutliers.size());
					for (Entry<String, Integer> curStr : tempOutliers.entrySet()) {
						// System.out.println(curStr);
						out.write("Sequence # " + curElement.getKey());
						if (containDeviceId)
							out.write("Device Id: " + deviceIdMap.get(curElement.getKey()));
						out.newLine();
						out.write("Frequency: " + curStr.getValue() + "\t" + curStr.getKey());
						out.newLine();
						out.newLine();
						String[] subStr = curStr.getKey().split(",");
						for (String tempStr : subStr) {
							out.write(metaDataMapping.get(tempStr) + "\t");
							// System.out.println(metaDataMapping.get(tempStr));
						}
						out.newLine();
						out.newLine();
						out.newLine();
					}
					out.newLine();
				}
			}
			out.close();
			System.out.println(numOutlierTimeSeries + " , " + numOutliersInTotal + " , " + maxOutlierInOneSeries);
			return devicesThatContainOutliers;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
