package outlierdetection;

import java.util.HashSet;

/**
 * Created by yizhouyan on 6/28/17.
 */
public class ViolationSequence {
    private String violationSeq;
    private HashSet<String> violatedSeq;

    public ViolationSequence(String violationSeq){
        this.violationSeq = violationSeq;
        this.violatedSeq = new HashSet<String>();
    }

    public void addToViolations(HashSet<String> finalSuperSequences){
        // add to violation
        this.violatedSeq.addAll(finalSuperSequences);
    }

    public void addToViolations(String finalSuperSequence){
        // add to violation
       this.violatedSeq.add(finalSuperSequence);
    }

    public String printViolation(){
        // output format: violation seq [occurrences-1][-2][-3] | violated seq1 | violated seq2
        String str = violationSeq ;
        for(String seq: violatedSeq){
            str += "|" + seq;
        }
        return str;
    }

    public String getViolationSeq() {
        return violationSeq;
    }

    public void setViolationSeq(String violationSeq) {
        this.violationSeq = violationSeq;
    }

    public HashSet<String> getViolatedSeq() {
        return violatedSeq;
    }

    public void setViolatedSeq(HashSet<String> violatedSeq) {
        this.violatedSeq = violatedSeq;
    }
}
