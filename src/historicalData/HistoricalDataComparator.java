package historicalData;

import java.util.Comparator;
import java.util.Date;

public class HistoricalDataComparator implements Comparator<HistoricalDataElement>{

	@Override
	public int compare(HistoricalDataElement arg0, HistoricalDataElement arg1) {
		Date date0 = arg0.getStartDate();
		long m0 = date0.getTime();
		
		Date date1 = arg1.getStartDate();
		long m1 = date1.getTime();
		
		if(m0 < m1)
			return -1;
		if(m1 < m0)
			return 1;
		return 0;
	}

}
