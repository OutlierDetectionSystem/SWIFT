package streaming.util;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by yizhouyan on 7/28/17.
 */
public class ManageHashMap {
    // add new items to
    public static void addAllToHashSet(HashMap<Integer, HashSet<String>> targetMap, HashSet<String> newItems, int index){
        if(targetMap.containsKey(index)){
            targetMap.get(index).addAll(newItems);
        }else{
            targetMap.put(index, newItems);
        }
    }
}
