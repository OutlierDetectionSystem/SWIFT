package streaming.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhouyan on 7/27/17.
 */
public class SplitDataIntoBatchWithCut {
    private int batchSize;
    private int windowSize;

    public SplitDataIntoBatchWithCut(int batchSize, int windowSize){
        this.batchSize = batchSize;
        this.windowSize = windowSize;
    }

    public void readInDatasetWithDeviceIds(String inputPath, HashMap<Integer, ArrayList<String>> inputStringArray, HashMap<Integer, String> deviceIdMap ) {
        System.out.println("Read in dataset with device id....");
        File file = new File(inputPath);
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            int count = 0;
            while ((str = in.readLine()) != null) {
                String [] subs = str.split("\t");
                deviceIdMap.put(count, subs[1]);
                ArrayList<String> stringSplitInBatch = new ArrayList<String>();
                String [] subsplits = subs[2].split(",");
                if(subsplits.length >= windowSize) {
                    String newStr = "";
                    for (int i = 0; i < windowSize; i++) {
                        newStr += subsplits[i] + ",";
                    }
                    if (newStr.length() > 0)
                        newStr = newStr.substring(0, newStr.length() - 1);
                    stringSplitInBatch.add(newStr);
                    if (subsplits.length > windowSize) {
                        int globalIndex = windowSize;
                        while (subsplits.length > globalIndex) {
                            String newOutput = "";
                            for (int i = globalIndex; i < Math.min(batchSize + globalIndex, subsplits.length); i++) {
                                newOutput += subsplits[i] + ",";
                            }
                            if (newOutput.length() > 0)
                                newOutput = newOutput.substring(0, newOutput.length() - 1);
                            stringSplitInBatch.add(newOutput);
                            globalIndex += batchSize;
                        }
                    }
                }
                if(stringSplitInBatch.size() > 0)
                    inputStringArray.put(count, stringSplitInBatch);
                count++;
//                System.out.println(count);
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public void outputDeviceIdMap(HashMap<Integer, String> deviceIdMap, String deviceIdPath){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(deviceIdPath)));
            for (Map.Entry<Integer, String> singleDevice : deviceIdMap.entrySet()) {
                bw.write(singleDevice.getKey() + "\t" + singleDevice.getValue());
                bw.newLine();
            }
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void readInDataset(String inputPath, String newInputPath, String deviceIdPath){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newInputPath)));
            HashMap<Integer, ArrayList<String>> inputStringArray = new HashMap<>();
            HashMap<Integer, String> deviceIdMap = new HashMap<>();
            readInDatasetWithDeviceIds(inputPath, inputStringArray, deviceIdMap);
            outputDeviceIdMap(deviceIdMap, deviceIdPath);

            int globalIndex = 0;
            while(inputStringArray.size() > 0) {
                System.out.println(inputStringArray.size() + " pending devices");
                HashMap<Integer, ArrayList<String>> remainingString = new HashMap<>();
                for(int i = 0; i< 10000; i++){
                    if(inputStringArray.containsKey(i)){
                        bw.write(i + "\t" + inputStringArray.get(i).get(globalIndex));
                        bw.newLine();
                    if (inputStringArray.get(i).size() > globalIndex + 1)
                        remainingString.put(i, inputStringArray.get(i));
                    }
                }
                bw.newLine();
                globalIndex++;
                inputStringArray = remainingString;
            }
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//		 String inputPath = "ConnectedLogs.tsv";
        String inputPath = "data/realdata/real10000data_compressedDic_id_concat.csv";
//        String inputPath = "data/realdata/CT_data_One.csv";
        int batchSize = Integer.parseInt(args[0]);
        int windowSize  = Integer.parseInt(args[1]);
        String newInputPath = "data/realdata/real10000data_batch_" + batchSize + "_" + windowSize + ".csv";
        String deviceIdPath = "data/realdata/real10000data_deviceId_"+ batchSize + "_" + windowSize + ".csv";
//        String newInputPath = "data/realdata/CT_data_batch_" + batchSize + "_" + windowSize + ".csv";
//        String deviceIdPath = "data/realdata/CT_data_deviceId_"+ batchSize + "_" + windowSize + ".csv";
        long startTime = System.currentTimeMillis();
        SplitDataIntoBatchWithCut splitData = new SplitDataIntoBatchWithCut(batchSize, windowSize);
        splitData.readInDataset(inputPath, newInputPath, deviceIdPath);
        System.out.println("Split dataset into batches takes " + (System.currentTimeMillis() - startTime) / 1000
                + " seconds!");
    }
}
