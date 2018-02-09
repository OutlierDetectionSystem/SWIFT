package baseline.seqkrimp;

import baseline.base.FSCandidate;
import baseline.base.FreqSequence;

import java.util.*;

public class FSDetectionInWindow {
	private String[] inputString;
	private int minSupport = 2;
	private int itemGap;
	private int seqGap;
	private boolean[] availability;
	private ArrayList<FSCandidate> fsCandidateList = new ArrayList<FSCandidate>();
	private HashMap<String, Integer> frequentSequences;
//	private int MDLScore;

	public FSDetectionInWindow(String[] inputString, int itemGap, int seqGap) {
		this.setInputString(inputString);
		this.itemGap = itemGap;
		this.seqGap = seqGap;
		this.availability = new boolean[inputString.length];
		for (int i = 0; i < this.availability.length; i++)
			this.availability[i] = true;
		this.frequentSequences = new HashMap<String, Integer>();
//		this.MDLScore = inputString.length;
	}

	public boolean isValidFreqSeq(HashMap<ArrayList<String>, Integer> ExistingFreqSeqs, FreqSequence curFP,
								  HashMap<ArrayList<String>, Integer> tempExistingFreqSeqs){
		String curStr = curFP.getFreqSeqInString();
		ArrayList<String> curSplitStr = new ArrayList<String>(Arrays.asList(curStr.split(",")));
		int curSupport = curFP.getSupportNum();
		for(Map.Entry<ArrayList<String>, Integer> parentSeq: ExistingFreqSeqs.entrySet()){
			if(parentSeq.getValue() ==  curSupport && strArrayContains(parentSeq.getKey(), curSplitStr)) {
				return false;
			}
		}
		tempExistingFreqSeqs.put(curSplitStr, curSupport);
		return true;
	}

	/**
	 * check sub array
	 *
	 * @param strList1
	 * @param strList2
	 * @return
	 */
	public boolean strArrayContains(ArrayList<String> strList1, ArrayList<String> strList2) {
		boolean isContained = false;

		for (int i = 0; i < strList1.size() - strList2.size() + 1; i++) {
			int k = i;
			int j = 0;
			while (k < strList1.size() && j < strList2.size()) {
				if (strList1.get(k).equals(strList2.get(j))) {
					k++;
					j++;
				} else {
					k++;
				}
			}
			if (j == strList2.size()) {
				isContained = true;
				break;
			}
		}

		return isContained;

	}

	public void findCloseFreqSeq (ArrayList<FreqSequence> totalFrequentSeqs){
		if (totalFrequentSeqs.size() == 0)
			return;
		int currentLength = totalFrequentSeqs.get(0).getItemNumInFreqSeq();
		int currentIndexInFS = 0;
		HashMap<ArrayList<String>, Integer> ExistingFreqSeqs = new HashMap<>();
		while (currentLength > 1 && currentIndexInFS < totalFrequentSeqs.size()) {
			HashMap<ArrayList<String>, Integer> tempExistingFreqSeqs = new HashMap<>();
			// deal with all fs with size = currentLength;
			while (currentIndexInFS < totalFrequentSeqs.size()
					&& totalFrequentSeqs.get(currentIndexInFS).getItemNumInFreqSeq() == currentLength) {
				FreqSequence curFSObj = totalFrequentSeqs.get(currentIndexInFS);
				curFSObj.generateSupport();
				if(isValidFreqSeq(ExistingFreqSeqs, curFSObj, tempExistingFreqSeqs)){
					String str = curFSObj.getFreqSeqInString();
					curFSObj.generateIndexesForSequence();
					FSCandidate tempFSCandidate = new FSCandidate(str, curFSObj.getIndexesForSequence());
					this.fsCandidateList.add(tempFSCandidate);
//					System.out.println(str);
				}
				currentIndexInFS++;
			}
			ExistingFreqSeqs.putAll(tempExistingFreqSeqs);
			if (currentIndexInFS < totalFrequentSeqs.size())
				currentLength = totalFrequentSeqs.get(currentIndexInFS).getItemNumInFreqSeq();
			else
				break;
		}
	}

	public void findClosedFreqSeqInOneString() {
		ArrayList<FreqSequence> totalFrequentSeqs = new ArrayList<FreqSequence>();
		// first compute frequent sequences
		PrefixSpanTool pst = new PrefixSpanTool(inputString, this.itemGap, this.seqGap);
		totalFrequentSeqs = pst.prefixSpanCalculate();

		// then sort by length of the string
		Collections.sort(totalFrequentSeqs);
		// init support number and a list of boolean for each sequence
		findCloseFreqSeq(totalFrequentSeqs);
		Collections.sort(this.fsCandidateList);
		// System.out.println(finalFSCandidates.get(0).printFSCandidate());
	}

