package com.cc.traffic.redis;

public class UserKey extends BasePrefix {

	private UserKey(String prefix) {
		super(prefix);
	}
	public static final UserKey getById = new UserKey("id");
	public static final UserKey getByName = new UserKey("Name");
}
