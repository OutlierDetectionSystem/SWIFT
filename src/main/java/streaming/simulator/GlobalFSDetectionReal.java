package streaming.simulator;//package streaming.outlierdetection;
//
//import streaming.patterngen.SingleFSDetection;
//import streaming.util.FileUtile;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map.Entry;
//
//public class GlobalFSDetectionReal {
//	private int itemGap;
//	private int seqGap;
//	private int smallSupport;
//	private int largeSupport;
//
//	public GlobalFSDetectionReal(int itemGap,
//                                 int seqGap, int smallSupport, int largeSupport) {
//		this.itemGap = itemGap;
//		this.seqGap = seqGap;
//		this.smallSupport = smallSupport;
//		this.largeSupport = largeSupport;
//	}
//
//
//	public void detectGlobalFrequentSequence(String inputPath, int windowSize) {
//		File file = new File(inputPath);
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(file));
//			String str;
//			int count = 0;
//			while ((str = in.readLine()) != null) {
////				if(count > 0) {
//					String[] subs = str.split("\t");
//					String inputStr = subs[2];
//					SingleFSDetection localFS = new SingleFSDetection(inputStr, windowSize, this.itemGap, this.seqGap);
//					localFS.FrequentSequenceMining();
//					if (count % 1 == 0) {
//						System.out.println(count + " Finished!" + localFS.getFrequentPatternsWithCounts().size());
//					}
////				}
//				count++;
//			}
//			in.close();
//		} catch (IOException e) {
//			e.getStackTrace();
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
//	public static void main(String[] args) {
////		 String inputPath = "ConnectedLogs.tsv";
////		String inputPath = "extractedClean_less10000_withId.tsv";
////		String metaPath = "extractedMeta.tsv";
////		 String inputPath = "logfile.tsv";
////		String inputPath = "data/inputData/vlc-data.txt";
//		String inputPath = "data/realdata/real10000data_compressedDic_id.csv";
//		long startTime = System.currentTimeMillis();
//		int itemgap = 0;
//		int seqGap = 1000;
//		int smallSupCount = 1;
//		int largeSupCount = 500;
////		GlobalFSDetection globalFS = new GlobalFSDetection(inputStringArray, deviceIdMap, null,
////				itemgap, seqGap, smallSupCount, largeSupCount, true);
//		GlobalFSDetectionReal globalFS = new GlobalFSDetectionReal(itemgap, seqGap, smallSupCount, largeSupCount);
//		globalFS.detectGlobalFrequentSequence(inputPath, 500);
//
//		System.out.println("Compute Global Frequent Sequence takes " + (System.currentTimeMillis() - startTime) / 1000
//				+ " seconds!");
//	}
//}
