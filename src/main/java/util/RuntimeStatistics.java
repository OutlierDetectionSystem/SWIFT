package util;

/**
 * Created by yizhouyan on 7/30/17.
 */
public class RuntimeStatistics {
    public long sumTime = 0;
    public long sumMDL = 0;
    public int totalCount = 0;

    public double getAverageMDL(){
        return sumMDL * 1.0/ totalCount;
    }
    public double getAverageTimeCost(){
        return sumTime * 1.0 /1000/totalCount;
    }
}
