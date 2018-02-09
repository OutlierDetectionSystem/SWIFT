package streaming.batchopt.patterngen;

import decay.DecayFunction;
import decay.ExpDecay;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by yizhouyan on 8/9/17.
 */
public class SingleFSDetectionNewBatchTest {
    DecayFunction decayFunction;
    @Before
    public void setup(){
        decayFunction = new ExpDecay(0.1);
    }

    @Test
    public void frequentSequenceMining_TotalExpireTotalNewFS() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),12);
        fsdetect.readBatchMoreElement("A,B,C".split(","),3);
        fsdetect.printCurrentStatus();
        assertEquals(2,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_TotalExpireTotalNewFS_2() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),9);
        fsdetect.readBatchMoreElement("A,B,C".split(","),3);
        fsdetect.printCurrentStatus();
        assertEquals(3,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_TotalPartExpireNewFS() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),9);
        fsdetect.readBatchMoreElement("A,B,C,D".split(","),4);
        fsdetect.printCurrentStatus();
        assertEquals(5,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_TotalPartExpireWithNewMatchFS() throws Exception {
        String str = "A,B,C,D,A,B,C,D,B,C,D,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),14);
        fsdetect.readBatchMoreElement("A,B,C,D".split(","),4);
        fsdetect.printCurrentStatus();
        assertEquals(4,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_checkHoldingList() throws Exception {
        String str = "A,B,C,D,A,B,C,D,B,C,D,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),14);
        fsdetect.readBatchMoreElement("B,C,D".split(","),3);
        fsdetect.printCurrentStatus();
        assertEquals(4,fsdetect.getMainSequence().size());
    }
}