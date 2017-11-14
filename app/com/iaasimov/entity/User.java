package com.iaasimov.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "f_user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	@Column(name = "username", nullable = false, updatable = true, insertable = true)
	private String userName;
	@Column(name = "token", nullable = false, updatable = true, insertable = true)
	private String token;
	@Column(name = "email", nullable = false, updatable = true, insertable = true)
	private String email;
	@Column(name = "c_latitude", nullable = false, updatable = true, insertable = true)
	private String currentLatitude;
	@Column(name = "c_longitude", nullable = false, updatable = true, insertable = true)
	private String currentLongitude;
	@Column(name = "fb_id", nullable = false, updatable = true, insertable = true)
	private String fbId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCurrentLatitude() {
		return currentLatitude;
	}
	public void setCurrentLatitude(String currentLatitude) {
		this.currentLatitude = currentLatitude;
	}
	public String getCurrentLongitude() {
		return currentLongitude;
	}
	public void setCurrentLongitude(String currentLongitude) {
		this.currentLongitude = currentLongitude;
	}
	public String getFbId() {
		return fbId;
	}
	public void setFbId(String fbId) {
		this.fbId = fbId;
	}
}
