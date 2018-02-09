package decay;

/**
 * Created by yizhouyan on 7/26/17.
 */
public class DecayFunction {
    protected double decayRate;
    public DecayFunction(double decayRate){
        this.decayRate = decayRate;
    }

    public double ComputeWeight(int windowId) {
        return 0;
    }
}
