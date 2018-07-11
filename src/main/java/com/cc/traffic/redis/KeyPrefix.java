package com.cc.traffic.redis;

public interface KeyPrefix {
	String getPrefix();
	int getExpireSeconds();
}
