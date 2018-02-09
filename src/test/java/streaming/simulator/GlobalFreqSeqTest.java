package streaming.simulator;//package streaming.outlierdetection;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.HashSet;
//
//import static org.junit.Assert.*;
//
///**
// * Created by yizhouyan on 7/21/17.
// */
//public class GlobalFreqSeqTest {
//    private GlobalFreqSeq globalFreqSeq;
//    @Before
//    public void setup(){
//        globalFreqSeq = new GlobalFreqSeq(5,2,2);
//    }
//
//    @Test
//    public void addToTypicalPatterns() throws Exception {
//        HashMap<String, Integer> freqSeqWithCount = new HashMap<String, Integer>();
//        freqSeqWithCount.put("A,B,C", 5);
//        freqSeqWithCount.put("A,C,D",2);
//        freqSeqWithCount.put("A,B,D",1);
//        globalFreqSeq.addToTypicalPatterns(freqSeqWithCount, 0);
//        HashSet<String> violations = globalFreqSeq.addToTypicalPatterns(freqSeqWithCount,1);
//        HashMap<String, HashSet<Integer>> expectedResults = new HashMap<String, HashSet<Integer>>();
//        HashSet<Integer> deviceIds = new HashSet<Integer>();
//        deviceIds.add(0);
//        deviceIds.add(1);
//        expectedResults.put("A,B,C", deviceIds);
//        assertEquals(globalFreqSeq.getCurrentFreqPatterns(), expectedResults);
//        HashSet<String> expectedViolations = new HashSet<String>();
//        expectedViolations.add("A,C,D");
//        assertEquals(violations, expectedViolations);
//    }
//
//}