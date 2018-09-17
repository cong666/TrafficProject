package com.cc.traffic.result;

public class CodeMsg {

	private int code;
	private String msg;

	// Generally
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "Server Error");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "Parameter check error：%s");
	// Login 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session invalide");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "Password is empty");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "PhoneNumber is empty");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "PhoneNumber wrong format");
	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "PhoneNumber not exist");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "Password is wrong");

	// Item 5003XX

	// Order 5004XX
	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "Order not exist");

	// flash sale 5005XX
	public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "Flash Sale is Over");
	public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "Can not do repeate bought");

	private CodeMsg() {
	}

	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}

}
