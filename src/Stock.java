import java.util.*;
import java.io.*;

public class Stock {
  // This represents a stock, it has attributes and a list of
  // stock date/prices and stock dividend date/amounts
	
  StockAttributes stockAttributes;
  List<StockDateAndPrice> stockArrayList = new ArrayList<StockDateAndPrice>(10);
  List<StockDateAndDividendAndSplit> stockDividendArrayList = new ArrayList<StockDateAndDividendAndSplit>(10);
  
  public Stock(String _ticker, float _shares, Calendar _sDate, Calendar _eDate) {
	  this.stockAttributes = new StockAttributes(_ticker, "Not Found", _shares, _sDate, _eDate);
    this.stockArrayList.clear();
    this.stockDividendArrayList.clear();
  }
  
  public StockAttributes getStockAttributes() {
	  return stockAttributes;
  }
  public String getTicker() {
    return stockAttributes.getTicker();
  }
  public int getStockListSize() {
    return stockArrayList.size();
  }
  public int getDividendListSize() {
	  return stockDividendArrayList.size();	  
  }
  public List<StockDateAndPrice> getStockInfo() {
	  return stockArrayList;
  }
  public List<StockDateAndDividendAndSplit> getStockDividendInfo() {
	  return stockDividendArrayList;
  }
  
  public boolean putInStockList(String _stockDate, float _open, float _high,
                                float _low, float _close, float _adjClose,
                                long _volume) {
    boolean success = true;
    StockDateAndPrice sdp = this.hasDate(_stockDate);
    if (sdp == null ) {
      sdp = new StockDateAndPrice(_stockDate, _open, _high, _low, _close,
                               _adjClose, _volume);
      stockArrayList.add(sdp);     
    }
    
    return success;
  }
  
  public boolean putInStockList(String _stockDate, 
                           String _open, String _high, String _low, 
                           String _close, String _adjClose, String _volume) {
    boolean isGood;
    float _o, _h, _l, _c, _a;
    long _v;
    _o = _h = _l = _c = _a = 0;
    _v = 0;
    isGood = true;
    try {
      _o = Float.valueOf(_open).floatValue();
      _h = Float.valueOf(_high).floatValue();
      _l = Float.valueOf(_low).floatValue();
      _c = Float.valueOf(_close).floatValue();
      _a = Float.valueOf(_adjClose).floatValue();
      _v = Long.valueOf(_volume).longValue();
    }
    catch(Exception e) {
      isGood = false;
    }
    if (isGood == true) 
      isGood = this.putInStockList(_stockDate, _o, _h, _l, _c, _a, _v);
    return isGood;
  }  

  public StockDateAndPrice hasDate(String _theDate) {
    Calendar _chkDate = CalendarHelper.getCal(_theDate);
    return this.hasDate(_chkDate);    
  }
  
  public StockDateAndPrice hasDate(Calendar theCal) {
	  StockDateAndPrice holdSdp = null;
	  for (StockDateAndPrice sdp : stockArrayList) {
		  if (sdp.getStockDate().compareTo(theCal) == 0) {
			  holdSdp = sdp;
			  break;
		  }
	  }
	  return holdSdp;
  }
  
  public StockDateAndDividendAndSplit hasDividendDate(String _theDate) {
    Calendar _chkDate = CalendarHelper.getCal(_theDate);
    return this.hasDividendDate(_chkDate);    
  }
	  
  public StockDateAndDividendAndSplit hasDividendDate(Calendar theCal) {
  	StockDateAndDividendAndSplit holdSdd = null;
    for (StockDateAndDividendAndSplit sdd : stockDividendArrayList) {
	    if (sdd.getTransactionDate().compareTo(theCal) == 0) {
	    	holdSdd = sdd;
		   break;
	    }
    }
	  return holdSdd;
  }

  public StockDateAndDividendAndSplit getAtIndex(int i) {
    StockDateAndDividendAndSplit holdSdd = null;
    if (i < stockDividendArrayList.size()) {
      holdSdd = stockDividendArrayList.get(i);
    }
    return holdSdd;
  }
  
  public StockDateAndPrice getClosestDate(Calendar theCal) {
	  StockDateAndPrice holdSdp = null, lastSdp = null;
	  int compValue, lastCompValue;
	  lastCompValue = 0;
	  for (StockDateAndPrice sdp : stockArrayList) {
		  compValue = sdp.getStockDate().compareTo(theCal);
		  if ((holdSdp == null) || (compValue == 0)) {
		    holdSdp = sdp;  // Match or first record looked at
		    if (compValue == 0) break;
		  }
		  else if (lastCompValue != compValue) { // Switched < >
			  if (compValue > 0)     // current value is greater than last one
				  holdSdp = lastSdp;  // use last value.
			  else
			    holdSdp = sdp;       // New value is less
			  break;
		  }
		  lastSdp = sdp;
		  lastCompValue = compValue;		  
	  }
	  return holdSdp;
  }
  
