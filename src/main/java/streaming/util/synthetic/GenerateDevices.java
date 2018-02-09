package streaming.util.synthetic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class GenerateDevices {
	private int windowSize = 10000;
	private int batchSize = 500;
	private int numBatches = 10000;

	private int numElementsInDevices = 0;
	private boolean verbose = false;

	public GenerateDevices(int windowSize, int batchSize, int numBatches, boolean verbose) {
		this.windowSize = windowSize;
		this.verbose = verbose;
		this.batchSize = batchSize;
		this.numBatches = numBatches;
	}

	public String generateSequences(ArrayList<String> patternCandidates, int sequenceSize){
		int numPatterns = patternCandidates.size();
		int countCurSize = 0;
		StringBuilder curPattern = new StringBuilder();
		while(countCurSize < sequenceSize){
			int selectedPC = StdRandom.gaussian(numPatterns - 1, 0, numPatterns / 2, numPatterns / 5);
			String currentPC = patternCandidates.get(selectedPC);
			int currentPCLength = currentPC.split(",").length;
			if(countCurSize + currentPCLength > sequenceSize){
				int restLen = sequenceSize-countCurSize;
				String [] splits = currentPC.split(",");
				for(int i = 0; i< restLen; i++){
					curPattern.append(splits[i] + ",");
				}
				break;
			}else{
				curPattern.append(currentPC + ",");
				countCurSize += currentPCLength;
			}
		}
		String returnStr = curPattern.toString();
		if(returnStr.length() > 0)
			returnStr = returnStr.substring(0, returnStr.length()-1);
		return returnStr;
	}

	public void addPatternCandidates(ArrayList<String> patternCandidates, String outputFilePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFilePath)));
			// init window and output
			writer.write(1 + "\t" + generateSequences(patternCandidates, windowSize));
			writer.newLine();
			writer.newLine();
			for(int i = 0; i< numBatches; i++){
				writer.write(1 + "\t" + generateSequences(patternCandidates, batchSize));
				writer.newLine();
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
