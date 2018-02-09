package base;

import decay.DecayFunction;
import history.HistoryPatternList;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by yizhouyan on 7/30/17.
 */
public class SingleFS {
    protected int windowSize = 100;
    protected LinkedList<String> inputElements;
    protected int itemGap;
    protected int seqGap;
    protected HistoryPatternList historyPatterns;

    public SingleFS(int windowSize, int itemGap, int seqGap, DecayFunction decayFunction){
        this.windowSize = windowSize;
        this.itemGap = itemGap;
        this.seqGap = seqGap;
        this.historyPatterns = new HistoryPatternList(decayFunction);
    }
    public int getWindowSize() {
        return windowSize;
    }
    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
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
    public HashMap<String, Integer> getFrequentPatternsWithCounts(){
        return new HashMap<String, Integer>();
    }
    public HistoryPatternList getHistoryPatterns() {
        return historyPatterns;
    }
    public void setHistoryPatterns(HistoryPatternList historyPatterns) {
        this.historyPatterns = historyPatterns;
    }
    public void addCurrentFreqPatternToHistroy(){}
    public void readBatchMoreElement(String [] inputArray, int batchSize){}
    public int getMDLScore(){
        return 0;
    }
    public String getFreqPatternsInString(){
        return "";
    }
}
