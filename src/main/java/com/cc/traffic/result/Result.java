package com.cc.traffic.result;

public class Result<T> {
	private int code;
	private String msg;
	private T data;
	
	private Result(T data) {
		/*
		 * default success code and message 
		 * */
		this.code=0;
		this.msg="success";
		this.data = data;
	}

	/*
	 * Don't care of the data when error returned
	 * */
	private Result(CodeMsg codeMsg) {
		if(codeMsg==null) {
			return;
		}
		this.code=codeMsg.getCode();
		this.msg = codeMsg.getMsg();
	}

	/*
	 * call when success
	 * */
	public static <T> Result<T> success(T data){
		return new Result<T>(data);
	}
	/*
	 * call when error
	 * */
	public static <T> Result<T> error(CodeMsg codeMsg){
		return new Result<T>(codeMsg);
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}
	
}
