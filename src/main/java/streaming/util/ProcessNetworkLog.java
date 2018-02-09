package streaming.util;

import java.io.*;
import java.util.*;

/**
 * Created by yizhouyan on 10/17/17.
 */
public class ProcessNetworkLog {
    private HashMap<Integer,String> metaData;
    private String inputData;

    public ProcessNetworkLog(){
            this.metaData = new HashMap<>();
    }
    public void readInMetaData(String metaFile){
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(metaFile)));
            String str;
            while((str = br.readLine())!=null){
                metaData.put(Integer.parseInt(str.split("\t")[0]),str.split("\t")[1]);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInInputData(String inputFile){
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(inputFile)));
            String str;
            int count = 0;
            StringBuilder strBuilder = new StringBuilder();
            while((str = br.readLine())!=null){
                strBuilder.append(str.split("\t")[2] + ",");
                count++;
                if(count == 5000)
                    break;
            }
            br.close();
            String inputStr = strBuilder.toString();
            this.inputData = inputStr.substring(0,inputStr.length()-1);
            System.out.println(inputStr.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printAndSaveFinalTopPatterns(HashMap<String, Integer> freqPatternsWithCounts){
        Set<Integer> frequenciesInSet = new HashSet<>(freqPatternsWithCounts.values());
        ArrayList<Integer> frequencies = new ArrayList<>(frequenciesInSet);
        Collections.sort(frequencies, Collections.<Integer>reverseOrder());
        HashMap<Integer, ArrayList<String>> freqPatternsOrderedByCount = new HashMap<>();
        for(Map.Entry<String, Integer> entry: freqPatternsWithCounts.entrySet()){
            if(freqPatternsOrderedByCount.containsKey(entry.getValue())){
                freqPatternsOrderedByCount.get(entry.getValue()).add(entry.getKey());
            }else{
                freqPatternsOrderedByCount.put(entry.getValue(), new ArrayList<String>());
                freqPatternsOrderedByCount.get(entry.getValue()).add(entry.getKey());
            }
        }
        HashMap<String, Integer> finalTopPatterns = new HashMap<String, Integer>();
        ArrayList<String> finalTopPatternsInList = new ArrayList<>();

        for(int i: frequencies){
            for(String str: freqPatternsOrderedByCount.get(i)){
                finalTopPatterns.put(str, i);
                finalTopPatternsInList.add(str);
            }
//            if(finalTopPatterns.size() >= 50)
//                break;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data/results/jmlr-single")));
            for(String finalP: finalTopPatternsInList){
                String metaForP = getMetaForPattern(finalP);
                System.out.println(finalP + "\t"  + metaForP + "\t"+ finalTopPatterns.get(finalP));
                bw.write(finalP + "\t"  + metaForP + "\t"+ finalTopPatterns.get(finalP));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMetaForPattern(String pattern){
        String meta = "";
        String [] splitsP = pattern.split(",");
        for(String str: splitsP){
            meta += metaData.get(Integer.parseInt(str)) + ",";
        }
        if(meta.length() > 0)
            meta = meta.substring(0, meta.length()-1);
        return meta;
    }
    public void processJMLR(String inputFile, String metaFile){
        readInInputData(inputFile);
        readInMetaData(metaFile);
//        SingleFSDetection singleFSDetection = new SingleFSDetection(80000,
//                5, 80000, null);
//        singleFSDetection.initialization(inputData.split(","), 100);
//        baseline.seqkrimp.SingleFSDetection singleFSDetection = new baseline.seqkrimp.SingleFSDetection(80000,
//                0, 80000, null);
//        singleFSDetection.initialization(inputData.split(","));
        streaming.patterngen.SingleFSDetection singleFSDetection = new streaming.patterngen.SingleFSDetection(258688820,
                0, 10, null);
        singleFSDetection.initialization(inputData.split(","));
//        baseline.gokrimp.SingleFSDetection singleFSDetection = new baseline.gokrimp.SingleFSDetection(80000,
//                0, 80000, null);
//        singleFSDetection.initialization(inputData.split(","));
        HashMap<String, Integer> freqPatternsWithCounts = singleFSDetection.getFrequentPatternsWithCounts();
        System.out.println(freqPatternsWithCounts.size());
        printAndSaveFinalTopPatterns(freqPatternsWithCounts);
    }
    public static void main(String [] args){

        String inputFile = "data/realdata/CT_data.csv";
        String metaFile = "data/realdata/CT_data_meta";
        ProcessNetworkLog processJMLR = new ProcessNetworkLog();
        processJMLR.processJMLR(inputFile, metaFile);
    }
}
