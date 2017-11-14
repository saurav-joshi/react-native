package com.iaasimov.utils;

import java.util.ArrayList;
import java.util.List;

public class PagedSearchResult {

	private long foundCount = 0;
	private List<Object> foundList = new ArrayList<Object>();
	
	public long getFoundCount() {
		return foundCount;
	}
	public void setFoundCount(long foundCount) {
		this.foundCount = foundCount;
	}
	
	public List<Object> getFoundList() {
		return foundList;
	}
	public void setFoundList(List<Object> foundList) {
		this.foundList = foundList;
	}
}
