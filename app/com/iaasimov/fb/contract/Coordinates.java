package com.iaasimov.fb.contract;

import javax.annotation.Generated;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Coordinates {

	@SerializedName("lat")
	@Expose
	private Double latitude;
	@SerializedName("long")
	@Expose
	private Double longitude;
	
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(latitude).append(longitude).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Coordinates) == false) {
			return false;
		}
		Coordinates rhs = ((Coordinates) other);
		return new EqualsBuilder().append(latitude, rhs.latitude).append(longitude, rhs.longitude).isEquals();
	}

}
