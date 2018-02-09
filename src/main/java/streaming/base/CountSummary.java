package streaming.base;

import streaming.base.atomics.BaseElement;

import java.util.LinkedList;

public class CountSummary {
	private int count;
	private int identifier;
	private LinkedList<BaseElement> refToElement;

	public CountSummary(int identifier) {
		this.count = 0;
		this.identifier = identifier;
		this.refToElement = new LinkedList<BaseElement>();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public LinkedList<BaseElement> getRefToElement() {
		return refToElement;
	}

	public void setRefToElement(LinkedList<BaseElement> refToElement) {
		this.refToElement = refToElement;
	}

	public void removeElementFromList(BaseElement element) {
		refToElement.remove(element);
		count--;
//		count = refToElement.size();
	}

	public void clearCountSummary() {
		count = 0;
		refToElement.clear();
	}

	public boolean addElementToList(BaseElement element) {
		boolean added = false;
		for (int i = 0; i < this.refToElement.size(); i++) {
			if (this.refToElement.get(i).getStartIndex() > element.getStartIndex()) {
				refToElement.add(i, element);
				added = true;
				break;
			}
		}
		if(!added)
			refToElement.add(element);
		count++;
		if (count > 1)
			return true;
		else
			return false;
	}

	public String printSummary() {
		String finalRes = "Count:" + count + "\t" + "Size of Ref: " + refToElement.size();
		for(BaseElement e: refToElement){
			finalRes += e + ",";
		}
		return finalRes;
	}
}
