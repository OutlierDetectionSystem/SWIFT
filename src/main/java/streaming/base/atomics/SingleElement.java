package streaming.base.atomics;

public class SingleElement implements BaseElement {
	private int identifier;
	private String item;
	private int indexOfItemStart;
	private boolean repeatItem = false;
	private int indexOfItemEnd;

	public SingleElement(int identifier, String item, int indexOfItemStart) {
		this.setIdentifier(identifier);
		this.item = item;
		this.setIndexOfItemStart(indexOfItemStart);
		this.setIndexOfItemEnd(indexOfItemStart);
		this.setRepeatItem(false);
	}

	@Override
	public boolean equals(BaseElement e) {
		if(e.getClass().toString().endsWith("FrequentPattern"))
			return false;
		SingleElement ele = (SingleElement) e;
		if (this.identifier == ele.getIdentifier() && this.item == ele.item
				&& this.indexOfItemStart == ele.indexOfItemStart && this.repeatItem == ele.repeatItem)
			return true;
		else
			return false;
	}

	@Override
	public int getNumEventsInElement() {
		return indexOfItemEnd-indexOfItemStart+1;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public void setIndexOfItemStart(int indexOfItemStart) {
		this.indexOfItemStart = indexOfItemStart;
	}

	public void setIndexOfItemEnd(int indexOfItemEnd) {
		this.indexOfItemEnd = indexOfItemEnd;
	}

	public void minusOneOfEndIndex() {
		this.indexOfItemEnd--;
		if (this.indexOfItemEnd <= this.indexOfItemStart)
			this.repeatItem = false;
	}

	public void addOneOfStartIndex() {
		this.indexOfItemStart++;
		if (this.indexOfItemStart >= this.indexOfItemEnd)
			this.repeatItem = false;
	}

	public void addNumberOfStartIndex(int number) {
		this.indexOfItemStart += number;
		if (this.indexOfItemStart >= this.indexOfItemEnd)
			this.repeatItem = false;
	}

	public boolean isRepeatItem() {
		return repeatItem;
	}

	public void setRepeatItem(boolean repeatItem) {
		this.repeatItem = repeatItem;
	}

	@Override
	public int getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	@Override
	public int getStartIndex() {
		return indexOfItemStart;
	}

	@Override
	public int getEndIndex() {
		return indexOfItemEnd;
	}

	public void setEndIndex(int endIndexNew) {
		this.indexOfItemEnd = endIndexNew;
		if(this.indexOfItemEnd == this.indexOfItemStart)
			this.repeatItem = false;
		else
			this.repeatItem = true;
	}

	@Override
	public int getFrontEleItemIndex() {
		return this.indexOfItemEnd;
	}

	@Override
	public int getBackEleItemIndex() {
		return this.indexOfItemStart;
	}

	@Override
	public int getFrontEleSeqIndex() {
		return this.indexOfItemEnd;
	}

	@Override
	public int getBackEleSeqIndex() {
		return this.indexOfItemStart;
	}

	@Override
	public String getItemsInString() {
		return this.item;
	}

	@Override
	public String getStartItem() {
		return this.item;
	}

	@Override
	public String printElementInfo() {
		String finalRes = identifier + "\t" + repeatItem + "," + item + "[" + indexOfItemStart + "," + indexOfItemEnd
				+ "]";
		return finalRes;
	}
}
