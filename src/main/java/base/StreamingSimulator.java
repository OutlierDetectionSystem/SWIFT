package base;

import decay.DecayFunction;
import decay.ExpDecay;
import outlierdetection.OutlierDetection;
import streaming.batchopt.simulator.StreamingSimulatorBatchOpt;
import util.ParameterSpace;
import util.RuntimeStatistics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhouyan on 7/30/17.
 */
public class StreamingSimulator <T extends SingleFS>{
    protected int itemGap;
    protected int seqGap;
    protected int windowSize;
    protected int batchSize = 100;
    protected DecayFunction decayFunction;
    protected HashMap<Integer, T> singleSequenceSimulator;
    protected OutlierDetection outlierDetection;
    protected int globalIndex;
    protected long timeReadBatch = 0;
    protected long timeOutlierDetection = 0;
    protected long timeAddHistroy = 0;
    protected RuntimeStatistics runtimeStatistics;

    public StreamingSimulator(ParameterSpace parameterSpace,
                              RuntimeStatistics runtimeStatistics) {
        this.itemGap = parameterSpace.itemgap;
        this.seqGap = parameterSpace.seqGap;
        this.windowSize = parameterSpace.windowSize;
        this.batchSize = parameterSpace.batchSize;
        this.singleSequenceSimulator =  new HashMap<Integer, T>();
        this.decayFunction = new ExpDecay(0.1);
        this.outlierDetection = new OutlierDetection(singleSequenceSimulator, parameterSpace.outputFolder,
                parameterSpace.minLocalSupport, parameterSpace.maxLocalSupport, parameterSpace.minGlobalSupport);
        this.runtimeStatistics = runtimeStatistics;
    }

    public void initializationForEachDevice(int deviceId, String strInDevice){
    }

    public void initilization(HashMap<Integer, String> inputStringArray){
        for(Map.Entry<Integer, String> inputStr: inputStringArray.entrySet()){
//            System.out.println(inputStr.getKey());
            initializationForEachDevice(inputStr.getKey(), inputStr.getValue());
        }
        globalIndex = windowSize;
        System.out.println("Init Finished!");
    }

    public void startStreamingSimulator(HashMap<Integer, String> deviceIdMap,
                                        HashMap<String, String> metaDataMapping,
                                        HashMap<Integer, String> inputStringArray){
        globalIndex += batchSize;
        for(Map.Entry<Integer, String> inputStr: inputStringArray.entrySet()){
//            System.out.println(inputStr.getKey());
            long start = System.currentTimeMillis();
            String [] newBatchEvents = inputStr.getValue().split(",");
            singleSequenceSimulator.get(inputStr.getKey()).readBatchMoreElement(newBatchEvents, newBatchEvents.length);
            runtimeStatistics.sumTime += System.currentTimeMillis() - start;
            timeReadBatch += System.currentTimeMillis()-start;
            start = System.currentTimeMillis();
            if(globalIndex > 5000){
                outlierDetection.detectOutliers(inputStr.getKey());
                timeOutlierDetection += System.currentTimeMillis()- start;
            }
            start = System.currentTimeMillis();
            singleSequenceSimulator.get(inputStr.getKey()).addCurrentFreqPatternToHistroy();
            runtimeStatistics.sumMDL += singleSequenceSimulator.get(inputStr.getKey()).getMDLScore();
            runtimeStatistics.totalCount += 1;
            timeAddHistroy += System.currentTimeMillis()-start;
        }
        outlierDetection.clearCurPatternFrequency();
        outlierDetection.outputAndClearOutliers(globalIndex, deviceIdMap, metaDataMapping);
//        System.out.println(singleSequenceSimulator.get(1).getMDLScore());
    }

    public void readInBatchDataset(HashMap<Integer, String> deviceIdMap,
                                   HashMap<String, String> metaDataMapping, String newInputPath, int printCount){
        try {
            BufferedReader in = new BufferedReader(new FileReader(newInputPath));
            String str;
            int count = 0;
            HashMap<Integer, String> batchInput = new HashMap<>();
            while ((str = in.readLine()) != null) {
                if(str.length() == 0){
                    long start = System.currentTimeMillis();
                    if(count == 0) {
                        System.out.println("Start Initilization...");
                        initilization(batchInput);
//                        System.out.println("Init: " + batchInput.get(0).split(",").length);
                    }
                    else {
                        startStreamingSimulator(deviceIdMap, metaDataMapping, batchInput);
                        if(count % printCount == 0) {
                            System.out.println("More Events: " + batchInput.size());
                            System.out.println("Sum MDL: " + runtimeStatistics.sumMDL + ", Sum Time: " + runtimeStatistics.sumTime
                                    + ", Total Count: " + runtimeStatistics.totalCount);
                            System.out.println("Average MDL: " + runtimeStatistics.getAverageMDL());
                            System.out.println("Average Time: " + runtimeStatistics.getAverageTimeCost());
                        }
                    }
//                    System.out.println("Expire Time: " + TimeCostStatistic.expireTime/1000 +
//                            " , Merge Time: " + TimeCostStatistic.mergeTime/1000);
                    timeReadBatch = 0;
                    timeOutlierDetection = 0;
                    timeAddHistroy = 0;
                    count++;
                    batchInput.clear();
                }else {
                    String[] subs = str.split("\t");
                    batchInput.put(Integer.parseInt(subs[0]), subs[1]);
                }
            }
            in.close();
            System.out.println("Sum MDL: " + runtimeStatistics.sumMDL + ", Sum Time: " + runtimeStatistics.sumTime
                    + ", Total Count: " + runtimeStatistics.totalCount);
            System.out.println("Average MDL: " + runtimeStatistics.getAverageMDL());
            System.out.println("Average Time: " + runtimeStatistics.getAverageTimeCost());
        } catch (IOException e) {
            e.getStackTrace();
        }
        outlierDetection.outputFinalStaticOutlierResults(deviceIdMap, metaDataMapping);
    }

//    public void readInBatchDataset(HashMap<Integer, String> deviceIdMap,
//                                   HashMap<String, String> metaDataMapping, String newInputPath){
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(newInputPath));
//            String str;
//            int count = 0;
//            HashMap<Integer, String> batchInput = new HashMap<>();
//            str = in.readLine();
//            batchInput.put(8094,str.split("\t")[1]);
//            initilization(batchInput);
//            batchInput.clear();
//            while ((str = in.readLine()) != null) {
//                batchInput.put(8094,str.split("\t")[1]);
//                startStreamingSimulator(deviceIdMap, metaDataMapping, batchInput);
//                batchInput.clear();
//                count++;
//            }
//            in.close();
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//    }

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
}
