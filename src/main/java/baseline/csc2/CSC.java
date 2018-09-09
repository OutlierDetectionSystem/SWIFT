package baseline.csc2;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhouyan on 9/1/18.
 */
public class CSC {
    private String[] inputString;
    private int minSupport = 2;
    private int itemGap;
    private BitSet availability;
    private ArrayList<Episode> frequentSequences;

    public CSC(String[] inputString, int itemGap, int seqGap) {
        this.setInputString(inputString);
        this.availability = new BitSet(inputString.length);
        this.availability.set(0, inputString.length, true);
        this.itemGap = itemGap;
        this.frequentSequences = new ArrayList<>();
    }

    public void CSCMain(){
        boolean coveringExists = true;

        while(coveringExists && availability.cardinality() > 0){
            ArrayList<Episode> curEpisodeList = new ArrayList<>();
            ComputeBestExtensions bestExtensions = new ComputeBestExtensions(inputString, availability, itemGap);
            ArrayList<Episode> episodeCandidates = bestExtensions.bestExtensions();
            if(episodeCandidates.size() ==0){
                break;
            }
            FindOverlapMatrix findOverlapMatrix = new FindOverlapMatrix(inputString, availability);
            int [][] overlapMatrix = findOverlapMatrix.findOverlapMatrix(episodeCandidates);
            HashMap<Episode, Integer> mapToIndex = episodeToIndexInOverlapMatrix(episodeCandidates);
            BitSet candidateAvail = new BitSet(episodeCandidates.size());
            candidateAvail.set(0, episodeCandidates.size(), true);

            int curBestOverlapScore = 0;
            do{
                int curBestEpisode = findEpisodeWithLargestOverlapScore(episodeCandidates, candidateAvail, curEpisodeList,
                        mapToIndex, overlapMatrix);
                curBestOverlapScore = overlapScore(episodeCandidates.get(curBestEpisode), curEpisodeList, mapToIndex, overlapMatrix);
                if(curBestOverlapScore > 0){
                    curEpisodeList.add(episodeCandidates.get(curBestEpisode));
                    candidateAvail.set(curBestEpisode, false);
                }else if(curBestOverlapScore <= 0 && curEpisodeList.size() == 0){
                    coveringExists = false;
                }
            }while(candidateAvail.cardinality() > 0 && curBestOverlapScore > 0);

            this.frequentSequences.addAll(curEpisodeList);
            // mark all occurrences in curEpisodeList as unavailable
            for(Episode episode: curEpisodeList){
                ArrayList<Integer> setOcc = episode.getAllOccurrences();
                for(int occ: setOcc){
                    availability.set(occ, false);
                }
            }
        }
    }

    public int findEpisodeWithLargestOverlapScore(ArrayList<Episode> episodeCandidates,
                                                      BitSet candidateAvail,
                                                      ArrayList<Episode> curEpisodeList,
                                                      HashMap<Episode, Integer> mapToIndex,
                                                      int [][] overlapMatrix){
        int bestEpisode = candidateAvail.nextSetBit(0);
        int bestEpisodeScore = overlapScore(episodeCandidates.get(bestEpisode), curEpisodeList, mapToIndex, overlapMatrix);

        int pos = bestEpisode + 1;
        while(pos < candidateAvail.length()) {
            int curEpisodeIndex = candidateAvail.nextSetBit(pos);
            int curScore = overlapScore(episodeCandidates.get(curEpisodeIndex), curEpisodeList, mapToIndex, overlapMatrix);
            if(curScore > bestEpisodeScore){
                bestEpisode = curEpisodeIndex;
                bestEpisodeScore = curScore;
            }
            pos = curEpisodeIndex + 1;
        }
        return bestEpisode;
    }

    public HashMap<Episode, Integer> episodeToIndexInOverlapMatrix(ArrayList<Episode> episodeCandidates){
        HashMap<Episode, Integer> mapToIndex = new HashMap<>();
        for(int i = 0; i< episodeCandidates.size();i++){
            mapToIndex.put(episodeCandidates.get(i), i);
        }
        return mapToIndex;
    }

    public int overlapScore(Episode pattern, ArrayList<Episode> curEpisodeList,
                            HashMap<Episode, Integer> mapToIndex, int [][] overlapMatrix){
        int overlapScore = 0;
        int indexForCur = mapToIndex.get(pattern);
        for(Episode existEpisode: curEpisodeList){
            int indexForExist = mapToIndex.get(existEpisode);
            overlapScore += overlapMatrix[indexForCur][indexForExist];
        }
        return pattern.getSupport() * pattern.getLength() - (2 * pattern.getLength() + pattern.getSupport() + 1) - overlapScore;
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
        for (Episode freqSeq : frequentSequences) {
            MDLScore -= (freqSeq.getLength()-1) * freqSeq.getSupport() - 1;
        }
        return MDLScore;
    }

//    public int getMDLScore() {
//        int MDLScore = this.availability.cardinality();
//        for(Episode freqSeq: frequentSequences){
//            MDLScore += freqSeq.getSupport() + 1;
//        }
//        return MDLScore;
//    }

    public ArrayList<Episode> getFrequentSequences() {
        return frequentSequences;
    }

    public void setFrequentSequences(ArrayList<Episode> frequentSequences) {
        this.frequentSequences = frequentSequences;
    }

    public static void main(String[] args) {
        String s = "D,H,A,B,C,D,A,B,C,D,H,A,B,C";
//		 String s = "A,A,B,C,D,A,B,C,C,B,A,C,B,A,A,B,C";
//		String s = "B,C,A,B,C,A,B,C,A,B,C";
        //		String s = "D,H,A,B,C,E,D,A,B,C,E,D,H,A,B,C,E";
        // System.out.println("Previous sequence size: " + s.split(",").length);
        int itemGap = 2000;
        int seqGap = 2000;
        CSC obj = new CSC(s.split(","), itemGap, seqGap);
        obj.CSCMain();
        System.out.println(obj.getMDLScore());
    }
}
