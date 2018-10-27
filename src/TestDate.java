import java.io.BufferedWriter;
import java.io.FileWriter;


public class TestDate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		java.util.Calendar myCal = CalendarHelper.getTodayCal();
		java.util.Calendar endCal = CalendarHelper.getLastDayOfMonth(myCal);
		java.util.Calendar nxtCal = CalendarHelper.getLastDayOfNextMonth(myCal);
        System.out.println(CalendarHelper.getIsoDate(myCal));
        System.out.println(CalendarHelper.getIsoDate(endCal));
        System.out.println(CalendarHelper.getIsoDate(nxtCal));        
        
        try {
        FileWriter fw = new FileWriter("/home/sduffy/stocks/dum.dum", false);
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write("This is a test"); bw.newLine(); bw.newLine();
	    bw.write(CalendarHelper.getIsoDate(nxtCal)); bw.newLine();
	    bw.flush();
	    bw.close();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        
        
	}

}
