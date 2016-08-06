package bettingManager.statsCalculation;

import java.util.ArrayList;

public class Alias {

	public ArrayList<String> tipsters = new ArrayList<String>();
	
	public String aliasName;
	
	public boolean selected = true;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public ArrayList<String> getTipsters() {
		return tipsters;
	}
	public void setTipsters(ArrayList<String> tipsters) {
		this.tipsters = tipsters;
	}


	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
}
