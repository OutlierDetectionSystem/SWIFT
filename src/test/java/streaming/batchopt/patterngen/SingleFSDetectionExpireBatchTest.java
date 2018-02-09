package streaming.batchopt.patterngen;

import decay.DecayFunction;
import decay.ExpDecay;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 8/9/17.
 */
public class SingleFSDetectionExpireBatchTest {
    DecayFunction decayFunction;
    @Before
    public void setup(){
        decayFunction = new ExpDecay(0.1);
    }

    @Test
    public void frequentSequenceMining_TotalExpireFS() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","), 1);
        fsdetect.expireBatchSizeEvents(3);
        fsdetect.printCurrentStatus();
        assertEquals(2,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_PartExpireFS() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.expireBatchSizeEvents(2);
        fsdetect.printCurrentStatus();
        assertEquals(3,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_TotalPartExpireFS() throws Exception {
        String str = "A,B,C,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.expireBatchSizeEvents(4);
        fsdetect.printCurrentStatus();
        assertEquals(3,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_TotalPartExpireWithNewMatchFS() throws Exception {
        String str = "A,B,C,D,A,B,C,D,B,C,D,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.expireBatchSizeEvents(5);
        fsdetect.printCurrentStatus();
        assertEquals(3,fsdetect.getMainSequence().size());
    }

    @Test
    public void frequentSequenceMining_checkHoldingList() throws Exception {
        String str = "A,B,C,D,A,B,C,D,B,C,D,B,C,D";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),1);
        fsdetect.expireBatchSizeEvents(3);
        fsdetect.printCurrentStatus();
        assertEquals(4,fsdetect.getMainSequence().size());
    }
}