package outlierdetection;//package streaming.outlierdetection;
//
//import streaming.patterngen.SingleFSDetection;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
///**
// * Created by yizhouyan on 7/26/17.
// */
//public class OutlierDetectionBackup {
//    private ArrayList<SingleFSDetection> singleSequenceSimulator;
//    private String outputDir;
//    private int minLocalSupport;
//    private int maxLocalSupport;
//    private int minGlobalSupport;
//    private HashMap<String, Integer> globalFrequencyOfCurrent = new HashMap<String, Integer>();
//    private HashMap<Integer, HashSet<String>> infrequentOutliers = new HashMap<Integer, HashSet<String>>();
//    private HashMap<Integer, HashSet<String>> localFreqOutliers = new HashMap<Integer, HashSet<String>>();
//
//    public OutlierDetectionBackup(ArrayList<SingleFSDetection> singleSequenceSimulator, String outputDir, int minLocalSupport,
//                                  int maxLocalSupport, int minGlobalSupport){
//        this.singleSequenceSimulator = singleSequenceSimulator;
//        this.outputDir = outputDir;
//        this.minLocalSupport = minLocalSupport;
//        this.minGlobalSupport = minGlobalSupport;
//        this.maxLocalSupport = maxLocalSupport;
//    }
//
//    public void detectOutliers(int index, int currentGlobalIndex, HashMap<Integer, String> deviceIdMap,
//                               HashMap<String, String> metaDataMapping){
//        HashSet<String>infrequentOutliers = this.detectInfrequentOutliers(index);
//        this.infrequentOutliers.put(index, infrequentOutliers);
//        HashSet<String> localFreqOutliers = this.detectLocallyFrequentOutliers(index);
//        this.localFreqOutliers.put(index, infrequentOutliers);
//        if(infrequentOutliers.size() > 0){
//            String fileName = "infrequent.txt";
//            outputOutliers(infrequentOutliers, index, currentGlobalIndex, deviceIdMap, metaDataMapping, fileName);
//        }
//        if(localFreqOutliers.size() > 0){
//            String fileName = "localoutlier.txt";
//            outputOutliers(localFreqOutliers, index, currentGlobalIndex, deviceIdMap, metaDataMapping, fileName);
////            System.out.println(localFreqOutliers.size() + " local outliers detected!");
//        }
//    }
//
//    public void outputAndClearOutliers(){
//
//    }
//
//    public void outputOutliers(HashSet<String>outliers, int index, int currentGlobalIndex, HashMap<Integer, String> deviceIdMap,
//                                         HashMap<String, String> metaDataMapping, String fileName){
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputDir, fileName), true));
//            bw.write("Device id: " + deviceIdMap.get(index) + "\t" + "From: " + currentGlobalIndex);
//            bw.newLine();
//            for(String outlier: outliers){
//                bw.write(outlier);
//                bw.newLine();
//                String strInMeta = "";
//                String[] subs = outlier.split(",");
//                for (String substring : subs) {
//                    strInMeta += metaDataMapping.get(substring.trim()) + "\t";
//                }
//                bw.write(strInMeta);
//                bw.newLine();
//                bw.newLine();
//            }
//
//            bw.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public int getGlobalFrequencyOfPattern(String pattern){
//        if(globalFrequencyOfCurrent.containsKey(pattern)){
//            return globalFrequencyOfCurrent.get(pattern);
//        }else{
//            int count = 0;
//            for(SingleFSDetection singleFSDetection: singleSequenceSimulator){
//                HashMap<String, Double> historyWeights = singleFSDetection.getHistoryPatterns().getHistoryPatternWeight();
//                if(historyWeights.containsKey(pattern) && historyWeights.get(pattern) >= minLocalSupport)
//                    count++;
//            }
//            globalFrequencyOfCurrent.put(pattern, count);
//            return globalFrequencyOfCurrent.get(pattern);
//        }
//    }
//
//    public HashSet<String> detectLocallyFrequentOutliers(int index){
//        HashSet<String> localFreqOutliers = new HashSet<String>();
//        HashMap<String, Double> historyWeights = singleSequenceSimulator.get(index).getHistoryPatterns().getHistoryPatternWeight();
//        HashMap<String, Integer> currentFreqSeqs = singleSequenceSimulator.get(index).getFrequentPatternsWithCounts();
//        for(String curFreqSeq: currentFreqSeqs.keySet()){
//            if(!historyWeights.containsKey(curFreqSeq))
//                continue;
//            if(historyWeights.get(curFreqSeq) >= maxLocalSupport &&
//                    getGlobalFrequencyOfPattern(curFreqSeq) < minGlobalSupport){
//                localFreqOutliers.add(curFreqSeq);
//            }
//        }
//        return localFreqOutliers;
//    }
//
//    /**
//     * Mark as outlier if it is not frequent locally and not frequent globally
//     */
//    public HashSet<String> detectInfrequentOutliers(int index){
//        HashSet<String> infrequentOutliers = new HashSet<String>();
//        HashMap<String, Double> historyWeights = singleSequenceSimulator.get(index).getHistoryPatterns().getHistoryPatternWeight();
//        HashMap<String, Integer> currentFreqSeqs = singleSequenceSimulator.get(index).getFrequentPatternsWithCounts();
//        for(String curFreqSeq: currentFreqSeqs.keySet()){
//            if(currentFreqSeqs.get(curFreqSeq) >= 5 && (!historyWeights.containsKey(curFreqSeq) || historyWeights.get(curFreqSeq) < minLocalSupport) &&
//                    getGlobalFrequencyOfPattern(curFreqSeq) < minGlobalSupport){
//                infrequentOutliers.add(curFreqSeq);
//            }
//        }
//        return infrequentOutliers;
//    }
//
//    public void clearCurPatternFrequency(){
//        this.globalFrequencyOfCurrent.clear();
//    }
//}
