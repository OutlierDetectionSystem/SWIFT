package streaming.base.atomics;

import com.sun.xml.internal.rngom.ast.builder.GrammarSection;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.junit.Before;
import org.junit.Test;
import streaming.util.CombineSingleEvents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 8/11/17.
 */
public class FrequentPatternTest {
    public SingleElement s1 = new SingleElement(1,"A", 0);
    public SingleElement s2 = new SingleElement(1, "A", 1);

    SingleElement s3 = new SingleElement(2, "B", 3);
    SingleElement s4 = new SingleElement(1, "A", 4);
    SingleElement s5 = new SingleElement(1, "A", 5);

    @Test
    public void combineSingleElementsWithSameIdentifier() throws Exception {
        s2.setEndIndex(2);
        ArrayList<Integer> fpIndex = new ArrayList<>();
        fpIndex.add(6);
        fpIndex.add(7);
        fpIndex.add(8);
        FrequentPattern fp1 = new FrequentPattern(3, new ArrayList<String>(Arrays.asList("A,B,C".split(","))),
                fpIndex,
               new ArrayList<BaseElement>(), "A,B,C");
        LinkedList<BaseElement> sList = new LinkedList<>();
        sList.add(s1);
        sList.add(s2);
        sList.add(s3);
        sList.add(s4);
        sList.add(s5);
        sList.add(fp1);
        LinkedList<BaseElement> results = CombineSingleEvents.combineSingleElementsWithSameIdentifier(sList);
        for(BaseElement b: results) {
            System.out.println(b.printElementInfo());
        }
    }

}