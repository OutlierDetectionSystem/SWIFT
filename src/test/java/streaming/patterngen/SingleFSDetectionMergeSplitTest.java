package streaming.patterngen;

import decay.DecayFunction;
import decay.ExpDecay;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertTrue;

/**
 * Created by yizhouyan on 7/16/17.
 */
public class SingleFSDetectionMergeSplitTest {
    DecayFunction decayFunction;
    @Before
    public void setup(){
        decayFunction = new ExpDecay(0.1);
    }

    @Test
    public void frequentSequenceMining_SimpleSplit() throws Exception {
        String str = "A,B,C,D,A,B,C,D,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_SplitThenMerge() throws Exception {
        String str = "D,H,A,B,C,E,D,A,B,C,E,D,H,A,B,C,E";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("D,H"));
        expectedRows.add(new String("A,B,C,E"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_WaitingList() throws Exception {
        String str = "A,B,C,D,E,F,G,H,A,B,C,D,E,F,Y,Z,G,H,Y,Z,Y,Z,A,B,C,D,E,F";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C,D,E,F"));
        expectedRows.add(new String("G,H"));
        expectedRows.add(new String("Y,Z"));
        assertTrue(expectedRows.equals(results));
    }

}