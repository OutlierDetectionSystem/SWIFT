package baseline.csc2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yizhouyan on 9/2/18.
 */
public class OccListForDelta {
    private HashMap<Integer, ArrayList<EleOcc>> occListForDelta;
    private int itemGap;

    public OccListForDelta(Episode curPattern, Episode curElement, int itemGap){
        this.occListForDelta = new HashMap<>();
        this.itemGap = itemGap;
        for(int i = 1; i <= itemGap + 1; i++){
            occListForDelta.put(i, new ArrayList<EleOcc>());
        }
        findLists(curPattern, curElement);
    }

    public void findLists(Episode curPattern, Episode curElement){
        ArrayList<Integer> startsForCurPattern = curPattern.getStartPoints();
        ArrayList<Integer> endsForCurPattern = curPattern.getEndPoints();
        ArrayList<Integer> startsForCurElement = curElement.getStartPoints();
        for(int i = 0; i< startsForCurPattern.size(); i++){
            int curStart = startsForCurPattern.get(i);
            int curEnd = endsForCurPattern.get(i);
            // find first occ of curElement after curEnd
            int startEleIndex = binarySearch(curEnd, startsForCurElement);
            while(startEleIndex < startsForCurElement.size() && (startsForCurElement.get(startEleIndex)-curEnd <= itemGap+1)){
                int delta = startsForCurElement.get(startEleIndex) - curEnd;
                this.occListForDelta.get(delta).add(new EleOcc(curStart, startsForCurElement.get(startEleIndex)));
                startEleIndex++;
            } // end while
        } // end for
    }

    public int gapBest(){
        int bestGap = 1;
        for(int i = 2; i <= itemGap + 1; i++){
            if(occListForDelta.get(i).size() > occListForDelta.get(bestGap).size()){
                bestGap = i;
            }
        }
        return bestGap;
    }

    public ArrayList<EleOcc> getEleOccListForGap(int gap){
        return occListForDelta.get(gap);
    }

    public static int binarySearch(int target, ArrayList<Integer> startsForCurElement)
    {
        int low = 0;
        int high = startsForCurElement.size(); // numElems is the size of the array i.e arr.size()
        while (low != high) {
            int mid = low + (high-low)/2; // Or a fancy way to avoid int overflow
            if (startsForCurElement.get(mid) <= target) {
                low = mid + 1;
            }
            else {
                high = mid;
            }
        }
        return low;
    }
}

class EleOcc{
    private int prevStart;
    private int eleStart;
    public EleOcc(int prevStart, int eleStart){
        this.prevStart = prevStart;
        this.eleStart = eleStart;
    }
    public int getPrevStart(){
        return prevStart;
    }
}