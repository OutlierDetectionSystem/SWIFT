package history;

import decay.DecayFunction;
import tiltedwindow.TiltedWindowList;

import java.util.HashMap;

/**
 * Created by yizhouyan on 7/24/17.
 */
public class HistoryPatternList {
    private TiltedWindowList tiltedWindowList;
    private HashMap<String, Double> historyPatternWeight;
    private DecayFunction decayFunction;

    public HistoryPatternList(DecayFunction decayFunction){
        this.historyPatternWeight = new HashMap<String, Double>();
        this.decayFunction = decayFunction;
        this.tiltedWindowList = new TiltedWindowList(historyPatternWeight, decayFunction);
    }

    public void addNewBatch(HashMap<String, Integer> newPatterns){
        this.tiltedWindowList.addNewBatch(newPatterns);
    }

    public TiltedWindowList getTiltedWindowList() {
        return tiltedWindowList;
    }

    public void setTiltedWindowList(TiltedWindowList tiltedWindowList) {
        this.tiltedWindowList = tiltedWindowList;
    }

    public HashMap<String, Double> getHistoryPatternWeight() {
        return historyPatternWeight;
    }

    public void setHistoryPatternWeight(HashMap<String, Double> historyPatternWeight) {
        this.historyPatternWeight = historyPatternWeight;
    }
}