  public String dumpStockDates() {
	  String theStr = getTicker();
	  for (StockDateAndPrice sdp : stockArrayList) {
		  theStr += "," + CalendarHelper.getIsoDate(sdp.getStockDate());
	  }
	  return theStr;
  }
    
  
  public String dumpDividendDates() {
	  String theStr = getTicker();
	  for (StockDateAndDividendAndSplit sdd : stockDividendArrayList) {
		  theStr += "," + CalendarHelper.getIsoDate(sdd.getTransactionDate());
	  }
	  return theStr;
  }
    
  public String toString() {
    String theStr = stockAttributes.toString() +
        		        " NumStockDates: " + Integer.toString(stockArrayList.size()) +
                    " NumDivDates: " + Integer.toString(stockDividendArrayList.size());    		        
    		        
    return theStr;        
  }
  
  public void dump2File(String _subDir) {
    String fname = "/home/sduffy/stocks/" +
                   (_subDir.length() > 0 ? _subDir+"/" : "") +
                   this.getTicker() + ".csv";
    java.util.Calendar tempCal;
    StockDateAndPrice stdp;
    StockDateAndDividendAndSplit stdd;
    // JIC running on windows 
    fname = fname.replace('/',File.separatorChar);    		 
	  try {
	    FileWriter fw = new FileWriter(fname, false);
	    BufferedWriter bw = new BufferedWriter(fw);
	    
	    // Write out header, then the dates
	    bw.write("Symbol:,,"+this.getTicker()); bw.newLine();
	    bw.write("Name:,," + this.getStockAttributes().getName()); bw.newLine();
	    bw.write("Shares:,,"+Float.toString(this.getStockAttributes().getShares())); 
	    bw.newLine(); bw.newLine();
	    bw.write(StockDateAndPrice.csvHdrClose()); bw.newLine();
	  
	    //String dumString = stockListing.dumpStockDates();
	    //String dumString2 = stockListing.dumpDividendDates();
	    //System.out.println("Div Dates: " + dumString2);
	  
	    tempCal = this.getStockAttributes().getStartDate();
	    int theVal = this.getTicker().compareTo("MO");
	    System.out.println("******* " + this.getTicker() + " val: " + Integer.toString(theVal));
	    if ( theVal == 0) {
	      System.out.println(CalendarHelper.getIsoDate(tempCal) + " end: " +
	          CalendarHelper.getIsoDate(this.getStockAttributes().getEndDate()));
	    }
	    while (tempCal.compareTo(this.getStockAttributes().getEndDate()) < 0) {
	      stdp = this.getClosestDate(tempCal);
	      if (stdp != null) {
	        String theStr = stdp.toCsvClose();
	        bw.write(theStr); bw.newLine();
	      }
	      tempCal = CalendarHelper.getNextEnd(tempCal);
	    }
	    stdp = this.getClosestDate(this.getStockAttributes().getEndDate());
	    if (stdp != null)
	      bw.write(stdp.toCsvClose()); bw.newLine();
	  	    
	    // Output dividends
	    bw.newLine();
	    bw.write(StockDateAndDividendAndSplit.csvHdr()); bw.newLine();
	    for (int jdx = this.getDividendListSize(); jdx > 0; jdx--) {
	      stdd = this.getAtIndex(jdx-1);
	      	      
	      bw.write(stdd.toCsv()); 
	      
	      // Write out the stock date/price for the closest to the date
	      // the divident was paid
	      stdp = this.getClosestDate(stdd.getTransactionDate());
	      if (stdp != null)	bw.write(",,"+stdp.toCsvClose());
	      
	      bw.newLine();    		      
	    }    		    
	   
	    bw.flush();
	    bw.close();
	  }
	  catch (Exception e) {
	    e.printStackTrace();
	  }
  }
  // Return stock info for a give date, return Ticker,,Name,,Shares,,Date,,ClosingPrice
  public String getStockInfoForDate(Calendar theDate) {
    
    String rtnStr = this.getTicker() + ",," + this.getStockAttributes().getName() + ",," +
                    Float.toString(this.getStockAttributes().getShares()) + ",,";
    
    StockDateAndPrice stdp = this.getClosestDate(theDate);    
    if (stdp != null) {
      rtnStr += stdp.toCsvClose();
    }
    return rtnStr;
  }
  
  public String getInfoForFirstEntry() {
    int size = this.getStockListSize();
    if (size > 0) {
      Calendar firstDate = stockArrayList.get(0).getStockDate();
      return getStockInfoForDate(firstDate);
    }
    else
      return "";
  }
  
  public String getInfoForLastEntry() {
    int size = this.getStockListSize();
    if (size > 0) {
      Calendar lastDate = stockArrayList.get(size-1).getStockDate();
      return getStockInfoForDate(lastDate);
    }
    else
      return "";
  }  
}