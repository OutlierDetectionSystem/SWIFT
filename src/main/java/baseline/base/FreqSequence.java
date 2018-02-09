package baseline.base;

import java.util.ArrayList;

public class FreqSequence implements Comparable {
	private ArrayList<ResItemArrayPair> itemPairList;

	private int supportNum = 0;

	// only generate if used
	private ArrayList<ArrayList<Integer>> indexesForSequence = new ArrayList<ArrayList<Integer>>();

	public ArrayList<ArrayList<Integer>> getIndexesForSequence() {
		return indexesForSequence;
	}

	public void generateSupport(){
		this.supportNum = this.itemPairList.get(0).index.size();
	}
	public void generateIndexesForSequence() {

		for (int i = 0; i < supportNum; i++) {
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			for (ResItemArrayPair resPair : itemPairList) {
				tempList.add(resPair.index.get(i));
			}
			indexesForSequence.add(tempList);
		}
	}

	public ArrayList<ResItemArrayPair> getItemPairList() {
		return itemPairList;
	}

	public void setItemPairList(ArrayList<ResItemArrayPair> itemPairList) {
		this.itemPairList = itemPairList;
	}

	public FreqSequence() {
		itemPairList = new ArrayList<ResItemArrayPair>();
	}

	public FreqSequence(ArrayList<ResItemArrayPair> itemPairList) {
		this.itemPairList = itemPairList;
	}

	public void addItemToSequence(ResItemArrayPair newLetter) {
		itemPairList.add(newLetter);
	}

	public int getSupportNum() {
		return supportNum;
	}

	public void setSupportNum(int supportNum) {
		this.supportNum = supportNum;
	}

	public int getItemNumInFreqSeq() {
		return this.itemPairList.size();
	}

	public String getFreqSeqInString() {
		String str = "";
		for (ResItemArrayPair curItem : this.itemPairList) {
			str += curItem.item + ",";
		}
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	/**
	 * copy a frequent sequence
	 * 
	 * @return
	 */
	public FreqSequence copyFreqSeqence(ArrayList<Boolean> ifHasNewItemInPrevSeq) {
		FreqSequence copySeq = new FreqSequence();

		for (ResItemArrayPair itemPair : this.itemPairList) {
			ArrayList<Integer> deleteItemSets = new ArrayList<>();
			ResItemArrayPair tempItemPair;
			String tempLetter = itemPair.item;
			ArrayList<Integer> tempIndexes = new ArrayList<Integer>(itemPair.index);
			for (int i = 0; i < ifHasNewItemInPrevSeq.size(); i++) {
				if (!ifHasNewItemInPrevSeq.get(i))
					deleteItemSets.add(tempIndexes.get(i));
			}
			tempIndexes.removeAll(deleteItemSets);
			tempItemPair = new ResItemArrayPair(tempLetter, tempIndexes);
			copySeq.itemPairList.add(tempItemPair);
		}

		return copySeq;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if (this.getItemNumInFreqSeq() > ((FreqSequence) o).getItemNumInFreqSeq())
			return -1;
		else if (this.getItemNumInFreqSeq() < ((FreqSequence) o).getItemNumInFreqSeq())
			return 1;
		else
			return 0;
	}

}
