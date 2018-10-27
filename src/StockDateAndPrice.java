//import java.util.Date;

public class StockDateAndPrice {
  java.util.Calendar stockDate;
  float open, high, low, close, adjClose;
  long volume;
  
  public StockDateAndPrice(String _sdate, float _open, float _high, float _low,
                             float _close, float _adjClose, long _volume) {
    this.setStockDate(_sdate);
    this.setOpen(_open);
    this.setHigh(_high);
    this.setLow(_low);
    this.setClose(_close);
    this.setAdjClose(_adjClose);
    this.setVolume(_volume);
  }
  public StockDateAndPrice(String _sdate, String _open, String _high,
		                   String _low, String _close, String _adjClose,
		                   String _volume) {
	  float _o, _h, _l, _c, _a;
	  long _v;
	  _o = Float.parseFloat(_open);
	  _h = Float.parseFloat(_high);
	  _l = Float.parseFloat(_low);
	  _c = Float.parseFloat(_close);
	  _a = Float.parseFloat(_adjClose);
	  _v = Long.parseLong(_volume);
	  
	  this.setStockDate(_sdate);
	  this.setOpen(_o);
	  this.setHigh(_h);
	  this.setLow(_l);
	  this.setClose(_c);
	  this.setAdjClose(_a);
	  this.setVolume(_v);	  
  }  
  public java.util.Calendar getStockDate() {
    return stockDate;
  }
  public float getOpen() {
    return open;
  }
  public float getHigh() {
    return high;
  }
  public float getLow() {
    return low;
  }
  public float getClose() {
    return close;
  }
  public float getAdjClose() {
    return adjClose;
  }
  public long getVolume() {
    return volume;
  }
  
  protected void setStockDate(String _sDate) {
    this.stockDate = CalendarHelper.getCal(_sDate);    
  }  
  protected void setStockDate(int _year, int _month, int _day) {
    this.stockDate = new java.util.GregorianCalendar(_year, _month-1, _day);
  }
  
  protected void setOpen(float _open) {
    this.open = _open;
  }
  protected void setHigh(float _high) {
    this.high = _high;
  }
  protected void setLow(float _low) {
    this.low = _low;
  }
  protected void setClose(float _close) {
    this.close = _close;
  }
  protected void setAdjClose(float _adjClose) {
    this.adjClose = _adjClose;
  }
  protected void setVolume(long _volume) {
    this.volume = _volume;
  }
  
  public String toString() {
    return CalendarHelper.getIsoDate(this.getStockDate()) + " " +
           "Open: " + Float.toString(this.getOpen()) +
           "High: " + Float.toString(this.getHigh()) + 
           "Low: " + Float.toString(this.getLow()) +
           "Close: " + Float.toString(this.getClose()) +
           "AdjClose: " + Float.toString(this.getAdjClose());           
  }
  
  // We put open in fourth column after date in case they want to include dividends
  public String toCsvAll() {
	    return CalendarHelper.getIsoDate(this.getStockDate()) + ",,,," +
	            Float.toString(this.getOpen()) + ",," +
	            Float.toString(this.getHigh()) + ",," + 
	            Float.toString(this.getLow()) + ",," +
	            Float.toString(this.getClose()) + ",," +
	            Float.toString(this.getAdjClose()) + ",," +
	            Long.toString(this.getVolume());
  }
  public static String csvHdrAll() {
	    return "Date,,,," +
	           "Open,," +
	           "High,," + 
	           "Low,," +
	           "Close,," +
	           "AdjClose,," +
	           "Volume";           
  }
  
  // We put open in fourth column after date in case they want to include dividends
  public String toCsvClose() {
	    return CalendarHelper.getIsoDate(this.getStockDate()) + ",," +
	            Float.toString(this.getClose());            
  }
  public static String csvHdrClose() {
	    return "Date,," +
	           "Close";    
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
}