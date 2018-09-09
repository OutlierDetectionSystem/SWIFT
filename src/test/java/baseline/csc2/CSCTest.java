package baseline.csc2;

import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 9/2/18.
 */
public class CSCTest {
    @Test
    public void OverlapMatrixTest() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C,A,B,C,A,B,C,A,B,C";
        String[] inputString = str.split(",");
        BitSet availability = new BitSet(inputString.length);
        availability.set(0, inputString.length, true);
        ComputeBestExtensions bestExtensions = new ComputeBestExtensions(inputString, availability, 0);
        ArrayList<Episode> episodeCandidates = bestExtensions.bestExtensions();
        FindOverlapMatrix findOverlapMatrix = new FindOverlapMatrix(inputString, availability);
        int [][] overlapMatrix = findOverlapMatrix.findOverlapMatrix(episodeCandidates);
        for(int i = 0; i< overlapMatrix.length; i++)
            for(int j = 0; j< overlapMatrix.length; j++)
                System.out.println(overlapMatrix[i][j]);
    }

    @Test
    public void CSCMainTest_1() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C,A,B,C,A,B,C,A,B,C";
        String[] inputString = str.split(",");
        CSC csc = new CSC(inputString, 0, 1000);
        csc.CSCMain();
        assertEquals(1,csc.getFrequentSequences().size());
        assertEquals(7,csc.getMDLScore());
    }

    @Test
    public void CSCMainTest_2() throws Exception {
        String str = "D,H,A,B,C,D,A,B,C,D,H,A,B,C";
        String[] inputString = str.split(",");
        CSC csc = new CSC(inputString, 0, 1000);
        csc.CSCMain();
        assertEquals(0,csc.getFrequentSequences().size());
//        assertEquals(7,csc.getMDLScore());
    }

    @Test
    public void CSCMainTest_3() throws Exception {
        String str = "D,H,A,B,C,D,A,B,C,D,A,B,C,D,A,B,C,D,A,B,C,D,A,B,C,D,H,A,B,C";
        String[] inputString = str.split(",");
        CSC csc = new CSC(inputString, 0, 1000);
        csc.CSCMain();
        System.out.println(csc.getFrequentSequences().get(0).getContents().toString());
        assertEquals(1,csc.getFrequentSequences().size());
        assertEquals(13,csc.getMDLScore());
    }

    @Test
    public void CSCMainTest_5() throws Exception {
        String str = "A,A,A,A,A,A,A,A,A,A,A,A,A,A,A,A";
        String[] inputString = str.split(",");
        CSC csc = new CSC(inputString, 0, 1000);
        csc.CSCMain();
        assertEquals(0,csc.getFrequentSequences().size());
//        assertEquals(7,csc.getMDLScore());
    }

}