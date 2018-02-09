package streaming.util;

import streaming.base.CountSummary;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.SingleElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by yizhouyan on 8/11/17.
 */
public class CombineSingleEvents {
    public static LinkedList<BaseElement> combineSingleElementsWithSameIdentifier(LinkedList<BaseElement> mergeRes){
//		System.out.println("Start... ");
        LinkedList<BaseElement> removeEles = new LinkedList<>();
        int index = 0;
        while(index < mergeRes.size()){
            BaseElement baseElement = mergeRes.get(index);
            if(baseElement.getClass().toString().endsWith("SingleElement")){
                int startIdentifier = baseElement.getIdentifier();
                index++;
                while(index < mergeRes.size()){
                    BaseElement nextElement = mergeRes.get(index);
                    if(nextElement.getClass().toString().endsWith("SingleElement") && nextElement.getIdentifier() == startIdentifier){
                        ((SingleElement) baseElement).setEndIndex(nextElement.getEndIndex());
                        removeEles.add(nextElement);
                        index++;
                    }else
                        break;
                }
            }else{
                index++;
            }
        }
        mergeRes.removeAll(removeEles);
        return mergeRes;
    }

    public static void combineSingleElementsWithSameIdentifier(LinkedList<BaseElement> mainSequence,
                                                                                  HashMap<Integer, CountSummary> identifierToCount,
                                                                                  HashSet<Integer> MoreThanOne){
        LinkedList<BaseElement> removeEles = new LinkedList<>();
        int index = 0;
        while(index < mainSequence.size()){
            BaseElement baseElement = mainSequence.get(index);
            if(baseElement.getClass().toString().endsWith("SingleElement")){
                int startIdentifier = baseElement.getIdentifier();
                index++;
                while(index < mainSequence.size()){
                    BaseElement nextElement = mainSequence.get(index);
                    if(nextElement.getClass().toString().endsWith("SingleElement") && nextElement.getIdentifier() == startIdentifier){
                        ((SingleElement) baseElement).setEndIndex(nextElement.getEndIndex());
                        removeEles.add(nextElement);
                        index++;
                    }else
                        break;
                }
            }else{
                index++;
            }
        }
        for(BaseElement removeEle: removeEles){
            mainSequence.remove(removeEle);
            identifierToCount.get(removeEle.getIdentifier()).removeElementFromList(removeEle);
            if(identifierToCount.get(removeEle.getIdentifier()).getCount() < 2)
                MoreThanOne.remove(removeEle.getIdentifier());
        }
    }
}
