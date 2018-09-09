package baseline.csc2;

import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 9/2/18.
 */
public class ComputeBestExtensionsTest {
    private ComputeBestExtensions bestExtensions;

    public void setup_1(){
        String str = "A,B,C,A,B,C,A,B,C";
        String[] inputString = str.split(",");
        BitSet availability = new BitSet(inputString.length);
        availability.set(0, inputString.length, true);
        bestExtensions = new ComputeBestExtensions(inputString, availability, 0);
    }

    @Test
    public void generateOneNodeEpisode_allAvailable() throws Exception {
        setup_1();
        assertEquals(3, bestExtensions.generateOneNodeEpisode().size());
        assertEquals(3, bestExtensions.generateOneNodeEpisode().get("A").getSupport());
        assertEquals(3, bestExtensions.generateOneNodeEpisode().get("B").getSupport());
        assertEquals(3, bestExtensions.generateOneNodeEpisode().get("C").getSupport());
    }

    public void setup_2(){
        String str = "A,B,C,A,B,C,A,B,C";
        String[] inputString = str.split(",");
        BitSet availability = new BitSet(inputString.length);
        availability.set(0, inputString.length, true);
        availability.set(0, false);
        availability.set(3, false);
        availability.set(2, false);
        bestExtensions = new ComputeBestExtensions(inputString, availability, 0);
    }

    @Test
    public void generateOneNodeEpisode() throws Exception {
        setup_2();
        assertEquals(3, bestExtensions.generateOneNodeEpisode().size());
        assertEquals(1, bestExtensions.generateOneNodeEpisode().get("A").getSupport());
        assertEquals(3, bestExtensions.generateOneNodeEpisode().get("B").getSupport());
        assertEquals(2, bestExtensions.generateOneNodeEpisode().get("C").getSupport());
    }

    public void setup_3(){
        String str = "A,B,C,A,B,C,A,B,C,A,B,C,A,B,C,A,B,C";
        String[] inputString = str.split(",");
        BitSet availability = new BitSet(inputString.length);
        availability.set(0, inputString.length, true);
        bestExtensions = new ComputeBestExtensions(inputString, availability, 0);
    }

    @Test
    public void bestExtension() throws Exception {
        setup_3();
        System.out.println(bestExtensions.bestExtensions().size());
        System.out.println(bestExtensions.bestExtensions().get(0).getContents().toString());
        System.out.println(bestExtensions.bestExtensions().get(1).getContents().toString());
        System.out.println(bestExtensions.bestExtensions().get(2).getContents().toString());
    }
}