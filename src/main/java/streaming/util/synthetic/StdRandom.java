package streaming.util.synthetic;

import java.util.Random;

public class StdRandom {
	public static Random random = new Random(System.currentTimeMillis());

	public static boolean bernoulli(double p) {
		return uniform() < p;
	}

	public static double uniform() {
		return random.nextDouble();
	}

	/**
	 * return a random number between minNumber and maxNumber (inclusively)
	 * 
	 * @param minNumber:
	 *            small number
	 * @param maxNumber:
	 *            large number
	 * @return
	 */
	public static int randomNumber(int minNumber, int maxNumber) {
		return minNumber + random.nextInt(maxNumber - minNumber + 1);
	}

	/**
	 * Return an integer with a standard Gaussian distribution.
	 */
	public static int gaussian(int maxNumber, int minNumber, int mean, int deviation) {
		// use the polar form of the Box-Muller transform
		int r = 0;
		do {
			double val = random.nextGaussian() * deviation + mean;
			r = (int) Math.round(val);
		} while (r < minNumber || r > maxNumber);
		return r;
	}

}
