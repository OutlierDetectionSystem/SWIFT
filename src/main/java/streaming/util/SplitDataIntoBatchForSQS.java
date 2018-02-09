package streaming.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhouyan on 7/27/17.
 */
public class SplitDataIntoBatchForSQS {
    private int batchSize;
    private int windowSize;

    public SplitDataIntoBatchForSQS(int batchSize, int windowSize) {
        this.batchSize = batchSize;
        this.windowSize = windowSize;
    }

    public void readInDatasetWithDeviceIds(String inputPath, String newInputPath) {
        System.out.println("Read in dataset with device id....");
        File file = new File(inputPath);

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newInputPath)));
            String str;
            int count = 0;
            while ((str = in.readLine()) != null) {
                String[] subs = str.split("\t");
                ArrayList<String> stringSplitInBatch = new ArrayList<String>();
                String[] subsplits = subs[2].split(",");
                if (subsplits.length >= windowSize) {
                    StringBuilder newStrBuilder = new StringBuilder();
                    for (int i = 0; i < windowSize; i++) {
                        newStrBuilder.append(subsplits[i] + " ");
                    }
                    String newStr = newStrBuilder.toString();
                    if (newStr.length() > 0)
                        newStr = newStr.substring(0, newStr.length() - 1);
                    stringSplitInBatch.add(newStr);
                    if (subsplits.length > windowSize) {
                        int globalIndex = windowSize;
                        while (subsplits.length > globalIndex) {
                            StringBuilder newOutputBuilder = new StringBuilder();
                            int newEventSize = Math.min(batchSize, subsplits.length - globalIndex);
                            for (int i = globalIndex - windowSize + newEventSize; i < globalIndex + newEventSize; i++) {
                                newOutputBuilder.append(subsplits[i] + " ");
                            }
                            String newOutput = newOutputBuilder.toString();
                            if (newOutput.length() > 0)
                                newOutput = newOutput.substring(0, newOutput.length() - 1);
                            stringSplitInBatch.add(newOutput);
                            globalIndex += batchSize;
                        }
                    }
                }
                if (stringSplitInBatch.size() > 0) {
                    int globalIndex = 0;
                    while(stringSplitInBatch.size() > globalIndex) {
                        bw.write(stringSplitInBatch.get(globalIndex));
                        bw.write(" -1 ");
                        globalIndex++;
                    }
                }
                count++;
                System.out.println(count);
            }
            in.close();
            bw.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }


    public static void main(String[] args) {
//		 String inputPath = "ConnectedLogs.tsv";
        String inputPath = "data/realdata/real10000data_compressedDic_id.csv";
//        String inputPath = "data/realdata/test";
        int batchSize = Integer.parseInt(args[0]);
        int windowSize = Integer.parseInt(args[1]);
        String newInputPath = "data/realdata/SQS_batch_" + batchSize + "_" + windowSize + ".csv";
//        String newInputPath = "data/realdata/testdata_batch_" + batchSize + "_" + windowSize + ".csv";
//        String deviceIdPath = "data/realdata/testdata_deviceId_"+ batchSize + "_" + windowSize + ".csv";
        long startTime = System.currentTimeMillis();
        SplitDataIntoBatchForSQS splitData = new SplitDataIntoBatchForSQS(batchSize, windowSize);
        splitData.readInDatasetWithDeviceIds(inputPath, newInputPath);
        System.out.println("Split dataset into batches takes " + (System.currentTimeMillis() - startTime) / 1000
                + " seconds!");
    }
}
