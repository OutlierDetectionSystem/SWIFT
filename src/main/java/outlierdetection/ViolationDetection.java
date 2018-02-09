package outlierdetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by yizhouyan on 6/16/17.
 */
public class ViolationDetection{
    private HashMap<Integer, HashMap<String, ViolationSequence>> violations;
    public ViolationDetection(){
        this.violations = new HashMap<Integer,HashMap<String, ViolationSequence>>();
    }

    public HashMap<Integer, HashMap<String, ViolationSequence>> detectViolations(SequencesInvertedIndex freqSeqs,
                                                                                 HashSet<String> violationCandidates,
                                                                                 int deviceId){
        if(freqSeqs.getSequencesInList().size() == 0 || violationCandidates.size()== 0)
            return null;
        for(String violationCandidate: violationCandidates){
            ArrayList<String> sequenceWithAllElements = freqSeqs.getSequencesWithAllElements(violationCandidate);
            for(String violatedSeq: sequenceWithAllElements){
                String [] violatedSeqArray = violatedSeq.split(",");
                String [] violatingSeqArray = violationCandidate.split(",");
                if(violatedSeqArray.length > violatingSeqArray.length){
                    if(strArrayContains(violatedSeqArray, violatingSeqArray)){
                        this.addToViolations(violationCandidate, violatedSeq, deviceId);
                    }
                }else if(violatedSeqArray.length == violatingSeqArray.length){
                    if(disorderedSequence(violatedSeq, violationCandidate)) {
                        this.addToViolations(violationCandidate, violatedSeq, deviceId);
                    }
                }
            }
        }
        return violations;
    }

    public void addToViolations(String curSeq, String finalSuperSequence, int deviceId){
        // add to violation
        HashMap<String, ViolationSequence> violationsCurDevice;
        if(violations.containsKey(deviceId)){
            violationsCurDevice = violations.get(deviceId);
        }else{
            violations.put(deviceId, new HashMap<String, ViolationSequence>());
            violationsCurDevice = violations.get(deviceId);
        }

        if(violationsCurDevice.containsKey(curSeq)){
            violationsCurDevice.get(curSeq).addToViolations(finalSuperSequence);
        }else {
            ViolationSequence newViolationSeq = new ViolationSequence(curSeq);
            newViolationSeq.addToViolations(finalSuperSequence);
            violationsCurDevice.put(curSeq, newViolationSeq);
        }
    }

    public HashMap<Integer,HashMap<String, ViolationSequence>> getViolations() {
        return violations;
    }

    public void setViolations(HashMap<Integer,HashMap<String, ViolationSequence>> violations) {
        this.violations = violations;
    }

    public boolean disorderedSequence(String originalSeq, String targetSeq){
        if(originalSeq.equals(targetSeq))
            return false;
        HashMap<String, Integer> mapForOriginalSeq = new HashMap<String, Integer>();
        String [] originalSplits = originalSeq.split(",");
        for(String str: originalSplits){
            if(mapForOriginalSeq.containsKey(str))
                mapForOriginalSeq.put(str, mapForOriginalSeq.get(str)+1);
            else
                mapForOriginalSeq.put(str, 1);
        }
        HashMap<String, Integer> mapForTargetSeq = new HashMap<String, Integer>();
        String [] targetSplits = targetSeq.split(",");
        for(String str: targetSplits){
            if (mapForTargetSeq.containsKey(str))
                mapForTargetSeq.put(str, mapForTargetSeq.get(str) + 1);
            else
                mapForTargetSeq.put(str, 1);
        }
        if(mapForOriginalSeq.size() != mapForTargetSeq.size())
            return false;
        for(String str: mapForOriginalSeq.keySet()){
            if(!mapForTargetSeq.containsKey(str))
                return false;
            if(mapForOriginalSeq.get(str) != mapForTargetSeq.get(str)){
                return false;
            }
        }
        return true;
    }

    /**
     * check sub array
     *
     * @param strList1
     * @param strList2
     * @return
     */
    public boolean strArrayContains(String [] strList1, String [] strList2) {
        boolean isContained = false;

        for (int i = 0; i < strList1.length - strList2.length + 1; i++) {
            int k = i;
            int j = 0;
            while (k < strList1.length && j < strList2.length) {
                if (strList1[k].equals(strList2[j])) {
                    k++;
                    j++;
                } else {
                    k++;
                }
            }
            if (j == strList2.length) {
                isContained = true;
                break;
            }
        }
        return isContained;
    }

