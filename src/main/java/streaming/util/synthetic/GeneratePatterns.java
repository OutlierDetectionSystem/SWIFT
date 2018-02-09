package streaming.util.synthetic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class GeneratePatterns {
	public GeneratePatterns() {
	}

	public ArrayList<String> generatePatternCandidates(int numPatterns, int maxLengthFreqPatterns, int alphabetSize) {
		ArrayList<String> patternCandidates = new ArrayList<String>();
		for (int i = 0; i < numPatterns; i++) {
			String tempStr = "";
			int curLength = 0;
			curLength = StdRandom.gaussian(maxLengthFreqPatterns, 2, (int) Math.round(maxLengthFreqPatterns * 2.0 / 3),
					(int) Math.round(maxLengthFreqPatterns / 5));
			HashSet<Integer> alreadyAdded = new HashSet<Integer>();
			for (int j = 0; j < curLength; j++) {
				int item = StdRandom.random.nextInt(alphabetSize);
				while (alreadyAdded.contains(item)) {
					item = StdRandom.random.nextInt(alphabetSize);
				}
				alreadyAdded.add(item);
				tempStr += item + ",";
			}
			if (tempStr.length() > 0)
				tempStr = tempStr.substring(0, tempStr.length() - 1);
			patternCandidates.add(tempStr);
		}
		return patternCandidates;
	}


	public void outputPatternCandidatesToFile(ArrayList<String> patternCandidates, String fileName) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
			for (String curFS : patternCandidates) {
				writer.write(curFS);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
