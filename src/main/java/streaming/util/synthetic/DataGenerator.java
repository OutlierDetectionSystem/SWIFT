package streaming.util.synthetic;

import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataGenerator {
	private int windowSize = 10000;
	private int batchSize = 500;
	private int numBatches = 10000;

	private int alphabetSize = 1000;
	private int maxLengthFreqPatterns = 10;
	private int numPatterns = 5000;

	private String outputFilePath;
	private boolean verbose;
	private ArrayList<String> patternCandidates;

	public DataGenerator(int alphabetSize, int windowSize, int batchSize, int numBatches, int maxLengthFreqPatterns,
			int numPatterns, String outputFilePath, boolean verbose) {
		this.alphabetSize = alphabetSize;
		this.windowSize = windowSize;
		this.maxLengthFreqPatterns = maxLengthFreqPatterns;
		this.batchSize = batchSize;
		this.numPatterns = numPatterns;
		this.numBatches = numBatches;
		this.outputFilePath = outputFilePath;
		this.verbose = verbose;
	}

	public void generatePatterns() {
		GeneratePatterns gp = new GeneratePatterns();
		this.patternCandidates = gp.generatePatternCandidates(this.numPatterns, this.maxLengthFreqPatterns,
				this.alphabetSize);
		gp.outputPatternCandidatesToFile(patternCandidates, "PatternCandidates.csv");
	}

	public void generateDevices() {
		GenerateDevices gd = new GenerateDevices(windowSize, batchSize, numBatches, verbose);
		gd.addPatternCandidates(patternCandidates, outputFilePath);
	}

	public void generateData() {
		// build up a set of pattern candidates
		generatePatterns();
		generateDevices();
	}

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("a", true, "alphabet size");
		options.addOption("w", true, "window size");
		options.addOption("b", true, "batch size");
		options.addOption("i", true, "number of iterations");
		options.addOption("m", true, "max length of frequent patterns");
		options.addOption("p", true, "number of pattern candidates");
		options.addOption("o", true, "output file directory");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
			int alphabetSize = Integer.parseInt(cmd.getOptionValue("a"));
			int windowSize = Integer.parseInt(cmd.getOptionValue("w"));
			int batchSize = Integer.parseInt(cmd.getOptionValue("b"));
			int numBatches = Integer.parseInt(cmd.getOptionValue("i"));
			int maxLengthFreqPatterns = Integer.parseInt(cmd.getOptionValue("m"));
			int numPatterns = Integer.parseInt(cmd.getOptionValue("p"));
			String outputFileDic = cmd.getOptionValue("o");

			String outputFilePath = outputFileDic + "synthetic_" + alphabetSize + "_" + maxLengthFreqPatterns
					+ "_" + numPatterns + "_" + numBatches + "_"  + batchSize + "_"+ windowSize + ".csv";
			String deviceIdPath = outputFileDic + "deviceId_" + alphabetSize + "_" + maxLengthFreqPatterns
					+ "_" + numPatterns + "_" + numBatches + "_"  + batchSize + "_"+ windowSize + ".csv";
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(deviceIdPath)));
			bw.write("1\t1");
			bw.newLine();
			bw.close();
			boolean verbose = true;
			DataGenerator dataGenerator = new DataGenerator(alphabetSize, windowSize, batchSize, numBatches,
					maxLengthFreqPatterns, numPatterns, outputFilePath, verbose);
			dataGenerator.generateData();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
