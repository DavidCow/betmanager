package bettingManager.gui;

public class ObservableMessage {
	private int fromWhichFilter = -1;
	private Object msg;
	
	public ObservableMessage(int fromWhere, Object msg) {
		this.fromWhichFilter = fromWhere;
		this.msg = msg;
	}
	
	public int getFromWhichFilter() {
		return fromWhichFilter;
	}
	public void setFromWhichFilter(int fromWhichFilter) {
		this.fromWhichFilter = fromWhichFilter;
	}
	public Object getMsg() {
		return msg;
	}
	public void setMsg(Object msg) {
		this.msg = msg;
	}
}
