package baseline.base;

import java.util.ArrayList;

public class FSCandidate implements Comparable {
	private String seqInStr;
	private ArrayList<ArrayList<Integer>> indexes;
	private int supportNum;
	private int lengthOfStr;
	private int mergeBenefit;

	public FSCandidate(String seqInStr, ArrayList<ArrayList<Integer>> indexes) {
		this.seqInStr = seqInStr;
		this.indexes = indexes;
		this.supportNum = this.indexes.size();
		this.lengthOfStr = seqInStr.split(",").length;
		this.mergeBenefit = this.supportNum * (this.lengthOfStr - 1) - 1;
//		this.mergeBenefit = this.supportNum * (this.lengthOfStr - 1) - this.lengthOfStr;
	}

	public boolean updateIndexes(boolean[] availability, int minSupport) {
		ArrayList<ArrayList<Integer>> tempDelete = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> tempIndex : indexes) {
			boolean deleteSeq = false;
			for (Integer i : tempIndex) {
				if (!availability[i]) {
					deleteSeq = true;
					break;
				}
			}
			if (deleteSeq)
				tempDelete.add(tempIndex);
		}

		for (ArrayList<Integer> tempIndex : tempDelete) {
			this.indexes.remove(tempIndex);
		}
		this.supportNum = this.indexes.size();
		this.mergeBenefit = this.supportNum * (this.lengthOfStr - 1) - 1;
//		this.mergeBenefit = this.supportNum * (this.lengthOfStr - 1) - this.lengthOfStr;
		return this.indexes.size() >= minSupport;
	}

	public String printFSCandidate() {
		String str = "";
		str += "FS: " + this.seqInStr + "\n";
		str += "Indexes: ";
		for (ArrayList<Integer> listOfIndexes : indexes) {
			str += "[";
			for (Integer i : listOfIndexes)
				str += i + " ";
			str += "] ";
		}
		str += "\n" + "Number of support: " + this.supportNum + " ," + " FS Length: " + this.lengthOfStr
				+ " FS Benefits: " + this.mergeBenefit;
		return str;
	}

	public String getSeqInStr() {
		return seqInStr;
	}

	public void setSeqInStr(String seqInStr) {
		this.seqInStr = seqInStr;
	}

	public ArrayList<ArrayList<Integer>> getIndexes() {
		return indexes;
	}

	public void setIndexes(ArrayList<ArrayList<Integer>> indexes) {
		this.indexes = indexes;
	}

	public int getSupportNum() {
		return supportNum;
	}

	public void setSupportNum(int supportNum) {
		this.supportNum = supportNum;
	}

	public int getLengthOfStr() {
		return lengthOfStr;
	}

	public void setLengthOfStr(int lengthOfStr) {
		this.lengthOfStr = lengthOfStr;
	}

	public int getMergeBenefit() {
		return mergeBenefit;
	}

	public void setMergeBenefit(int mergeBenefit) {
		this.mergeBenefit = mergeBenefit;
	}

	@Override
	public int compareTo(Object o) {
		FSCandidate other = (FSCandidate) o;
		if (other.getMergeBenefit() > this.getMergeBenefit())
			return 1;
		else if (other.getMergeBenefit() < this.getMergeBenefit())
			return -1;
		else
			return 0;
	}

}
