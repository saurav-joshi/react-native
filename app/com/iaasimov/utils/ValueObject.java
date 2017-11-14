package com.iaasimov.utils;

import java.io.Serializable;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.1 $ 
 */
public class ValueObject implements Serializable {
	
	private static final long serialVersionUID = 1L;	

	//key of value object
	private String key ;
	//value object 
	private Object value ;
	
	private String op="=";
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		if(value==null){
			this.value = new String("");
		}else{
			this.value = value;
		}	
	}
	
	/**
	 * Contructor
	 * 
	 * @param key
	 * @param value
	 */
	public ValueObject(String key, Object value) {
		super();
		this.key = key;
		if(value==null){
			this.value = new String("");
		}else{
			this.value = value;
		}	
	}
	
	/**
	 * Contructor
	 * 
	 * @param key
	 * @param value
	 */
	public ValueObject(String key, String op, Object value) {
		super();
		this.key = key;
		if(value==null){
			this.value = new String("");
		}else{
			this.value = value;
		}	
		this.op=op;
		if (this.op==null) {
			this.op="=";
		}
	}
	
	/**
	 * Default contructor
	 */
	public ValueObject() {
		super();
		
	}
	
	
	public String toString() {
		return key + op + value.toString();
	}
	
	public int hasCode() {
		if (key ==null) {
			return super.hashCode();
		}
		return key.hashCode();
	}
	
	
}
