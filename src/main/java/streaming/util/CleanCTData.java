package streaming.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhouyan on 7/27/17.
 */
public class CleanCTData {
    private HashMap<String, Integer> metaDataMap = new HashMap<>();
    private int indexForMap = 0;

    private int batchSize;
    private int windowSize;

    public CleanCTData(int batchSize, int windowSize){
        this.batchSize = batchSize;
        this.windowSize = windowSize;
    }

    public void outputDeviceIdMap(String deviceIdPath){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(deviceIdPath)));

                bw.write(1 + "\t" + 1);
                bw.newLine();

            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void outputMetaData(String metaDataPath){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(metaDataPath)));
            for (Map.Entry<String, Integer> singleMeta : metaDataMap.entrySet()) {
                bw.write(singleMeta.getValue() + "\t" + singleMeta.getKey() );
                bw.newLine();
            }
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
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

    public void cleanCTDataset(String inputPath, String outputPath, String metaDataPath, String deviceIdPath){
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
//            ArrayList<String> stringSplitInBatch = new ArrayList<String>();
            if(events.size() >= windowSize) {
                String newStr = "";
                for (int i = 0; i < windowSize; i++) {
                    newStr += events.get(i) + ",";
                }
                if (newStr.length() > 0)
                    newStr = newStr.substring(0, newStr.length() - 1);
                bw.write(1 + "\t" + newStr);
                bw.newLine();
                bw.newLine();
                if (events.size() > windowSize) {
                    int globalIndex = windowSize;
                    while (events.size() > globalIndex) {
                        String newOutput = "";
                        for (int i = globalIndex; i < Math.min(batchSize + globalIndex, events.size()); i++) {
                            newOutput += events.get(i) + ",";
                        }
                        if (newOutput.length() > 0)
                            newOutput = newOutput.substring(0, newOutput.length() - 1);
                        bw.write(1 + "\t" + newOutput);
                        bw.newLine();
                        bw.newLine();
                        globalIndex += batchSize;
//                        System.out.println(globalIndex);
                    }
                }
            }

            bw.close();
            in.close();
            outputMetaData(metaDataPath);
            outputDeviceIdMap(deviceIdPath);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputPath = "data/realdata/CT_data_formatted_concat.csv";
        int batchSize = Integer.parseInt(args[0]);
        int windowSize  = Integer.parseInt(args[1]);

        String metaDataMap = "data/realdata/CT_data_meta_One";
        String outputPath = "data/realdata/CT_data_batch_" + batchSize + "_" + windowSize + ".csv";
        String deviceIdPath = "data/realdata/CT_data_deviceId_"+ batchSize + "_" + windowSize + ".csv";

        long startTime = System.currentTimeMillis();
        CleanCTData splitData = new CleanCTData(batchSize, windowSize);
        splitData.cleanCTDataset(inputPath, outputPath, metaDataMap, deviceIdPath);
        System.out.println("Clean CT dataset takes " + (System.currentTimeMillis() - startTime) / 1000
                + " seconds!");
    }
}
