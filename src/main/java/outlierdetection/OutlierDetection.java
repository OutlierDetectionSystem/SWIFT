package outlierdetection;

import base.SingleFS;
import streaming.util.ManageHashMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by yizhouyan on 7/26/17.
 */
public class OutlierDetection <T extends SingleFS> {
    private HashMap<Integer, T> singleSequenceSimulator;
    private String outputDir;
    private int minLocalSupport;
    private int maxLocalSupport;
    private int minGlobalSupport;
    private HashMap<String, Integer> globalFrequencyOfCurrent = new HashMap<String, Integer>();
    private HashMap<Integer, HashSet<String>> infrequentOutliers = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> localFreqOutliers = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> hisInfrequentOutliers = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> hisLocalFreqOutliers = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> localFreqOutlierChange = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> infrequentOutlierChange = new HashMap<Integer, HashSet<String>>();

    private HashMap<Integer, HashSet<String>> allInfrequentOutliers = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> allLocalFreqOutliers = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> allLocalFreqOutlierChange = new HashMap<Integer, HashSet<String>>();
    private HashMap<Integer, HashSet<String>> allInfrequentOutlierChange = new HashMap<Integer, HashSet<String>>();

    public OutlierDetection(HashMap<Integer, T> singleSequenceSimulator, String outputDir, int minLocalSupport,
                            int maxLocalSupport, int minGlobalSupport){
        this.singleSequenceSimulator = singleSequenceSimulator;
        this.outputDir = outputDir;
        this.minLocalSupport = minLocalSupport;
        this.minGlobalSupport = minGlobalSupport;
        this.maxLocalSupport = maxLocalSupport;
    }

    public void putInToHistory(HashMap<Integer, HashSet<String>> history, HashMap<Integer, HashSet<String>> newPatterns){
        for(Map.Entry<Integer, HashSet<String>> newPattern: newPatterns.entrySet()){
            ManageHashMap.addAllToHashSet(history, newPattern.getValue(), newPattern.getKey());
        }
    }

    public void detectOutliers(int index){
        HashSet<String>infrequentOutliers = this.detectInfrequentOutliers(index);
        this.infrequentOutliers.put(index, infrequentOutliers);
        HashSet<String> localFreqOutliers = this.detectLocallyFrequentOutliers(index);
        this.localFreqOutliers.put(index, localFreqOutliers);
    }

    public void outputAndClearOutliers(int currentGlobalIndex, HashMap<Integer, String> deviceIdMap,
                                       HashMap<String, String> metaDataMapping){
        if(this.infrequentOutliers.size() > 0){
            String fileName = "infrequent.txt";
            outputOutliers(this.infrequentOutliers, currentGlobalIndex, deviceIdMap, metaDataMapping, fileName);
        }
        if(this.localFreqOutliers.size() > 0){
            String fileName = "localoutliers.txt";
            outputOutliers(this.localFreqOutliers, currentGlobalIndex, deviceIdMap, metaDataMapping, fileName);
        }
        if(this.infrequentOutlierChange.size() > 0){
            String fileName = "infrequentChange.txt";
            outputOutliers(this.infrequentOutlierChange, currentGlobalIndex, deviceIdMap, metaDataMapping, fileName);
        }
        if(this.localFreqOutlierChange.size() > 0){
            String fileName = "localoutlierChange.txt";
            outputOutliers(this.localFreqOutlierChange, currentGlobalIndex, deviceIdMap, metaDataMapping, fileName);
        }

        putInToHistory(hisLocalFreqOutliers, localFreqOutliers);
        putInToHistory(hisInfrequentOutliers, infrequentOutliers);

        putInToHistory(allLocalFreqOutliers, localFreqOutliers);
        putInToHistory(allInfrequentOutliers, infrequentOutliers);
        putInToHistory(allInfrequentOutlierChange, infrequentOutlierChange);
        putInToHistory(allLocalFreqOutlierChange, localFreqOutlierChange);

        this.infrequentOutliers.clear();
        this.localFreqOutliers.clear();
        this.localFreqOutlierChange.clear();
        this.infrequentOutlierChange.clear();
    }

