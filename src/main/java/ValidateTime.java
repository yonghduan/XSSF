import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ValidateTime
{
    private Date startTime;
    private Date endTime;

    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();

    public ValidateTime(String timeStr)
    {
        int length = timeStr.length();
        boolean isBeyondOneDay = false;
        if(length > 11)
            isBeyondOneDay = true;

        if(isBeyondOneDay)
        {
            Date[] dates = switchIntoDateBeyondADay(timeStr);
            startTime = dates[0];
            startCal.setTime(startTime);
            endTime = dates[1];
            endCal.setTime(endTime);
        }
        else
        {
            //只有一天
            Date[] dateObj = switchIntoDate(timeStr);
            startTime = dateObj[0];
            startCal.setTime(startTime);
            endTime = dateObj[1];
            endCal.setTime(endTime);
        }
    }

    public Date[] switchIntoDate(String oneDayTimeStr)
    {
        String trimStr = oneDayTimeStr.trim();
        String[] dateTimeStr = trimStr.split("日");
        String[] monthAndDate = dateTimeStr[0].split("\\.");
        String[] time = dateTimeStr[1].split("-");

        int month = Integer.valueOf(monthAndDate[0]);
        int date = Integer.valueOf(monthAndDate[1]);
        int startHour = Integer.valueOf(time[0]);
        int endHour = Integer.valueOf(time[1]);

        Calendar startCal = Calendar.getInstance();
        startCal.set(2022,month - 1,date,startHour,0,0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(2022,month - 1,date,endHour,0,0);
        endCal.add(Calendar.HOUR,2);

        return new Date[]{startCal.getTime(),endCal.getTime()};
    }

    public Date[] switchIntoDateBeyondADay(String beyondOneDayStr)
    {
        String trimStr = beyondOneDayStr.trim();
        String[] twoDate = trimStr.split("-");
        Date firstDate = switchSingleTimeStr(twoDate[0],false);
        Date secondDate = switchSingleTimeStr(twoDate[1],true);
        return new Date[]{firstDate,secondDate};
    }

    public Date switchSingleTimeStr(String str,boolean endTimeFlag)
    {
        String[] monthDayAndHour = str.split("日");
        String[] monthAndDay = monthDayAndHour[0].split("\\.");
        int month = Integer.valueOf(monthAndDay[0]);
        int day = Integer.valueOf(monthAndDay[1]);
        int hour = Integer.valueOf(monthDayAndHour[1]);
        Calendar cal = Calendar.getInstance();
        cal.set(2022,month - 1,day,hour,0,0);
        if(endTimeFlag)
            cal.add(Calendar.HOUR,2);
        return cal.getTime();
    }

    public boolean isTimeInRange(String timeStr) throws ParseException
    {
        String trimStr = timeStr.trim();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date  = dateFormat.parse(trimStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND,0);
        Date newDate = cal.getTime();
        String newDateStr = dateFormat.format(newDate);

       /* SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat1.format(cal.getTime()));
        System.out.println(dateFormat1.format(startCal.getTime()));
        System.out.println(dateFormat1.format(endCal.getTime()));*/

        if(newDateStr.equals(dateFormat.format(startTime)) || newDateStr.equals(dateFormat.format(endTime)))
            return true;

        if(cal.after(startCal) && cal.before(endCal))
            return true;
        else
            return false;




    }
}
