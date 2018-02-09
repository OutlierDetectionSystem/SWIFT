package outlierdetection;

import java.util.*;

/**
 * Created by yizhouyan on 6/15/17.
 */
public class SequencesInvertedIndex {
    private int bitsetSize = 100;
    private int increaseRate = 100;
    private ArrayList<String> SequencesInList;
    private HashMap<String, BitSet> invertedIndexOfSequences;

    public SequencesInvertedIndex(int bitsetSize, int increaseRate) {
        this.SequencesInList = new ArrayList<String>();
        this.invertedIndexOfSequences = new HashMap<String, BitSet>();
        this.bitsetSize = bitsetSize;
    }

    public void addFreqSeqsToList(Set<String> currentFreqSeqs) {
        if (SequencesInList.size() + currentFreqSeqs.size() > bitsetSize) {
            // need to enlarge bitsets
            int previousBitSetSize = bitsetSize;
            // compute increase number
            int numIncrease = (int) Math.ceil((SequencesInList.size() + currentFreqSeqs.size() - bitsetSize) *
                    1.0 / increaseRate);
            bitsetSize += increaseRate * numIncrease;
            HashMap<String, BitSet> newInvertedIndexOfFS = new HashMap<String, BitSet>();
            for (Map.Entry<String, BitSet> entry : invertedIndexOfSequences.entrySet()) {
                BitSet newTempBitSet = new BitSet(bitsetSize);
                newTempBitSet.or(entry.getValue());
                newInvertedIndexOfFS.put(entry.getKey(), newTempBitSet);
            }
            this.invertedIndexOfSequences = newInvertedIndexOfFS;
        }

        // start adding frequent sequence to list
        int startIndex = SequencesInList.size();
        for (String str : currentFreqSeqs) {
            SequencesInList.add(str);
            String[] itemsInCurStr = str.split(",");
            for (String item : itemsInCurStr) {
                if (!invertedIndexOfSequences.containsKey(item)) {
                    BitSet currentItemBS = new BitSet(bitsetSize);
                    invertedIndexOfSequences.put(item, currentItemBS);
                }
                invertedIndexOfSequences.get(item).set(startIndex, true);
            }
            startIndex++;
        }
    }

    /**
     * Get sequences with the same first element and all other elements 
     * @param sequence
     * @return
     */
    public ArrayList<String> getSequencesWithAllElements(String sequence){
       ArrayList<String> returnSeqList = new ArrayList<String>();
       String [] subs = sequence.split(",");
       BitSet currentBitSet = new BitSet(bitsetSize);
       currentBitSet.set(0, bitsetSize-1, true);
       for(String item: subs){
           if(invertedIndexOfSequences.containsKey(item))
               currentBitSet.and(invertedIndexOfSequences.get(item));
           else
               return returnSeqList;
       }

        for (int i = currentBitSet.nextSetBit(0); i >= 0; i = currentBitSet.nextSetBit(i+1)) {
            // if has the same first element
        	if(this.SequencesInList.get(i).split(",")[0].equals(subs[0]))
        		returnSeqList.add(this.SequencesInList.get(i));
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
        }
       return returnSeqList;
    }

    public String freqSeqInString() {
        String finalResult = "Frequent Sequence: \n";
        for (String str : this.SequencesInList) {
            finalResult += str + "\n";
        }
        return finalResult;
    }

    public String bitSetsInString() {
        String finalResults = "BitSets: \n";
        for (Map.Entry<String, BitSet> entry : this.invertedIndexOfSequences.entrySet()) {
            finalResults += entry.getKey() + "\t" + entry.getValue() + "\n";
        }
        return finalResults;
    }

    public int getBitsetSize() {
        return bitsetSize;
    }

    public void setBitsetSize(int bitsetSize) {
        this.bitsetSize = bitsetSize;
    }

    public ArrayList<String> getSequencesInList() {
        return SequencesInList;
    }

    public void setSequencesInList(ArrayList<String> sequencesInList) {
        this.SequencesInList = sequencesInList;
    }

    public HashMap<String, BitSet> getInvertedIndexOfSequences() {
        return invertedIndexOfSequences;
    }

    public void setInvertedIndexOfSequences(HashMap<String, BitSet> invertedIndexOfSequences) {
        this.invertedIndexOfSequences = invertedIndexOfSequences;
    }

    public static void main(String[] args) {
        SequencesInvertedIndex sequencesInvertedIndex = new SequencesInvertedIndex(2, 2);
        Set<String> currentFreqSeqs = new HashSet<String>();
        currentFreqSeqs.add("A,B,C");
        currentFreqSeqs.add("A,B,D");
        currentFreqSeqs.add("B,C,A");
        sequencesInvertedIndex.addFreqSeqsToList(currentFreqSeqs);
        System.out.println(sequencesInvertedIndex.freqSeqInString());
        System.out.println(sequencesInvertedIndex.bitSetsInString());

        sequencesInvertedIndex.addFreqSeqsToList(currentFreqSeqs);
        System.out.println(sequencesInvertedIndex.freqSeqInString());
        System.out.println(sequencesInvertedIndex.bitSetsInString());
        sequencesInvertedIndex.getSequencesWithAllElements("A,B");
//        BitSet bits1 = new BitSet(10);
//        BitSet bits2 = new BitSet(5);
//        bits2.set(0, true);
//        bits2.set(2, true);
//        bits1.or(bits2);
//        System.out.println(bits1);
    }
}
