package baseline.gokrimp;

/**
 * Created by yizhouyan on 7/30/17.
 */
public class SingleEvent implements Comparable{
    private String event;
    private int support;
    private int firstIndex;

    public SingleEvent(String event, int support, int firstIndex){
        this.event = event;
        this.support = support;
        this.firstIndex = firstIndex;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public void addOneToSupport(){
        this.support += 1;
    }

    public void removeFromSupport(int reduceSupport){
        this.support -= reduceSupport;
    }
    @Override
    public int compareTo(Object o) {
        if(this.support > ((SingleEvent) o).support)
            return -1;
        else if(this.support < ((SingleEvent) o).support)
            return 1;
        else{
            if(this.firstIndex > ((SingleEvent) o).firstIndex)
                return 1;
            else if(this.firstIndex < ((SingleEvent) o).firstIndex)
                return -1;
            return 0;
        }

    }
}
