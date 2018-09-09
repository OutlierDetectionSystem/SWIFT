package baseline.csc2;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by yizhouyan on 9/2/18.
 */
public class ComputeBestExtensions {
    private String[] inputString;
    private int itemGap;
    private BitSet availability;

    public ComputeBestExtensions(String [] inputString, BitSet availability, int itemGap){
        this.inputString = inputString;
        this.availability = availability;
        this.itemGap = itemGap;
    }

    public void addOccToEpisodeMap(HashMap<String, Episode> oneNodeEpisodes, String content, int index){
        if(oneNodeEpisodes.containsKey(content)){
            oneNodeEpisodes.get(content).addOneMoreOccurrence(index);
        }else{
            Episode newEpisode = new Episode(content, index);
            oneNodeEpisodes.put(content, newEpisode);
        }
    }

    public HashMap<String,Episode> generateOneNodeEpisode(){
        HashMap<String, Episode> oneNodeEpisodes = new HashMap<String, Episode>();
        if(availability.cardinality() > 0){
            int pos = 0;
            while(pos < availability.length()) {
                int nextSet = availability.nextSetBit(pos);
                addOccToEpisodeMap(oneNodeEpisodes, inputString[nextSet], nextSet);
                pos = nextSet+1;
            }
        }
        return oneNodeEpisodes;
    }

    public ArrayList<Episode> bestExtensions(){
        ArrayList<Episode> candidateEpisodes = new ArrayList<>();
        HashMap<String, Episode> oneNodeEpisodes = generateOneNodeEpisode();
        for(Map.Entry<String, Episode> curOneNodeEpisode: oneNodeEpisodes.entrySet()){
            Episode curPattern = curOneNodeEpisode.getValue();
            Episode newPattern = curPattern;
            while((curPattern = extensions(curPattern, oneNodeEpisodes))!=null){
                newPattern = curPattern;
            } // end while
            if(computeScore(newPattern) > 0){
                candidateEpisodes.add(newPattern);
            }
        }
        return candidateEpisodes;
    }

    public int computeScore(Episode pattern){
        return pattern.getSupport() * pattern.getLength() - (2 * pattern.getLength() + pattern.getSupport() + 1);
    }

    public HashMap<String, Episode> excludeEpisodesFromPattern(Episode curPattern, HashMap<String, Episode> oneNodeEpisodes){
        HashSet<String> usedEvent = new HashSet<>(curPattern.getContents());
        HashMap<String, Episode> newEpisodes = new HashMap<>();
        for(Map.Entry<String, Episode> curOneNodeEpisode: oneNodeEpisodes.entrySet()){
            if(!usedEvent.contains(curOneNodeEpisode.getKey())){
                newEpisodes.put(curOneNodeEpisode.getKey(), curOneNodeEpisode.getValue());
            }
        }
        return newEpisodes;
    }

    /**
     *
     * @param curPattern Episode with its occurrence list
     * @param oneNodeEpisodes Set of frequent one node episodes with their occurrence lists
     * @return Episode extension with highest frequency and minimum inter-event gap
     */
    public Episode extensions(Episode curPattern, HashMap<String, Episode> oneNodeEpisodes){
        int maxFreqency = (2*(curPattern.getLength()+1)+1)/curPattern.getLength();
        int minGap = this.itemGap + 2;
        Episode maxPattern = null;
        HashMap<String, Episode> newOneNodeEpisodes = excludeEpisodesFromPattern(curPattern, oneNodeEpisodes);
        for(Map.Entry<String, Episode> curOneNodeEpisode: newOneNodeEpisodes.entrySet()){
            OccListForDelta occListForDelta = new OccListForDelta(curPattern, curOneNodeEpisode.getValue(), itemGap);
            int gapBest = occListForDelta.gapBest();
            ArrayList<EleOcc> listForBestGap = occListForDelta.getEleOccListForGap(gapBest);
            if(listForBestGap.size() >= maxFreqency){
                if(listForBestGap.size() > maxFreqency || gapBest < minGap){
                    // form new episode using listForBestGap
                    Episode beta = new Episode(curPattern, curOneNodeEpisode.getKey(), gapBest, listForBestGap);
                    maxPattern = beta;
                    maxFreqency = beta.getSupport();
                    minGap = gapBest;
                }
            }
        }
        return maxPattern;
    }
}
