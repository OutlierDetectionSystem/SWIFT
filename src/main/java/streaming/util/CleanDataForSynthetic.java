package streaming.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yizhouyan on 7/27/17.
 */
public class CleanDataForSynthetic {
    private int batchSize;
    private int windowSize;

    public CleanDataForSynthetic(int batchSize, int windowSize){
        this.batchSize = batchSize;
        this.windowSize = windowSize;
    }

    public void cleanCTDataset(String inputPath, String outputPath){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputPath)));
            BufferedReader in = new BufferedReader(new FileReader(inputPath));
            String str;
            ArrayList<Integer> events = new ArrayList<>();
            while ((str = in.readLine()) != null) {
                if(str.equals(""))
                    continue;
                String[] subs = str.split("\t");
                String[] subsplits = subs[1].split(",");
                for(String substr: subsplits){
                    events.add(Integer.parseInt(substr));
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
                if (events.size() > windowSize) {
                    int globalIndex = windowSize;
                    while (events.size() > globalIndex) {
                        StringBuilder sbuilder = new StringBuilder();
                        for (int i = globalIndex-(windowSize-batchSize); i < Math.min(batchSize + globalIndex, events.size()); i++) {
                            sbuilder.append(events.get(i) + " ");
                        }
                        String newOutput = sbuilder.toString();
                        if (newOutput.length() > 0)
                            newOutput = newOutput.substring(0, newOutput.length() - 1);
                        bw.write(newOutput+ " -1 ");
                        globalIndex += batchSize;
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
        String inputPath = args[0];
        String outputPath = args[1];
        int batchSize = Integer.parseInt(args[2]);
        int windowSize  = Integer.parseInt(args[3]);

        long startTime = System.currentTimeMillis();
        CleanDataForSynthetic splitData = new CleanDataForSynthetic(batchSize, windowSize);
        splitData.cleanCTDataset(inputPath, outputPath);
        System.out.println("Clean synthetic dataset takes " + (System.currentTimeMillis() - startTime) / 1000
                + " seconds!");
    }
}
