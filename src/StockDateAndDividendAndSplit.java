//import java.util.Date;

public class StockDateAndDividendAndSplit {
  java.util.Calendar transactionDate;
  char recordType; // D for Dividend, S for Split
  String splitValue;
  float dividend;
  
  public StockDateAndDividendAndSplit(String _sdate,
		                              char _recType,
		                              String _splitAmt,
		                              float _dividend) {
    this.setTransactionDate(_sdate);
    this.setRecordType(_recType);
    this.setSplitValue(_splitAmt);
    this.setDividend(_dividend);
  }
  public StockDateAndDividendAndSplit(String _sdate,
		                              char _recType,
		                              String _splitAmt,
		                              String _dividend) {
	this.setTransactionDate(_sdate);
	this.setRecordType(_recType);
    this.setSplitValue(_splitAmt);
	this.setDividend(Float.parseFloat(_dividend));
  }
  
  public StockDateAndDividendAndSplit(String _type,
		                              String _date,
		                              String _value) {	
	this.setTransactionDate(_date);
	if (_type.charAt(0) == 'D') {
	  this.setRecordType('D');
	  this.setSplitValue("");
	  this.setDividend(Float.parseFloat(_value));
	}
	else if (_type.charAt(0) == 'S') {		
	  this.setRecordType('S');
	  this.setSplitValue(_value);
	  this.setDividend((float)0);	
	}
	else {
	  this.setRecordType('U');
	  this.setSplitValue("");
	  this.setDividend((float)0);
	}	
  }
    
  public java.util.Calendar getTransactionDate() {
    return transactionDate;
  }
  public float getDividend() {
    return dividend;
  }
  public String getSplitValue() {
	  return splitValue;
  }
  public float getSplitFactor() {
    float factor = (float)1.0;
    if (splitValue.isEmpty() == false) {
      String[] dummy = splitValue.split(":");
      if (dummy.length > 1) {
        float num = Float.parseFloat(dummy[0]);
        float den = Float.parseFloat(dummy[1]);
        factor = num/den;
      }
    }
    return factor;
  }
  
  public char getRecordType() {
	  return recordType;	  
  }
  protected void setTransactionDate(String _sDate) {
    this.transactionDate = CalendarHelper.getCal(_sDate);    
  }  
  protected void setTransactionDate(int _year, int _month, int _day) {
    this.transactionDate = new java.util.GregorianCalendar(_year, _month-1, _day);
  }  
  protected void setDividend(float _dividend) {
    this.dividend = _dividend;
  }
  protected void setSplitValue(String _splitValue) {
	  this.splitValue = _splitValue;
  }
  protected void setRecordType(char _recordType) {
	  this.recordType = _recordType;	  
  }
  public String toString() {
    return CalendarHelper.getIsoDate(this.getTransactionDate()) + " " +
      (this.getRecordType() == 'D' ? "Dividend: " + Float.toString(this.getDividend()) :
        (this.getRecordType() == 'S' ? "Split: " + this.getSplitValue() : "Unknown"));
  }
  public String toCsv() {
	String rtnString = CalendarHelper.getIsoDate(this.getTransactionDate());
	if (this.getRecordType() == 'D') {
	  rtnString += ",Div," + Float.toString(this.getDividend());
	}
	else 
	  if (this.getRecordType() == 'S') {
	    rtnString += ",Split," + this.getSplitValue();
	  }
	  else rtnString += ",Unknown";
    return rtnString;           
  }
  public static String csvHdr() {
	    return "Date,Type," +
	           "Value";           
  }
}