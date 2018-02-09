package gokrimp;

import baseline.gokrimp.SingleEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 7/30/17.
 */
public class SingleEventTest {
    @Test
    public void compareToTest() throws Exception {
        SingleEvent s1 = new SingleEvent("A",2,3);
        SingleEvent s2 = new SingleEvent("B",3,2);
        ArrayList<SingleEvent> sList = new ArrayList<SingleEvent>();
        sList.add(s1);
        sList.add(s2);
        Collections.sort(sList);
        assertEquals(sList.get(0), s2);
    }

}