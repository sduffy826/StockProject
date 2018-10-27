import java.io.*;
import java.util.*;

// import com.spd.stock.*;

// This program creates csv files with the historical prices for 
// given stocks (includes dividends).  The arguments are:
//    String filename - represents name of the file containing the stocks
//                      you want to pull, each line in the file should
//                      contain the stock symbol, and the number of
//                      shares you have.
//    String startDate (optional) starting date to pull (formats avail below)
//    String endDate (optional) ending date to pull for (see formats below)
//
//    dateFormats (string) can be one of following: mm/dd/yyyy, mm-dd-yyyy, yyyymmdd, yyyy-mm-dd
// 
public class YahooStockPull {
  public static void main(String[] args) throws Exception {
    Calendar defaultStartCal = CalendarHelper.getCal(2011,10,18);
    Calendar defaultEndCal   = new GregorianCalendar();
    
    PullStockInfoFromYahoo pullStockInfoFromYahoo = PullStockInfoFromYahoo.getInstance();
    StockSymbolFactory stockSymbolFactory = StockSymbolFactory.getInstance();
    
    Map<String, Stock> stockList = new HashMap<String, Stock>(20);
    List<String> symbolList = new ArrayList<String>(20);
    String subDir = "";
    if (args.length == 0) {
      System.out.println("You must pass arguments into the program\nSee prologue");
    }
    else {      	
      // If user specified args after filename, they should be the start
      // and end date to pull for (dates should be iso format)
      if (args.length > 1) defaultStartCal = CalendarHelper.getCal(args[1]);
      if (args.length > 2) defaultEndCal = CalendarHelper.getCal(args[2]);    	    	
      
      processFile(args[0], defaultStartCal, defaultEndCal, symbolList, stockList);
      if ((args[0].substring(0,3).compareToIgnoreCase("mom") == 0) ||
          (args[0].substring(0,3).compareToIgnoreCase("dad") == 0) ||
          (args[0].substring(0,3).compareToIgnoreCase("all") == 0)) {
        subDir = args[0].substring(0,3).toLowerCase();
      }
    }
    // Call method that will populate the list of names
    stockSymbolFactory.processSymbols(symbolList);
    System.out.println(stockSymbolFactory.toString());
    dumpSymList(symbolList);
    buildListOfNames(stockList, stockSymbolFactory, pullStockInfoFromYahoo);
    dumpStocks2File(symbolList, stockList,subDir);
    dumpStocks2OneFile(symbolList, stockList, subDir);
    //dumpStocks2Console(stockList);
    System.out.println("All Done");
  }
  
  private static void processFile(String _fileIn,
		                              Calendar _startDate,
		                              Calendar _endDate,
		                              List<String> _symbolList,
		                              Map<String, Stock> _stockList) {
    String inLine;
	  float numShares;
	  Stock aStock;
	  Calendar date2Start, date2End;
	  try {
	    BufferedReader in = new BufferedReader(new FileReader(_fileIn));
	    while ((inLine = in.readLine()) != null) {
	      String theArray[] = inLine.trim().split("[ \\t]+");  // One or more spaces
	      if (theArray.length < 2) {
	        System.out.println("Invalid record for parsing, bypassed: " + inLine);
	      }
	      else {
	        theArray[1] = theArray[1].replaceAll("[,]","");
	        System.out.println("Processing: " + theArray[0] + " shares: " + theArray[1]);
	    		
	        _symbolList.add(theArray[0].toUpperCase());
	        numShares = 0;
	        if (theArray.length > 1)
	          numShares = Float.parseFloat(theArray[1]);
	        if (theArray.length > 2) 
	          date2Start = CalendarHelper.getCal(theArray[2]);
	        else
	          date2Start = CalendarHelper.getCal(_startDate);
	      
	        if (theArray.length > 3) 
	          date2End = CalendarHelper.getCal(theArray[3]);
	        else
		        date2End = CalendarHelper.getCal(_endDate);
	      
	        aStock = new Stock(theArray[0].toUpperCase(),numShares, date2Start, date2End);
	        _stockList.put(theArray[0].toUpperCase(),aStock);
	      }  // end of the else
	    }  // end of while
	    in.close();
	 }  // end of try block
	  catch (FileNotFoundException e) {
	    System.out.println("File not found " + e);	
  	}
	  catch (IOException e) {
	    e.printStackTrace();
	  }
  }
  
  private static void dumpSymList(List<String> theList) {
	  System.out.println("---> dumpSymList in YahooStockPull.java <---");
	  for (int i = 0; i < theList.size(); i++) {
	    System.out.println("Pos: " + Integer.toString(i) + " " + theList.get(i));
	  }
  }
  
