package baseline.csc2;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by yizhouyan on 9/2/18.
 */
public class Episode {
    private int length;
    private ArrayList<String> contents;
    private ArrayList<Integer> gaps;
    private int support;
    private ArrayList<Integer> startPoints;

    public Episode(String content, int index){
        this.length = 1;
        this.support = 1;
        this.contents = new ArrayList<>();
        this.contents.add(content);
        this.gaps = new ArrayList<>();
        this.startPoints = new ArrayList<>();
        this.startPoints.add(index);
    }

    public Episode(Episode pattern, String addContent, int bestGap, ArrayList<EleOcc> listOfOcc){
        this.length = pattern.getLength() + 1;
        this.contents = new ArrayList<String>();
        this.contents.addAll(pattern.getContents());
        this.contents.add(addContent);
        this.gaps = new ArrayList<>();
        this.gaps.addAll(pattern.getGaps());
        this.gaps.add(bestGap);
        this.startPoints = new ArrayList<>();
        for(EleOcc eleOcc: listOfOcc){
            this.startPoints.add(eleOcc.getPrevStart());
        }
        this.support = this.startPoints.size();
    }

    public boolean existOccStartingAtTime(int timestamp, int pos){
        int sumGap = 0;
        for(int i = 0; i< pos; i++){
            sumGap += gaps.get(i);
        }
        for(int time: startPoints){
            if(time + sumGap == timestamp)
                return true;
        }
        return false;
    }

    public ArrayList<Integer> getAllOccurrences(){
        ArrayList<Integer> occ = new ArrayList<>();
        for(int start: startPoints){
            occ.add(start);
            int gapsum = 0;
            for(int gap: gaps){
                gapsum += gap;
                occ.add(start+gapsum);
            }
        }
        return occ;
    }


    public void addOneMoreOccurrence(int index){
        this.support++;
        this.startPoints.add(index);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }

    public ArrayList<Integer> getGaps() {
        return gaps;
    }

    public void setGaps(ArrayList<Integer> gaps) {
        this.gaps = gaps;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public ArrayList<Integer> getStartPoints() {
        return startPoints;
    }

    public ArrayList<Integer> getEndPoints(){
        int sumGaps = 0;
        for(int gap: this.gaps)
            sumGaps += gap;
        ArrayList<Integer> endPoints = new ArrayList<>();
        for(int startPoint: this.startPoints){
            endPoints.add(startPoint+sumGaps);
        }
        return endPoints;
    }

    public void setStartPoints(ArrayList<Integer> startPoints) {
        this.startPoints = startPoints;
    }

}
