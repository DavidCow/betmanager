package moneyManagement;

public class Kelly {

	private static double brPercent(double odds, double winPercentage){
		double r = (winPercentage * (odds) - 1) / odds;
		return r;
	}
	
	public static void main(String[] args) {
		double r = brPercent(2, 0.66);
		System.out.println(r);
	}
}
