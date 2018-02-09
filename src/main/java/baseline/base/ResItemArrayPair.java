package baseline.base;

import java.util.ArrayList;

public class ResItemArrayPair {
	// save each letter in the sequence
	public String item;
	// save the global index list in previous time sequence pattern
	public ArrayList<Integer> index;

	public ResItemArrayPair(String letter) {
		this.item = letter;
		this.index = new ArrayList<Integer>();
	}
	public ResItemArrayPair(String letter, ArrayList<Integer> index){
		this.item = letter;
		this.index = index;
	}
	public void setIndexes(ArrayList<Integer> index){
		this.index.addAll(index);
	}
	public void addToIndex(int tempIndex) {
		index.add(tempIndex);
	}
}