    public void outputOutliers(HashMap<Integer, HashSet<String>> outliers, int currentGlobalIndex,
                               HashMap<Integer, String> deviceIdMap, HashMap<String, String> metaDataMapping,
                               String fileName){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputDir, fileName), true));
            for(Map.Entry<Integer, HashSet<String>> oneDevice: outliers.entrySet()) {
                // get filtered outliers
                HashSet<String> filteredOutliers = new HashSet<String>();
                filteredOutliers = oneDevice.getValue();
                if(filteredOutliers.size() >0 ) {
                    bw.write("Device id: " + deviceIdMap.get(oneDevice.getKey()) + "\t" + "From: " + currentGlobalIndex);
                    bw.newLine();
                    for (String outlier : filteredOutliers) {
                        bw.write(outlier);
                        bw.newLine();
                        String strInMeta = "";
                        String[] subs = outlier.split(",");
                        for (String substring : subs) {
                            strInMeta += metaDataMapping.get(substring.trim()) + "\t";
                        }
                        bw.write(strInMeta);
                        bw.newLine();
                        bw.newLine();
                    }
                }
            }
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void outputFinalStaticOutlierResults(HashMap<Integer, String> deviceIdMap,
                                                HashMap<String, String> metaDataMapping){
        this.outputFinalFilteredOutliers(allInfrequentOutliers, allInfrequentOutlierChange, deviceIdMap,
                metaDataMapping, "FinalFilteredInfrequentOutliers.txt");
        this.outputFinalFilteredOutliers(allLocalFreqOutliers, allLocalFreqOutlierChange, deviceIdMap,
                metaDataMapping, "FinalFilteredLocalOutliers.txt");
    }

    public void outputFinalFilteredOutliers(HashMap<Integer, HashSet<String>> allOutliers,
                                            HashMap<Integer, HashSet<String>> changedOutliers,
                                            HashMap<Integer, String> deviceIdMap,
                                            HashMap<String, String> metaDataMapping,
                                            String fileName){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputDir, fileName)));
            for(Map.Entry<Integer, HashSet<String>> oneDevice: allOutliers.entrySet()) {
                // get filtered outliers
                HashSet<String> filteredOutliers = new HashSet<String>();
                filteredOutliers.addAll(oneDevice.getValue());
                if(changedOutliers.containsKey(oneDevice.getKey())){
                   filteredOutliers.removeAll(changedOutliers.get(oneDevice.getKey()));
                }
                if(filteredOutliers.size() > 0 ) {
                    bw.write("Device id: " + deviceIdMap.get(oneDevice.getKey()));
                    bw.newLine();
                    for (String outlier : filteredOutliers) {
                        bw.write(outlier);
                        bw.newLine();
                        String strInMeta = "";
                        String[] subs = outlier.split(",");
                        for (String substring : subs) {
                            strInMeta += metaDataMapping.get(substring.trim()) + "\t";
                        }
                        bw.write(strInMeta);
                        bw.newLine();
                        bw.newLine();
                    }
                }
            }
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getGlobalFrequencyOfPattern(String pattern){
        if(globalFrequencyOfCurrent.containsKey(pattern)){
            return globalFrequencyOfCurrent.get(pattern);
        }else{
            int count = 0;
            for(T singleFSDetection: singleSequenceSimulator.values()){
                HashMap<String, Double> historyWeights = singleFSDetection.getHistoryPatterns().getHistoryPatternWeight();
                if(historyWeights.containsKey(pattern) && historyWeights.get(pattern) >= minLocalSupport)
                    count++;
            }
            globalFrequencyOfCurrent.put(pattern, count);
            return globalFrequencyOfCurrent.get(pattern);
        }
    }

    public HashSet<String> detectLocallyFrequentOutliers(int index){
        HashSet<String> localFreqOutliers = new HashSet<String>();
        HashMap<String, Double> historyWeights = singleSequenceSimulator.get(index).getHistoryPatterns().getHistoryPatternWeight();
        HashMap<String, Integer> currentFreqSeqs = singleSequenceSimulator.get(index).getFrequentPatternsWithCounts();
        HashSet<String> historyLocalOutliers;
        if(this.hisLocalFreqOutliers.containsKey(index))
            historyLocalOutliers = this.hisLocalFreqOutliers.get(index);
        else
            historyLocalOutliers = new HashSet<>();

        HashSet<String> localChange = new HashSet<>();

        for(String curFreqSeq: currentFreqSeqs.keySet()){
            if(!historyWeights.containsKey(curFreqSeq))
                continue;
            if(historyWeights.get(curFreqSeq) >= maxLocalSupport){
                if(getGlobalFrequencyOfPattern(curFreqSeq) < minGlobalSupport){
                    localFreqOutliers.add(curFreqSeq);
                }else if(historyLocalOutliers.contains(curFreqSeq)){
                    localChange.add(curFreqSeq);
                }
            }
        }
        if(localChange.size() > 0){
            localFreqOutlierChange.put(index, localChange);
            historyLocalOutliers.removeAll(localChange);
        }
        return localFreqOutliers;
    }

    /**
     * Mark as outlier if it is not frequent locally and not frequent globally
     */
    public HashSet<String> detectInfrequentOutliers(int index) {
        HashSet<String> infrequentOutliers = new HashSet<String>();
        HashMap<String, Double> historyWeights = singleSequenceSimulator.get(index).getHistoryPatterns().getHistoryPatternWeight();
        HashMap<String, Integer> currentFreqSeqs = singleSequenceSimulator.get(index).getFrequentPatternsWithCounts();
        HashSet<String> historyInfrequentOutliers;
        if(hisInfrequentOutliers.containsKey(index))
            historyInfrequentOutliers = this.hisInfrequentOutliers.get(index);
        else
            historyInfrequentOutliers = new HashSet<>();

        HashSet<String> infrequentChange = new HashSet<>();

        for (String curFreqSeq : currentFreqSeqs.keySet()) {
            if (currentFreqSeqs.get(curFreqSeq) >= 5) {
                if ((!historyWeights.containsKey(curFreqSeq) || historyWeights.get(curFreqSeq) < minLocalSupport) &&
                        getGlobalFrequencyOfPattern(curFreqSeq) < minGlobalSupport) {
                    infrequentOutliers.add(curFreqSeq);
                } else if (historyInfrequentOutliers.contains(curFreqSeq)) {
                    infrequentChange.add(curFreqSeq);
                }
            }
        }
        if(infrequentChange.size() > 0) {
            infrequentOutlierChange.put(index, infrequentChange);
            historyInfrequentOutliers.removeAll(infrequentChange);
        }
        return infrequentOutliers;
    }


    public void clearCurPatternFrequency(){
        this.globalFrequencyOfCurrent.clear();
    }
}
