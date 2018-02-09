package outlierdetection;//package streaming.outlierdetection;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//
//public class GlobalFreqSeq {
//	private HashMap<String, HashSet<Integer>> CurrentFreqPatterns;
//	private SequencesInvertedIndex freqSeqInList;
//	private HashSet<String> typicalPatterns;
//	private int minLocalSupport;
//	private int minGlobalSupport;
//	private int minViolationSupport;
//
//	public GlobalFreqSeq(int minLocalSupport, int minGlobalSupport, int minViolationSupport){
//		this.minGlobalSupport = minGlobalSupport;
//		this.minLocalSupport = minLocalSupport;
//		this.minViolationSupport = minViolationSupport;
//		this.freqSeqInList = new SequencesInvertedIndex(100,100);
//		CurrentFreqPatterns = new HashMap<String, HashSet<Integer>>();
//		this.typicalPatterns = new HashSet<String>();
//	}
//
//	/**
//	 *
//	 * @param freqSeqWithCount
//	 * @param deviceId
//	 * @return violation candidates (frequency < minLocalSupport, frequency >= minViolationSupport)
//	 */
//	public HashSet<String> addToTypicalPatterns(HashMap<String, Integer> freqSeqWithCount, int deviceId){
//		HashSet<String> violationCandidates = new HashSet<String>();
//		for(Map.Entry<String, Integer> freqSeqCandidate: freqSeqWithCount.entrySet()){
//			if(freqSeqCandidate.getValue() >= minLocalSupport)
//				addToCurrentFreqPattern(freqSeqCandidate.getKey(), deviceId);
//			else if(freqSeqCandidate.getValue() >= minViolationSupport)
//				violationCandidates.add(freqSeqCandidate.getKey());
//		}
//		return violationCandidates;
//	}
//
//	public void addToCurrentFreqPattern(String freqSeq, int deviceId){
//		if(CurrentFreqPatterns.containsKey(freqSeq)){
//			CurrentFreqPatterns.get(freqSeq).add(deviceId);
//		}else{
//			HashSet<Integer> deviceIdSet = new HashSet<>();
//			deviceIdSet.add(deviceId);
//			CurrentFreqPatterns.put(freqSeq, deviceIdSet);
//		}
//		if(CurrentFreqPatterns.get(freqSeq).size() >= this.minGlobalSupport && !typicalPatterns.contains(freqSeq)) {
//			HashSet<String> oneTypicalPattern = new HashSet<>();
//			oneTypicalPattern.add(freqSeq);
//			this.freqSeqInList.addFreqSeqsToList(oneTypicalPattern);
//			typicalPatterns.add(freqSeq);
//		}
//	}
//
//	public void outputTypicalPatterns(String targetDirectory, HashMap<Integer, String> deviceIdMap,
//									  HashMap<String, String> metaDataMapping, int globalIndex){
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(targetDirectory,"TypicalPatternReport-" + globalIndex + ".txt")));
//			for(String freqSeq: freqSeqInList.getSequencesInList()){
//				bw.write(freqSeq);
//				bw.newLine();
//				String strInMeta = "";
//				String[] subs = freqSeq.split(",");
//				for (String substring : subs) {
//					strInMeta += metaDataMapping.get(substring.trim()) + "\t";
//				}
//				bw.write(strInMeta);
//				bw.newLine();
//				bw.newLine();
//			}
//
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public void outputLocalOutliers(String targetDirectory, HashMap<Integer, String> deviceIdMap,
//									HashMap<String, String> metaDataMapping, int globalIndex){
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(targetDirectory,"LocalOutlierReport-" + globalIndex + ".txt")));
//			for(Map.Entry<String, HashSet<Integer>>  curSeq: this.CurrentFreqPatterns.entrySet()){
//				if(curSeq.getValue().size() <= 1) {
//					bw.write("Device ids: " );
//					for(Integer id: curSeq.getValue()){
//						bw.write(deviceIdMap.get(id) + "\t");
//					}
//					bw.newLine();
//					bw.write(curSeq.getKey());
//					bw.newLine();
//					String strInMeta = "";
//					String[] subs = curSeq.getKey().split(",");
//					for (String substring : subs) {
//						strInMeta += metaDataMapping.get(substring.trim()) + "\t";
//					}
//					bw.write(strInMeta);
//					bw.newLine();
//					bw.newLine();
//				}
//			}
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		this.CurrentFreqPatterns.clear();
////		freqSeqInList = new SequencesInvertedIndex(100,100);
//	}
//
//	public HashMap<String, HashSet<Integer>> getCurrentFreqPatterns() {
//		return CurrentFreqPatterns;
//	}
//
//	public void setCurrentFreqPatterns(HashMap<String, HashSet<Integer>> currentFreqPatterns) {
//		CurrentFreqPatterns = currentFreqPatterns;
//	}
//
//	public SequencesInvertedIndex getFreqSeqInList() {
//		return freqSeqInList;
//	}
//
//	public void setFreqSeqInList(SequencesInvertedIndex freqSeqInList) {
//		this.freqSeqInList = freqSeqInList;
//	}
//
//	public int getMinLocalSupport() {
//		return minLocalSupport;
//	}
//
//	public void setMinLocalSupport(int minLocalSupport) {
//		this.minLocalSupport = minLocalSupport;
//	}
//
//	public int getMinGlobalSupport() {
//		return minGlobalSupport;
//	}
//
//	public void setMinGlobalSupport(int minGlobalSupport) {
//		this.minGlobalSupport = minGlobalSupport;
//	}
//
//	public int getMinViolationSupport() {
//		return minViolationSupport;
//	}
//
//	public void setMinViolationSupport(int minViolationSupport) {
//		this.minViolationSupport = minViolationSupport;
//	}
//
//	public HashSet<String> getTypicalPatterns() {
//		return typicalPatterns;
//	}
//
//	public void setTypicalPatterns(HashSet<String> typicalPatterns) {
//		this.typicalPatterns = typicalPatterns;
//	}
//}
