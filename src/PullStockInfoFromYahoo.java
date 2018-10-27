import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class PullStockInfoFromYahoo {
    private static final boolean DEBUGIT = true;
    private static final PullStockInfoFromYahoo stockTickerDateRange = new PullStockInfoFromYahoo();
    
    private PullStockInfoFromYahoo() {}
    public static PullStockInfoFromYahoo getInstance() {
      return stockTickerDateRange;
    }
    
    public synchronized void refreshStock(Stock _aStock) {
    	System.out.println("In refreshStock with: " + _aStock.getTicker());
    	this.refreshStockValues(_aStock.getTicker(),
    			                _aStock.getStockInfo(),
    			                _aStock.getStockAttributes().getStartDate(),
    			                _aStock.getStockAttributes().getEndDate());
    	this.refreshDividendInfo(_aStock.getTicker(),
    			                 _aStock.getStockDividendInfo(),
    			                 _aStock.getStockAttributes().getStartDate(),
       			                 _aStock.getStockAttributes().getEndDate());
    }
    
    private synchronized void refreshStockValues(String _symbol,
    		                                     List<StockDateAndPrice> _list,
    		                                     Calendar _startCal,
    		                                     Calendar _endCal) {                 
      try {
    	System.out.println("In refreshStockValues with: " + _symbol);  
        String urlStr = new String("http://ichart.finance.yahoo.com/table.csv?s=");
        urlStr = urlStr + _symbol + 
                 "&a=" + Integer.toString(CalendarHelper.getMonthOffSet(_startCal)) +
                 "&b=" + Integer.toString(CalendarHelper.getDay(_startCal)) +
                 "&c=" + Integer.toString(CalendarHelper.getYear(_startCal)) +
                 "&d=" + Integer.toString(CalendarHelper.getMonthOffSet(_endCal)) +
                 "&e=" + Integer.toString(CalendarHelper.getDay(_endCal)) +
                 "&f=" + Integer.toString(CalendarHelper.getYear(_endCal)) +
                 "&g=d";
        if (DEBUGIT) System.out.println("Sending: " + urlStr);
        URL yahooFin = new URL(urlStr);
               
        URLConnection yc = yahooFin.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
           
        String inputLine;
        if (DEBUGIT) System.out.println("Reading response");
        while ((inputLine = in.readLine()) != null) {
          String[] yahooStockInfo = inputLine.split(",");
          try {
          	float dummy = Float.parseFloat(yahooStockInfo[1]);
            _list.add(new StockDateAndPrice(yahooStockInfo[0],   // Date
        	                                yahooStockInfo[1],   // Open
                                            yahooStockInfo[2],   // High
                                            yahooStockInfo[3],   // Low
                                            yahooStockInfo[4],   // Close
                                            yahooStockInfo[6],   // AdjClose
                                            yahooStockInfo[5])); // Volume
          }
          catch (NumberFormatException e) { /* No action required */ }
          catch (Exception e) {
        	  System.out.println("Ignoring line: " + inputLine);
        	  e.printStackTrace();
          }          
        }        
        in.close();
        if (DEBUGIT) System.out.println("Done reading response");
      } 
      catch (Exception ex) {
        // log.error("Unable to get stockinfo for: " + symbol + ex);
        System.out.println("Unable to get stock information for: "+ _symbol + ex);        
      }	
    }
    
    private synchronized void refreshDividendValues(String _symbol,
                                      List<StockDateAndDividendAndSplit> _list,
                                      Calendar _startCal,
                                      Calendar _endCal) { 
      try {
    	System.out.println("In refreshDividendValues with: " + _symbol);
  	    String urlStr = new String("http://table.finance.yahoo.com/table.csv?s=");
        urlStr = urlStr + _symbol + 
            "&a=" + Integer.toString(CalendarHelper.getMonthOffSet(_startCal)) +
            "&b=" + Integer.toString(CalendarHelper.getDay(_startCal)) +
            "&c=" + Integer.toString(CalendarHelper.getYear(_startCal)) +
            "&d=" + Integer.toString(CalendarHelper.getMonthOffSet(_endCal)) +
            "&e=" + Integer.toString(CalendarHelper.getDay(_endCal)) +
            "&f=" + Integer.toString(CalendarHelper.getYear(_endCal)) +
            "&g=v&ignored=.csv";
        if (DEBUGIT) System.out.println("Sending: " + urlStr);
        URL yahooFin = new URL(urlStr);
        URLConnection yc = yahooFin.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        if (DEBUGIT) System.out.println("Reading response");
        while ((inputLine = in.readLine()) != null) {
          String[] yahooStockInfo = inputLine.split(",");
          try {
          	float dummy = Float.parseFloat(yahooStockInfo[1]);
            _list.add(new StockDateAndDividendAndSplit(yahooStockInfo[0],   // Date
            		  'D',"", yahooStockInfo[1])); // Indicator, SplitAmt, Dividend
          }
          catch (NumberFormatException e) { /* Do nothing */ }
          catch (Exception e) {
      	    System.out.println("Ignoring dividend response: " + inputLine);
      	    e.printStackTrace();
          } 
        }
        in.close();
        if (DEBUGIT) System.out.println("Done reading response");
      } 
      catch (Exception ex) {
        // log.error("Unable to get stockinfo for: " + symbol + ex);
        System.out.println("Unable to get stock information for: "+ _symbol + ex);      
      }
    }    
      
    private synchronized void refreshDividendInfo(String _symbol,
    		List<StockDateAndDividendAndSplit> _list,
    		Calendar _startCal,
    		Calendar _endCal) { 
      try {
        String urlStr = new String("http://ichart.finance.yahoo.com/x?s=");
        urlStr += _symbol + 
                  "&a=" + Integer.toString(CalendarHelper.getMonthOffSet(_startCal)) +
                  "&b=" + Integer.toString(CalendarHelper.getDay(_startCal)) +
                  "&c=" + Integer.toString(CalendarHelper.getYear(_startCal)) +
                  "&d=" + Integer.toString(CalendarHelper.getMonthOffSet(_endCal)) +
                  "&e=" + Integer.toString(CalendarHelper.getDay(_endCal)) +
                  "&f=" + Integer.toString(CalendarHelper.getYear(_endCal)) +
                  "&g=v&y=0&z=30000&ignored=.csv";
        if (DEBUGIT) System.out.println("Sending: " + urlStr);
        URL yahooFin = new URL(urlStr);
        URLConnection yc = yahooFin.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        if (DEBUGIT) System.out.println("Reading response");
        while ((inputLine = in.readLine()) != null) {
          String[] yahooStockInfo = inputLine.split(",");
          if (yahooStockInfo.length > 2) {
        	_list.add(new StockDateAndDividendAndSplit(
        			        yahooStockInfo[0].trim(),   // Tag
                            yahooStockInfo[1].trim(),   // Date
                            yahooStockInfo[2])); // Value
          }          
        }
        in.close();
        if (DEBUGIT) System.out.println("Done reading response");
      } 
      catch (Exception ex) {
        System.out.println("Unable to get dividend information for: "+ _symbol + ex);      
      }
      return;
    }        
}