package streaming.batchopt.patterngen;

import decay.DecayFunction;
import decay.ExpDecay;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by yizhouyan on 8/9/17.
 */
public class SingleFSDetectionMergeNewBatchTest {
    DecayFunction decayFunction;
    @Before
    public void setup(){
        decayFunction = new ExpDecay(0.1);
    }

    @Test
    public void frequentSequenceMining_TotalExpireTotalNewFS() throws Exception {
        String str = "A,B,C,F,A,B,E,A,B,C,A,B,C";
        SingleFSDetection fsdetect = new SingleFSDetection(10000, 0, 10, decayFunction);
        fsdetect.initialization(str.split(","),13);
        fsdetect.readBatchMoreElement("F,A,B,E".split(","),3);
        fsdetect.printCurrentStatus();
        assertEquals(4,fsdetect.getMainSequence().size());
    }
}