import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
public class StockSymbolFactory {
    private static final boolean DEBUGIT = true;
    private static final StockSymbolFactory stockSymbolName = new StockSymbolFactory();
    private static Map<String, StockName> stockSymbolNameList = new HashMap<String, StockName>();
    private StockSymbolFactory() {}  // Can't instantiate
    public static StockSymbolFactory getInstance() {
      return stockSymbolName;
    }
    
    public String getStockName4Ticker(String symbol) {
      StockName stockName;
      if (stockSymbolNameList.containsKey(symbol))
        stockName = stockSymbolNameList.get(symbol);
      else {
    	  // Not found call refresh list and search again
    	  this.refreshList(symbol);
    	  if (stockSymbolNameList.containsKey(symbol))
    	    stockName = stockSymbolNameList.get(symbol);
    	  else
          stockName = new StockName(symbol,"** NOT FOUND **");
      }       
      return stockName.getName();
    }
    
    public void processSymbols(String theArray[]) {
    	String allOfEm = "";
    	for (String theone : theArray) {
    		allOfEm += "+" + theone;
    	}
    	this.refreshList(allOfEm.substring(1));
    }
    
    public void processSymbols(List<String> aList) {
      String allOfEm = "";
      for (int i = 0; i < aList.size(); i++) {
    	  allOfEm += "+" + aList.get(i);
      }
      this.refreshList(allOfEm.substring(1));
    }
    
    private synchronized void refreshList(String _symList) {
      try {    	  
         // See http://www.gummy-stuff.org/Yahoo-data.htm
         // for the tags to get elements out of yahoo finance
    	   StockName stockName;
         String urlStr = new String("http://finance.yahoo.com/d/quotes.csv?s=");
         urlStr = urlStr + _symList + "&f=sn"; // s is symbol, n is to get name
         if (DEBUGIT) System.out.println("Sending: " + urlStr);
         URL yahooFin      = new URL(urlStr);
         URLConnection yc  = yahooFin.openConnection();
         BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
         String inputLine;
         if (DEBUGIT) System.out.println("Reading response");
         while ((inputLine = in.readLine()) != null) {
           String[] yahooStockInfo = inputLine.split(",");
           if (yahooStockInfo.length > 1) {
             yahooStockInfo[0] = yahooStockInfo[0].replaceAll("[\"]","");
             yahooStockInfo[1] = yahooStockInfo[1].replaceAll("[\"]","");
             if (stockSymbolNameList.containsKey(yahooStockInfo[0]) == false) {
             	 stockName = new StockName(yahooStockInfo[0], yahooStockInfo[1]);
               stockSymbolNameList.put(yahooStockInfo[0], stockName);
             }
           } // of of if on checking elements read
         } // End of while
      }  	
      catch (Exception e) {
        System.out.println("Exception raised: ");
        e.printStackTrace();
      }
      return;
    }    
        
    public String toString() {
      StockName stockName;
      String allData = "";
      for (Map.Entry<String, StockName> item : stockSymbolNameList.entrySet()) {
        stockName = item.getValue(); 
    	  allData = allData + "Symbol: " + stockName.toString() + '\n';        
      }      
      return allData;
    }    
}