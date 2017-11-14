package com.iaasimov.entity;

import java.io.Serializable;

public class CustomerApp implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private long customerId;
	private String appName;
	private String appId;
	private long createdDate;
	private String createdDateString;
	private long expiredDate;
	private long requestLimited;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	public long getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(long expiredDate) {
		this.expiredDate = expiredDate;
	}
	public long getRequestLimited() {
		return requestLimited;
	}
	public void setRequestLimited(long requestLimited) {
		this.requestLimited = requestLimited;
	}
	public String getCreatedDateString() {
		return createdDateString;
	}
	public void setCreatedDateString(String createdDateString) {
		this.createdDateString = createdDateString;
	}
}
