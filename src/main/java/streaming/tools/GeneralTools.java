package streaming.tools;

import streaming.base.atomics.BaseElement;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yizhouyan on 7/24/17.
 */
public class GeneralTools {
    public static LinkedList<BaseElement> mergeTwoList(ArrayList<BaseElement> inner, ArrayList<BaseElement> main) {
        LinkedList<BaseElement> finalList = new LinkedList<BaseElement>();
        int i = 0;
        int j = 0;
        while (i < inner.size() || j < main.size()) {
            if (i == inner.size()) {
                finalList.add(main.get(j));
                j++;
            } else if (j == main.size()) {
                finalList.add(inner.get(i));
                i++;
            } else {
                if (main.get(j).getStartIndex() <= inner.get(i).getStartIndex()) {
                    finalList.add(main.get(j));
                    j++;
                } else {
                    finalList.add(inner.get(i));
                    i++;
                }
            }
        }
        return finalList;
    }

}
