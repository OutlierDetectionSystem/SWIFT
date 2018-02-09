package decay;

/**
 * Created by yizhouyan on 7/24/17.
 */
public class ExpDecay extends DecayFunction {
    public ExpDecay(double decayRate){
        super(decayRate);
    }

    /**
     * Compute Weight
     */
    @Override
    public double ComputeWeight(int windowId) {
        return Math.exp(-decayRate * windowId);
    }
}
