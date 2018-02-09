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
public class SingleFSDetectionBasicTest {
    DecayFunction decayFunction;
    @Before
    public void setup(){
        decayFunction = new ExpDecay(0.1);
    }
    @Test
    public void frequentSequenceMining_Basic_1() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 3, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        fsdetect.printCurrentStatus();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_2() throws Exception {
        String str = "A,B,C,D,B,C,A,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 3, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C,D"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_3() throws Exception {
        String str = "A,B,C,E,D,A,B,F,C,D,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 1, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_4() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C,D,A,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C"));
        expectedRows.add(new String("A,B,C,D"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_5() throws Exception {
        String str = "A,B,C,A,B,C,A,B,D,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 3, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_6() throws Exception {
        String str = "A,B,A,B,C,D,A,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 5, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C,D"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_7() throws Exception {
        String str = "A,B,A,B,A,B,A,B";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,A,B"));
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_8() throws Exception {
        String str = "A,B,C,A,B,C,D,E,F,D,E,F";
        SingleFSDetection fsdetect = new SingleFSDetection(str, 10000, 0, 10, decayFunction);
        fsdetect.FrequentSequenceMining();
        HashSet<String> results = fsdetect.getFrequentPatterns();

        HashSet<String> expectedRows = new HashSet<String>();
        expectedRows.add(new String("A,B,C"));
        expectedRows.add(new String("D,E,F"));
        assertTrue(expectedRows.equals(results));
    }

}