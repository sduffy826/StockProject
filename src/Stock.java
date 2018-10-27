import java.util.*;
import java.io.*;
import java.lang.Math.*;

public class Stock {
  // This represents a stock, it has attributes and a list of
  // stock date/prices and stock dividend date/amounts
	
  StockAttributes stockAttributes;
  List<StockDateAndPrice> stockArrayList = new ArrayList<StockDateAndPrice>(1000);
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
	  lastCompValue = 33;
	  for (StockDateAndPrice sdp : stockArrayList) {
	    compValue = CalendarHelper.getDaysDifference(theCal,sdp.getStockDate());
		  //compValue = sdp.getStockDate().compareTo(theCal);
		  if ((holdSdp == null) || (compValue == 0)) {
		    holdSdp = sdp;  // Match or first record looked at
		    if (compValue == 0) break;
		    else lastCompValue = compValue;  // first time
		  }
		  else 
		    if (Math.abs(lastCompValue) >= Math.abs(compValue)) {  // Last delta was larger
		      holdSdp = sdp;
		      if ((lastCompValue*compValue) < 0) break; // Switched signs we r done		        
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
  
  // Write stock symbol, and close for each date
  public void dump2Bw(BufferedWriter _bw, Calendar _start, Calendar _end, boolean _incWeekends,
                      boolean _monthIncrements, boolean _writeHeader) throws Exception {
    java.util.Calendar tempCal;
    StockDateAndPrice stdp;
    tempCal = _start;
    
    String header = "Dates:,,";
    String aLine = "Symbol:,," + this.getTicker();
        
    while (tempCal.compareTo(_end) < 0) {
      if (_monthIncrements) 
        stdp = this.getClosestDate(tempCal);
      else
        stdp = this.hasDate(tempCal);
      
      if (_incWeekends || CalendarHelper.getDayOfWeek(tempCal).charAt(0)!='S') {
        aLine += ",";        
        //_bw.write(",");
        if (stdp != null) {        
          aLine += Float.toString(stdp.getClose());        
        }
        aLine += ",";
        if (_writeHeader) header += "," + CalendarHelper.getIsoDate(tempCal) + ",";
      }
      if (_monthIncrements)
        tempCal = CalendarHelper.getNextEnd(tempCal);
      else
        tempCal = CalendarHelper.getNextDay(tempCal);
    }
         
    stdp = this.getClosestDate(_end);
    if (stdp != null) {
      if (_writeHeader) header += "," + CalendarHelper.getIsoDate(stdp.getStockDate());
      aLine += "," + Float.toString(stdp.getClose());
    }
    if (_writeHeader) {
      _bw.write(header);
      _bw.newLine();
    }
    _bw.write(aLine);
    _bw.newLine();    
  }
   
  
  public void calcValue() {
    float numShares, theClose, divAmt, divValue, splitAmt, costBasis;
    java.util.Calendar theDate, eDate;
    StockDateAndDividendAndSplit sdadas;
    StockDateAndPrice stdp;  
    String spltStr;
    int numRecs = 0;
    int compValue;
    boolean writeValue = true;
    numShares = this.getStockAttributes().getShares();
    System.out.println("Symbol:,,"+this.getTicker());
    System.out.println("Name:,," + this.getStockAttributes().getName());
    System.out.println("Shares:,,"+Float.toString(numShares)); 
    theDate = this.getStockAttributes().getStartDate();
    eDate   = this.getStockAttributes().getEndDate();
    stdp = this.getClosestDate(theDate); 
    if ( stdp != null ) {
      costBasis = stdp.getClose() * numShares;
    }
    else costBasis = (float)0.0;
    while ((compValue = theDate.compareTo(eDate)) <= 0) {
      sdadas = hasDividendDate(theDate);
      if (compValue == 0) writeValue = true;  // Want last value
      if ((sdadas != null) || writeValue) {
        stdp = this.getClosestDate(theDate); 
        if (stdp == null) {
          System.out.println("** have dividend but no price: " + theDate.toString()); 
        }
        else {
          numRecs++;
          theClose = stdp.getClose();
          if (theClose == 0.0) theClose = (float)1.0;
          if (sdadas != null) {
            divAmt   = sdadas.getDividend();
            spltStr = sdadas.getSplitValue();
            splitAmt = sdadas.getSplitFactor();
          }
          else {
            divAmt = (float)0.0;
            spltStr = "";
            splitAmt = (float)1.0;
          }
          
          numShares *= splitAmt;
          divValue = numShares * divAmt;
          numShares += (divValue)/theClose;
          costBasis += divValue;
          System.out.println("Date:," + CalendarHelper.getIsoDate(theDate) +
                             ",Close:," + Float.toString(theClose) +
                             ",DivAmt:," + Float.toString(divAmt) +
                             ",DivValue:," + Float.toString(divValue) +
                             ",Split Factor:," + Float.toString(splitAmt) +
                             ",Shares:," + numShares + 
                             ",Value:," + Float.toString(numShares*theClose) +
                             ",CostBasis:," + costBasis);
        }
      }
      theDate = CalendarHelper.getNextDay(theDate);
      writeValue = false;
    }        
    System.out.println("Split/Div's found: " + Integer.toString(numRecs));
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
	      bw.write(stdd.toCsv()); bw.newLine();    		      
	    }    		    
	   
	    bw.flush();
	    bw.close();
	  }
	  catch (Exception e) {
	    e.printStackTrace();
	  }
  }  
}