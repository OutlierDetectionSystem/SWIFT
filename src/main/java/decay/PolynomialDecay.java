package decay;

/**
 * Created by yizhouyan on 7/26/17.
 */
public class PolynomialDecay extends DecayFunction{
    public PolynomialDecay(double decayRate){
        super(decayRate);
    }

    @Override
    public double ComputeWeight(int windowId) {
        return Math.pow(windowId+1, -decayRate);
    }
}