	public HashMap<String, Integer> greedyDetectFS() {
		if(this.fsCandidateList.size() == 0)
			return this.frequentSequences;
		// if the max length is larger than 0 and there are sequences in the
		// array
		int maxBenefit = this.fsCandidateList.get(0).getMergeBenefit();
		while (this.fsCandidateList.size() > 0 && maxBenefit > 0) {
//			System.out.println(fsCandidateList.get(0).getSeqInStr() + "," + maxBenefit);
			// first eliminate the first item in fsCandidateList
			ArrayList<ArrayList<Integer>> eliminateIndexes = fsCandidateList.get(0).getIndexes();
			this.frequentSequences.put(fsCandidateList.get(0).getSeqInStr(), fsCandidateList.get(0).getSupportNum());
//			System.out.println(
//					"NEW FS: " + fsCandidateList.get(0).getSeqInStr() + "," + fsCandidateList.get(0).getSupportNum());
			for (ArrayList<Integer> tempIndex : eliminateIndexes) {
				int tempStartIndex = tempIndex.get(0);
				int tempEndIndex = tempIndex.get(tempIndex.size() - 1);
				for (int i = tempStartIndex; i <= tempEndIndex; i++) {
					this.availability[i] = false;
				}
			}

			// then update each array, delete those strings that has nothing
			// available
			ArrayList<FSCandidate> deleteCandidate = new ArrayList<FSCandidate>();
			deleteCandidate.add(fsCandidateList.get(0));
			for (int i = 1; i < fsCandidateList.size(); i++) {
				if (!fsCandidateList.get(i).updateIndexes(this.availability, this.minSupport))
					deleteCandidate.add(fsCandidateList.get(i));
			}

			for (FSCandidate str : deleteCandidate) {
				this.fsCandidateList.remove(str);
			}
//			this.MDLScore -= maxBenefit;
			Collections.sort(this.fsCandidateList);
			maxBenefit = (this.fsCandidateList.size() > 0) ? (this.fsCandidateList.get(0).getMergeBenefit()) : 0;
		}
//		System.out.println(this.MDLScore);
		return this.frequentSequences;
	}

	public String[] getInputString() {
		return inputString;
	}

	public void setInputString(String[] inputString) {
		this.inputString = inputString;
	}

	public int getMinSupport() {
		return minSupport;
	}

	public void setMinSupport(int minSupport) {
		this.minSupport = minSupport;
	}

	public int getMDLScore() {
		int MDLScore = this.inputString.length;
		for (Map.Entry<String, Integer> freqSeq : frequentSequences.entrySet()) {
			MDLScore -= (freqSeq.getKey().split(",").length-1) * freqSeq.getValue() - 1;
		}
		return MDLScore;
	}

//	public void setMDLScore(int MDLScore) {
//		this.MDLScore = MDLScore;
//	}

	public static void main(String[] args) {
//		String s = "D,H,A,B,C,D,A,B,C,D,H,A,B,C";
		 String s = "A,A,B,C,D,A,B,C,C,B,A,C,B,A,A,B,C";
		// String s =
		// "1,5,1,5,1,5,6,1,0,1,0,0,0,1,5,1,5,6,1,5,6,1,5,5,5,6,1,5,5,5,5,5,6,1,5,6,1,5,5,6,1,5,5,5,5,5,5,6,1,5,5,5,6,1,5,1,5,1,5,1,5,5,5,6,1,5,1,5,5,6,1,5,6,1,5,1,5,1,5,6,1,5,5,5,6,1,0,1,5,6,1,5,5,5,6,1,5,6,1,5";
		// String s =
		// "21,22,23,24,25,26,27,28,24,14,27,29,30,27,31,30,27,31,30,27,31,30,27,31,30,32,27,28,24,14,33,34,0,24,35,36,14,37,38,14,14,39,40,41,42,43,44,45,46,47,26,32,27,28,24,26,32,26,32,26,32,40,26,32,48,14,14,49,50,51,52,48,14,14,49,50,53,54,42,55,56,57,58,59,60,61,62,63,26,32,26,32,26,32,26,32,26,32,64,23,24,25,27,28,24,14,27,29,30,27,31,30,27,31,30,27,31,30,27,31,30,26,32,27,28,24,14,27,28,24,14,27,29,30,27,31,30,27,31,30,33,34,0,24,35,36,14,37,38,14,40,41,14,39,47,42,43,44,45,46,26,32,27,28,24,26,32,26,32,26,32,40,26,32,48,14,14,49,50,51,52,48,14,14,49,50,53,54,42,55,56,57,58,59,63,60,61,62,26,32,26,32,26,32,26,32,26,32,64,26,23,24,25,27,28,24,14,27,29,30,27,31,30,27,31,30,27,31,30,32,27,31,30,33,34,0,24,35,36,14,37,38,14,40,41,65,14,66,42,43,67,67,44,45,46,47,68,69,70,26,32,71,72,73,71,72,73,0,74,75,76,77,78,79,78,79,80,26,32,81,69,4,11,76,21,22,26,32,68,21,22,68,21,22,77,78,79,78,79,80,26,32,68,21,22,68,40,21,22,77,78,79,78,79,80,26,32,68,21,22,48,14,14,82,50,51,52,48,14,14,82,50,53,54,42,55,56,57,58,59,68,63,21,22,60,61,62,68,77,78,79,78,79,78,79,80,26,32,21,22,68,21,22,68,21,22,77,78,79,78,79,80,26,32,68,21,22,68,21,77,78,79,78,79,80,26,32,22,68,21,22,68,21,22,77,78,79,78,79,80,26,32,68,21,22,68,21,22,77,78,79,78,79,80,26,32,64,27,28,24,14,68,83,84,69,77,78,79,80,26,32,4,11,21,22";
		// System.out.println("Previous sequence size: " + s.split(",").length);
		int itemGap = 0;
		int seqGap = 10;

		FSDetectionInWindow obj = new FSDetectionInWindow(s.split(","), itemGap, seqGap);
		obj.findClosedFreqSeqInOneString();
		obj.greedyDetectFS();
		System.out.println(obj.getMDLScore());
	}
}
