package tiltedwindow;

import decay.DecayFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhouyan on 7/26/17.
 */
public class TiltedWindowList {
    private ArrayList<TiltedWindow> windowList;
    private ArrayList<Double> windowWeights;
    private DecayFunction decayFunction;
    private HashMap<String, Double> historyPatternWeight;

    public TiltedWindowList(HashMap<String, Double> historyPatternWeight, DecayFunction decayFunction){
        this.windowList = new ArrayList<TiltedWindow>();
        this.historyPatternWeight = historyPatternWeight;
        this.windowWeights = new ArrayList<Double>();
        this.decayFunction = decayFunction;
    }

    public double getWindowWeight(int windowId){
        for(int i = windowWeights.size(); i<= windowId; i++) {
            windowWeights.add(windowId,decayFunction.ComputeWeight(windowId));
        }
        return windowWeights.get(windowId);
    }

    public void addNewBatchWeightsToHistory(HashMap<String, Integer> newPatternsWithWeights, double weight){
        for(Map.Entry<String, Integer> entry: newPatternsWithWeights.entrySet()){
            if(historyPatternWeight.containsKey(entry.getKey())){
                historyPatternWeight.put(entry.getKey(), historyPatternWeight.get(entry.getKey()) + entry.getValue() * weight);
            }else
                historyPatternWeight.put(entry.getKey(), entry.getValue() * weight);
        }
    }

    public void updateBatchWeightsToHistory(HashMap<String, Integer> newPatternsWithWeights,
                                            double prevWeight, double curWeight){
        for(Map.Entry<String, Integer> entry: newPatternsWithWeights.entrySet()){
            historyPatternWeight.put(entry.getKey(), historyPatternWeight.get(entry.getKey()) +
                    entry.getValue() * curWeight - entry.getValue() * prevWeight);
        }
    }

    public void removeBatchWeightsFromHistory(HashMap<String, Integer> newPatternsWithWeights,
                                            double prevWeight){
        for(Map.Entry<String, Integer> entry: newPatternsWithWeights.entrySet()){
            historyPatternWeight.put(entry.getKey(), historyPatternWeight.get(entry.getKey()) - entry.getValue() * prevWeight);
        }
    }

    public void addNewBatch(HashMap<String, Integer> newPatternsWithWeights) {
        ArrayList<TiltedWindow> newWindowList = new ArrayList<TiltedWindow>();
        // first create a new tilted window for this new batch
        TiltedWindow newTiltedWindow = new TiltedWindow(0, getWindowWeight(0), newPatternsWithWeights);
        newWindowList.add(newTiltedWindow);
        addNewBatchWeightsToHistory(newPatternsWithWeights, getWindowWeight(0));

        // traverse previous windows, if changed, then update weights
        if(windowList.size() > 0) {
            TiltedWindow tempTiltedWindow = new TiltedWindow(1, getWindowWeight(1),
                    windowList.get(0).getPatternsWithWeights());
            newWindowList.add(tempTiltedWindow);
            updateBatchWeightsToHistory(windowList.get(0).getPatternsWithWeights(), getWindowWeight(0), getWindowWeight(1));
            if (windowList.size() > 1) {
                int toIndex = recursiveAddNewBatch(1, newWindowList);
                for(int i = toIndex + 1; i < windowList.size(); i++)
                    newWindowList.add(windowList.get(i));
            }
        }
        this.windowList = newWindowList;
    }

    public int recursiveAddNewBatch(int index, ArrayList<TiltedWindow> newWindowList){
        if(windowList.size() <= index)
            return windowList.size();
        if(windowList.get(index).getIntermediateRes() == null) {
            newWindowList.get(newWindowList.size() - 1).setIntermediateRes(windowList.get(index));
            return index;
        }else{
            // merge the window and add to next
            TiltedWindow newTiltedWindow = new TiltedWindow(index+1, getWindowWeight(index+1),
                    mergeTiltedWindow(windowList.get(index)));
            newWindowList.add(newTiltedWindow);
            removeBatchWeightsFromHistory(windowList.get(index).getPatternsWithWeights(), getWindowWeight(index));
            removeBatchWeightsFromHistory(windowList.get(index).getIntermediateRes().getPatternsWithWeights(),
                    getWindowWeight(index));
            addNewBatchWeightsToHistory(newTiltedWindow.getPatternsWithWeights(), getWindowWeight(index+1));
            if(windowList.size() > index + 1)
                return recursiveAddNewBatch(index+1, newWindowList);
            else
                return windowList.size();
        }
    }

    public HashMap<String, Integer> mergeTiltedWindow(TiltedWindow mergeWindow){
        HashMap<String, Integer> finalFrequency = new HashMap<String, Integer>();
        finalFrequency.putAll(mergeWindow.getPatternsWithWeights());
        for(Map.Entry<String, Integer> intermediate: mergeWindow.getIntermediateRes().getPatternsWithWeights().entrySet()){
            if(finalFrequency.containsKey(intermediate.getKey())){
                finalFrequency.put(intermediate.getKey(), Math.max(intermediate.getValue(),
                        finalFrequency.get(intermediate.getKey())));
            }else{
                finalFrequency.put(intermediate.getKey(), intermediate.getValue());
            }
        }
        return finalFrequency;
    }

    public void printTiltedWindowList(){
        for(TiltedWindow tw: windowList){
            System.out.print(tw.getWindowId() + "\t" + tw.getWindowWeight() + "\t");
            for(Map.Entry<String, Integer> entry: tw.getPatternsWithWeights().entrySet())
                System.out.print(entry.getKey() + "|" + entry.getValue() + ",");
            System.out.print("\t [");
            if(tw.getIntermediateRes()!=null){
                for(Map.Entry<String, Integer> entry: tw.getIntermediateRes().getPatternsWithWeights().entrySet())
                    System.out.print(entry.getKey() + "|" + entry.getValue() + ",");
            }
            System.out.println("]");
        }
        System.out.println();
    }

    public ArrayList<TiltedWindow> getWindowList() {
        return windowList;
    }

    public void setWindowList(ArrayList<TiltedWindow> windowList) {
        this.windowList = windowList;
    }
}
