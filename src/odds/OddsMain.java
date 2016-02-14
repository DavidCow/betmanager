package odds;

public class OddsMain {
	public static void main(String[] args) {
		double winOdds = 3.0;
		double drawOdds = 3.0;
		double lossOdds = 3.0;
		
		double layOdds = winOdds / (winOdds - 1);
		
		double backP = 1.0 / winOdds;
		double layP = 1.0 / drawOdds + 1 / lossOdds;
		
		double back = 1.0 / backP;
		double lay = 1.0 / layP;
		
		double result = 0.33 * (2 * back - 6) + 0.66 * (2 * lay - 5);
		
		double result2 = 0.33 * (2 * 3.00 - 6) + 0.66 * (1 * 2.00 - 5);
		System.out.println(result);
	}
}
