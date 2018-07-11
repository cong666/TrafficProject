package com.cc.traffic.redis;

public abstract class BasePrefix implements KeyPrefix{
	private int expireSeconds;

	private String prefix;
	
	//0 means never expired
	public BasePrefix(String prefix) {
		this(0,prefix);
	}
	
	public BasePrefix(int expireSeconds,String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}
	

	public int getExpireSeconds() {
		return expireSeconds;
	}

	public String getPrefix() {
		String className = getClass().getSimpleName();
		return className+":"+prefix;
	}
}
