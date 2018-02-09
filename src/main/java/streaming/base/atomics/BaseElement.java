package streaming.base.atomics;

public interface BaseElement {

	public int getIdentifier();

	public void setIdentifier(int identifier);
	
	public int getStartIndex();

	public int getEndIndex();
	
	public int getFrontEleItemIndex();
	
	public int getBackEleItemIndex();
	
	public int getFrontEleSeqIndex();
	
	public int getBackEleSeqIndex();
	
	public String getItemsInString();
	
	public String getStartItem();
	
	public String printElementInfo();
	
	public boolean equals(BaseElement e);

	public int getNumEventsInElement();
}
