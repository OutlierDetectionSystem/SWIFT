package streaming.base;

import streaming.base.atomics.BaseElement;

import java.util.ArrayList;

public class MergeCandidate {
	private ArrayList<BaseElement> frontPair;
	private ArrayList<BaseElement> backPair;
	private int benefit;
	private int lengthGain;

	public MergeCandidate(ArrayList<BaseElement> frontPair, ArrayList<BaseElement> backPair, int benefit,
                          int lengthGain) {
		this.frontPair = frontPair;
		this.backPair = backPair;
		this.benefit = benefit;
		this.lengthGain = lengthGain;
	}
	
	public ArrayList<BaseElement> getFrontPair() {
		return frontPair;
	}

	public void setFrontPair(ArrayList<BaseElement> frontPair) {
		this.frontPair = frontPair;
	}

	public ArrayList<BaseElement> getBackPair() {
		return backPair;
	}

	public void setBackPair(ArrayList<BaseElement> backPair) {
		this.backPair = backPair;
	}

	public int getBenefit() {
		return benefit;
	}

	public void setBenefit(int benefit) {
		this.benefit = benefit;
	}

	public int getLengthGain() {
		return lengthGain;
	}

	public void setLengthGain(int lengthGain) {
		this.lengthGain = lengthGain;
	}
	
}
