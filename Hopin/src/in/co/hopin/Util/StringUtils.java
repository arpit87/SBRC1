package in.co.hopin.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class StringUtils {	
	
    
    public static boolean isEmpty(String s){
        return (s == null || s.length() == 0);
    }

    public static boolean isBlank(String s){
        if (isEmpty(s)) {
            return true;
        }
            
        int strLen = s.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(s.charAt(i)) == false){
                return false;
            }
        }

        return true;
    }
    
    public static String formatDate(String fromFormat,String toFormat,String date_time)
    {
    	SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
    	Date date;
    	String newFormat = date_time;
		try {
			date = formatter.parse(date_time);
			formatter.applyPattern(toFormat);
			newFormat = formatter.format(date);	    	
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		return newFormat;
    }
    
    public static String gettodayDateInFormat(String format)
    {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date now = new Date();  		 
		String date = dateFormat.format(now);
		return date;
    }
    
    public static String getFutureDateInformat(int future_num_days, String format)
    {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date now = new Date();  		 
		Calendar cal = Calendar.getInstance();  
		cal.setTime(now);		
		cal.add(Calendar.DATE, future_num_days);   
		Date travelDate = cal.getTime();				
		String date = dateFormat.format(travelDate);
		return date;
    }
    
    public static String getFutureTimeInformat(int add_minutes, String format)
    {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date now = new Date();  		 
		Calendar cal = Calendar.getInstance();  
		cal.setTime(now);		
		cal.add(Calendar.MINUTE, add_minutes);   
		Date travelDate = cal.getTime();				
		String date = dateFormat.format(travelDate);
		return date;
    }
    
    public static String getDateFromTplusString(String TplusString,String format)
    {    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date now = new Date();  		 
		Calendar cal = Calendar.getInstance();  
		cal.setTime(now);
		Date travelDate = cal.getTime();					
		int days = TplusString.charAt(TplusString.length()-1)-48;
		if(days>0 && days<9)
		{
			cal.add(Calendar.DATE, days);   
			travelDate = cal.getTime();
		}		
		String date = dateFormat.format(travelDate);
		return date;		
    }
    
    public static boolean checkIfRequestExpired(String dateTime)
	{
		//currently chking for 2 hrs
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
		try {
			Date date =  formatter.parse(dateTime);	
			Long currentTime = System.currentTimeMillis();
			Long instaTime = date.getTime();			
			if( currentTime - instaTime  > 7.2e6)
				return true;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return false;
	}
	
	
}

