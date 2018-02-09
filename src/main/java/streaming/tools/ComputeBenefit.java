package streaming.tools;

import streaming.base.CountSummary;
import streaming.base.MergeCandidate;
import streaming.base.atomics.BaseElement;
import streaming.base.atomics.FrequentPattern;
import streaming.base.atomics.SingleElement;

import java.util.ArrayList;
import java.util.LinkedList;

public class ComputeBenefit {
	public static MergeCandidate GenerateMergePairForOneIdentifier(CountSummary elementCS, int itemGap, int seqGap){
		LinkedList<BaseElement> curElements = elementCS.getRefToElement();
		if(curElements.size() < 4 || curElements.get(0).getClass().toString().endsWith("SingleElement"))
			return null;

		ArrayList<BaseElement> pairFront = new ArrayList<BaseElement>();
		ArrayList<BaseElement> pairBack = new ArrayList<BaseElement>();
		int pos = 0;
		int gapPunishment = 0;
		// start from the first element, check how many pairs can be formed
		while (pos + 1 < curElements.size()) {
			if ((curElements.get(pos+1).getBackEleItemIndex()
					- curElements.get(pos).getFrontEleItemIndex() <= 1 + itemGap)
					&& (curElements.get(pos+1).getBackEleSeqIndex()
					- curElements.get(pos).getFrontEleSeqIndex() <= 1 + seqGap)) {
				pairFront.add(curElements.get(pos));
				pairBack.add(curElements.get(pos+1));
				gapPunishment += curElements.get(pos+1).getBackEleItemIndex()- curElements.get(pos).getFrontEleItemIndex()-1;
				pos += 2;
			}else
				pos += 1;
		} // end while
		// compute benefits(how many single points can be reduced) and
		// length benefit(length reduction)
		if (pairFront.size() > 1) {
			return ComputeBenefit.ComputeBenefitsForSinglePattern(pairFront, pairBack, elementCS, gapPunishment);
		} else
			return null;
	}
	/**
	 * Check if can merge, if can, compute benefit This can form pair (Front,
	 * Back)
	 *
	 * @param csFront
	 * @param csBack
	 */
	public static MergeCandidate GenerateMergePairForTwoIdentifiers(CountSummary csFront, CountSummary csBack,
																	int itemGap, int seqGap) {
		if(csFront.equals(csBack)){
			return ComputeBenefit.GenerateMergePairForOneIdentifier(csFront, itemGap, seqGap);
		}
		LinkedList<BaseElement> frontElements = csFront.getRefToElement();
		LinkedList<BaseElement> backElements = csBack.getRefToElement();
		int posFront = 0;
		int posBack = 0;
		ArrayList<BaseElement> pairFront = new ArrayList<BaseElement>();
		ArrayList<BaseElement> pairBack = new ArrayList<BaseElement>();
		int gapPunishment = 0;
		// start from front element, check how many pairs can be formed
		while (posFront < frontElements.size() && posBack < backElements.size()) {
			if (frontElements.get(posFront).getStartIndex() < backElements.get(posBack).getStartIndex()) {
				// find the nearest one to the back element
				int startEle = posFront;
				for (int i = posFront + 1; i < frontElements.size(); i++) {
					if (frontElements.get(i).getStartIndex() < backElements.get(posBack).getStartIndex()) {
						startEle = i;
					} else
						break;
				}
				if ((backElements.get(posBack).getBackEleItemIndex()
						- frontElements.get(startEle).getFrontEleItemIndex() <= 1 + itemGap)
						&& (backElements.get(posBack).getBackEleSeqIndex()
						- frontElements.get(startEle).getFrontEleSeqIndex() <= 1 + seqGap)) {
					pairFront.add(frontElements.get(startEle));
					pairBack.add(backElements.get(posBack));
					gapPunishment += backElements.get(posBack).getBackEleItemIndex()-frontElements.get(startEle).getFrontEleItemIndex()-1;
				}
				posFront = startEle + 1;
				posBack++;
			} else {
				// front > back
				posBack++;
			}
		} // end while
		// compute benefits(how many single points can be reduced) and
		// length benefit(length reduction)
		if (pairFront.size() > 1) {
			return ComputeBenefit.ComputeBenefitsForTwoElements(pairFront, pairBack, csFront, csBack, gapPunishment);
		} else
			return null;
	}

	public static MergeCandidate ComputeBenefitsForTwoElements(ArrayList<BaseElement> pairFront,
                                                               ArrayList<BaseElement> pairBack, CountSummary csFront,
                                                               CountSummary csBack, int gapPunishment) {
		if (pairFront.get(0).getClass().toString().endsWith("SingleElement")) {
			if (pairBack.get(0).getClass().toString().endsWith("SingleElement")) {
				return ComputeBenefit.ComputeBenefitsForSingles(pairFront, pairBack, gapPunishment);
			} else if (pairBack.get(0).getClass().toString().endsWith("FrequentPattern")) {
				return ComputeBenefit.ComputeBenefitsForSinglePatternPair(pairFront, pairBack, csBack, gapPunishment);
			} else
				return null;
		} else if (pairFront.get(0).getClass().toString().endsWith("FrequentPattern")) {
			if (pairBack.get(0).getClass().toString().endsWith("SingleElement")) {
				return ComputeBenefit.ComputeBenefitsForPatternSinglePair(pairFront, pairBack, csFront, gapPunishment);
			} else if (pairBack.get(0).getClass().toString().endsWith("FrequentPattern")) {
				return ComputeBenefit.ComputeBenefitsForPatterns(pairFront, pairBack, csFront, csBack, gapPunishment);
			} else
				return null;
		} else
			return null;
	}


