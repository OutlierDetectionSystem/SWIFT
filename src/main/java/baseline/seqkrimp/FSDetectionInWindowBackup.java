package baseline.seqkrimp;//package baseline.detection;
//
//import baseline.base.FSCandidate;
//import baseline.base.FreqSequence;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//
//public class FSDetectionInWindowBackup {
//	private String[] inputString;
//	private int minSupport = 2;
//	private int itemGap;
//	private int seqGap;
//	private boolean[] availability;
//	private ArrayList<FSCandidate> fsCandidateList = new ArrayList<FSCandidate>();
//	private HashMap<String, Integer> frequentSequences;
//
//	public FSDetectionInWindowBackup(String[] inputString, int itemGap, int seqGap) {
//		this.setInputString(inputString);
//		this.itemGap = itemGap;
//		this.seqGap = seqGap;
//		this.availability = new boolean[inputString.length];
//		for (int i = 0; i < this.availability.length; i++)
//			this.availability[i] = true;
//		this.frequentSequences = new HashMap<String, Integer>();
//	}
//
//	public ArrayList<String> generateSubSequences(String currentStr, HashMap<String, FreqSequence> FreqSeqsInMap) {
//		ArrayList<String> subSequences = new ArrayList<String>();
//		String[] subItems = currentStr.split(",");
//		for (int i = subItems.length - 1; i >= 0; i--) {
//			ArrayList<String> tempList = new ArrayList<String>();
//			String curStr = subItems[i];
//			for (String subStr : subSequences) {
//				tempList.add(curStr + "," + subStr);
//			}
//			subSequences.add(curStr);
//			subSequences.addAll(tempList);
//		}
//		ArrayList<String> deleteStrings = new ArrayList<String>();
//		for (String subStr : subSequences) {
//			if (!FreqSeqsInMap.containsKey(subStr))
//				deleteStrings.add(subStr);
//		}
//		subSequences.removeAll(deleteStrings);
//		return subSequences;
//	}
//
//	public void checkEachSequence(String currentSeq, HashMap<String, FreqSequence> FreqSeqsInMap) {
//		// first generate an array list of subsequences of this currentSeq (only
//		// save those on the frequent sequence list)
//		ArrayList<String> subSequences = generateSubSequences(currentSeq, FreqSeqsInMap);
//		FreqSequence currentSequence = FreqSeqsInMap.get(currentSeq);
//		// System.out.println("current Sequence: " +
//		// currentSequence.getFreqSeqInString());
//		for (String subStr : subSequences) {
//			if (subStr.equals(currentSeq))
//				continue;
//			else {
//				// get the frequent sequence
//				if (FreqSeqsInMap.containsKey(subStr)) {
//					FreqSequence fs = FreqSeqsInMap.get(subStr);
//					if (fs.isInvalid(minSupport))
//						continue;
//					if (fs.getSupportNum() - currentSequence.getSupportNum() < minSupport) {
//						fs.setInvalid();
//						// remove from list and map
//						FreqSeqsInMap.remove(subStr);
//						// totalFrequentSeqs.remove(fs);
//					} else {
//						fs.setPartInValid(currentSequence);
//						if (fs.isInvalid(minSupport)) {
//							FreqSeqsInMap.remove(subStr);
//							// totalFrequentSeqs.remove(fs);
//						}
//					}
//				}
//			} // end else
//		}
//	}
//
//	public void findFreqSeqInOneString() {
//		ArrayList<FreqSequence> totalFrequentSeqs = new ArrayList<FreqSequence>();
//		// first compute frequent sequences
//		PrefixSpanTool pst = new PrefixSpanTool(inputString, this.itemGap, this.seqGap);
//		totalFrequentSeqs = pst.prefixSpanCalculate();
//
//		// then sort by length of the string
//		Collections.sort(totalFrequentSeqs);
//		// init support number and a list of boolean for each sequence
//		HashMap<String, FreqSequence> FreqSeqsInMap = new HashMap<String, FreqSequence>();
//		for (FreqSequence fs : totalFrequentSeqs) {
//			fs.generateSupportNumAndInitIndexes();
//			FreqSeqsInMap.put(fs.getFreqSeqInString(), fs);
//		}
//
//		// check each frequent sequence,
//		// start from the longest one, until there is no unchecked frequent
//		// sequence left
//		for (int i = 0; i < totalFrequentSeqs.size(); i++) {
//			// System.out.println(totalFrequentSeqs.size());
//			if (FreqSeqsInMap.containsKey(totalFrequentSeqs.get(i).getFreqSeqInString()))
//				checkEachSequence(totalFrequentSeqs.get(i).getFreqSeqInString(), FreqSeqsInMap);
//		}
//		// ArrayList<FSCandidate> finalFSCandidates = new
//		// ArrayList<FSCandidate>();
//		for (String str : FreqSeqsInMap.keySet()) {
////			System.out.println("Result for one: " + str + ", Frequency: " + FreqSeqsInMap.get(str).getSupportNum());
//			FreqSequence tempFS = FreqSeqsInMap.get(str);
//			ArrayList<ArrayList<Integer>> tempIndexes = tempFS.getFinalIndexesForSequence();
//			FSCandidate tempFSCandidate = new FSCandidate(str, tempIndexes);
//			this.fsCandidateList.add(tempFSCandidate);
////			System.out.println(tempFSCandidate.printFSCandidate());
//		}
//		Collections.sort(this.fsCandidateList);
//		// System.out.println(finalFSCandidates.get(0).printFSCandidate());
//	}
//
//	public HashMap<String, Integer> greedyDetectFS() {
//		if(this.fsCandidateList.size() == 0)
//			return this.frequentSequences;
//		// if the max length is larger than 0 and there are sequences in the
//		// array
//		int maxBenefit = this.fsCandidateList.get(0).getMergeBenefit();
//		while (this.fsCandidateList.size() > 0 && maxBenefit > 0) {
//			// first eliminate the first item in fsCandidateList
//			ArrayList<ArrayList<Integer>> eliminateIndexes = fsCandidateList.get(0).getIndexes();
//			this.frequentSequences.put(fsCandidateList.get(0).getSeqInStr(), fsCandidateList.get(0).getSupportNum());
////			System.out.println(
////					"NEW FS: " + fsCandidateList.get(0).getSeqInStr() + "," + fsCandidateList.get(0).getSupportNum());
//			for (ArrayList<Integer> tempIndex : eliminateIndexes) {
//				int tempStartIndex = tempIndex.get(0);
//				int tempEndIndex = tempIndex.get(tempIndex.size() - 1);
//				for (int i = tempStartIndex; i <= tempEndIndex; i++) {
//					this.availability[i] = false;
//				}
//			}
//
//			// then update each array, delete those strings that has nothing
//			// available
//			ArrayList<FSCandidate> deleteCandidate = new ArrayList<FSCandidate>();
//			deleteCandidate.add(fsCandidateList.get(0));
//			for (int i = 1; i < fsCandidateList.size(); i++) {
//				if (!fsCandidateList.get(i).updateIndexes(this.availability, this.minSupport))
//					deleteCandidate.add(fsCandidateList.get(i));
//			}
//
//			for (FSCandidate str : deleteCandidate) {
//				this.fsCandidateList.remove(str);
//			}
//			Collections.sort(this.fsCandidateList);
//			maxBenefit = (this.fsCandidateList.size() > 0) ? (this.fsCandidateList.get(0).getMergeBenefit()) : 0;
//		}
//		return this.frequentSequences;
//	}
//
//	public String[] getInputString() {
//		return inputString;
//	}
//
//	public void setInputString(String[] inputString) {
//		this.inputString = inputString;
//	}
//
//	public int getMinSupport() {
//		return minSupport;
//	}
//
//	public void setMinSupport(int minSupport) {
//		this.minSupport = minSupport;
//	}
//
//	public static void main(String[] args) {
//		String s = "D,H,A,B,C,D,A,B,C,D,H,A,B,C";
//		// String s = "A,A,B,C,D,A,B,C,C,B,A,C,B,A,A,B,C";
//		// String s =
//		// "1,5,1,5,1,5,6,1,0,1,0,0,0,1,5,1,5,6,1,5,6,1,5,5,5,6,1,5,5,5,5,5,6,1,5,6,1,5,5,6,1,5,5,5,5,5,5,6,1,5,5,5,6,1,5,1,5,1,5,1,5,5,5,6,1,5,1,5,5,6,1,5,6,1,5,1,5,1,5,6,1,5,5,5,6,1,0,1,5,6,1,5,5,5,6,1,5,6,1,5";
//		// String s =
//		// "21,22,23,24,25,26,27,28,24,14,27,29,30,27,31,30,27,31,30,27,31,30,27,31,30,32,27,28,24,14,33,34,0,24,35,36,14,37,38,14,14,39,40,41,42,43,44,45,46,47,26,32,27,28,24,26,32,26,32,26,32,40,26,32,48,14,14,49,50,51,52,48,14,14,49,50,53,54,42,55,56,57,58,59,60,61,62,63,26,32,26,32,26,32,26,32,26,32,64,23,24,25,27,28,24,14,27,29,30,27,31,30,27,31,30,27,31,30,27,31,30,26,32,27,28,24,14,27,28,24,14,27,29,30,27,31,30,27,31,30,33,34,0,24,35,36,14,37,38,14,40,41,14,39,47,42,43,44,45,46,26,32,27,28,24,26,32,26,32,26,32,40,26,32,48,14,14,49,50,51,52,48,14,14,49,50,53,54,42,55,56,57,58,59,63,60,61,62,26,32,26,32,26,32,26,32,26,32,64,26,23,24,25,27,28,24,14,27,29,30,27,31,30,27,31,30,27,31,30,32,27,31,30,33,34,0,24,35,36,14,37,38,14,40,41,65,14,66,42,43,67,67,44,45,46,47,68,69,70,26,32,71,72,73,71,72,73,0,74,75,76,77,78,79,78,79,80,26,32,81,69,4,11,76,21,22,26,32,68,21,22,68,21,22,77,78,79,78,79,80,26,32,68,21,22,68,40,21,22,77,78,79,78,79,80,26,32,68,21,22,48,14,14,82,50,51,52,48,14,14,82,50,53,54,42,55,56,57,58,59,68,63,21,22,60,61,62,68,77,78,79,78,79,78,79,80,26,32,21,22,68,21,22,68,21,22,77,78,79,78,79,80,26,32,68,21,22,68,21,77,78,79,78,79,80,26,32,22,68,21,22,68,21,22,77,78,79,78,79,80,26,32,68,21,22,68,21,22,77,78,79,78,79,80,26,32,64,27,28,24,14,68,83,84,69,77,78,79,80,26,32,4,11,21,22";
//		// System.out.println("Previous sequence size: " + s.split(",").length);
//		int itemGap = 1;
//		int seqGap = 10;
//
//		FSDetectionInWindowBackup obj = new FSDetectionInWindowBackup(s.split(","), itemGap, seqGap);
//		obj.findFreqSeqInOneString();
//		obj.greedyDetectFS();
//	}
//}
