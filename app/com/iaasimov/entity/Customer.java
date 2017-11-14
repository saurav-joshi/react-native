package com.iaasimov.entity;

import java.io.Serializable;

public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String phone;
	private String email;
	private String companyName;
	private int status;
	private boolean isAdmin;
	private boolean isSystemAdmin;
	private long parentId;
	private long contractStarted;
	private long contractEnded;
	private long requestLimited;
	private String password;
	private long createdDate;
	private String contractStartedString;
	private String contractEndedString;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public boolean getIsSystemAdmin() {
		return isSystemAdmin;
	}
	public void setIsSystemAdmin(boolean isSystemAdmin) {
		this.isSystemAdmin = isSystemAdmin;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public long getContractStarted() {
		return contractStarted;
	}
	public void setContractStarted(long contractStarted) {
		this.contractStarted = contractStarted;
	}
	public long getContractEnded() {
		return contractEnded;
	}
	public void setContractEnded(long contractEnded) {
		this.contractEnded = contractEnded;
	}
	public long getRequestLimited() {
		return requestLimited;
	}
	public void setRequestLimited(long requestLimited) {
		this.requestLimited = requestLimited;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	public String getContractStartedString() {
		return contractStartedString;
	}
	public void setContractStartedString(String contractStartedString) {
		this.contractStartedString = contractStartedString;
	}
	public String getContractEndedString() {
		return contractEndedString;
	}
	public void setContractEndedString(String contractEndedString) {
		this.contractEndedString = contractEndedString;
	}
}