  private static void buildListOfNames(Map<String, Stock> _stockList,
		                               StockSymbolFactory _stsn,
		                               PullStockInfoFromYahoo _stdr) { 
    // This method loops thru the stockList, it puts the name in for 
    // each symbol, then calls the refreshStock method on it, that one
    // will get the stock date/price and the date/dividend values.
	  
    String theName;
    System.out.println("--------------------------------------");	  
	  
    Stock aStock;
    for (Map.Entry<String, Stock> item : _stockList.entrySet()) {
      String symbol = item.getKey();
      aStock = item.getValue();	 
      theName = _stsn.getStockName4Ticker(symbol);
      System.out.println("Symbol: " + symbol + " name: " + theName);
      aStock.getStockAttributes().setName(_stsn.getStockName4Ticker(symbol));
      _stdr.refreshStock(aStock);
	  }      
	  return;
  }
  
  private static void dumpStocks2File(List<String> theList,
                                      Map<String, Stock> _stockList, String _subDir) { 
    // We use theList to determine the order we want to output too, it contains the order
    // of the data in the file passed in.
    Stock aStock;
    for (int i = 0; i < theList.size(); i++) {
      aStock = _stockList.get(theList.get(i));
      System.out.println("*-*-*- " + aStock.getTicker());
      aStock.dump2File(_subDir);
    }         
    return;
  }
  
	private static void dumpStocks2OneFile(List<String> theList,
			Map<String, Stock> _stockList, String _subDir) {
	  java.util.Calendar tempCal = Calendar.getInstance();
	  String fname = "/home/sduffy/stocks/" +
        (_subDir.length() > 0 ? _subDir+"/" : "") +
        "allStock" + CalendarHelper.getIsoDate(tempCal) + ".csv";
	  try {
	    FileWriter fw = new FileWriter(fname, false);
      BufferedWriter bw = new BufferedWriter(fw);
          
	  	// We use theList to determine the order we want - same as order given to us
		  Stock aStock;
		  for (int i = 0; i < theList.size(); i++) {
			  aStock = _stockList.get(theList.get(i));
			  bw.write(aStock.getInfoForLastEntry());
			  bw.newLine();
		  }
		  
		  bw.flush();
		  bw.close();
	  }
    catch (Exception e) {
      e.printStackTrace();
    }
		return;
	}
    
  
  private static void dumpStocks2Console(Map<String, Stock> _stockList) { 
    for (Map.Entry<String, Stock> item : _stockList.entrySet()) {
      System.out.println(item.getValue().toString());	  
    }      
    return;
  }  
}
//       
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    StockDateAndPrice sDandP;
//    StockDateAndDividend sDandD;
//    
//       
//    	for (int i = 0; i < symList.size(); i++) {
//    		String theSym = symList.get(i);
//    		stockListing = stdr.getStockListForSymbol(theSym);    	
//    	    if (stockListing != null ) {
//    		  String fname = "/home/sduffy/stocks/" + theSym + ".csv";
//    		  // JIC running on windows 
//    		  fname = fname.replace('/',File.separatorChar);    		 
//    		  try {
//    		    FileWriter fw = new FileWriter(fname, false);
//    		    BufferedWriter bw = new BufferedWriter(fw);
//    		    
//    		    // Write out header, then the dates
//    		    bw.write("Symbol:,,"+theSym); bw.newLine(); 
//    		    bw.write("Shares:,,"+theArray[1]); bw.newLine(); bw.newLine();
//    		    bw.write(StockDateAndPrice.csvHdrClose()); bw.newLine();
//    		  
//    		    //String dumString = stockListing.dumpStockDates();
//    		    //String dumString2 = stockListing.dumpDividendDates();
//    		    //System.out.println("Div Dates: " + dumString2);
//    		    tempCal = sCal;
//    		    while (tempCal.compareTo(eCal) < 0) {
//    			  sDandP        = stockListing.getClosestDate(tempCal);
//    			  String theStr = sDandP.toCsvClose();
//    			  bw.write(theStr); bw.newLine();
//    			  tempCal = CalendarHelper.getNextEnd(tempCal);    			
//    	        }
//    		    sDandP = stockListing.getClosestDate(eCal);
//    		    bw.write(sDandP.toCsvClose()); bw.newLine();
//    		    
//    		    // Output dividends
//    		    bw.write(StockDateAndDividend.csvHdr()); bw.newLine();
//    		    for (int jdx = stockListing.getDividendListSize(); jdx > 0; jdx--) {
//    		      sDandD = stockListing.getAtIndex(jdx-1);
//    		      bw.write(sDandD.toCsv()); bw.newLine();    		      
//    		    }    		    
//    		    
//    		    bw.flush();
//    		    bw.close();
//    		  }
//    		  catch (Exception e) {
//    			e.printStackTrace();
//    		  }
//    		} // end for stocklisting != null
//    	}
//    	
//    	
//    	
//    	
//    	
//    	
//    	
//    	
//    	
    	
    	
 //       stdr.toString();
  //      System.out.println("hi");
        // TODO Auto-generated method stub
        //    System.out.println(s.toString());
 
