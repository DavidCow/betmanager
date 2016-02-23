package historicalData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoricalDataElement {
	/**
	 * @return the hdpList
	 */
	public List<HdpElement> getHdpList() {
		return hdpList;
	}

	/**
	 * @param hdpList the hdpList to set
	 */
	public void setHdpList(List<HdpElement> hdpList) {
		this.hdpList = hdpList;
	}

	private Date startDate;
	private String league;
	private String host;
	private String guest;
	private String source;
	
	/**
	 * @return the totalList
	 */
	public List<TotalElement> getTotalList() {
		return totalList;
	}

	/**
	 * @param totalList the totalList to set
	 */
	public void setTotalList(List<TotalElement> totalList) {
		this.totalList = totalList;
	}

	// Odds
	private List<OneTwoElement> oneTwoList;
	private List<TotalElement> totalList;
	private List<HdpElement> hdpList;
	
	public HistoricalDataElement(Date startDate, String league, String host, String guest, String source){
		this.startDate = startDate;
		this.league = league;
		this.host = host;
		this.guest = guest;
		this.source = source;
	}

	/**
	 * @param oneTwoList the oneTwoList to set
	 */
	public void setOneTwoList(List<OneTwoElement> oneTwoList) {
		this.oneTwoList = oneTwoList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guest == null) ? 0 : guest.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((league == null) ? 0 : league.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HistoricalDataElement other = (HistoricalDataElement) obj;
		if (guest == null) {
			if (other.guest != null)
				return false;
		} else if (!guest.equals(other.guest))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (league == null) {
			if (other.league != null)
				return false;
		} else if (!league.equals(other.league))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the oneTwoList
	 */
	public List<OneTwoElement> getOneTwoList() {
		return oneTwoList;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the guest
	 */
	public String getGuest() {
		return guest;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return the league
	 */
	public String getLeague() {
		return league;
	}
}
