package moneyManagement;

public class StakeCalculation {

	public static double blogaBetPercent(double take){
		double averageTake = 0.35;
		double res = Math.min(1.5, 0.5 * take / averageTake);
		return res;		
	}
	
	public static double betAdvisorPercent(double take){
		double res = Math.min(1, take / 100);
		return res;	
	}
	
	public static void main(String[] args) {
		double r = betAdvisorPercent(110);
		System.out.println(r);
	}
}
