package gnt.sd.controller;

public class ServiceRespone {
	private ServiceAction _action;
	private Object _data;
	private ResultCode _code;
	
	public ServiceRespone(ServiceAction action, Object data, ResultCode resultCode) {
		_action = action;
		_data = data;
		_code = resultCode;
	}
	
	public ServiceRespone(ServiceAction action, Object data) {
		this(action,data,ResultCode.Success);
	}
	
	public boolean isSuccess() {
		return (_code == ResultCode.Success);
	}
	
	public Object getData() {
		return _data;
	}
	
	public ServiceAction getAction() {
		return _action;
	}
	
	public ResultCode getResultCode() {
		return _code;
	}
}