    /**
     * Order by devices
     * @param typicalPatterns
     * @param targetDirectory
     * @param deviceIdMap
     * @param metaDataMapping
     * @param globalIndex
     */
    public void outputViolationResultsToFile(HashSet<String> typicalPatterns, String targetDirectory,
                                             HashMap<Integer, String> deviceIdMap,
                                             HashMap<String, String> metaDataMapping, int globalIndex){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(targetDirectory, "ViolationReport-"+
                    globalIndex + ".txt")));

            for (Map.Entry<Integer, HashMap<String, ViolationSequence>> violationOneDevice: violations.entrySet()){
                // check if this device contains any available violations
                HashMap<String, ViolationSequence> validViolations = new HashMap<String, ViolationSequence>();
                for(Map.Entry<String, ViolationSequence> singleViolation: violationOneDevice.getValue().entrySet()){
                    if(!typicalPatterns.contains(singleViolation.getKey())) {
                        validViolations.put(singleViolation.getKey(), singleViolation.getValue());
                    }
                }
                if(validViolations.size() > 0) {
                    bw.write("Device id: " + deviceIdMap.get(violationOneDevice.getKey()));
                    bw.newLine();
                    for (Map.Entry<String, ViolationSequence> singleViolation : validViolations.entrySet()) {
                        for (String violatedSeq : singleViolation.getValue().getViolatedSeq()) {
                            bw.write("Sequence: " + singleViolation.getKey());
                            bw.newLine();
                            bw.write("Violated Sequence: " + violatedSeq);
                            bw.newLine();
                            String strInMetaViolating = "";
                            String[] subs = singleViolation.getKey().split(",");
                            for (String substring : subs) {
                                strInMetaViolating += metaDataMapping.get(substring.trim()) + "\t";
                            }
                            bw.write("Sequence: " + strInMetaViolating);
                            bw.newLine();
                            String strInMetaViolated = "";
                            String[] subsViolated = violatedSeq.split(",");
                            for (String substring : subsViolated) {
                                strInMetaViolated += metaDataMapping.get(substring.trim()) + "\t";
                            }
                            bw.write("Violated Sequence: " + strInMetaViolated);
                            bw.newLine();
                        }

                    }
                    bw.newLine();
                    bw.newLine();
                }
            }

            bw.close();
//            this.violations.clear();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Order by Violations, if the violation appears in more than XXX device, we do not report it
     * @param typicalPatterns
     * @param targetDirectory
     * @param deviceIdMap
     * @param metaDataMapping
     * @param globalIndex
     */
    public void outputFilteredViolationResultsToFile(HashSet<String> typicalPatterns, String targetDirectory,
                                                     HashMap<Integer, String> deviceIdMap,
                                                     HashMap<String, String> metaDataMapping, int globalIndex){
        // first filter violations that appear in more than one device or length < 2
        HashMap<String, HashSet<Integer>> sequenceFrequency = new HashMap<String, HashSet<Integer>>();
        for (Map.Entry<Integer, HashMap<String, ViolationSequence>> violationOneDevice: violations.entrySet()){
            for(Map.Entry<String, ViolationSequence> singleViolation: violationOneDevice.getValue().entrySet()){
                if(!typicalPatterns.contains(singleViolation.getKey()) &&
                        singleViolation.getKey().split(",").length > 1) {
                    for (String violatedSeq : singleViolation.getValue().getViolatedSeq()) {
                        String curViolation = singleViolation.getKey() + "|" + violatedSeq;
                        addToSequenceFrequency(sequenceFrequency, curViolation, violationOneDevice.getKey());
                    }
                }
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(targetDirectory, "ViolationReportFiltered-"+
                    globalIndex + ".txt")));
            for (Map.Entry<String, HashSet<Integer>> curViolation: sequenceFrequency.entrySet()){
                if(curViolation.getValue().size() < 2){
                    bw.write("Device ids: ");
                    for(Integer id: curViolation.getValue()){
                        bw.write(deviceIdMap.get(id) + "\t");
                    }
                    bw.newLine();
                    String [] violationSplits = curViolation.getKey().split("\\|");
                    bw.write("Sequence: " + violationSplits[0]);
                    bw.newLine();
                    bw.write("Violated Sequence: " + violationSplits[1]);
                    bw.newLine();
                    String strInMetaViolating = "";
                    String[] subs = violationSplits[0].split(",");
                    for (String substring : subs) {
                        strInMetaViolating += metaDataMapping.get(substring.trim()) + "\t";
                    }
                    bw.write("Sequence: " + strInMetaViolating);
                    bw.newLine();
                    String strInMetaViolated = "";
                    String[] subsViolated = violationSplits[1].split(",");
                    for (String substring : subsViolated) {
                        strInMetaViolated += metaDataMapping.get(substring.trim()) + "\t";
                    }
                    bw.write("Violated Sequence: " + strInMetaViolated);
                    bw.newLine();
                    bw.newLine();
                    bw.newLine();
                }
            }

            bw.close();
//            this.violations.clear();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void addToSequenceFrequency(HashMap<String, HashSet<Integer>> sequenceFrequency, String violation, int deviceId){
        if(sequenceFrequency.containsKey(violation))
            sequenceFrequency.get(violation).add(deviceId);
        else{
            HashSet<Integer> deviceIdSet = new HashSet<>();
            deviceIdSet.add(deviceId);
            sequenceFrequency.put(violation, deviceIdSet);
        }
    }
}
