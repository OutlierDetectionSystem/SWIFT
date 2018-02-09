package streaming.simulator;

import org.junit.Before;
import org.junit.Test;
import outlierdetection.SequencesInvertedIndex;
import outlierdetection.ViolationDetection;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 7/21/17.
 */
public class ViolationDetectionTest {
    private SequencesInvertedIndex freqSeqs;
    private ViolationDetection vd;

    @Before
    public void setup(){
        freqSeqs = new SequencesInvertedIndex(100,100);
        HashSet<String> frequentSequences = new HashSet<>();
        frequentSequences.add("A,B,C");
        frequentSequences.add("A,C,D");
        frequentSequences.add("B,C,D");
        freqSeqs.addFreqSeqsToList(frequentSequences);
        this.vd = new ViolationDetection();
    }

    @Test
    public void disorderedSequence_SuperSequence() throws Exception {
        assertFalse(vd.disorderedSequence("A,B,C", "A,B"));
    }

    @Test
    public void disorderedSequence_SubSequence() throws Exception {
        assertFalse(vd.disorderedSequence("A,B", "A,B,C"));
    }

    @Test
    public void disorderedSequence_SubDisorderedSequence() throws Exception {
        assertFalse(vd.disorderedSequence("B,A", "A,B,C"));
    }

    @Test
    public void disorderedSequence_EqualSequence() throws Exception {
        assertFalse(vd.disorderedSequence("A,B,C", "A,B,C"));
    }

    @Test
    public void disorderedSequence_DisorderSequence() throws Exception {
        assertTrue(vd.disorderedSequence("A,B,C", "A,C,B"));
    }

    @Test
    public void disorderedSequence_RepeatEqualSequence() throws Exception {
        assertFalse(vd.disorderedSequence("A,B,C,C", "A,B,C,C"));
    }

    @Test
    public void disorderedSequence_RepeatDisorderSequence() throws Exception {
        assertTrue(vd.disorderedSequence("A,C,B,C", "A,B,C,C"));
    }

    @Test
    public void detectViolations_SubViolation_1() throws Exception {
        HashSet<String> violationCandidates = new HashSet<>();
        violationCandidates.add("B,C");
        vd.detectViolations(freqSeqs,violationCandidates,0);
        HashSet<String> results = vd.getViolations().get(0).get("B,C").getViolatedSeq();
        assertEquals(results.size(),1);
        HashSet<String> expectedResults = new HashSet<>();
        expectedResults.add("B,C,D");
        assertEquals(results, expectedResults);
    }

    @Test
    public void detectViolations_SubViolation_2() throws Exception {
        HashSet<String> violationCandidates = new HashSet<>();
        violationCandidates.add("A,C");
        vd.detectViolations(freqSeqs,violationCandidates,0);
        HashSet<String> results = vd.getViolations().get(0).get("A,C").getViolatedSeq();
        assertEquals(results.size(),2);
        HashSet<String> expectedResults = new HashSet<>();
        expectedResults.add("A,B,C");
        expectedResults.add("A,C,D");
        assertEquals(results, expectedResults);
    }

    @Test
    public void detectViolations_SubViolation_3() throws Exception {
        HashSet<String> violationCandidates = new HashSet<>();
        violationCandidates.add("C,D");
        vd.detectViolations(freqSeqs,violationCandidates,0);
        assertEquals(vd.getViolations().size(),0);
    }

    @Test
    public void detectViolations_SuperViolation() throws Exception {
        HashSet<String> violationCandidates = new HashSet<>();
        violationCandidates.add("A,B,C,D");
        vd.detectViolations(freqSeqs,violationCandidates,0);
        assertEquals(vd.getViolations().size(),0);
    }

    @Test
    public void detectViolations_EqualLengthViolation_1() throws Exception {
        HashSet<String> violationCandidates = new HashSet<>();
        violationCandidates.add("A,C,B");
        vd.detectViolations(freqSeqs,violationCandidates,0);
        HashSet<String> results = vd.getViolations().get(0).get("A,C,B").getViolatedSeq();
        assertEquals(results.size(),1);
        HashSet<String> expectedResults = new HashSet<>();
        expectedResults.add("A,B,C");
        assertEquals(results, expectedResults);
    }

    @Test
    public void detectViolations_EqualLengthViolation_2() throws Exception {
        HashSet<String> violationCandidates = new HashSet<>();
        violationCandidates.add("C,A,B");
        vd.detectViolations(freqSeqs,violationCandidates,0);
        assertEquals(vd.getViolations().size(),0);
    }

    @Test
    public void addToViolationsTest() throws Exception {
        vd.addToViolations("A,C,B","A,B,C",0);
        vd.addToViolations("A,C", "A,B,C",0);
        vd.addToViolations("A,C","A,C,D",0);
        vd.addToViolations("B,C","A,B,C",1);
        assertEquals(vd.getViolations().size(),2);
        assertEquals(vd.getViolations().get(0).size(),2);
        assertEquals(vd.getViolations().get(0).get("A,C").getViolatedSeq().size(),2);
        assertEquals(vd.getViolations().get(0).get("A,C").getViolatedSeq().iterator().next(), "A,B,C");
        assertEquals(vd.getViolations().get(1).size(),1);
    }
}