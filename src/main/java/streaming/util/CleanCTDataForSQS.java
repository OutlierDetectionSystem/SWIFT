package streaming.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhouyan on 7/27/17.
 */
public class CleanCTDataForSQS {
    private HashMap<String, Integer> metaDataMap = new HashMap<>();
    private int indexForMap = 0;

    private int batchSize;
    private int windowSize;

    public CleanCTDataForSQS(int batchSize, int windowSize){
        this.batchSize = batchSize;
        this.windowSize = windowSize;
    }

    public int getIdentifier(String value){
        if(metaDataMap.containsKey(value))
            return metaDataMap.get(value);
        else{
            metaDataMap.put(value, indexForMap);
            indexForMap++;
            return metaDataMap.get(value);
        }
    }

    public void cleanCTDataset(String inputPath, String outputPath){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputPath)));
            BufferedReader in = new BufferedReader(new FileReader(inputPath));
            String str;
            ArrayList<Integer> events = new ArrayList<>();
            while ((str = in.readLine()) != null) {
                String[] subs = str.split("\t");
                String[] subsplits = subs[2].split(",");

                for(String substr: subsplits){
                    if(substr.split("\\|")[0].equals("u"))
                        continue;
                    events.add(getIdentifier(substr.split("\\|")[0]));
                }
            }
            System.out.println("End Reading " + events.size());

            if(events.size() >= windowSize) {
                String newStr = "";
                for (int i = 0; i < windowSize; i++) {
                    newStr += events.get(i) + " ";
                }
                if (newStr.length() > 0)
                    newStr = newStr.substring(0, newStr.length() - 1);
                bw.write(newStr + " -1 ");
                int count = 0;
                if (events.size() > windowSize) {
                    int globalIndex = windowSize;
                    while (events.size() > globalIndex) {
                        String newOutput = "";
                        for (int i = globalIndex-(windowSize-batchSize); i < Math.min(batchSize + globalIndex, events.size()); i++) {
                            newOutput += events.get(i) + " ";
                        }
                        if (newOutput.length() > 0)
                            newOutput = newOutput.substring(0, newOutput.length() - 1);
                        bw.write(newOutput+ " -1 ");
                        globalIndex += batchSize;
                        if(count++ == 10000)
                            break;
                    }
                }

            }
            bw.close();
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputPath = "data/realdata/CT_data_formatted.csv";
        int batchSize = Integer.parseInt(args[0]);
        int windowSize  = Integer.parseInt(args[1]);

        String outputPath = "data/realdata/CT_data_batch_SQS_" + batchSize + "_" + windowSize + ".csv";
        long startTime = System.currentTimeMillis();
        CleanCTDataForSQS splitData = new CleanCTDataForSQS(batchSize, windowSize);
        splitData.cleanCTDataset(inputPath, outputPath);
        System.out.println("Clean CT dataset takes " + (System.currentTimeMillis() - startTime) / 1000
                + " seconds!");
    }
}
