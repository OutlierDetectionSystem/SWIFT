package baseline.csc2;

import java.util.*;

/**
 * Created by yizhouyan on 9/2/18.
 */
public class FindOverlapMatrix {
    private String[] inputString;
    private BitSet availability;

    public FindOverlapMatrix(String [] inputString, BitSet availability){
        this.inputString = inputString;
        this.availability = availability;
    }

    public void addToWaitingList(HashMap<String, LinkedList<AutomataState>> waitingList,
                                 Episode matchPattern, int pos, int indexInCandidate){
        String waitEle = matchPattern.getContents().get(pos);
        if(waitingList.containsKey(waitEle)){
            waitingList.get(waitEle).add(new AutomataState(matchPattern, pos, indexInCandidate));
        }else{
            waitingList.put(waitEle, new LinkedList<AutomataState>());
            waitingList.get(waitEle).add(new AutomataState(matchPattern, pos, indexInCandidate));
        }
    }

    public int[][] findOverlapMatrix(ArrayList<Episode> candidateList){
        int [][] overlapMatrix = new int[candidateList.size()][candidateList.size()];
        for(int i = 0; i< candidateList.size(); i++){
            for(int j = 0; j<candidateList.size(); j++){
                overlapMatrix[i][j] = 0;
            }
        }
        HashMap<String, LinkedList<AutomataState>> waitingList = new HashMap<>();
        for(int i = 0; i< candidateList.size(); i++){
            addToWaitingList(waitingList, candidateList.get(i), 0, i);
        }

        if(availability.cardinality() > 0){
            int pos = 0;
            while(pos < availability.length()) {
                int timestamp = availability.nextSetBit(pos);
                String curEle = inputString[timestamp];
                HashSet <Integer> overlapList = new HashSet<>();
                LinkedList<AutomataState> removeEle = new LinkedList<>();
                if(waitingList.get(curEle) != null) {
                    for (AutomataState curAutomata : waitingList.get(curEle)) {
                        if (curAutomata.getPos() == 0) {
                            if (curAutomata.getMatchPattern().existOccStartingAtTime(timestamp, curAutomata.getPos())) {
                                overlapList.add(curAutomata.getIndexInCandidates());
                                addToWaitingList(waitingList, curAutomata.getMatchPattern(),
                                        curAutomata.getPos() + 1, curAutomata.getIndexInCandidates());
                            }
                        } else {
                            if (curAutomata.getMatchPattern().existOccStartingAtTime(timestamp, curAutomata.getPos())) {
                                overlapList.add(curAutomata.getIndexInCandidates());
                                if (curAutomata.getPos() != curAutomata.getMatchPattern().getLength() - 1) {
                                    addToWaitingList(waitingList, curAutomata.getMatchPattern(),
                                            curAutomata.getPos() + 1, curAutomata.getIndexInCandidates());
                                }
                                removeEle.add(curAutomata);
                            }
                        }
                    }
                    waitingList.get(curEle).removeAll(removeEle);
                    // increment overlapMatrix by 1
                    for (int i : overlapList)
                        for (int j : overlapList) {
                            if (i == j)
                                continue;
                            else
                                overlapMatrix[i][j] += 1;
                        }
                }
                pos = timestamp+1;
            }
        }
        return overlapMatrix;
    }
}

class AutomataState{
    private int indexInCandidates;
    private Episode matchPattern;
    private int pos;

    public AutomataState(Episode matchPattern, int pos, int indexInCandidates){
        this.matchPattern = matchPattern;
        this.pos = pos;
        this.indexInCandidates = indexInCandidates;
    }

    public int getIndexInCandidates() {
        return indexInCandidates;
    }

    public void setIndexInCandidates(int indexInCandidates) {
        this.indexInCandidates = indexInCandidates;
    }

    public Episode getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(Episode matchPattern) {
        this.matchPattern = matchPattern;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}