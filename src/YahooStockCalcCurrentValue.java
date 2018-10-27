import java.io.*;
import java.util.*;

public class YahooStockCalcCurrentValue {
  public static void main(String[] args) throws Exception {
    Calendar defaultStartCal = CalendarHelper.getCal(1990,05,14);
    Calendar defaultEndCal   = new GregorianCalendar();
    
    PullStockInfoFromYahoo pullStockInfoFromYahoo = PullStockInfoFromYahoo.getInstance();
    StockSymbolFactory stockSymbolFactory = StockSymbolFactory.getInstance();
    
    Map<String, Stock> stockList = new HashMap<String, Stock>(20);
    List<Stock> stockArrayList = new ArrayList<Stock>(20);
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
      
      procFile(args[0], defaultStartCal, defaultEndCal, symbolList, stockList, stockArrayList);
      if ((args[0].substring(0,3).compareToIgnoreCase("mom") == 0) ||
          (args[0].substring(0,3).compareToIgnoreCase("dad") == 0)) {
        subDir = args[0].substring(0,3).toLowerCase();
      }
    }
    // Call method that will populate the list of names
    stockSymbolFactory.processSymbols(symbolList);
    System.out.println(stockSymbolFactory.toString());
    dumpSymList(symbolList);
    
    processList(stockArrayList, stockSymbolFactory, pullStockInfoFromYahoo);
    dumpStockArrayList(stockArrayList);
    
    /*
    buildListOfNames(stockList, stockSymbolFactory, pullStockInfoFromYahoo);
    dumpStocks2File(symbolList, stockList,subDir);
    
    dumpStocks2OneFile(symbolList, stockList, defaultStartCal, defaultEndCal, false, true);
    */
    //dumpStocks2Console(stockList);
    System.out.println("All Done");
  }
  
  private static void procFile(String _fileIn,
		                           Calendar _startDate,
		                           Calendar _endDate,
		                           List<String> _symbolList,
		                           Map<String, Stock> _stockMap,
		                           List<Stock> _stockArrayList) {
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
	        _stockMap.put(theArray[0].toUpperCase(),aStock);
	        
	        _stockArrayList.add(aStock);
	      }  // end of the else
	    }  // end of while
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
  
  private static void dumpStockArrayList(List<Stock> _stockArrayList) {    
    String theName;
    System.out.println("--------------------------------------");   
    
    for (int i = 0; i < _stockArrayList.size(); i++) {
      _stockArrayList.get(i).calcValue();
    }          
    return;
  }  
  
  private static void processList(List<Stock> _stockArrayList,
                                  StockSymbolFactory _stsn,
                                  PullStockInfoFromYahoo _stdr) {
    Stock aStock;
    String theName;
    for (int i = 0; i < _stockArrayList.size(); i++) {
      aStock  = _stockArrayList.get(i);
      theName = _stsn.getStockName4Ticker(aStock.getTicker());
      aStock.getStockAttributes().setName(theName);
      _stdr.refreshStock(aStock);
    }
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
  
  private static void dumpStocks2OneFile(List<String> theList, Map<String, Stock> _stockList,
                                         Calendar startCal, Calendar endCal, boolean incWeekends, boolean monthReport) {
    // We use theList to determine the order we want to output too, it contains the order
    // of the data in the file passed in.
    String fileName  = "/home/sduffy/stocks/allOfEm.csv";
    fileName         = fileName.replace('/',File.separatorChar);
    boolean writeHdr = true;
    
    try {
      FileWriter _fw = new FileWriter(fileName, false);
      BufferedWriter _bw = new BufferedWriter(_fw);
      
      Stock aStock; 
      for (int i = 0; i < theList.size(); i++) {
        aStock = _stockList.get(theList.get(i));
        aStock.dump2Bw(_bw,startCal,endCal,incWeekends,monthReport,writeHdr);
        writeHdr = false;  // Only true for first call
      }   
      _bw.flush();
      _bw.close();
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
 
