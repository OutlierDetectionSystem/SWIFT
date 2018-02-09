package baseline.base;

public class ItemPair {
	// save each item in the sequence
	private String item;
	// save the global index in previous time sequence pattern
	private int index;
	
	public ItemPair(String item, int index){
		this.setItem(item);
		this.setIndex(index);
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
  
}
