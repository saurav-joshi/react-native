package com.iaasimov.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "f_conversation")
public class Conversation {
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	@Column(name = "user_id", nullable = false, updatable = true, insertable = true)
	private Long userId;
	@Column(name = "question", nullable = false, updatable = true, insertable = true)
	private String question;
	@Column(name="answer", nullable=false, updatable=true, insertable=true)
	private String answer;
	@Column(name = "posted_date", nullable = false, updatable = true, insertable = true)
	private Date postedDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
