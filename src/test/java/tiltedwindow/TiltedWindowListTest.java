package tiltedwindow;

import org.junit.Before;
import org.junit.Test;
import decay.DecayFunction;
import decay.ExpDecay;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 7/26/17.
 */
public class TiltedWindowListTest {
    private DecayFunction decayFunction;
    private HashMap<String, Double> historyPatternWeight;

    @Before
    public void setUp() throws Exception {
        this.decayFunction = new ExpDecay(0.1);
        this.historyPatternWeight = new HashMap<String, Double>();
    }

    @Test
    public void addNewBatchTestWindowListSize() throws Exception {
        TiltedWindowList tiltedWindowList = new TiltedWindowList(historyPatternWeight, decayFunction);
        for(int i = 1; i< 10; i++){
            HashMap<String, Integer> newPatternsWithWeights = new HashMap<String, Integer>();
            newPatternsWithWeights.put("" + i,1);
            tiltedWindowList.addNewBatch(newPatternsWithWeights);
            tiltedWindowList.printTiltedWindowList();
        }
        for(Map.Entry<String, Double> entry: historyPatternWeight.entrySet()){
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    @Test
    public void addNewBatchTestWindow() throws Exception {
        TiltedWindowList tiltedWindowList = new TiltedWindowList(historyPatternWeight, decayFunction);
        HashMap<String, Integer> newPatternsWithWeights = new HashMap<String, Integer>();
        newPatternsWithWeights.put("A" ,1);
        newPatternsWithWeights.put("B" ,2);
        newPatternsWithWeights.put("C" ,5);
        tiltedWindowList.addNewBatch(newPatternsWithWeights);
        tiltedWindowList.printTiltedWindowList();

        HashMap<String, Integer> newPatternsWithWeights2 = new HashMap<String, Integer>();
        newPatternsWithWeights2.put("A" ,2);
        newPatternsWithWeights2.put("B" ,2);
        newPatternsWithWeights2.put("C" ,1);
        tiltedWindowList.addNewBatch(newPatternsWithWeights2);
        tiltedWindowList.printTiltedWindowList();

        for(Map.Entry<String, Double> entry: historyPatternWeight.entrySet()){
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }

        HashMap<String, Integer> newPatternsWithWeights3 = new HashMap<String, Integer>();
        newPatternsWithWeights3.put("D" ,2);
        newPatternsWithWeights3.put("A" ,2);
        newPatternsWithWeights3.put("B" ,1);
        tiltedWindowList.addNewBatch(newPatternsWithWeights3);
        tiltedWindowList.printTiltedWindowList();

        for(Map.Entry<String, Double> entry: historyPatternWeight.entrySet()){
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }

        HashMap<String, Integer> newPatternsWithWeights4 = new HashMap<String, Integer>();
        newPatternsWithWeights4.put("D" ,3);
        newPatternsWithWeights4.put("A" ,4);
        newPatternsWithWeights4.put("B" ,1);
        tiltedWindowList.addNewBatch(newPatternsWithWeights4);
        tiltedWindowList.printTiltedWindowList();

        for(Map.Entry<String, Double> entry: historyPatternWeight.entrySet()){
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    @Test
    public void mergeTiltedWindow() throws Exception {
        TiltedWindowList list = new TiltedWindowList(historyPatternWeight, decayFunction);
        HashMap<String, Integer> newPatternsWithWeights = new HashMap<String, Integer>();
        newPatternsWithWeights.put("A" ,1);
        newPatternsWithWeights.put("B" ,2);
        newPatternsWithWeights.put("C" ,5);
        TiltedWindow tiltedWindow = new TiltedWindow(1,1, newPatternsWithWeights);
        HashMap<String, Integer> newPatternsWithWeights2 = new HashMap<String, Integer>();
        newPatternsWithWeights2.put("A" ,2);
        newPatternsWithWeights2.put("B" ,2);
        newPatternsWithWeights2.put("C" ,1);
        TiltedWindow intermediateWindow = new TiltedWindow(1,1, newPatternsWithWeights2);
        tiltedWindow.setIntermediateRes(intermediateWindow);
        HashMap<String, Integer> actualResults = list.mergeTiltedWindow(tiltedWindow);
        assertEquals(2, actualResults.get("A").intValue());
        assertEquals(2, actualResults.get("B").intValue());
        assertEquals(5, actualResults.get("C").intValue());
    }

}