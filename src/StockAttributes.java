import java.util.Calendar;

public class StockAttributes {
  String ticker;
  String name;
  float shares;
  Calendar startDate;
  Calendar endDate;
  
  public StockAttributes(String _ticker, String _name, float _shares,
		                 Calendar _sDate, Calendar _eDate) {
    this.setTicker(_ticker);
    this.setName(_name);
    this.setShares(_shares);
    this.setStartDate(_sDate);
    this.setEndDate(_eDate);
  }
  public String getTicker() {
    return ticker;
  }
  public String getName() {
    return name;
  }
  public float getShares() {
	return shares;
  }
  public Calendar getStartDate() {
	  return startDate;
  }
  public Calendar getEndDate() {
	  return endDate;
  }
  protected void setName(String _name) {
    this.name = _name;    
  }  
  protected void setTicker(String _ticker) {
    this.ticker = _ticker;
  } 
  protected void setShares(float _shares) {
	this.shares = _shares;
  }
  public String toString() {
    return (this.getTicker() + " " + this.getName() + " Shares: " + 
            Float.toString(this.getShares())+
            " Start: " + CalendarHelper.getIsoDate(this.getStartDate())+
            " End: " + CalendarHelper.getIsoDate(this.getEndDate()));           
  }
  public void setStartDate(Calendar _sDate) {
	  this.startDate = _sDate;
  }
  public void setEndDate(Calendar _endDate) {
	  this.endDate = _endDate;
  }
  public String toCsv() {
	    return (this.getTicker() + ",," +
	            this.getName() + ",," +
	    		Float.toString(this.getShares()));           
  }
  public static String csvHdr() {
	    return "Ticker,,Name,,Shares";
  }
}