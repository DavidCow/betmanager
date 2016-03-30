package blogaBetHistoricalDataParsing;

import java.util.Comparator;
import java.util.Date;

public class BlogaBetComparator implements Comparator<BlogaBetElement>{
	
	@Override
	public int compare(BlogaBetElement arg0, BlogaBetElement arg1) {
		Date date0 = arg0.getGameDate();
		long m0 = date0.getTime();
		
		Date date1 = arg1.getGameDate();
		long m1 = date1.getTime();
		
		if(m0 < m1)
			return -1;
		if(m1 < m0)
			return 1;
		return 0;
	}
}
