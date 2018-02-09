package gokrimp;

import baseline.gokrimp.GoKrimp;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 7/31/17.
 */
public class GoKrimpTestBasic {
    @Test
    public void frequentSequenceMining_Basic_1() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        GoKrimp obj = new GoKrimp(str.split(","), 3, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B,C", 3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_2() throws Exception {
        String str = "A,B,C,D,B,C,A,B,C,D";
        GoKrimp obj = new GoKrimp(str.split(","), 3, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("B,C", 3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_3() throws Exception {
        String str = "A,B,C,E,D,A,B,F,C,D,A,B,C";
        GoKrimp obj = new GoKrimp(str.split(","), 1, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B,C", 3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_4() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C,D,A,B,C,D";
        GoKrimp obj = new GoKrimp(str.split(","), 1, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B,C", 4);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_5() throws Exception {
        String str = "A,B,C,A,B,C,A,B,D,A,B,C";
        GoKrimp obj = new GoKrimp(str.split(","), 3, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B,C", 3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_6() throws Exception {
        String str = "A,B,A,B,C,D,A,B,C,D";
        GoKrimp obj = new GoKrimp(str.split(","), 5, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B", 3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_7() throws Exception {
        String str = "A,B,A,B,A,B,A,B";
        GoKrimp obj = new GoKrimp(str.split(","), 0, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B", 4);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_8() throws Exception {
        String str = "A,B,C,D,A,B,C,D,A,B,C";
        GoKrimp obj = new GoKrimp(str.split(","), 0, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B,C", 3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_9() throws Exception {
        String str = "D,H,A,B,C,E,D,A,B,C,E,D,H,A,B,C,E";
        GoKrimp obj = new GoKrimp(str.split(","), 0, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B,C,E", 3);
        assertTrue(expectedRows.equals(results));
    }

    @Test
    public void frequentSequenceMining_Basic_10() throws Exception {
        String str = "A,B,C,D,E,F,G,H,A,B,C,D,E,F,Y,Z,G,H,Y,Z,Y,Z,A,B,C,D,E,F";
        GoKrimp obj = new GoKrimp(str.split(","), 0, 10);
        obj.GoKrimpAlgorithm();
        HashMap<String,Integer> results = obj.getFrequentSequences();
        System.out.println(obj.getMDLScore());
        HashMap<String, Integer> expectedRows = new HashMap<String, Integer>();
        expectedRows.put("A,B,C,D,E,F", 3);
        expectedRows.put("Y,Z", 3);
        assertTrue(expectedRows.equals(results));
    }

}