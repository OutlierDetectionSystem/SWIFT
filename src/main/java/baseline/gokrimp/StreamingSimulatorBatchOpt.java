package baseline.gokrimp;

import base.StreamingSimulator;
import org.apache.commons.cli.*;

import streaming.util.FileUtile;
import util.ParameterSpace;
import util.RuntimeStatistics;

import java.util.HashMap;

/**
 * Detect violations only at the observation points,
 * otherwise just do summarize the max frequency of each freq seq
 */
public class StreamingSimulatorBatchOpt extends StreamingSimulator<SingleFSDetection> {

	public StreamingSimulatorBatchOpt(ParameterSpace parameterSpace,
                                      RuntimeStatistics runtimeStatistics) {
		super(parameterSpace, runtimeStatistics);
	}

	public void initializationForEachDevice(int deviceId, String strInDevice){
		SingleFSDetection singleFSDetection = new SingleFSDetection(windowSize,
				this.itemGap, this.seqGap, decayFunction);
		singleFSDetection.initialization(strInDevice.split(","));
		singleFSDetection.addCurrentFreqPatternToHistroy();
		this.singleSequenceSimulator.put(deviceId, singleFSDetection);
	}

	public static void main(String[] args) {
		System.out.println("New Version of GoKrimp Algorithm");
		// create Options object
		Options options = new Options();
		// add configuration file option
		options.addOption("f", true, "configuration file path");
		options.addOption("w", true, "window size");
		options.addOption("b", true, "batch size");
		options.addOption("p", true, "print per episodes");
		CommandLineParser parser = new DefaultParser();

		try {
			ParameterSpace parameterSpace = new ParameterSpace();
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("f")) {
				System.out.println(cmd.getOptionValue("f"));
				String configurationFilePath = cmd.getOptionValue("f");
				parameterSpace.readInParametersFromFile(configurationFilePath);
			}
			if(cmd.hasOption("w")){
				System.out.println(cmd.getOptionValue("w"));
				int windowSize = Integer.parseInt(cmd.getOptionValue("w"));
				parameterSpace.windowSize = windowSize;
			}
			if(cmd.hasOption("b")){
				int batchSize = Integer.parseInt(cmd.getOptionValue("b"));
				parameterSpace.batchSize = batchSize;
			}
			int printCount = 0;
			if(cmd.hasOption("p")){
				printCount = Integer.parseInt(cmd.getOptionValue("p"));
			}
			parameterSpace.inputPath += parameterSpace.batchSize + "_" + parameterSpace.windowSize + ".csv";
			parameterSpace.deviceIdPath += parameterSpace.batchSize + "_" + parameterSpace.windowSize + ".csv";
			parameterSpace.outputParameterSpace();
			HashMap<String, String> metaDataMapping = FileUtile.readInMetaDataToMemory(FileUtile.readInDataset(parameterSpace.metaDataFile));
			HashMap<Integer, String> deviceIdMap = new HashMap<Integer, String>();
			FileUtile.readInDeviceIds(parameterSpace.deviceIdPath, deviceIdMap);

			long startTime = System.currentTimeMillis();
			RuntimeStatistics runtimeStatisticsSeqKrimp = new RuntimeStatistics();
			StreamingSimulatorBatchOpt globalFS = new StreamingSimulatorBatchOpt(parameterSpace, runtimeStatisticsSeqKrimp);
			globalFS.readInBatchDataset(deviceIdMap, metaDataMapping, parameterSpace.inputPath, printCount);
			System.out.println("Compute Global Frequent Sequence takes " + (System.currentTimeMillis() - startTime) / 1000
					+ " seconds!");

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
