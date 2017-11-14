package com.iaasimov.fb.contract;

import javax.annotation.Generated;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Action {

	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("url")
	@Expose
	private String url;
	@SerializedName("title")
	@Expose
	private String title;
	@SerializedName("messenger_extensions")
	@Expose
	private boolean messengerExtensions;
	@SerializedName("webview_height_ratio")
	@Expose
	private String webviewHeightRatio;
	@SerializedName("fallback_url")
	@Expose
	private String fallbackUrl;
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @return The type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            The type
	 */
	public void setType(String type) {
		this.type = type;
	}

	public boolean isMessengerExtensions() {
		return messengerExtensions;
	}

	public void setMessengerExtensions(boolean messengerExtensions) {
		this.messengerExtensions = messengerExtensions;
	}

	public String getWebviewHeightRatio() {
		return webviewHeightRatio;
	}

	public void setWebviewHeightRatio(String webviewHeightRatio) {
		this.webviewHeightRatio = webviewHeightRatio;
	}

	public String getFallbackUrl() {
		return fallbackUrl;
	}

	public void setFallbackUrl(String fallbackUrl) {
		this.fallbackUrl = fallbackUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(type).append(webviewHeightRatio).append(fallbackUrl).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Action) == false) {
			return false;
		}
		Action rhs = ((Action) other);
		return new EqualsBuilder().append(type, rhs.type).append(webviewHeightRatio, rhs.webviewHeightRatio).append(fallbackUrl, rhs.fallbackUrl)
				.isEquals();
	}

}
