package streaming.batchopt.patterngen;

import decay.DecayFunction;
import decay.ExpDecay;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 8/8/17.
 */
public class SingleFSDetectionBasicInitTest {
    DecayFunction decayFunction;
    @Before
    public void setup(){
        decayFunction = new ExpDecay(0.1);
    }
    @Test
    public void frequentSequenceMining_Basic_1() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 3, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C"),3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_2() throws Exception {
        String str = "A,B,C,D,B,C,A,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 3, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C,D"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_3() throws Exception {
        String str = "A,B,C,E,D,A,B,F,C,D,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 1, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C,D"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_4() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C,D,A,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C"),2);
        expectedRows.put(new String("A,B,C,D"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_5() throws Exception {
        String str = "A,B,C,A,B,C,A,B,D,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 3, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C"),2);
        expectedRows.put(new String("A,B"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_6() throws Exception {
        String str = "A,B,A,B,C,D,A,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 5, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C,D"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_7() throws Exception {
        String str = "A,B,A,B,A,B,A,B";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,A,B"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_8() throws Exception {
        String str = "A,B,C,A,B,C,D,E,F,D,E,F";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C"),2);
        expectedRows.put(new String("D,E,F"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_9() throws Exception {
        String str = "A,B,C,D,A,B,C,D,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C,D"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_10() throws Exception {
        String str = "D,H,A,B,C,E,D,A,B,C,E,D,H,A,B,C,E";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C,E,D"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_11() throws Exception {
        String str = "A,B,C,D,E,F,G,H,A,B,C,D,E,F,Y,Z,G,H,Y,Z,Y,Z,A,B,C,D,E,F";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("G,H"),2);
        expectedRows.put(new String("A,B,C,D,E,F"),3);
        expectedRows.put(new String("Y,Z"),3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_12() throws Exception {
        String str = "A,A,B,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 1, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,B,C"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_13() throws Exception {
        String str = "A,A,B,B,A,B,A,A,B,B";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,A,B,B"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_14() throws Exception {
        String str = "A,A,B,B,A,B,A,A,B,B,A,B";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,A,B,B,A,B"),2);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_15() throws Exception {
        String str = "A,A,B,B,A,A,B,B,A,A,B,B";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.printCurrentStatus();
        HashMap<String, Integer> results = fsdetect.getFrequentPatternsWithCounts();
        HashMap<String, Integer> expectedRows = new HashMap<>();
        expectedRows.put(new String("A,A,B,B"),3);
        assertTrue(expectedRows.equals(results));
    }
}