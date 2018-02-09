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
public class SingleFSDetectionRepeatLetterTest {
    DecayFunction decayFunction;
    @Before
    public void setup(){
        decayFunction = new ExpDecay(0.1);
    }

    @Test
    public void frequentSequenceMining_Repeat_1() throws Exception {
        String str = "A,A,B,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 1, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Repeat_2() throws Exception {
        String str = "A,A,B,B,A,B,A,A,B,B";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,A,B,B"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Repeat_3() throws Exception {
        String str = "A,A,B,B,A,B,A,A,B,B,A,B";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,A,B,B,A,B"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Repeat_4() throws Exception {
        String str = "A,A,B,B,A,A,B,B,A,A,B,B";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,A,B,B"));
        assertTrue(expectedRows.equals(results));
    }
}