	public static MergeCandidate ComputeBenefitsForSingles(ArrayList<BaseElement> pairFront,
                                                           ArrayList<BaseElement> pairBack, int gapPunishment) {
		// two lists of elements are singles || doubles
//		int benefit = 0;
		int dicSizeGain = -1;
		int lengthGain = 0;
		lengthGain += pairFront.size() * 1;
		if(lengthGain + dicSizeGain - gapPunishment > 0)
			return new MergeCandidate(pairFront, pairBack, dicSizeGain, lengthGain);
		else
			return null;
	}

	public static MergeCandidate ComputeBenefitsForPatterns(ArrayList<BaseElement> pairFront,
                                                            ArrayList<BaseElement> pairBack, CountSummary csFront,
                                                            CountSummary csBack, int gapPunishment) {
		// two lists of elements are patterns
//		int benefit = 0;
		int dicSizeGain = 1;
		// if part of AB, CD can form ABCD, then extra AB/CD still need to form pattern, dic size +1
		if(csFront.getCount() - pairFront.size() > 1)
			dicSizeGain -= 1;
		if(csBack.getCount() - pairBack.size() > 1)
			dicSizeGain -= 1;

		int lengthGain = pairFront.size();
		// if only one left, then this pattern has to seperate, therefore only check if 1 left
		if (csFront.getCount() - pairFront.size() == 1) {
			lengthGain -= (((FrequentPattern) pairFront.get(0)).getItems().size()-1);
//			return null;
		}
		if (csBack.getCount() - pairBack.size() == 1) {
			lengthGain -= (((FrequentPattern) pairBack.get(0)).getItems().size()-1);
//			return null;
		}
		if(dicSizeGain + lengthGain - gapPunishment > 0)
			return new MergeCandidate(pairFront, pairBack, dicSizeGain, lengthGain);
		else
			return null;
	}

	public static MergeCandidate ComputeBenefitsForSinglePattern(ArrayList<BaseElement> pairFront,
                                                                 ArrayList<BaseElement> pairBack, CountSummary csBack,
                                                                 int gapPunishment) {
		int dicSizeGain = 0;
		// if part of AB, AB can form ABAB, then extra AB still need to form pattern, dic size +1
		if((csBack.getCount() - (pairFront.size() + pairBack.size())) > 1)
			dicSizeGain -= 1;

		int lengthGain = pairFront.size();
		if ((csBack.getCount() - (pairFront.size() + pairBack.size())) == 1) {
			lengthGain -= (((FrequentPattern) pairBack.get(0)).getItems().size()-1);
		}
		if(dicSizeGain + lengthGain - gapPunishment > 0)
			return new MergeCandidate(pairFront, pairBack, dicSizeGain, lengthGain);
		else
			return null;
	}

	public static MergeCandidate ComputeBenefitsForSinglePatternPair(ArrayList<BaseElement> pairFront,
																	 ArrayList<BaseElement> pairBack,
																	 CountSummary csBack, int gapPunishment) {
		// front list is single/repeat , back list if pattern
//		int benefit = 0;
		int dicSizeGain = 0;
		if(csBack.getCount()- pairBack.size() > 1)
			dicSizeGain -= 1;

		int lengthGain = 0;
		lengthGain += pairFront.size() * 1;
		if(csBack.getCount() - pairBack.size() == 1)
			lengthGain -= (((FrequentPattern) pairBack.get(0)).getItems().size()-1);
		if(dicSizeGain + lengthGain - gapPunishment > 0)
			return new MergeCandidate(pairFront, pairBack, dicSizeGain, lengthGain);
		else
			return null;
	}

	public static MergeCandidate ComputeBenefitsForPatternSinglePair(ArrayList<BaseElement> pairFront,
																	 ArrayList<BaseElement> pairBack,
																	 CountSummary csFront, int gapPunishment) {
		// front list is pattern, back list is single/repeat
//		int benefit = 0;
		int dicSizeGain = 0;
		if(csFront.getCount()- pairFront.size() > 1)
			dicSizeGain -= 1;

		int lengthGain = 0;
		lengthGain += pairBack.size() * 1;
		if(csFront.getCount() - pairFront.size() == 1)
			lengthGain -= (((FrequentPattern) pairFront.get(0)).getItems().size()-1);
		if(dicSizeGain + lengthGain- gapPunishment > 0)
			return new MergeCandidate(pairFront, pairBack, dicSizeGain, lengthGain);
		else
			return null;
	}

	public static void main(String[] args) {
		CountSummary front = new CountSummary(1);
		front.addElementToList(new SingleElement(1, "A", 0));
		front.addElementToList(new SingleElement(1, "A", 2));
		// front.addElementToList(new SingleElement(1, "A", 4));
		CountSummary back = new CountSummary(2);
		back.addElementToList(new SingleElement(2, "B", 1));
		back.addElementToList(new SingleElement(2, "B", 3));
		// back.addElementToList(new SingleElement(2, "B", 5));
		ComputeBenefit.GenerateMergePairForTwoIdentifiers(front, back, 0, 10);
		System.out.println(front.getRefToElement().get(0).getClass().toString().endsWith("SingleElement"));
	}
}
