import java.util.*;
public class CalendarHelper {
  public static Calendar getCal(int year, int month, int day) {
    return new GregorianCalendar(year, month-1, day, 0, 0, 0); // Time 0:0:0
  }
  // Returns another object with same date
  public static Calendar getCal(Calendar _cal) {
    return new GregorianCalendar(_cal.get(Calendar.YEAR),
    		                     _cal.get(Calendar.MONTH),
    		                     _cal.get(Calendar.DAY_OF_MONTH));
  }
  public static Calendar getTodayCal() {
	  return new GregorianCalendar();
  }
  public static Calendar getCal(String _strDate) {
    String theMo, theDay, theYr;
    int iMo, iDay, iYr;
      
    char theChar = _strDate.charAt(2);
    if ( theChar == '/' || theChar == '-' ) {
      theMo  = _strDate.substring(0,2);  // Substr does not include char at ending position
      theDay = _strDate.substring(3,5);  // So this returns pos 3 and 4 only 
      theYr  = _strDate.substring(6);
    }
    else if (_strDate.length() == 8) {
      theMo  = _strDate.substring(4,6);
      theDay = _strDate.substring(6,8);
      theYr  = _strDate.substring(0,4); 	
    }	
    else {
      theMo  = _strDate.substring(5,7);
      theDay = _strDate.substring(8);
      theYr  = _strDate.substring(0,4);
    }
    try {
      iMo  = Integer.parseInt(theMo);
      iDay = Integer.parseInt(theDay);
      iYr  = Integer.parseInt(theYr);
    }
    catch(NumberFormatException e) {
      iMo = 1; iDay = 1; iYr = 1990;
    }
    return CalendarHelper.getCal(iYr, iMo, iDay);
  }
    
  public static int getYear(Calendar theCal) {
    return theCal.get(Calendar.YEAR);
  }
  public static int getMonthOffSet(Calendar theCal) {
    return theCal.get(Calendar.MONTH);
  }
  public static int getRealMonth(Calendar theCal) {
    return theCal.get(Calendar.MONTH)+1;
  }
  public static int getDay(Calendar theCal) {
    return theCal.get(Calendar.DAY_OF_MONTH);
  }
  public static String getDayOfWeek(Calendar theCal) {
    //create an array of days
    String[] strDays = new String[]{"Sunday","Monday","Tuesday","Wednesday","Thusday","Friday","Saturday"};
   
    return strDays[theCal.get(Calendar.DAY_OF_WEEK) - 1];
  }
  
  public static String getIsoDate(Calendar theCal) {
    if ( theCal != null ) {
      return (new java.text.SimpleDateFormat("yyyy-MM-dd")).format(theCal.getTime());    
    }
    return "";
  }  
  public static Calendar getLastDayOfMonth(Calendar theCal) {	  
	GregorianCalendar calObj = new GregorianCalendar(theCal.get(Calendar.YEAR), theCal.get(Calendar.MONTH), theCal.get(Calendar.DATE));
	calObj.set(Calendar.DAY_OF_MONTH, calObj.getActualMaximum(Calendar.DAY_OF_MONTH));
	return calObj;
  }
  public static Calendar getLastDayOfNextMonth(Calendar theCal) {
	  //GregorianCalendar calObj = new GregorianCalendar(Calendar.YEAR, Calendar.MONTH, 1);
	  GregorianCalendar calObj = new GregorianCalendar(theCal.get(Calendar.YEAR), theCal.get(Calendar.MONTH), 1);
	  calObj.add(Calendar.MONTH,1);
	  calObj.set(Calendar.DAY_OF_MONTH, calObj.getActualMaximum(Calendar.DAY_OF_MONTH));
	  return calObj;
  }  
  public static Calendar getNextDay(Calendar theCal) {
    GregorianCalendar calObj = new GregorianCalendar(theCal.get(Calendar.YEAR), theCal.get(Calendar.MONTH), 
                                                     theCal.get(Calendar.DAY_OF_MONTH));
    calObj.add(Calendar.DAY_OF_MONTH,1);    
    return calObj;
  }  
      
  public static Calendar getNextEnd(Calendar theCal) {
	  Calendar newCal = CalendarHelper.getLastDayOfMonth(theCal);
	  if (newCal.compareTo(theCal) == 0) {
		  newCal = CalendarHelper.getLastDayOfNextMonth(theCal);
	  }
	  return newCal;
  }
  public static boolean datesMatch(Calendar obj1, Calendar obj2) {
    // Return true if the dates are the same
    return obj1.get(Calendar.YEAR) == obj2.get(Calendar.YEAR)&&
           obj1.get(Calendar.MONTH)== obj2.get(Calendar.MONTH)&&
           obj1.get(Calendar.DAY_OF_MONTH)== obj2.get(Calendar.DAY_OF_MONTH); 
  }
  public static int getDaysDifference(Calendar obj1, Calendar obj2) {
    // Returns the number of days between two calendar objects, we calc difference in milliseconds and
    // then divide by milliseconds in a day
    long ms1 = obj1.getTime().getTime(); 
    long ms2 = obj2.getTime().getTime();
    return (int) ((ms1-ms2)/(1000*60*60*24));
  }
  
  
  
  
}