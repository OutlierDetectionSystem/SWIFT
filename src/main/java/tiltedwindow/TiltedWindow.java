package tiltedwindow;

import java.util.HashMap;

/**
 * Created by yizhouyan on 7/26/17.
 */
public class TiltedWindow {
    private int windowId;
    private double windowWeight;
    private TiltedWindow intermediateRes;
    private HashMap<String, Integer> patternsWithWeights;

    public TiltedWindow(int windowId, double windowWeight, HashMap<String, Integer> patternsWithWeights){
        this.windowId = windowId;
        this.windowWeight = windowWeight;
        this.patternsWithWeights = patternsWithWeights;
        this.intermediateRes = null;
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public double getWindowWeight() {
        return windowWeight;
    }

    public void setWindowWeight(double windowWeight) {
        this.windowWeight = windowWeight;
    }

    public TiltedWindow getIntermediateRes() {
        return intermediateRes;
    }

    public void setIntermediateRes(TiltedWindow intermediateRes) {
        this.intermediateRes = intermediateRes;
    }

    public HashMap<String, Integer> getPatternsWithWeights() {
        return patternsWithWeights;
    }

    public void setPatternsWithWeights(HashMap<String, Integer> patternsWithWeights) {
        this.patternsWithWeights = patternsWithWeights;
    }
}
