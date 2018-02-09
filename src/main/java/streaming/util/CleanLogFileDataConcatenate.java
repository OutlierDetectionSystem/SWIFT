package streaming.util;

import java.io.*;

/**
 * Created by yizhouyan on 9/25/17.
 */
public class CleanLogFileDataConcatenate {

    public CleanLogFileDataConcatenate(){
    }

    public void cleanCTData(String inputFile, String outputFile){
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));
            String str = "";
            int count = 0;
            int seqID = 0;
            String tempStr = "";


            while ((str = br.readLine()) != null) {
                String [] splits = str.split("\t");
                tempStr += splits[2] + ",";
                count += splits[2].split(",").length;

                if(count >= 10000){
                    tempStr = tempStr.substring(0, tempStr.length()-1);
                    bw.write(seqID + "\t" + seqID + "\t" + tempStr);
                    bw.newLine();
                    count = 0;
                    tempStr = "";
                    seqID += 1;
                }
            }

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args){
        String inputPath = "data/realdata/real10000data_compressedDic_id.csv";
//        String inputPath = "data/realdata/CT_data_formatted.csv";
        String outputPath = "data/realdata/real10000data_compressedDic_id_concat.csv";
        String inputFile = inputPath;
        String outputFile = outputPath;
//        String inputFile = "data/inputData/CT_data_formatted_noTS.csv";
//        String outputFile = "data/inputData/CT_data_formatted_noTS_concat.csv";
        CleanLogFileDataConcatenate cleanDataset = new CleanLogFileDataConcatenate();
        cleanDataset.cleanCTData(inputFile, outputFile);
    }
}
