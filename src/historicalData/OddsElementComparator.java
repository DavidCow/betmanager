package historicalData;

import java.util.Comparator;

public class OddsElementComparator implements Comparator<OddsElement>{

	@Override
	public int compare(OddsElement arg0, OddsElement arg1) {
		long m0 = arg0.getTime();
		long m1 = arg1.getTime();
		
		if(m0 < m1)
			return -1;
		if(m1 < m0)
			return 1;
		return 0;
	}

}
